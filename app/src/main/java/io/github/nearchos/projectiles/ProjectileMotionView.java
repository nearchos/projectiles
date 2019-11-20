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

//    private Bitmap hill;
//    private int hillWidth;
//    private int hillHeight;
    private final Rect srcRect = new Rect();
    private final Rect dstRect = new Rect();

    public static final float MIN_ZOOM = 0.1f;
    public static final float MAX_ZOOM = 10.0f;

    private final Paint blackPaint = new Paint();

    private void init() {
        // the used picture is free on the public domain - from https://www.goodfreephotos.com
        // specific url: https://www.goodfreephotos.com/other-landscapes/meadow-and-single-tree-in-the-distance.jpg.php
//        hill = BitmapFactory.decodeResource(getResources(), R.drawable.hill);
//        hillWidth = hill.getWidth();
//        hillHeight = hill.getHeight();
//        Log.d(TAG, "zoom - hill width & height: " + hillWidth + " & " + hillHeight);

        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(Color.BLACK);

        final DisplayMetrics displaymetrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;
        Log.d(TAG, "zoom - screen width & height: " + screenWidth + " & " + screenHeight);
    }

    private final Map<Planet, Bitmap> planetImagesCache = new HashMap<>();

    private Bitmap getPlanetBitmap(final Planet planet) {
        System.out.println("planet: " + planet);
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
    public static final float TEXT_SIZE = 40f;
    public static final int RADIUS = 20;
    public static final float ARROW_MULTIPLIER = 2f; // multiplies the init velocity arrow length
    public static final float SMOOTHING = 0.5f;

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

        final double balancePointRange = Util.computeRange(Util.BALANCE_POINT_VELOCITY, Util.BALANCE_POINT_ANGLE_IN_RADIANS, gravity);
        final double balancePointCeiling = Util.computeMaxHeight(Util.BALANCE_POINT_VELOCITY, Util.BALANCE_POINT_ANGLE_IN_RADIANS, gravity);

        final float screenRange = width - 2 * BOUND;
        double zoom = 1d;
//        double zoom = range / screenRange;
//        double zoom = timePercent > 50 ? timePercent - 50 : 1d / (1 + 10-timePercent/5d);

        if(zoom < MIN_ZOOM) zoom = MIN_ZOOM;
        if(zoom > MAX_ZOOM) zoom = MAX_ZOOM;

        // draw zoom text and reference graphics
        paint.setColor(Color.BLACK);
        paint.setTextSize(TEXT_SIZE);
//        final double bitmapWidthToHeightRatio = hillWidth * 1d / hillHeight;
//        final int computedBitmapHeight = (int) (hillHeight * (1+zoom) * SMOOTHING);
//        final int computedBitmapWidth = (int) (computedBitmapHeight * bitmapWidthToHeightRatio);
////        final int computedBitmapHeight = (int) (zoom * hillHeight / 4);
////        final int computedBitmapWidth = (int) (zoom * hillWidth / 4);
//        srcRect.set(Math.max(0, hillWidth - computedBitmapWidth), Math.max(0, hillHeight/2 - computedBitmapHeight/2), hillWidth, Math.min(hillHeight, hillHeight/2 + computedBitmapHeight/2));
//        Log.d(TAG, "zoom - srcRect: " + srcRect.toShortString());
//        dstRect.set(0, 0, width, height);
////        canvas.drawBitmap(hill, srcRect, dstRect, paint);

        canvas.drawPaint(blackPaint);

        if(planet != null) {
            final Bitmap planetBitmap = getPlanetBitmap(planet);
            final float density = getResources().getDisplayMetrics().density;
Log.d(TAG, "zoom - density: " + density);
            final float bitmapWidth = planetBitmap.getWidth() / density;
            final float bitmapHeight = planetBitmap.getHeight() / density;
            srcRect.set(0, 0, planetBitmap.getWidth(), planetBitmap.getHeight());
Log.d(TAG, "zoom - bitmapWidth: " + bitmapWidth + ", screen width(): " + width);
            final int planetMaxWidth = Math.min((int) bitmapWidth, width);
            final int planetMaxHeight = Math.min((int) bitmapHeight, height);
            final int leftBound = (width - planetMaxWidth) / 2;
            Log.d(TAG, "zoom - leftBound: " + leftBound);
            dstRect.set(leftBound, 0, leftBound + planetMaxWidth, planetMaxHeight);
            canvas.drawBitmap(planetBitmap, srcRect, dstRect, paint);
        }

        canvas.drawText(String.format(Locale.US, "zoom: %.2f", zoom), BOUND, BOUND + TEXT_SIZE, paint);

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
        paint.setColor(Color.BLACK);
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