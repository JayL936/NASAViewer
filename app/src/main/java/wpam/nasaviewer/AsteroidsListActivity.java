package wpam.nasaviewer;


import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AsteroidsListActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader;
    String dayStart;
    String monthStart;
    String yearStart;
    String dayEnd;
    String monthEnd;
    String yearEnd;

    ArrayList<String> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asteroids_list);

        getSupportActionBar().setTitle("Asteroids");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        dayStart = extras.getString("DAYSTART");
        monthStart = extras.getString("MONTHSTART");
        yearStart = extras.getString("YEARSTART");

        dayEnd = extras.getString("DAYEND");
        monthEnd = extras.getString("MONTHEND");
        yearEnd = extras.getString("YEAREND");

        getJSON json = new getJSON();
        json.execute("https://api.nasa.gov/neo/rest/v1/feed?start_date=" + yearStart + "-" + monthStart + "-" + dayStart +
                "&end_date=" + yearEnd + "-" + monthEnd + "-" + dayEnd + "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();

                Intent intent1 = new Intent(getApplicationContext(), AsteroidShowActivity.class);
                Bundle extras = new Bundle();
                extras.putString("DATA", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                intent1.putExtras(extras);
                startActivity(intent1);
                return false;
            }
        });
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
            String begin = yearStart + "-" + monthStart + "-" + dayStart;
            String end = yearEnd + "-" + monthEnd + "-" + dayEnd;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendarActual = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();

            try {
                calendarActual.setTime(simpleDateFormat.parse(begin));
                calendarEnd.setTime(simpleDateFormat.parse(end));
            } catch (Exception e) {
                e.printStackTrace();
            }

            calendarEnd.add(Calendar.DATE, 1);

            try {
                JSONObject object = new JSONObject(message);
                JSONObject asteroids = object.getJSONObject("near_earth_objects");

                while (calendarActual.before(calendarEnd)) {
                    data = new ArrayList<String>();
                    String year = String.valueOf(calendarActual.get(Calendar.YEAR));
                    String month = String.valueOf(calendarActual.get(Calendar.MONTH) + 1);
                    if (Integer.parseInt(month) < 10)
                        month = "0" + month;
                    String day = String.valueOf(calendarActual.get(Calendar.DAY_OF_MONTH));
                    if (Integer.parseInt(day) < 10)
                        day = "0" + day;
                    String date = year + "-" + month + "-" + day;
                    JSONArray asteroidsList = asteroids.getJSONArray(date);
                    for (int i = 0; i < asteroidsList.length(); i++) {
                        JSONObject asteroid = asteroidsList.getJSONObject(i);
                        String name = "ID: " + asteroid.getString("neo_reference_id") + " Name: " + asteroid.getString("name");

                        data.add(name);
                    }
                    listDataHeader.add(date);
                    listDataChild.put(date, data);

                    calendarActual.add(Calendar.DATE, 1);
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error, please try again", Toast.LENGTH_SHORT);

                e.printStackTrace();
            }

            expListView = (ExpandableListView) findViewById(R.id.lvExp);
            listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar3);
            progressBar.setVisibility(View.GONE);
        }
    }
}
