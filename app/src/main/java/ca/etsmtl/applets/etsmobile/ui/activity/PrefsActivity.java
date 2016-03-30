package ca.etsmtl.applets.etsmobile.ui.activity;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ca.etsmtl.applets.etsmobile.R;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;

/**
 * Created by gnut3ll4 on 12/14/14.
 */
public class PrefsActivity extends PreferenceActivity {
    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment(),PrefsFragment.class.getName()).commit();//.addToBackStack(null).commit();

        setTitle(getString(R.string.action_preferences));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows the news_preferences for the first header.
     */
    public static class PrefsFragment extends PreferenceFragment {


        public static final String CHOIX_DES_SOURCES = "Choix des sources";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // app icon in action bar clicked; goto parent activity.
                    getActivity().onBackPressed();
                    AnalyticsHelper.getInstance(getActivity())
                            .sendActionEvent(getClass().getSimpleName(), CHOIX_DES_SOURCES);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }


        }
    }




}
