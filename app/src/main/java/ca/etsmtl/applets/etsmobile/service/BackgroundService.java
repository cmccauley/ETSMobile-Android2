package ca.etsmtl.applets.etsmobile.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.io.BaseEncoding;
import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.soap.SignetsMobileSoap;
import ca.etsmtl.applets.etsmobile.model.ArrayOfJoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.GoogleEventWrapper;
import ca.etsmtl.applets.etsmobile.model.JoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.ListeDeSessions;
import ca.etsmtl.applets.etsmobile.model.Seances;
import ca.etsmtl.applets.etsmobile.model.Trimestre;
import ca.etsmtl.applets.etsmobile.model.listeJoursRemplaces;
import ca.etsmtl.applets.etsmobile.model.listeSeances;
import ca.etsmtl.applets.etsmobile.util.Constants;
import ca.etsmtl.applets.etsmobile.util.SecurePreferences;
import ca.etsmtl.applets.etsmobile.util.Synchronizer;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by gnut3ll4 on 26/03/16.
 */
public class BackgroundService extends WakefulIntentService implements RequestListener<Object> {

    DatabaseHelper databaseHelper;
    Dao<JoursRemplaces, ?> daoJoursRemplaces;
    Dao<Seances, ?> daoSeances;


    String calendarId;

    public BackgroundService() {
        super("BackgroundService");
    }

    /**
     * Asynchronous background operations of service, with wakelock
     */
    @Override
    public void doWakefulWork(Intent intent) {
        Log.e("TEST", "BACKGROUND TASK EXECUTING.......");
        Calendar client;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        GoogleAccountCredential credential;
        SecurePreferences securePreferences = new SecurePreferences(this);

        databaseHelper = new DatabaseHelper(this);
        try {
            daoJoursRemplaces = databaseHelper.getDao(JoursRemplaces.class);
            daoSeances = databaseHelper.getDao(Seances.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String selectedAccount = securePreferences.getString(Constants.SELECTED_ACCOUNT, "");


        credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(CalendarScopes.CALENDAR));
        credential.setSelectedAccountName(selectedAccount);

        client = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Google-CalendarAndroidSample/1.0")
                .build();

        /*
        DataManager dataManager = DataManager.getInstance(this);

        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_SEANCES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        dataManager.getDataFromSignet(DataManager.SignetMethods.LIST_JOURSREMPLACES_CURRENT_AND_NEXT_SESSION, ApplicationManager.userCredentials, this);
        */
        SignetsMobileSoap signetsMobileSoap = new SignetsMobileSoap();

        //Checking if calendar exists and create it if not
        if (!selectedAccount.isEmpty()) {
            calendarId = securePreferences.getString(Constants.CALENDAR_ID, "");
            try {
                if (calendarId.isEmpty()) {
                    com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
                    calendar.setSummary("Mes cours ÉTS");
                    calendarId = client.calendars().insert(calendar).execute().getId();
                    securePreferences.edit().putString(Constants.CALENDAR_ID, calendarId).commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Inserting JoursRemplaces in local calendar
        SimpleDateFormat joursRemplacesFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA_FRENCH);
        //Inserting Seances in local calendar
        SimpleDateFormat seancesFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA_FRENCH);


        ArrayList<Event> events = new ArrayList<>();

        //Getting and syncing in DB JoursRemplaces from Signets
        Observable<Event> eventJoursRemplacesObservable =
                Observable.just(signetsMobileSoap)
                        .flatMap(signetsMobileSoap1 -> {
                            try {
                                return Observable.just(
                                        signetsMobileSoap1.listeSessions(
                                                ApplicationManager.userCredentials.getUsername(),
                                                ApplicationManager.userCredentials.getPassword()));
                            } catch (Exception e) {
                                return Observable.error(e);
                            }
                        })
                        .flatMap(listeDeSessions -> Observable.from(listeDeSessions.liste))
                        .filter(trimestre -> {
                            DateTime dtStart = new DateTime();
                            DateTime dtEnd = new DateTime(trimestre.dateFin);

                            return dtStart.isBefore(dtEnd.plusDays(1));
                        })
                        .flatMap(trimestre1 -> {

                            try {
                                return Observable.just(signetsMobileSoap.lireJoursRemplaces(trimestre1.abrege).listeJours);
                            } catch (Exception e) {
                                return Observable.error(e);
                            }

                        })
                        .doOnNext(new Synchronizer<>(daoJoursRemplaces)::synchronize)
                        .flatMap(Observable::from)
                        .flatMap(joursRemplaces -> {
                            try {
                                Event event = new Event();
                                String encodedId = BaseEncoding.base32Hex()
                                        .encode(joursRemplaces.dateOrigine.getBytes())
                                        .toLowerCase()
                                        .replace("=", "");

                                event.setId(encodedId);
                                event.setSummary(joursRemplaces.description);

                                Date dateStart = joursRemplacesFormatter.parse(joursRemplaces.dateOrigine);
                                EventDateTime eventDateTime = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(dateStart));

                                event.setStart(eventDateTime);
                                event.setEnd(eventDateTime.clone());

                                return Observable.just(event);
                            } catch (ParseException e) {
                                return Observable.error(e);
                            }
                        });

        Observable<Event> eventSeancesObservable =
                Observable.just(signetsMobileSoap)
                        .flatMap(signetsMobileSoap1 -> {
                            try {
                                return Observable.just(
                                        signetsMobileSoap1.listeSessions(
                                                ApplicationManager.userCredentials.getUsername(),
                                                ApplicationManager.userCredentials.getPassword()));
                            } catch (Exception e) {
                                return Observable.error(e);
                            }
                        })
                        .flatMap(listeDeSessions -> Observable.from(listeDeSessions.liste))
                        .filter(trimestre -> {
                            DateTime dtStart = new DateTime();
                            DateTime dtEnd = new DateTime(trimestre.dateFin);

                            return dtStart.isBefore(dtEnd.plusDays(1));
                        })
                        .flatMap(trimestre1 -> {
                            try {
                                return Observable.just(signetsMobileSoap.lireHoraireDesSeances(
                                        ApplicationManager.userCredentials.getUsername(),
                                        ApplicationManager.userCredentials.getPassword(), "",
                                        trimestre1.abrege, "", "")
                                        .ListeDesSeances);
                            } catch (Exception e) {
                                return Observable.error(e);
                            }

                        })
                        .flatMap(Observable::from)
                        .flatMap(seance -> {
                            seance.id = seance.coursGroupe +
                                    seance.dateDebut +
                                    seance.dateFin +
                                    seance.local;
                            return Observable.just(seance);
                        })
                        .toList()
                        .doOnNext(new Synchronizer<>(daoSeances)::synchronize)
                        .flatMap(Observable::from)
                        .flatMap(seance -> {
                            try {
                                Event event = new Event();
                                String encodedId = BaseEncoding.base32Hex()
                                        .encode(seance.id.getBytes())
                                        .toLowerCase()
                                        .replace("=", "");
                                event.setId(encodedId);
                                event.setSummary(seance.descriptionActivite.equals("Examen final") ? "Examen final " + seance.coursGroupe : seance.coursGroupe);
                                event.setLocation(seance.local);

                                Date dateStart = seancesFormatter.parse(seance.dateDebut);
                                Date dateEnd = seancesFormatter.parse(seance.dateFin);
                                EventDateTime eventStartDateTime = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(dateStart));
                                EventDateTime eventEndDateTime = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(dateEnd));

                                event.setStart(eventStartDateTime);
                                event.setEnd(eventEndDateTime);

                                return Observable.just(event);
                            } catch (ParseException e) {
                                return Observable.error(e);
                            }
                        });


