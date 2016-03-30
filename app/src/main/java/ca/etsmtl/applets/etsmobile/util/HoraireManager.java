package ca.etsmtl.applets.etsmobile.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observable;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.EventList;
import ca.etsmtl.applets.etsmobile.model.HoraireActivite;
import ca.etsmtl.applets.etsmobile.model.JoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.model.listeJoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.listeSeances;

public class HoraireManager extends Observable implements RequestListener<Object> {

    private Activity activity;
    private boolean syncSeancesEnded = false;
    private boolean syncJoursRemplacesEnded = false;
    private boolean syncEventListEnded = false;

    private String calendarName = "Mes cours";
    DatabaseHelper databaseHelper;
    Dao<JoursRemplaces, ?> daoJoursRemplaces;
    Dao<Seances, ?> daoSeances;


    public HoraireManager(final RequestListener<Object> listener, Activity activity) {
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
        try {
            daoJoursRemplaces = databaseHelper.getDao(JoursRemplaces.class);
            daoSeances = databaseHelper.getDao(Seances.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        e.printStackTrace();
    }

    @Override
    public void onRequestSuccess(final Object o) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                //lireJoursRemplaces
                if (o instanceof listeJoursRemplaces) {
                    listeJoursRemplaces listeJoursRemplaces = (listeJoursRemplaces) o;
                    new Synchronizer<JoursRemplaces>(daoJoursRemplaces)
                            .synchronize(listeJoursRemplaces.listeJours);
                    syncJoursRemplacesEnded = true;
                }

                //listeSeances
                if (o instanceof listeSeances) {
                    listeSeances listeSeancesObj = (listeSeances) o;

                    for (Seances SeancesInAPI : listeSeancesObj.ListeDesSeances) {

                        SeancesInAPI.id = SeancesInAPI.coursGroupe +
                                SeancesInAPI.dateDebut +
                                SeancesInAPI.dateFin +
                                SeancesInAPI.local;
                    }

                    new Synchronizer<Seances>(daoSeances)
                            .synchronize(listeSeancesObj.ListeDesSeances);

                    syncSeancesEnded = true;
                }

                // ETS Calendar Events
                if (o instanceof EventList) {

                    //Synchronizer
                    //Don't forget to override "equals" method in Event class to auto sync on id
                    
                    deleteExpiredEvent((EventList) o);
                    createOrUpdateEventListInBD((EventList) o);
                    syncEventListEnded = true;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (syncJoursRemplacesEnded && syncSeancesEnded && syncEventListEnded) {
                    SecurePreferences securePreferences = new SecurePreferences(activity);
                    if (securePreferences.getBoolean(Constants.FIRST_LOGIN, true)) {
                        securePreferences.edit().putBoolean(Constants.FIRST_LOGIN, false).commit();
                    }
                }
                HoraireManager.this.setChanged();
                HoraireManager.this.notifyObservers();
            }
        }.execute();

    }

    /**
     * Deletes entries in DB that doesn't exist on API
     *
     * @param
     */
    private void deleteExpiredEvent(EventList envEventList) {

        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        ArrayList<Event> dbEvents = new ArrayList<Event>();
        try {
            dbEvents = (ArrayList<Event>) dbHelper.getDao(Event.class).queryForAll();
            for (Event eventsNew : dbEvents) {

                if (!dbEvents.contains(eventsNew.getId())) {
                    Dao<Event, String> eventDao = dbHelper.getDao(Event.class);
                    eventDao.deleteById(eventsNew.getId());
                    Log.v("Supression", eventsNew.getId() + " supprimé");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds new API entries on DB or updates existing ones
     *
     * @param eventList
     */
    private void createOrUpdateEventListInBD(EventList eventList) {
        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        try {
            for (Event event : eventList) {
                dbHelper.getDao(Event.class).createOrUpdate(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void updateCalendar() throws Exception {

        DatabaseHelper dbHelper = new DatabaseHelper(activity);
        AndroidCalendarManager androidCalendarManager = new AndroidCalendarManager(activity);

        androidCalendarManager.deleteCalendar(calendarName);
        androidCalendarManager.createCalendar(calendarName);


        //Inserting JoursRemplaces in local calendar
        SimpleDateFormat joursRemplacesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH);
        ArrayList<JoursRemplaces> listeJoursRemplaces = (ArrayList<JoursRemplaces>) dbHelper.getDao(JoursRemplaces.class).queryForAll();


        for (JoursRemplaces joursRemplaces : listeJoursRemplaces) {
            androidCalendarManager.insertEventInCalendar(calendarName,
                    joursRemplaces.description,
                    joursRemplaces.description,
                    "",
                    joursRemplacesFormatter.parse(joursRemplaces.dateOrigine),
                    joursRemplacesFormatter.parse(joursRemplaces.dateOrigine));
        }


        //Inserting Seances in local calendar
        SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA_FRENCH);
        ArrayList<Seances> seances = (ArrayList<Seances>) dbHelper.getDao(Seances.class).queryForAll();


        for (Seances seance : seances) {


            androidCalendarManager.insertEventInCalendar(calendarName,
                    seance.descriptionActivite.equals("Examen final") ? "Examen final " + seance.coursGroupe : seance.coursGroupe,
                    seance.libelleCours + " - " + seance.descriptionActivite,
                    seance.local,
                    seancesFormatter.parse(seance.dateDebut),
                    seancesFormatter.parse(seance.dateFin));
        }


        //Inserting public calendar ETS

        ArrayList<Event> events = (ArrayList<Event>) dbHelper.getDao(Event.class).queryForAll();
        for (Event event : events) {
            androidCalendarManager.insertEventInCalendar(calendarName,
                    event.getTitle(),
                    "",
                    ""
                    ,
                    joursRemplacesFormatter.parse(event.getDateDebut()),
                    joursRemplacesFormatter.parse(event.getDateFin()));
        }


    }
}