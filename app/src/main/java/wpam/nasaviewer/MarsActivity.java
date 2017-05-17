package wpam.nasaviewer;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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

import static wpam.nasaviewer.R.id.date;
import static wpam.nasaviewer.R.id.textView;

public class MarsActivity extends AppCompatActivity {

    SeekBar seekBar;
    TextView textViewSolVal;
    TextView textViewMarsSol;
    TextView textViewMarsEarth;

    RadioButton radioButtonEarth;
    RadioButton radioButtonMars;

    DatePicker datePickerEarth;

    Spinner spinnerRovers;
    Spinner spinnerCameras;

    List<String> cCameras = new ArrayList<String>();
    List<String> oCameras = new ArrayList<String>();
    List<String> sCameras = new ArrayList<String>();

    private String landingDate;
    private String maxDate;
    private String maxSol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mars);

        populateSpinnerRovers();
        populateCamerasArrays();

        seekBar = (SeekBar) findViewById(R.id.seekBarSol);
        seekBar.setMax(100);
        seekBar.setVisibility(View.GONE);

        radioButtonEarth = (RadioButton) findViewById(R.id.radioButtonEarth);
        radioButtonEarth.setChecked(true);

        textViewMarsSol = (TextView) findViewById(R.id.textViewMarsMarsSol);
        textViewMarsSol.setVisibility(View.GONE);

        textViewSolVal = (TextView) findViewById(R.id.textViewSolVal);
        textViewSolVal.setVisibility(View.GONE);

        spinnerRovers = (Spinner) findViewById(R.id.spinnerRovers);
        spinnerCameras = (Spinner) findViewById(R.id.spinnerCameras);

        spinnerRovers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getJSON json;
                ArrayAdapter<String> dataAdapter;
                String selected = spinnerRovers.getSelectedItem().toString();
                switch (selected) {
                    case "Curiosity":
                        dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, cCameras);
                        spinnerCameras.setAdapter(dataAdapter);
                        json = new getJSON();
                        json.execute("https://api.nasa.gov/mars-photos/api/v1/manifests/curiosity?api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
                        break;
                    case "Opportunity":
                        dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, oCameras);
                        spinnerCameras.setAdapter(dataAdapter);
                        json = new getJSON();
                        json.execute("https://api.nasa.gov/mars-photos/api/v1/manifests/opportunity?api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
                        break;
                    case "Spirit":
                        dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, sCameras);
                        spinnerCameras.setAdapter(dataAdapter);
                        json = new getJSON();
                        json.execute("https://api.nasa.gov/mars-photos/api/v1/manifests/spirit?api_key=Wt9A065T8VZw0T7TKMPR2L2d3dyDmeRiJkv6ApDh");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                textViewSolVal.setText(String.valueOf(progress));
                // t1.setTextSize(progress);
                // Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();

            }
        });
    }

    private void populateCamerasArrays() {
        cCameras.add("All");
        cCameras.add("Front Hazard Avoidance Camera");
        cCameras.add("Rear Hazard Avoidance Camera");
        cCameras.add("Mast Camera");
        cCameras.add("Chemistry and Camera Complex");
        cCameras.add("Mars Hand Lens Imager");
        cCameras.add("Mars Descent Imager");
        cCameras.add("Navigation Camera");

        oCameras.add("All");
        oCameras.add("Front Hazard Avoidance Camera");
        oCameras.add("Rear Hazard Avoidance Camera");
        oCameras.add("Navigation Camera");
        oCameras.add("Panoramic Camera");
        oCameras.add("Miniature Thermal Emission Spectrometer (Mini-TES");

        sCameras.add("All");
        sCameras.add("Front Hazard Avoidance Camera");
        sCameras.add("Rear Hazard Avoidance Camera");
        sCameras.add("Navigation Camera");
        sCameras.add("Panoramic Camera");
        sCameras.add("Miniature Thermal Emission Spectrometer (Mini-TES");
    }

    private void populateSpinnerRovers() {
        spinnerRovers = (Spinner) findViewById(R.id.spinnerRovers);
        List<String> list = new ArrayList<String>();

        list.add("Curiosity");
        list.add("Opportunity");
        list.add("Spirit");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinnerRovers.setAdapter(dataAdapter);
    }

    public void onRadioButtonClicked(View view) {

        textViewMarsEarth = (TextView) findViewById(R.id.textViewMarsEarthDate);
        datePickerEarth = (DatePicker) findViewById(R.id.datePickerMars);

        textViewMarsSol = (TextView) findViewById(R.id.textViewMarsMarsSol);
        textViewSolVal = (TextView) findViewById(R.id.textViewSolVal);
        seekBar = (SeekBar) findViewById(R.id.seekBarSol);

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButtonEarth:
                if (checked) {
                    textViewMarsSol.setVisibility(View.GONE);
                    textViewSolVal.setVisibility(View.GONE);
                    seekBar.setVisibility(View.GONE);

                    textViewMarsEarth.setVisibility(View.VISIBLE);
                    datePickerEarth.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radioButtonSol:
                if (checked) {
                    textViewMarsEarth.setVisibility(View.GONE);
                    datePickerEarth.setVisibility(View.GONE);

                    textViewMarsSol.setVisibility(View.VISIBLE);
                    textViewSolVal.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                }
                // Ninjas rule
                break;
        }
    }

    public void btnShowMars_OnClick(View view) {
        Intent intent = new Intent(this, MarsPhotosActivity.class);
        Bundle extras = new Bundle();

        radioButtonEarth = (RadioButton) findViewById(R.id.radioButtonEarth);

        spinnerRovers = (Spinner) findViewById(R.id.spinnerRovers);
        String rover = String.valueOf(spinnerRovers.getSelectedItem());

        spinnerCameras = (Spinner) findViewById(R.id.spinnerCameras);
        String camera = String.valueOf(spinnerCameras.getSelectedItem());

        if(radioButtonEarth.isChecked()) {
            datePickerEarth = (DatePicker) findViewById(R.id.datePickerMars);
            String day = String.valueOf(datePickerEarth.getDayOfMonth());
            String month = String.valueOf(datePickerEarth.getMonth());
            String year = String.valueOf(datePickerEarth.getYear());

            extras.putBoolean("EARTH", true);
            extras.putString("DAY", day);
            extras.putString("MONTH", month);
            extras.putString("YEAR", year);
        }
        else {
            seekBar = (SeekBar) findViewById(R.id.seekBarSol);
            String sol = String.valueOf(seekBar.getProgress());
            extras.putBoolean("EARTH", false);
            extras.putString("SOL", sol);
        }

        extras.putString("ROVER", rover);
        extras.putString("CAMERA", camera);

        intent.putExtras(extras);
        startActivity(intent);
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                JSONObject object = new JSONObject(message);
                JSONObject manifest = object.getJSONObject("photo_manifest");
                landingDate = manifest.getString("landing_date");
                maxDate = manifest.getString("max_date");
                maxSol = manifest.getString("max_sol");

                Date dateStart = simpleDateFormat.parse(landingDate);
                Date dateEnd = simpleDateFormat.parse(maxDate);

                seekBar = (SeekBar) findViewById(R.id.seekBarSol);
                seekBar.setMax(Integer.valueOf(maxSol));

                datePickerEarth = (DatePicker) findViewById(R.id.datePickerMars);
                datePickerEarth.setMaxDate(dateEnd.getTime());
                datePickerEarth.setMinDate(dateStart.getTime());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
