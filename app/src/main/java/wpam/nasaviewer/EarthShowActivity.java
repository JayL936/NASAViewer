package wpam.nasaviewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EarthShowActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    Context context;

    String date;
    String day;
    String month;
    String year;
    String dim;
    String lat;
    String lng;
    String urlEarth;
    String cloudChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Earth");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth_show);

        imageView = (ImageView) findViewById(R.id.imageViewEarth);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        textView = (TextView) findViewById(R.id.textViewEarth);

        Glide.with(this)
                .load(R.drawable.loader)
                .asGif()
                .into(imageView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        lat = extras.getString("LATITUDE");
        lng = extras.getString("LONGITUDE");
        dim = extras.getString("DIM");
        day = extras.getString("DAY");
        month = extras.getString("MONTH");
        year = extras.getString("YEAR");
        cloudChecked = extras.getString("CLOUD");

        getJSON json = new getJSON();
        String query = "https://api.nasa.gov/planetary/earth/imagery?lon=" + lng + "&lat=" + lat +
                "&date=" + year + "-" + month + "-" + day + "&cloud_score=" + cloudChecked + "&api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh";
        json.execute(query);
    }

    public void BtnSaveEarth_OnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                BitmapDrawable draw = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = draw.getBitmap();

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Earth");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outFile));
                sendBroadcast(intent);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
                date = object.getString("date");
                urlEarth = object.getString("url");
                String text;
                if (Boolean.parseBoolean(cloudChecked)) {
                    String cloudScore = object.getString("cloud_score");
                    text = "Date: " + date + "\n" +
                            "Latitude: " + lat + "\n" +
                            "Longitude: " + lng + "\n" +
                            "Cloud score: " + cloudScore;
                } else {
                    text = "Date: " + date + "\n" +
                            "Latitude: " + lat + "\n" +
                            "Longitude: " + lng + "\n";
                }

                textView.setText(text);
                context = getApplicationContext();
                Glide.with(context)
                        .load(urlEarth)
                        .asBitmap()
                        .placeholder(R.drawable.loader)
                        .into(imageView);


            } catch (Exception e) {
                textView.setText("No image found. Try again with different parameters.");

                context = getApplicationContext();
                Glide.with(context)
                        .load(R.drawable.nasa_icon)
                        .into(imageView);
                e.printStackTrace();
            }
        }
    }
}
