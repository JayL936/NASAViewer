package wpam.nasaviewer;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class AsteroidsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asteroids);

        getSupportActionBar().setTitle("Asteroids");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text = (TextView) findViewById(R.id.textViewInfo);
        text.setTextColor(Color.parseColor("#FD4545"));


    }

    public void showAsteroidsBtn_OnClick(View view) {

        Intent intent = new Intent(this, AsteroidsListActivity.class);
        Bundle extras = new Bundle();

        DatePicker datePickerStart = (DatePicker) findViewById(R.id.datePickerStart);
        DatePicker datePickerEnd = (DatePicker) findViewById(R.id.datePickerEnd);

        datePickerEnd.setMaxDate(new Date().getTime());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String dayStart = String.valueOf(datePickerStart.getDayOfMonth());
        String monthStart = String.valueOf(datePickerStart.getMonth()+1);
        String yearStart = String.valueOf(datePickerStart.getYear());
        String dayEnd = String.valueOf(datePickerEnd.getDayOfMonth());
        String monthEnd = String.valueOf(datePickerEnd.getMonth()+1);
        String yearEnd = String.valueOf(datePickerEnd.getYear());

        Date dateStart = null;
        Date dateEnd = null;

        try {
            dateStart = simpleDateFormat.parse(dayStart + "/" + monthStart + "/" + yearStart);
            dateEnd = simpleDateFormat.parse(dayEnd + "/" + monthEnd + "/" + yearEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long diff = getDifference(dateStart, dateEnd);

        if (diff < 0) {
            Toast.makeText(getApplicationContext(), "Start day must be before end date.", Toast.LENGTH_LONG).show();
        } else if (diff > 7) {
            Toast.makeText(getApplicationContext(), "Difference must be no more than 7 days.", Toast.LENGTH_LONG).show();
        } else {
            extras.putString("DAYSTART", dayStart);
            extras.putString("MONTHSTART", monthStart);
            extras.putString("YEARSTART", yearStart);

            extras.putString("DAYEND", dayEnd);
            extras.putString("MONTHEND", monthEnd);
            extras.putString("YEAREND", yearEnd);

            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    private long getDifference(Date dateStart, Date dateEnd) {
        //milliseconds
        long different = dateEnd.getTime() - dateStart.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        return elapsedDays;
    }
}
