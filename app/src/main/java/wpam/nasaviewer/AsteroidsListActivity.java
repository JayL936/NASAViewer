package wpam.nasaviewer;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

        // Create a progress bar to display while the list loads
        // ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2); //new ProgressBar(this);
        // progressBar.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT,
        //         ListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
        // progressBar.setIndeterminate(true);
        //getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        // ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        // root.addView(progressBar);

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
                                                    return false;
                                                }
                                            });
    }

        // listening to single list item on click
      /*  lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                String num = ((TextView) view).getText().toString();

                // Launching new Activity on selecting single List Item
                Intent i = new Intent(getApplicationContext(), AsteroidShowActivity.class);
                // sending data to new activity
                i.putExtra("number", num);
                startActivity(i);
            }
        });*/

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
                        String name = asteroid.getString("name");

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
