package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ca.etsmtl.applets.etsmobile.ApplicationManager;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreCourse;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreCourses;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleCoreModule;
import ca.etsmtl.applets.etsmobile.model.Moodle.MoodleModuleContent;
import ca.etsmtl.applets.etsmobile.ui.adapter.ExpandableListMoodleSectionAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Displays downloadable resources for a Moodle course
 */
public class MoodleCourseDetailsFragment extends HttpFragment {

    public static final String TELECHARGE_FICHIER_MOODLE = "A téléchargé un fichier de moodle";
    public static final String CONSULTE_PAGE_MOODLE = "A consulté une page sur Moodle";
    public static String COURSE_ID = "COURSE_ID";
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 0;

    private long enqueue;
    private DownloadManager dm;
    int downloadPermission;

    private String moodleCourseId;

    private ExpandableListMoodleSectionAdapter expandableListMoodleAdapter;

    private ExpandableListView expListView;

    private HashMap<HeaderText, Object[]> listDataSectionName; // Pour gérer les ressources/liens par section
    private List<HeaderText> listDataHeader;

    private ArrayList<MoodleCoreModule> listMoodleLinkModules;
    private ArrayList<MoodleModuleContent> listMoodleResourceContents;

    private BroadcastReceiver receiver = null;


    public static MoodleCourseDetailsFragment newInstance(int moodleCourseId) {
        MoodleCourseDetailsFragment fragment = new MoodleCourseDetailsFragment();
        Bundle args = new Bundle();
        args.putString(COURSE_ID, Integer.toString(moodleCourseId));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && savedInstanceState == null) {
            Bundle bundle = getArguments();
            moodleCourseId = bundle.getString(COURSE_ID);
        } else {
            moodleCourseId = savedInstanceState.getString("moodleCourseId");
        }
        refreshPermission();
    }

    public void refreshPermission(){
        downloadPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void checkPermission(){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else refreshPermission();
    }

    @Override
    public void onActivityCreated(Bundle onSavedInstanceState) {
        super.onActivityCreated(onSavedInstanceState);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {

                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            MimeTypeMap map = MimeTypeMap.getSingleton();
                            String ext = MimeTypeMap.getFileExtensionFromUrl(uriString);
                            String type = map.getMimeTypeFromExtension(ext);

                            if (type == null)
                                type = "*/*";

                            Intent openFile = new Intent(Intent.ACTION_VIEW);
                            openFile.setDataAndType(Uri.parse(uriString), type);
                            try {
                                startActivity(openFile);
                            } catch(ActivityNotFoundException e) {
                                Toast.makeText(getActivity(), getString(R.string.cannot_open_file), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        
        queryMoodleCoreCourses(moodleCourseId);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("permission", "Inside permission granted");
                    refreshPermission();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(getActivity(), "you must enable permissions in settings",Toast.LENGTH_LONG).show();
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("moodleCourseId", moodleCourseId);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_moodle_details, container, false);
        super.onCreateView(inflater, v, savedInstanceState);

        expListView = (ExpandableListView) v.findViewById(R.id.expandableListView_moodle_courses_details);

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        super.onRequestFailure(e);
    }

    @Override
    public void onRequestSuccess(Object o) {

        if(o instanceof MoodleCoreCourses) {

            MoodleCoreCourses moodleCoreCourses = (MoodleCoreCourses) o;

            // create empty data
            listDataSectionName = new HashMap<HeaderText, Object[]>();
            listDataHeader = new ArrayList<HeaderText>();

            int positionSection = 0;

            for(MoodleCoreCourse coreCourse : moodleCoreCourses) {


                listMoodleLinkModules = new ArrayList<MoodleCoreModule>();
                listMoodleResourceContents = new ArrayList<MoodleModuleContent>();

                for(MoodleCoreModule coreModule : coreCourse.getModules()) {

                    if(coreModule.getModname().equals("folder")) {
                        if(coreModule.getContents() != null)
                            listMoodleResourceContents.addAll(coreModule.getContents());
                    } else if (coreModule.getModname().equals("url") || coreModule.getModname().equals("forum")) {
                        listMoodleLinkModules.add(coreModule);
                    } else if (coreModule.getModname().equals("resource")) {
                        listMoodleResourceContents.addAll(coreModule.getContents());
                    }
                }

                Object[] finalArray = ArrayUtils.addAll(listMoodleLinkModules.toArray(), listMoodleResourceContents.toArray());
                if(finalArray.length != 0)
                    listDataSectionName.put(new HeaderText(coreCourse.getName(), positionSection), finalArray);

                positionSection++;
            }



            listDataHeader.addAll(listDataSectionName.keySet());

            Collections.sort(listDataHeader, new Comparator<HeaderText>() {
                @Override
                public int compare(HeaderText headerText1, HeaderText headerText2) {

                    if(headerText1.getPosition() < headerText2.getPosition()) {
                        return -1;
                    } else if (headerText1.getPosition() == headerText2.getPosition()) {
                        return 0;
                    } else {
                        return 1;
                    }

                }
            });


            expandableListMoodleAdapter = new ExpandableListMoodleSectionAdapter(getActivity(), listDataHeader, listDataSectionName);
            expListView.setAdapter(expandableListMoodleAdapter);
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                    Object object = expandableListMoodleAdapter.getChild(groupPosition, childPosition);

                    if (object instanceof MoodleModuleContent) {
                        MoodleModuleContent item = (MoodleModuleContent) object;

                        String url = item.getFileurl() + "&token=" + ApplicationManager.userCredentials.getMoodleToken();
                        Uri uri = Uri.parse(url);
                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, item.getFilename());

//                      r.allowScanningByMediaScanner();

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        MimeTypeMap mimetype = MimeTypeMap.getSingleton();
                        String extension = FilenameUtils.getExtension(item.getFilename());

                        request.setMimeType(mimetype.getMimeTypeFromExtension(extension));

                        dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        checkPermission();
                        if(downloadPermission == 0) enqueue = dm.enqueue(request);

                        AnalyticsHelper.getInstance(getActivity())
                                .sendActionEvent(getClass().getSimpleName(), TELECHARGE_FICHIER_MOODLE);
                    }

                    if (object instanceof MoodleCoreModule) {
                        MoodleCoreModule item = (MoodleCoreModule) object;

                        String url = "";
                        if (item.getModname().equals("url")) {
                            url = item.getContents().get(0).getFileurl();
                        } else {
                            url = item.getUrl();
                        }


                        AnalyticsHelper.getInstance(getActivity())
                                .sendActionEvent(getClass().getSimpleName(), CONSULTE_PAGE_MOODLE);

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }

                    return true;
                }
            });
            super.onRequestSuccess(null);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();

    }


    /**
     * Query all resources for a Moodle course
     * @param idCourse
     */
    private void queryMoodleCoreCourses(final String idCourse) {
        SpringAndroidSpiceRequest<Object> request = new SpringAndroidSpiceRequest<Object>(null) {

            @Override
            public MoodleCoreCourses loadDataFromNetwork() throws Exception {
                String url = getActivity().getString(R.string.moodle_api_core_course_get_contents, ApplicationManager.userCredentials.getMoodleToken(), idCourse);

                return getRestTemplate().getForObject(url, MoodleCoreCourses.class);
            }
        };
        dataManager.sendRequest(request, MoodleCourseDetailsFragment.this);
    }



    @Override
    void updateUI() {
       loadingView.showLoadingView();
    }

    /**
     * Holder for headers in ExpandableListView
     */
    public class HeaderText {
        String headerName;
        int position;

        public HeaderText(String headerName, int position) {
            this.headerName = headerName;
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }
}
