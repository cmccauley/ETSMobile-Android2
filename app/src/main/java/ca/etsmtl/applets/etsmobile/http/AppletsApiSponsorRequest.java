package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
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

import ca.etsmtl.applets.etsmobile.model.Sponsor;
import ca.etsmtl.applets.etsmobile.model.SponsorList;
import ca.etsmtl.applets.etsmobile2.R;

public class AppletsApiSponsorRequest extends SpringAndroidSpiceRequest<SponsorList> {

    private Context context;

    public AppletsApiSponsorRequest(Context context) {
        super(SponsorList.class);
        this.context = context;
    }

    @Override
    public SponsorList loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.applets_api_sponsors);

        SponsorList sponsorList = null;

        try {

            // Instantiate the custom HttpClient to call Https request
            OkHttpClient client = new OkHttpClient();

            String apiCredentials = context.getString(R.string.credentials_api);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("authorization", "Basic " + new String(new Base64().encode(apiCredentials.getBytes())))
                    .addHeader("cache-control", "no-cache")
                    .build();

            Response response = client.newCall(request).execute();

            if(response.code() == 200){
                JSONObject data = new JSONObject(response.body().string());
                ObjectMapper mapper = new ObjectMapper();
                sponsorList = new SponsorList();
                Iterator keys = data.keys();

                while (keys.hasNext()) {

                    int imageResource = 0;
                    String currentDynamicKey = (String) keys.next();

                    //imageResource = assignResource(currentDynamicKey);

                    JSONArray arraySponsors = data.getJSONArray(currentDynamicKey);

                    for (int i = 0; i < arraySponsors.length(); i++) {
                        Sponsor sponsor = mapper.readValue(arraySponsors.getJSONObject(i).toString(), Sponsor.class);
                        //sponsor.setImageResource(imageResource);
                        sponsorList.add(sponsor);
                    }
                }
            } else {
                Log.d("API_ERROR", "AppletsApiSponsorRequest call is NOT 200 OK");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sponsorList;
    }
}
