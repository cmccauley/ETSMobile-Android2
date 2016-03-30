package ca.etsmtl.applets.etsmobile.util;

import android.app.Activity;
import android.os.AsyncTask;

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

                //Calendar ApplETS API with ETS
                syncEventListEnded = true;
                /*todo
                if (o instanceof EventList) {
                    EventList eventList = (EventList) o;

                    //Synchronizer
                    //Don't forget to override "equals" method in Event class to auto sync on id

                    syncEventListEnded = true;
                }
                //*/

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
                    joursRemplacesFormatter.parse(event.getStartDate()),
                    joursRemplacesFormatter.parse(event.getEndDate()));
        }


    }
}