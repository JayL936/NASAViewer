package wpam.nasaviewer;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class AsteroidsListActivity extends ListActivity { //implements LoaderManager.LoaderCallbacks<Cursor>{

    private ProgressDialog progressDialog;
    String dayStart;
    String monthStart;
    String yearStart;
    String dayEnd;
    String monthEnd;
    String yearEnd;

    private ListView lv;

    ArrayList<String> data = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_asteroids_list);

        // Create a progress bar to display while the list loads
      /*  ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT,
                ListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);*/

        //lv = (ListView) findViewById(R.id.text_list_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        dayStart = extras.getString("DAYSTART");
        monthStart = extras.getString("MONTHSTART");
        yearStart = extras.getString("YEARSTART");

        dayEnd = extras.getString("DAYEND");
        monthEnd = extras.getString("MONTHEND");
        yearEnd = extras.getString("YEAREND");

       // getJSON json = new getJSON();
        //json.execute("https://api.nasa.gov/neo/rest/v1/feed?start_date=" + yearStart + "-" + monthStart +"-" + dayStart +
       //         "&end_date=" + yearEnd + "-" + monthEnd + "-" + dayEnd + "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");

        data.add("one");
        data.add("two");
        // storing string resources into Array
        // here you store the array of string you got from the database
        String[] numbers = {"one", "two"};
        // Binding Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, numbers));
        // refer the ArrayAdapter Document in developer.android.com
        ListView lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });


    }

    private class getJSON extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

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
            try {
                JSONObject object = new JSONObject(message);
                JSONObject asteroids = object.getJSONObject("near_earth_objects");
                JSONArray asteroidsList = asteroids.getJSONArray(yearEnd + "-" + monthEnd + "-" + dayEnd);
                for (int i=0; i<asteroidsList.length(); i++) {
                    JSONObject asteroid = asteroidsList.getJSONObject(i);
                    String name = asteroid.getString("name");
                    data.add(name);
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error, please try again", Toast.LENGTH_SHORT);

                e.printStackTrace();
            }

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, data));
        }
    }

   /* // This is the Adapter being used to display the list's data
    SimpleCursorAdapter mAdapter;

    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME};

    // This is the select criteria
    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.WRAP_CONTENT,
                ListView.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

   // @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }*/
}
