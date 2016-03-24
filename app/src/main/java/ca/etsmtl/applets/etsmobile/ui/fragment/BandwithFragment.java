package ca.etsmtl.applets.etsmobile.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.etsmtl.applets.etsmobile.model.ConsommationBP;
import ca.etsmtl.applets.etsmobile.ui.adapter.LegendAdapter;
import ca.etsmtl.applets.etsmobile.util.AnalyticsHelper;
import ca.etsmtl.applets.etsmobile.util.Utility;
import ca.etsmtl.applets.etsmobile.views.MultiColorProgressBar;
import ca.etsmtl.applets.etsmobile.views.ProgressItem;
import ca.etsmtl.applets.etsmobile2.R;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Phil on 17/11/13. Coded by Laurence 26/03/14
 */
public class BandwithFragment extends Fragment {

    /** Constantes basées sur la requête qu'on reçoit en JSON de Cooptel */
    private final int DETAILED_BANDWITH_INDEX = 0;
    private final int QUOTA_INDEX = 1;
    private final int PORT = 0;
    private final int UPLOAD = 2;
    private final int DOWNLOAD = 3;
    private final String CONTENT = "content";
    static double uploadTot, downloadTot;
    double upload, download;
    private PieChartView chart;
    private PieChartData data;
    static double limit;
    static String phase, app;
    ArrayList<Double> upDownList = new ArrayList<>();



