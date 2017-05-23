package wpam.nasaviewer;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    RecyclerView recyclerView;
    ArrayList<String> urls = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mars_photos);

        recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        getExtrasFromBundle(extras);

        getJSON json = new getJSON();

        if (earth) {
            if (camera.equals("All")) {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "earth_date=" + year + "-" + month + "-" + day +
                        "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            }
            else {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "earth_date=" + year + "-" + month + "-" + day + "&camera=" + camera +
                        "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            }
        }
        else {
            if (camera.equals("All")) {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "sol=" + sol + "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
            } else {
                json.execute("https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?" +
                        "sol=" + sol + "&camera=" + camera +
                        "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
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
        camera = setCamera(extras.getString("CAMERA"));
    }

    private String setCamera(String camera) {
        switch (camera) {
            case "Front Hazard Avoidance Camera":
                return "FHAZ";
            case "Rear Hazarz Avoidance Camera":
                return  "RHAZ";
            case "Mast Camera":
                return "MAST";
            case "Chemistry and Camera Complex":
                return "CHEMCAM";
            case "Mars Hand Lens Imager":
                return "MAHLI";
            case "Mars Descent Imager":
                return "MARDI";
            case "Navigation Camera":
                return "NAVCAM";
            case "Panoramic Camera":
                return "PANCAM";
            case "Miniature Thermal Emission Spectrometer (Mini-TES)":
                return "MINITES";
            default:
                return "All";
        }
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

            recyclerView = (RecyclerView)findViewById(R.id.imagegallery);
            MyAdapter adapter = new MyAdapter(getApplicationContext(), urls);
            recyclerView.setAdapter(adapter);
        }
    }
}
