package wpam.nasaviewer;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;

public class AsteroidShowActivity extends AppCompatActivity {

    private String url;
    private String name;
    private String id;
    private String estKmMin;
    private String estKmMax;
    private boolean hazardous;
    private String approachDate;
    private String velocity;
    private String missDistanceKm;
    private String orbitingBody;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader;
    TextView textView;
    ArrayList<String> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asteroid_show);

        getSupportActionBar().setTitle("Asteroid");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String data = extras.getString("DATA");

        int lastIndex = data.indexOf("N");
        id = data.substring(4, lastIndex-1);

        getJSON json = new getJSON();
        json.execute("https://api.nasa.gov/neo/rest/v1/neo/" + id + "?api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");

        expListView = (ExpandableListView) findViewById(R.id.lvExpAsteroid);

        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id) {

                long packedPosition = expListView.getExpandableListPosition(position);

                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
             //   int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);


        /*  if group item clicked */
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    //  ...
                   /* Toast.makeText(
                            getApplicationContext(),
                            listDataHeader.get(groupPosition),
                            Toast.LENGTH_SHORT)
                            .show();*/

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = java.util.Calendar.getInstance();

                    try {
                        calendar.setTime(simpleDateFormat.parse(listDataHeader.get(groupPosition).substring(15)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.Events.TITLE, name);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());

                    startActivity(intent);
                }

                return false;
            }
        });

    }

    public void btnAsteroidJPL_OnClick (View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private class getJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] params) {
            BufferedReader rd = null;
            StringBuilder sb = null;
            String line = null;
            URL url = null;
            try {
                url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    sb.append(line + '\n');
                }
                return sb.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            listDataChild = new HashMap<String, List<String>>();
            listDataHeader = new ArrayList<String>();
            try {
                JSONObject object = new JSONObject(message);
                id = object.getString("neo_reference_id");
                name = object.getString("name");
                url = object.getString("nasa_jpl_url");
                JSONObject estDiameter = object.getJSONObject("estimated_diameter");
                JSONObject estDiameterKm = estDiameter.getJSONObject("kilometers");
                estKmMin = estDiameterKm.getString("estimated_diameter_min");
                estKmMax = estDiameterKm.getString("estimated_diameter_max");
                hazardous = object.getBoolean("is_potentially_hazardous_asteroid");
                JSONArray appDates = object.getJSONArray("close_approach_data");

                for (int i = 0; i < appDates.length(); i++) {
                    data = new ArrayList<String>();
                    JSONObject jDate = appDates.getJSONObject(i);
                    approachDate = "Approach date: " + jDate.getString("close_approach_date");
                    JSONObject relativeVelocity = jDate.getJSONObject("relative_velocity");
                    velocity = "Relative velocity (kmph): " + relativeVelocity.getString("kilometers_per_hour");
                    JSONObject missDistance = jDate.getJSONObject("miss_distance");
                    missDistanceKm = "Miss distance (km): " + missDistance.getString("kilometers");
                    orbitingBody = "Orbiting body: " + jDate.getString("orbiting_body");

                    listDataHeader.add(approachDate);
                    data.add(velocity);
                    data.add(missDistanceKm);
                    data.add(orbitingBody);
                    listDataChild.put(approachDate, data);
                }

                textView = (TextView) findViewById(R.id.textViewAsteroidShow);
                textView.setText("ID: " + id + "\n" +
                        "Name: " + name + "\n" +
                        "Minimum estimated diameter (km): " + estKmMin + "\n" +
                        "Maximum estimated diameter (km): " + estKmMax + "\n" +
                        "Hazardous: " + hazardous);


            } catch (Exception e) {
                textView.setText("Error occured. Please try again later.");
                e.printStackTrace();
            }

            expListView = (ExpandableListView) findViewById(R.id.lvExpAsteroid);
            listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarAsteroidShow);
            progressBar.setVisibility(View.GONE);
        }
    }
}
