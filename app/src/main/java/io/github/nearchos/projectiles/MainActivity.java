package io.github.nearchos.projectiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ProjectileMotionView projectileMotionView;

    private ViewPager viewPagerPlanets;

    private SeekBar seekBarInitialVelocity;
    private SeekBar seekBarAngle;
    private SeekBar seekBarTime;

    private TextView initialVelocityValue;
    private TextView angleValue;
    private TextView timeValue;

    private SeekBarHandler seekBarHandler = new SeekBarHandler();

    private Planet [] planets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init planets
        final String [] planetNames = getResources().getStringArray(R.array.planetNames);
        final int [] planetGravitiesAsIntegersTimes100 = getResources().getIntArray(R.array.planetGravities);
        final String [] planetDrawableNames = getResources().getStringArray(R.array.planetDrawablesNames);
        planets = new Planet[planetNames.length];
        for(int i = 0; i < planetNames.length; i++) {
            planets[i] = new Planet(planetNames[i], planetGravitiesAsIntegersTimes100[i] / 100d, planetDrawableNames[i]);
        }

        viewPagerPlanets = findViewById(R.id.viewPagerPlanets);
        viewPagerPlanets.setAdapter(new PlanetPagerAdapter(getSupportFragmentManager(), planets));
        viewPagerPlanets.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateSeekers();
            }
        });

        projectileMotionView = findViewById(R.id.ballisticsView);

        seekBarInitialVelocity = findViewById(R.id.seekBarInitialVelocity);
        seekBarAngle = findViewById(R.id.seekBarAngle);
        seekBarTime = findViewById(R.id.seekBarTime);

        seekBarInitialVelocity.setOnSeekBarChangeListener(seekBarHandler);
        seekBarAngle.setOnSeekBarChangeListener(seekBarHandler);
        seekBarTime.setOnSeekBarChangeListener(seekBarHandler);

        initialVelocityValue = findViewById(R.id.initialVelocityValue);
        angleValue = findViewById(R.id.angleValue);
        timeValue = findViewById(R.id.timeValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeekers();
    }

    public void swipeLeft(final View view) {
        viewPagerPlanets.arrowScroll(View.FOCUS_LEFT);
    }

    public void swipeRight(final View view) {
        viewPagerPlanets.arrowScroll(View.FOCUS_RIGHT);
    }

    private class SeekBarHandler implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateSeekers();
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) { /* nothing */ }

        @Override public void onStopTrackingTouch(SeekBar seekBar) { /* nothing */ }
    }

    private void updateSeekers() {
        final int initialVelocityInMetersPerSecond = seekBarInitialVelocity.getProgress();
        final int angleInDegrees = seekBarAngle.getProgress();
        final double angleInRadians = Util.degreesToRadians(angleInDegrees);
        final Planet planet = planets[viewPagerPlanets.getCurrentItem()];
        final int timePercent = seekBarTime.getProgress();
        final double timeOfFlight = Util.computeTimeOfFlight(initialVelocityInMetersPerSecond, angleInRadians, planet.getGravity());
        final double timeInSeconds = timeOfFlight * timePercent / 100d;

        initialVelocityValue.setText(getString(R.string.v, initialVelocityInMetersPerSecond));
        angleValue.setText(getString(R.string.a, angleInDegrees));
        timeValue.setText(getString(R.string.t, timeInSeconds, timePercent));

        projectileMotionView.update(initialVelocityInMetersPerSecond, angleInDegrees, planet.getGravity(), timePercent);
    }
}