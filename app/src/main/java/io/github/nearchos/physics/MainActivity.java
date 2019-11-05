package io.github.nearchos.physics;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ProjectileMotionView projectileMotionView;

    private SeekBar seekBarInitialVelocity;
    private SeekBar seekBarAngle;
    private SeekBar seekBarGravity;
    private SeekBar seekBarTime;

    private TextView initialVelocityValue;
    private TextView angleValue;
    private TextView gravityValue;
    private TextView timeValue;

    private SeekBarHandler seekBarHandler = new SeekBarHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectileMotionView = findViewById(R.id.ballisticsView);

        seekBarInitialVelocity = findViewById(R.id.seekBarInitialVelocity);
        seekBarAngle = findViewById(R.id.seekBarAngle);
        seekBarGravity = findViewById(R.id.seekBarGravity);
        seekBarTime = findViewById(R.id.seekBarTime);

        seekBarInitialVelocity.setOnSeekBarChangeListener(seekBarHandler);
        seekBarAngle.setOnSeekBarChangeListener(seekBarHandler);
        seekBarGravity.setOnSeekBarChangeListener(seekBarHandler);
        seekBarTime.setOnSeekBarChangeListener(seekBarHandler);

        initialVelocityValue = findViewById(R.id.initialVelocityValue);
        angleValue = findViewById(R.id.angleValue);
        gravityValue = findViewById(R.id.gravityValue);
        timeValue = findViewById(R.id.timeValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeekers();
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
        final int gravityOption = seekBarGravity.getProgress();
        final Planet planet = Planet.PLANETS[gravityOption];
        final int timePercent = seekBarTime.getProgress();
        final double timeOfFlight = Util.computeTimeOfFlight(initialVelocityInMetersPerSecond, angleInRadians, planet.getGravity());
        final double timeInSeconds = timeOfFlight * timePercent / 100d;

        initialVelocityValue.setText(getString(R.string.v, initialVelocityInMetersPerSecond));
        angleValue.setText(getString(R.string.a, angleInDegrees));
        gravityValue.setText(getString(R.string.g, planet.getName(), planet.getGravity()));
        timeValue.setText(getString(R.string.t, timeInSeconds, timePercent));

        Log.d("Ballistics", "iv: " + initialVelocityInMetersPerSecond + ", a: " + angleInDegrees + ", g: " + planet.getGravity() + ", t: " + timeInSeconds);
        projectileMotionView.update(initialVelocityInMetersPerSecond, angleInDegrees, planet.getGravity(), timePercent);
    }
}