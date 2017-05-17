package wpam.nasaviewer;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MarsPhotosActivity extends AppCompatActivity {

    private boolean earth;
    private String day;
    private String month;
    private String year;
    private String sol;
    private String rover;
    private String camera;

    List<String> urls = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mars_photos);

        getSupportActionBar().setTitle("Mars");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        getExtrasFromBundle(extras);

        getJSON json = new getJSON();

        if (earth) {
            if (camera.equals("All")) {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "earth_date=" + year + "-" + month + "-" + day +
                        "&page=1&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            }
            else {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "earth_date=" + year + "-" + month + "-" + day + "&camera=" + camera +
                        "&page=1&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            }
        }
        else {
            if (camera.equals("All")) {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "sol=" + sol + "&page=1&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            } else {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "sol=" + sol + "&camera=" + camera +
                        "&page=1&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            }
        }

    }

    private void getExtrasFromBundle(Bundle extras) {
        earth = extras.getBoolean("EARTH");

        if (earth) {
            day = extras.getString("DAY");
            month = extras.getString("MONTH");
            year = extras.getString("YEAR");
        } else {
            sol = extras.getString("SOL");
        }

        rover = extras.getString("ROVER");
        camera = extras.getString("CAMERA");
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
            try {
                JSONObject object = new JSONObject(message);
                JSONArray photos = object.getJSONArray("photos");
                for (int i = 0; i<photos.length(); i++) {
                    JSONObject photo = photos.getJSONObject(i);
                    String imgSrc = photo.getString("img_src");
                    urls.add(imgSrc);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