        Observable<List<GoogleEventWrapper>> remoteEventsSignets =
                Observable.merge(eventJoursRemplacesObservable, eventSeancesObservable)
                        .flatMap(event -> Observable.just(new GoogleEventWrapper(event)))
                        .toList();

        Observable<List<GoogleEventWrapper>> localEventsGoogle = Observable.just(client.events())
                .flatMap(events1 -> {
                    try {
                        return Observable.from(events1.list(calendarId).execute().getItems());
                    } catch (IOException e) {
                        return Observable.error(e);
                    }
                })
                .flatMap(event1 -> Observable.just(new GoogleEventWrapper(event1)))
                .toList();


        Observable.zip(localEventsGoogle, remoteEventsSignets,
                (localEvents, remoteEvents) -> {

                    try {
                        for (GoogleEventWrapper localObject : localEvents) {
                            if (!remoteEvents.contains(localObject)) {
                                client.events().delete(calendarId, localObject.getEvent().getId()).execute();
                            }
                        }

                        // Adds new API entries on DB or updates existing ones
                        for (GoogleEventWrapper remoteObject : remoteEvents) {
                            try {
                                client.events().get(calendarId, remoteObject.getEvent().getId()).execute();
                                client.events().update(calendarId, remoteObject.getEvent().getId(), remoteObject.getEvent()).execute();
                            } catch (GoogleJsonResponseException e) {
                                if (e.getStatusCode() == 404) {
                                    client.events().insert(calendarId, remoteObject.getEvent()).execute();
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return null;
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.e("TESTT", "SYNC COMPLETED");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        spiceException.printStackTrace();
    }


    @Override
    public void onRequestSuccess(Object o) {

        //lireJoursRemplaces
        if (o instanceof listeJoursRemplaces) {
            listeJoursRemplaces listeJoursRemplaces = (listeJoursRemplaces) o;
            new Synchronizer<JoursRemplaces>(daoJoursRemplaces)
                    .synchronize(listeJoursRemplaces.listeJours);
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

        }
    }
}