    /**
     * En date du 20 mars 2015
     * Voici un exemple du JSON qui est traité dans cette classe.
     *
    "results":{
        "table":[
            {
                "border":"1",
                "tbody":{
                    "tr":[
                        {
                            "td":[
                                "Port..",
                                "Date",
                                "Upload (Mo)",
                                "Download (Mo)"
                            ]
                        },
                        {
                            "td":[
                                "10033",
                                    "2015-03-01",
                                    {
                                        "align":"RIGHT",
                                        "content":"    354.39"
                                    },
                                    {
                                        "align":"RIGHT",
                                        "content":"  11204.47"
                                    }
                             ]
                        },
                        {...}, (X nombre de jour)
                        {
                            "td":[
                                {
                                    "colspan":"3",
                                    "b":"Total combiné:"
                                },
                                {
                                    "align":"RIGHT",
                                    "content":" 103794.37"
                                }
                            ]
                        }
                    ]
                }
            },
            {
                "border":"1",
                "width":"50%",
                "tbody":{
                    "tr":[
                        {
                            "td":[
                                " ",
                                "Quota (MO)"
                            ]
                        },
                        {
                            "td":[
                                "Quota permis pour la période",
                                {
                                    "align":"RIGHT",
                                    "content":"128000"
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    }
     */
    private double[] values;
    private String[] rooms;
    private MultiColorProgressBar progressBar;
    private ProgressBar loadProgressBar;
    private EditText editTextApp;
    public OnFocusChangeListener onFocusChangeColorEditText = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == editTextApp) {
                editTextApp.setTextColor(Color.RED);
            }

        }
    };
    private EditText editTextPhase;
    private GridView grid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bandwith, container, false);
        progressBar = (MultiColorProgressBar) v.findViewById(R.id.bandwith_progress);
        editTextApp = (EditText) v.findViewById(R.id.bandwith_editText_app);
        editTextPhase = (EditText) v.findViewById(R.id.bandwith_editText_phase);
        grid = (GridView) v.findViewById(R.id.bandwith_grid);
        loadProgressBar = (ProgressBar)v.findViewById(R.id.progressBarLoad);

        chart = (PieChartView) v.findViewById(R.id.chart);
        chart.setVisibility(View.INVISIBLE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        phase = defaultSharedPreferences.getString("Phase", "");
        app = defaultSharedPreferences.getString("App", "");

        if (phase.length() > 0 && app.length() > 0) {
            editTextApp.setHint(app);
            editTextPhase.setHint(phase);
            System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            month += 1;
            String url = getActivity().getString(R.string.bandwith_query, phase, app, month);
            Log.d("urlUsed", url);
            if(Utility.isNetworkAvailable(getActivity())){
                loadProgressBar.setVisibility(View.VISIBLE);
                new BandwithAsyncTask().execute(url);

            }

        }
        editTextPhase.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    reset();
                    Pattern p = Pattern.compile("[1,2,3,4]");
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        editTextApp.requestFocus();
                    } else {
                        setError(editTextPhase, getString(R.string.error_invalid_phase));
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextApp.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    if (editTextPhase.length() > 0) {
                        String phase = editTextPhase.getText().toString();
                        if (phase.equals("1") || phase.equals("2") || phase.equals("4")) {
                            if (editTextApp.getText().length() > 2) {
                                if (editTextPhase.length() > 0) {
                                    String app = editTextApp.getText().toString();
                                    getBandwith(phase, app);
                                }
                            }
                        } else if (phase.equals("3")) {
                            if (editTextApp.getText().length() > 3) {
                                if (editTextPhase.length() > 0) {
                                    String app = editTextApp.getText().toString();
                                    getBandwith(phase, app);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        AnalyticsHelper.getInstance(getActivity()).sendScreenEvent(getClass().getSimpleName());

        return v;
    }

    private void setError(final EditText edit, final String messageError) {

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    edit.setError(messageError);
                    edit.requestFocus();
                    edit.setHint(edit.getText());
                    edit.setText("");
                }
            });
        }
    }

    private void getBandwith(String phase, String app) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        month += 1;

        String url = getActivity().getString(R.string.bandwith_query, phase, app, month);

        Log.d("urlUsed", url);
        savePhaseAppPreferences(phase, app);
        if(Utility.isNetworkAvailable(getActivity())){
            loadProgressBar.setVisibility(View.VISIBLE);
            new BandwithAsyncTask().execute(url);

        }

    }

    private void savePhaseAppPreferences(String phase, String app) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Editor editor = defaultSharedPreferences.edit();
        editor.putString("Phase", phase);
        editor.putString("App", app);
        editor.commit();

    }

    private void updateProgressBarColorItems(double bandwidthQuota) {
        final int[] colorChoice = new int[]{R.color.red_bandwith, R.color.blue_bandwith, R.color.green_bandwith, R.color.purple_bandwith};
        int[] legendColors = new int[values.length];
        final Activity activity = getActivity();

        progressBar.clearProgressItems();

        for (int i = 0, color = 0; i < values.length - 1; ++i) {
            ProgressItem progressItem = new ProgressItem(colorChoice[color], (values[i] / bandwidthQuota) * 100);

            progressBar.addProgressItem(progressItem);
            legendColors[i] = colorChoice[color];
            color++;

            if (color == colorChoice.length)
                color = 0;
        }

        if (values.length > 0) {
            int lastValue = values.length - 1;
            ProgressItem progressItem = new ProgressItem(R.color.grey_bandwith, (values[lastValue] / bandwidthQuota) * 100);
            legendColors[lastValue] = R.color.grey_bandwith;
            progressBar.addProgressItem(progressItem);
        }


        if (activity != null) {
            final int[] colors = legendColors;

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    grid.setAdapter(new LegendAdapter(activity, rooms, colors));
                }
            });
        }

    }

    private void setProgressBar(final double total, final double quota) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setMax((int) quota);
                    progressBar.setProgress((int) total);
                    View v = getView();
                    String gb = getString(R.string.gigaoctetx);
                    double reste = Math.round((quota - total) * 100) / 100.0;
                    ((TextView) v.findViewById(R.id.bandwith_used_lbl)).setText(getString(R.string.utilise) + " "
                            + reste + gb);
                    ((TextView) v.findViewById(R.id.bandwith_max)).setText(quota + gb);
                }
            });
        }
    }

    private void reset() {
        View v = getView();
        if (v != null) {
            progressBar.setProgress(0);
            String gb = getString(R.string.gigaoctetx);
            ((TextView) v.findViewById(R.id.bandwith_used_lbl)).setText("");
            ((TextView) v.findViewById(R.id.bandwith_max)).setText(gb);
        }
    }

    private void chambreBP(ArrayList<ConsommationBP> list){
        ArrayList<ConsommationBP> autreChambre = new ArrayList<ConsommationBP>();
        double chambreUpTot=0, chambreDownTot=0, chambreTot;
        for(int i=0;i<list.size()-1; i++){
            ConsommationBP item = list.get(i);
            int id = list.get(0).getIdChambre();
            if(id == item.getIdChambre()){
                chambreUpTot = chambreUpTot + item.getUpload();
                chambreDownTot = chambreDownTot + item.getDownload();
            }else{
                autreChambre.add(item);
            }
        }
        if(autreChambre.size()>0){
            chambreTot = chambreUpTot/1024 + chambreDownTot/1024;
            upDownList.add(chambreTot);
            chambreBP(autreChambre);
        }else{
            chambreTot = chambreUpTot/1024 + chambreDownTot/1024;
            upDownList.add(chambreTot);
            rooms = new String[upDownList.size()];
            values = new double[upDownList.size()];
            for(int i=0; i<upDownList.size(); i++){
                values[i] = upDownList.get(i);
                int j =i+1;
                rooms[i] = "■ Chambre" + j + " " + String.format("%.2f",values[i]) + " Go";
            }
            upDownList.clear();
            updateProgressBarColorItems(limit);
        }
    }

    private class BandwithAsyncTask extends AsyncTask<String, Void, String> {

        private JSONObject query;

        @Override
        protected String doInBackground(String... param) {
            try {

                /*HttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                URI uriWeb = new URI(param[0]);
                request.setURI(uriWeb);
                HttpResponse response = httpClient.execute(request);
                int code = response.getStatusLine().getStatusCode();
                if (code == 200) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
                                .getContent(), "UTF-8"));
                        String json = reader.readLine();
                        JSONObject obj = new JSONObject(json);
                        query = (JSONObject) obj.get("query");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://api3.clubapplets.ca/cooptel?phase="+phase+"&appt="+app)
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                String jsonData = response.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONArray Jarray = Jobject.getJSONArray("consommations");
                ArrayList<ConsommationBP> consommationList = new ArrayList<>();
                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject object = Jarray.getJSONObject(i);
                    ConsommationBP consommationBP = new ConsommationBP(object);
                    consommationList.add(consommationBP);
                }
                limit = Jobject.getDouble("restant");

                chambreBP(consommationList);
                downloadTot = 0;
                uploadTot = 0;
                for(int i = 0; i<consommationList.size(); i++){
                    double uploadTotTemp, downloadTotTemp;
                    uploadTotTemp = consommationList.get(i).getUpload();
                    downloadTotTemp = consommationList.get(i).getDownload();
                    uploadTot = uploadTot + uploadTotTemp;
                    downloadTot = downloadTot + downloadTotTemp;
                }
                uploadTot = uploadTot / 1024;
                downloadTot = downloadTot / 1024;
                limit = limit /1024;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (isAdded()) {
                /*try {

                    if (!query.getString("results").equals("null")) {
                        JSONObject results = (JSONObject) query.get("results");
                        JSONArray arrayTable = results.getJSONArray("table");
                        JSONObject tableauElem = arrayTable.getJSONObject(DETAILED_BANDWITH_INDEX).getJSONObject("tbody");
                        JSONObject quota = arrayTable.getJSONObject(QUOTA_INDEX).getJSONObject("tbody");
                        JSONArray arrayElem = tableauElem.getJSONArray("tr");
                        HashMap<String, Double> map = getBandwithUserFromPort(arrayElem);
                        int size = map.size();
                        values = new double[size];
                        rooms = new String[size];
                        Iterator<String> iter = map.keySet().iterator();
                        int i = 0;

                        while (iter.hasNext()) {
                            String entry = iter.next();
                            if (!entry.equals("total")) {
                                double value = map.get(entry);
                                values[i] = Math.round((value / 1024) * 100) / 100.0;
                                String[] stringArray = entry.split("-");
                                if (stringArray.length > 1) {
                                    rooms[i] = "■ " + stringArray[1].toString() + " " + values[i] + " Go";
                                } else {
                                    int j = i + 1;
                                    rooms[i] =  "■ Chambre" + j + " " + values[i] + " Go";
                                }
                                i++;
                            }
                        }
                        JSONArray quotaJson = quota.getJSONArray("tr");
                        JSONObject objectQuota = (JSONObject) quotaJson.get(1);
                        JSONArray arrayQuota = objectQuota.getJSONArray("td");
                        double quotaValue = ((JSONObject) arrayQuota.get(1)).getDouble(CONTENT);
                        quotaValue = Math.round(quotaValue / 1024 * 100) / 100.0;
                        double total = map.get("total");
                        total = Math.round(total / 1024 * 100) / 100.0;
                        limit = Math.round((quotaValue - total) * 100) / 100.0;
                        values[size - 1] = limit;
                        rooms[size - 1] = "■ Restant " + limit + " Go";
                        setProgressBar(total, quotaValue);
                        updateProgressBarColorItems(quotaValue);
                        JSONObject objectElem = (JSONObject) arrayElem.get(arrayElem.length()-2);
                        Log.d("objectElem", objectElem.toString());
                        JSONArray arrayElemtd = objectElem.getJSONArray("td");
                        Log.d("arrayElemtd", arrayElemtd.toString());
                        upload = ((JSONObject) arrayElemtd.get(1)).getDouble(CONTENT);
                        download = ((JSONObject) arrayElemtd.get(2)).getDouble(CONTENT);
                        Log.d("Bandwidth", "upload and download: " + upload + " " + download);
                        updatePieChart();
                    } else {
                        setError(editTextApp, getString(R.string.error_invalid_app));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            loadProgressBar.setVisibility(View.GONE);
            updatePieChart();
            setProgressBar(downloadTot+uploadTot, limit);

            super.onPostExecute(s);

        }



        public void updatePieChart(){
            Log.d("reste", ""+ limit);
            List<SliceValue> values = new ArrayList<SliceValue>();
            double rest;
            rest = limit-uploadTot-downloadTot;
            //TODO put labels
            values.add(new SliceValue((float) uploadTot).setLabel("Upload : " + String.format("%.2f",uploadTot) + " Go").setColor(Color.BLUE));
            values.add(new SliceValue((float) downloadTot).setLabel("Download : " + String.format("%.2f",downloadTot) + " Go").setColor(Color.RED));
            values.add(new SliceValue((float) rest).setLabel("Restant : " + String.format("%.2f",rest) + " Go").setColor(Color.GREEN));

            data = new PieChartData(values);

            chart.setVisibility(View.VISIBLE);
            data.setHasLabels(true);
            chart.setPieChartData(data);
        }

        private String containtPort(String port, HashMap<String, Double> map) {
            Iterator<String> iter = map.keySet().iterator();
            while (iter.hasNext()) {
                String entry = iter.next();
                if (entry.contains(port)) {
                    return entry;
                }
            }
            return null;
        }

        private HashMap<String, Double> getBandwithUserFromPort(JSONArray array) {
            HashMap<String, Double> map = new HashMap<>();
            try {
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    JSONArray elem = obj.getJSONArray("td");
                    final boolean IS_NOT_LAST_ELEMENT = i < array.length() - 2;
                    final boolean IS_LAST_ELEMENT = i == array.length() - 1;

                    if (IS_NOT_LAST_ELEMENT) {
                        String portElem = elem.getString(PORT);
                        if (containtPort(portElem, map) != null)
                            portElem = containtPort(portElem, map);

                        JSONObject upload = elem.getJSONObject(UPLOAD);
                        JSONObject downLoad = elem.getJSONObject(DOWNLOAD);
                        double downUpLoad = upload.getDouble(CONTENT) + downLoad.getDouble(CONTENT);
                        if (map.containsKey(portElem)) {
                            double downUpLoadValue = map.get(portElem);
                            downUpLoad += downUpLoadValue;
                        }
                        map.put(portElem, downUpLoad);
                    } else if (IS_LAST_ELEMENT) {
                        JSONObject totalObject = (JSONObject) elem.get(1);
                        double total = totalObject.getDouble(CONTENT);
                        map.put("total", total);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return map;
        }

    }

}
