package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.octo.android.robospice.persistence.exception.SpiceException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.http.DataManager.SignetMethods;
import ca.etsmtl.applets.etsmobile.model.Etudiant;
import ca.etsmtl.applets.etsmobile.model.Programme;
import ca.etsmtl.applets.etsmobile.model.listeDesProgrammes;
import ca.etsmtl.applets.etsmobile.ui.adapter.ProfileAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.ProfilManager;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * @author Philippe, Laurence
 */
public class ProfilFragment extends HttpFragment {

    private ProfileAdapter profileAdapter;
    private ProfilManager profilManager;
    @Bind(R.id.listview_profile)
    ListView listViewProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileAdapter = new ProfileAdapter(getActivity(), null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_profil, container, false);
        super.onCreateView(inflater, v, savedInstanceState);
        ButterKnife.bind(this,v);

        loadingView.showLoadingView();

        dataManager.getDataFromSignet(SignetMethods.INFO_ETUDIANT, ApplicationManager.userCredentials, this, "");
        dataManager.getDataFromSignet(SignetMethods.LIST_PROGRAM, ApplicationManager.userCredentials, this, "");

        listViewProfile.setAdapter(profileAdapter);

        profilManager = new ProfilManager(getActivity());

        Button logoutButton = (Button) v.findViewById(R.id.profil_button_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationManager.deconnexion(getActivity());
            }
        });

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    void updateUI() {
        Etudiant etudiant = profilManager.getEtudiant();
        profileAdapter.updateEtudiant(etudiant);
        List<Programme> programmes = profilManager.getProgrammes();
        profileAdapter.updateListeDesProgrammes(new ArrayList<Programme>(programmes));
    }

    @Override
    public void onRequestSuccess(Object o) {
        super.onRequestSuccess(o);
        if (o != null) {
            if (o instanceof Etudiant) {

                Etudiant etudiant = (Etudiant) o;

                profileAdapter.updateEtudiant(etudiant);

                if (etudiant.erreur == null) {
                    // Save Etudiant class in DB
                    profilManager.updateEtudiant(etudiant);
                }
            } else if (o instanceof listeDesProgrammes) {

                listeDesProgrammes listeDesProgrammes = (listeDesProgrammes) o;

                if (listeDesProgrammes.erreur == null) {
                    for (Programme p : listeDesProgrammes.liste) {
                        profilManager.updateProgramme(p);
                    }
                }
            }
            updateUI();
        }

    }

    @Override
    public void onRequestFailure(SpiceException e) {
        loadingView.hideProgessBar();
    }


}
