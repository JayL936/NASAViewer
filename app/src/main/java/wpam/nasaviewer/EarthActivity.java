package wpam.nasaviewer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


public class EarthActivity extends AppCompatActivity implements LocationListener {

    EditText editLat;
    EditText editLng;
    EditText editDim;
    Switch auto;

    Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Earth");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earth);

        auto = (Switch) findViewById(R.id.switchAuto);
        auto.setChecked(true);



        editDim = (EditText) findViewById(R.id.editTextDim);
        editDim.setText("0.025");
        editDim.setVisibility(View.GONE);

        TextView dimText = (TextView) findViewById(R.id.textViewDim);
        dimText.setVisibility(View.GONE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = lm.getBestProvider(criteria, true);

        if (bestProvider != null && !bestProvider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            lm.requestLocationUpdates(bestProvider, 15000, 1, this);
            final Location location = lm.getLastKnownLocation(bestProvider);

            auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_LONG).show();
                        onLocationChanged(location);
                    }
                }
            });

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Activate GPS to find coordinates or set them manually", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (auto.isChecked()) {
            editLat = (EditText) findViewById(R.id.editTextLat);
            editLng = (EditText) findViewById(R.id.editTextLng);

            editLng.setText(String.valueOf(location.getLongitude()));
            editLat.setText(String.valueOf(location.getLatitude()));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void BtnShowEarth_OnClick(View view) {
        Intent intent = new Intent(this, EarthShowActivity.class);
        Bundle extras = new Bundle();

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePickerEarth);
        Switch switchEarth = (Switch) findViewById(R.id.switchEarth);

        extras.putString("LATITUDE", editLat.getText().toString());
        extras.putString("LONGITUDE", editLng.getText().toString());
        extras.putString("DIM", editDim.getText().toString());
        extras.putString("DAY", String.valueOf(datePicker.getDayOfMonth()));
        extras.putString("MONTH", String.valueOf(datePicker.getMonth()+1));
        extras.putString("YEAR", String.valueOf(datePicker.getYear()));
        extras.putString("CLOUD", String.valueOf(switchEarth.isChecked()));

        intent.putExtras(extras);
        startActivity(intent);
    }

    public void BtnShowMap_OnClick(View view) {

        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                editLat = (EditText) findViewById(R.id.editTextLat);
                editLng = (EditText) findViewById(R.id.editTextLng);

                String lat = String.valueOf(place.getLatLng().latitude);
                String lng = String.valueOf(place.getLatLng().longitude);
                editLng.setText(lng);
                editLat.setText(lat);
            }
        }
    }
}
