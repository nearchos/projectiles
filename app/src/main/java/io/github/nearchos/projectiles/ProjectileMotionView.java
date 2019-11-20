package io.github.nearchos.projectiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProjectileMotionView extends View {

    public static final String TAG = "projectiles";

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

    private final Map<Planet, Bitmap> planetImagesCache = new HashMap<>();

    private Bitmap getPlanetBitmap(final Planet planet) {
        if(!planetImagesCache.containsKey(planet)) {
            final int identifier = getContext().getResources().getIdentifier(planet.getResourceName(), "drawable", getContext().getPackageName());
            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), identifier);
            planetImagesCache.put(planet, bitmap);
        }
        return planetImagesCache.get(planet);
    }

    private int initialVelocity;
    private int angleInDegrees;
    private Planet planet;
    private double gravity;
    private int timePercent;

    void update(final int initialVelocityInMetersPerSecond, final int angleInDegrees, final Planet planet, final int timePercent) {
        this.initialVelocity = initialVelocityInMetersPerSecond;
        this.angleInDegrees = angleInDegrees;
        this.planet = planet;
        this.gravity = planet.getGravity();
        this.timePercent = timePercent;

        invalidate();
    }

    final Paint paint = new Paint();

    public static final int BOUND = 10;
    public static final float TEXT_SIZE = 20f;
    public static final int RADIUS = 30;
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
        double zoom = 1d;

        // draw zoom text and reference graphics
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE);

        canvas.drawPaint(blackPaint);
        // draw background grid
        {
            float gridStep;
            if(range > 500) {
                gridStep = 200f;
                gridPaint.setColor(Color.RED);
            } else if(range > 50) {
                gridStep = 20f;
                gridPaint.setColor(Color.GREEN);
            } else if(range > 5) {
                gridStep = 2f;
                gridPaint.setColor(Color.WHITE);
            } else {
                gridStep = 0.2f;
                gridPaint.setColor(Color.YELLOW);
            }
            int numOfGridLines = (int) (range / gridStep); // must be few to few dozens
            float gridLineDistance = (width - 2 * BOUND) / numOfGridLines;

            final float startY = height / 2;
            final float endY = height - TEXT_SIZE;
            final float stepY = (endY - startY) / (numOfGridLines - 1);
            for(float y = startY; y <= endY; y += stepY) {
                canvas.drawLine(BOUND, y, width - BOUND, y, gridPaint);
            }

            for(int x = 0; x < width - BOUND; x += gridLineDistance) {
                canvas.drawLine(x + BOUND, height - TEXT_SIZE, width / 4f + x / 2f, height / 2f, gridPaint);
            }
        }

        canvas.drawText(String.format(Locale.US, "zoom: %.2f", zoom), BOUND, BOUND + TEXT_SIZE, paint);
        canvas.drawText(String.format(Locale.US, "range: %.2f", range), BOUND, BOUND + 3 * TEXT_SIZE, paint);

        // draw orbit
        paint.setColor(Color.CYAN);
        final double initialVelocityY = Math.sin(angleInRadians) * initialVelocity;
        final float step = angleInDegrees > 45 ? 0.3f : 1f;
        for(float x = 0; x < screenRange; x+=step) {
            final double t = timeOfFlight * x / range;
            double y = initialVelocityY * t - gravity * t * t / 2d;
            if(y>0)
                canvas.drawCircle(x + BOUND, height - BOUND - (float) y, 2, paint);
        }

        // draw angle arrow
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4f);
        final double arrowX = Math.cos(angleInRadians) * initialVelocity * ARROW_MULTIPLIER;
        final double arrowY = Math.sin(angleInRadians) * initialVelocity * ARROW_MULTIPLIER;
        canvas.drawLine(BOUND, height - BOUND, BOUND + (float) arrowX, height - BOUND - (float) arrowY, paint);

        // draw range line
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f);
        final String rangeLabel = String.format(Locale.US, "%.2f m", range);
        final float rangeLabelWidth = paint.measureText(rangeLabel);
        final double targetX = range > width * 1.5f ? width * 1.5f : range;
        canvas.drawText(rangeLabel, (float) (targetX/2 - rangeLabelWidth/2), height, paint);
        canvas.drawLine(BOUND, height-BOUND, (float) (targetX/2 - rangeLabelWidth/2) - BOUND, height-BOUND, paint);
        canvas.drawLine((float) (targetX/2 + rangeLabelWidth/2) + BOUND, height-BOUND, (float) targetX - BOUND, height-BOUND, paint);

        // draw projectile at time t
        final double t = (timePercent / 100d) * timeOfFlight;
        final double x = t * range / timeOfFlight;
        double y = initialVelocityY * t - gravity * t * t / 2d;
        paint.setColor(Color.BLUE);
        if(y>=0) canvas.drawCircle((float) x + BOUND, height - BOUND - (float) y, 10, paint);
    }
}