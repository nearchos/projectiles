package io.github.nearchos.projectiles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.Locale;

public class ProjectileMotionView extends View {

    public ProjectileMotionView(Context context) {
        super(context);
        init();
    }

    public ProjectileMotionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private final Paint blackPaint = new Paint();
    private final Paint gridPaint = new Paint();

    private void init() {
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(Color.BLACK);

        final DisplayMetrics displaymetrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
    }

    private int initialVelocity;
    private int angleInDegrees;
    private double gravity;
    private int timePercent;

    void update(final int initialVelocityInMetersPerSecond, final int angleInDegrees, final double gravity, final int timePercent) {
        this.initialVelocity = initialVelocityInMetersPerSecond;
        this.angleInDegrees = angleInDegrees;
        this.gravity = gravity;
        this.timePercent = timePercent;

        invalidate();
    }

    final Paint paint = new Paint();

    public static final int BOUND = 10;
    public static final float TEXT_SIZE = 50f;
    public static final int RADIUS = 25;
    public static final float ARROW_MULTIPLIER = 2f; // multiplies the init velocity arrow length

    /**
     * Look at https://en.wikipedia.org/wiki/Projectile_motion
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int width = getWidth();
        final int height = getHeight();

        final double angleInRadians = Util.degreesToRadians(angleInDegrees);
        final double range = Util.computeRange(initialVelocity, angleInRadians, gravity);
        final double ceiling = Util.computeMaxHeight(initialVelocity, angleInRadians, gravity);
        final double timeOfFlight = Util.computeTimeOfFlight(initialVelocity, angleInRadians, gravity);

        final float screenRange = width - 2 * BOUND;

        // draw zoom text and reference graphics
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE);

        canvas.drawPaint(blackPaint);
        // draw background grid
        {
            float gridStep;
            if(range > 500) {
                gridStep = 200f;
                gridPaint.setColor(Color.GREEN);
//                gridPaint.setColor(Color.RED);
                gridPaint.setStrokeWidth(8f);
            } else if(range > 50) {
                gridStep = 20f;
                gridPaint.setColor(Color.GREEN);
                gridPaint.setStrokeWidth(4f);
            } else if(range > 5) {
                gridStep = 2f;
                gridPaint.setColor(Color.GREEN);
//                gridPaint.setColor(Color.WHITE);
                gridPaint.setStrokeWidth(2f);
            } else {
                gridStep = 0.2f;
                gridPaint.setColor(Color.GREEN);
//                gridPaint.setColor(Color.YELLOW);
                gridPaint.setStrokeWidth(1f);
            }
            final int numOfGridLines = (int) Math.max(1, range / gridStep); // must be few to few dozens
            final float gridLineDistance = (width - 2 * BOUND) / numOfGridLines;

            final float startY = height / 2;
            final float endY = height - TEXT_SIZE;
            final float stepY = (endY - startY) / (numOfGridLines - 1);
            for(float y = startY; y <= endY; y += stepY) {
                canvas.drawLine(BOUND, y, width - BOUND, y, gridPaint);
            }

            for(int x = -width/2; x < 3*width/2; x += gridLineDistance) {
                canvas.drawLine(x + BOUND, height - TEXT_SIZE, width / 4f + x / 2f, height / 2f, gridPaint);
            }
        }

        final String maxHeightText = getResources().getString(R.string.maxHeight, ceiling);
        canvas.drawText(maxHeightText, BOUND, BOUND + TEXT_SIZE, paint);

        // draw orbit
        paint.setColor(Color.WHITE);
        final double initialVelocityY = Math.sin(angleInRadians) * initialVelocity;
        final float step = angleInDegrees > 45 ? 0.3f : 1f;
        final double metersPerPixel = range / screenRange;
        for(float x = 0; x < screenRange; x+=step) {
            final double t = timeOfFlight * x / screenRange;
            double y = initialVelocityY * t - gravity * t * t / 2d;
            if(y>0) {
                float cy = (float) (y / metersPerPixel); // convert back to pixels
                canvas.drawCircle(x + BOUND, height - TEXT_SIZE - cy, 2, paint);
            }
        }

        // draw angle arrow
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(8f);
        final double arrowX = Math.cos(angleInRadians) * initialVelocity * ARROW_MULTIPLIER;
        final double arrowY = Math.sin(angleInRadians) * initialVelocity * ARROW_MULTIPLIER;
        canvas.drawLine(BOUND, height - TEXT_SIZE, BOUND + (float) arrowX, height - TEXT_SIZE - (float) arrowY, paint);

        // draw range line
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f);
        final String rangeLabel = String.format(Locale.US, "%.2f m", range);
        final float rangeLabelWidth = paint.measureText(rangeLabel);
        canvas.drawText(rangeLabel, screenRange/2 - rangeLabelWidth/2, height - 1, paint);
        canvas.drawLine(BOUND, height-TEXT_SIZE/2, screenRange/2 - rangeLabelWidth/2 - BOUND, height-TEXT_SIZE/2, paint);
        canvas.drawLine(screenRange/2 + rangeLabelWidth/2 + BOUND, height-TEXT_SIZE/2, screenRange - BOUND, height-TEXT_SIZE/2, paint);

        // draw projectile at time t
        final double t = (timePercent / 100d) * timeOfFlight;
        final double x = t * screenRange / timeOfFlight;
        double y = initialVelocityY * t - gravity * t * t / 2d;
        paint.setColor(Color.CYAN);
        final float cy = (float) (y / metersPerPixel);
        if(y>=0) canvas.drawCircle((float) x + BOUND, height - TEXT_SIZE - cy, RADIUS, paint);
    }
}