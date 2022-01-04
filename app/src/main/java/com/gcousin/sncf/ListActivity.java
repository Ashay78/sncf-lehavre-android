package com.gcousin.sncf;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ListActivity extends AppCompatActivity {

    private String list = "";
    private TextView textList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.textList = (TextView) findViewById(R.id.list);


        Bundle b = getIntent().getExtras();
        if(b != null) {
            int type = b.getInt("type");

            if (type == 0) {
                getListstart();
            } else {
                getListEnd();
            }
        }
    }

    private void getListstart() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:87413013/departures",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonStart = jsonObject.getJSONArray("departures");
                        for (int i = 0; i < jsonStart.length(); i++) {
                            JSONObject json = jsonStart.getJSONObject(i);
                            JSONObject displayInformation = json.getJSONObject("display_informations");

                            String direction = displayInformation.getString("direction");
                            String network = displayInformation.getString("network");
                            String trip = displayInformation.getString("trip_short_name");

                            JSONObject sdt = json.getJSONObject("stop_date_time");
                            String time = sdt.getString("departure_date_time");
                            this.list += "(" + time.substring(9, 11) + "h" + time.substring(11, 13) + ") | " + network + ":" + trip + " | " + direction + "\n";
                        }
                        this.textList.setText(this.list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "4c4b816a-9aa2-4a30-9a7d-e8ac574b9ba6");

                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }

    private void getListEnd() {
        RequestQueue request = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://api.sncf.com/v1/coverage/sncf/stop_areas/stop_area:SNCF:87413013/arrivals",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("arrivals");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            JSONObject displayInformation = json.getJSONObject("display_informations");
                            String network = displayInformation.getString("network");
                            String trip = displayInformation.getString("trip_short_name");
                            JSONObject route = json.getJSONObject("route");
                            JSONObject line = route.getJSONObject("line");
                            String lineId = line.getString("id");
                            AtomicBoolean find = new AtomicBoolean(false);

                            StringRequest subRequest = new StringRequest(Request.Method.GET, "https://api.sncf.com/v1/coverage/sncf/lines/" + lineId + "/departures",
                                    response1 -> {
                                        try {
                                            JSONObject subJsonObject = new JSONObject(response1);
                                            JSONArray subJsonArray = subJsonObject.getJSONArray("departures");
                                            for (int j = 0; j < jsonArray.length(); j++) {
                                                JSONObject jsonLine = subJsonArray.getJSONObject(j);
                                                JSONObject di2 = jsonLine.getJSONObject("display_informations");
                                                if (!di2.getString("trip_short_name").equals(trip)) {
                                                    continue;
                                                }

                                                find.set(true);

                                                JSONObject sp = jsonLine.getJSONObject("stop_point");
                                                String label = sp.getString("label");

                                                JSONObject sdt = json.getJSONObject("stop_date_time");
                                                String time = sdt.getString("arrival_date_time");

                                                this.list += " (" + time.substring(9, 11) + "h" + time.substring(11, 13) + ") | " + network + ":" + trip + " | " + label + "\n";

                                                String[] things = this.list.split("\n");
                                                Arrays.sort(things);
                                                this.list = String.join("\n", things) + "\n";

                                                break;
                                            }

                                            if (!find.get()) {
                                                JSONObject sdt = json.getJSONObject("stop_date_time");
                                                String time = sdt.getString("departure_date_time");
                                                String direction = displayInformation.getString("direction");
                                                this.list += " (" + time.substring(9, 11) + "h" + time.substring(11, 13) + ") | " + network + ":" + trip + " | " + direction + " !information\n";
                                            }
                                            this.textList.setText(this.list);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }, Throwable::printStackTrace) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String>  params = new HashMap<>();
                                    params.put("Authorization", "4dcb15e4-8147-451a-9a83-415714652227");

                                    return params;
                                }
                            };
                            request.add(subRequest);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "4dcb15e4-8147-451a-9a83-415714652227");

                return params;
            }
        };
        request.add(stringRequest);
    }

    public void quitActivity(View v) {
        this.finish();
    }
}
