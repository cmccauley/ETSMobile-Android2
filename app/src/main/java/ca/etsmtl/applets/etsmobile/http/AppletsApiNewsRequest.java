package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import java.util.Iterator;

import ca.etsmtl.applets.etsmobile.model.Nouvelle;
import ca.etsmtl.applets.etsmobile.model.Nouvelles;
import ca.etsmtl.applets.etsmobile2.R;

/**
 * Created by gnut3ll4 on 11/30/14.
 */
public class AppletsApiNewsRequest extends SpringAndroidSpiceRequest<Nouvelles> {

    private Context context;
    private String source;
    private String startDate;
    private String endDate;

    public AppletsApiNewsRequest(Context context, String source, String startDate, String endDate) {
        super(Nouvelles.class);
        this.context = context;
        this.source = source;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Nouvelles loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.applets_api_news, source, startDate, endDate);

        Nouvelles nouvelles = null;

        try {
            OkHttpClient client = new OkHttpClient();

            String apiCredentials = context.getString(R.string.credentials_api);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("authorization", "Basic " + new String(new Base64().encode(apiCredentials.getBytes())))
                    .addHeader("cache-control", "no-cache")
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() == 200) {
                JSONObject root = new JSONObject(response.body().string());
                JSONObject data = root.getJSONObject("data");
                ObjectMapper mapper = new ObjectMapper();
                nouvelles = new Nouvelles();

                Iterator keys = data.keys();
                while (keys.hasNext()) {

                    int imageResource = 0;
                    String currentDynamicKey = (String) keys.next();

                    imageResource = assignResource(currentDynamicKey);

                    JSONArray arrayNews = data.getJSONArray(currentDynamicKey);

                    for (int i = 0; i < arrayNews.length(); i++) {
                        Nouvelle nouvelle = mapper.readValue(arrayNews.getJSONObject(i).toString(), Nouvelle.class);
                        nouvelle.setImageResource(imageResource);
                        nouvelles.add(nouvelle);
                    }
                    Log.d("Nouvelles", TextUtils.join(",", nouvelles));
                }
            } else {
                    Log.d("API_ERROR", "AppletsApiNewsRequest call is NOT 200 OK");
            }
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return nouvelles;
    }


    private int assignResource(String key) {
        switch (key) {
            case "ets":
                return R.drawable.ic_ets;

            case "substance":
                return R.drawable.ic_substance;

            case "centresportif":
                return R.drawable.ic_centresportif;

            case "applets":
                return R.drawable.ic_applets;

            case "esports":
                return R.drawable.ic_esports;

            case "rafale":
                return R.drawable.ic_rafale;

            case "rockanddance":
                return R.drawable.ic_rockanddance;

            case "conjure":
                return R.drawable.ic_conjure;

            case "rockets":
                return R.drawable.ic_rockets;

            case "phoenix":
                return R.drawable.ic_phoenix;

            case "avioncargo":
                return R.drawable.ic_ace;

            case "clubcycliste":
                return R.drawable.ic_clubcycliste;

            case "football":
                return R.drawable.ic_football;

            case "ingenieuses":
                return R.drawable.ic_ingenieuses;

            case "debatpiranha":
                return R.drawable.ic_debatpiranha;

            case "radiopiranha":
                return R.drawable.ic_radiopiranha;

            case "walkingmachine":
                return R.drawable.ic_walkingmachine;

            case "atlhetsiques":
                return R.drawable.ic_athletsiques;

            case "aeets":
                return R.drawable.ic_aeets;

            case "rugby":
                return R.drawable.ic_rugby;

            case "bibliotheque":
                return R.drawable.ic_bibliotheque;

            case "capra":
                return R.drawable.ic_capra;

            case "ieee":
                return R.drawable.ic_ieee;

            case "pontpop":
                return R.drawable.ic_pontpop;

            case "omer":
                return R.drawable.ic_omer;

            case "baja":
                return R.drawable.ic_baja;

            case "canoedebeton":
                return R.drawable.ic_canoedebeton;

            case "chinook":
                return R.drawable.ic_chinook;

            case "sonia":
                return R.drawable.ic_sonia;

            case "lanets":
                return R.drawable.ic_lanets;

            case "formuleets":
                return R.drawable.ic_formuleets;

            case "eclipse":
                return R.drawable.ic_eclipse;

            case "turbulence":
                return R.drawable.ic_turbulence;

            case "preci":
                return R.drawable.ic_preci;

            case "reflets":
                return R.drawable.ic_reflets;

            case "crabeets":
                return R.drawable.ic_crabeets;

            case "decliq":
                return R.drawable.ic_decliq;

            case "quiets":
                return R.drawable.ic_quiets;

            case "dronolab":
                return R.drawable.ic_dronolab;

            case "liets":
                return R.drawable.ic_liets;

            case "radiosansgenie":
                return R.drawable.ic_radiosansgenie;

            case "coopets":
                return R.drawable.ic_coopets;

            case "integrale":
                return R.drawable.ic_integrale;

            case "geniale":
                return R.drawable.ic_geniale;
            default:
                return 0;

        }
    }
}
