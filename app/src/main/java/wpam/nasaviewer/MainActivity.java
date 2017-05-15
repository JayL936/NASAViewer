package wpam.nasaviewer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    //TODO menu bar in every activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ApodBtn_OnClick(View view) {
        Intent intent = new Intent(this, ApodActivity.class);
        startActivity(intent);
    }

    public void AsteroidsBtn_OnClick(View view) {
        Intent intent = new Intent(this, AsteroidsActivity.class);
        startActivity(intent);
    }

    public void EarthBtn_OnClick(View view) {
        Intent intent = new Intent(this, EarthActivity.class);
        startActivity(intent);
    }

    public void MarsBtn_OnClick(View view) {
        Intent intent = new Intent(this, MarsActivity.class);
        startActivity(intent);
    }
}
