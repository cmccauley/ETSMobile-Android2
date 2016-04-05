package ca.etsmtl.applets.etsmobile.http;

import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kobjects.base64.Base64;
import ca.etsmtl.applets.etsmobile.model.Event;
import ca.etsmtl.applets.etsmobile.model.EventList;
import ca.etsmtl.applets.etsmobile2.R;


public class AppletsApiCalendarRequest extends SpringAndroidSpiceRequest<EventList> {

    private Context context;
    private String startDate = "";
    private String endDate = "";

    public AppletsApiCalendarRequest(Context context, String startDate, String endDate) {
        super(EventList.class);
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public EventList loadDataFromNetwork() throws Exception {

        String url = context.getString(R.string.applets_api_calendar, "ets", startDate, endDate);
        EventList eventList = new EventList();

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
                String jsonData = response.body().string();
                JSONObject result = new JSONObject(jsonData);
                // If the returned value of "data" returned by the API becomes an Array,
                // change the type of JSONObject to JSONArray
                JSONObject data = result.getJSONObject("data");

                // The API might eventually return other JSONArray or JSONObject
                // You get them here with the key of the Array/Object
                JSONArray ets = data.getJSONArray("ets");
                for(int i=0; i<ets.length();i++){
                    JSONObject contents = ets.getJSONObject((i));
                    Event event = new Event(contents.getString("id"),
                            contents.getString("start_date"), contents.getString("end_date"),
                            contents.getString("summary"));
                    eventList.add(event);
                }
            } else {
                Log.d("API_ERROR", "AppletsApiCalendarRequest call is NOT 200 OK");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        return eventList;
    }
}
