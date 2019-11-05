package io.github.nearchos.physics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

    private Bitmap hill;
    private int hillWidth;
    private int hillHeight;
    private final Rect srcRect = new Rect();
    private final Rect dstRect = new Rect();

    private void init() {
        // the used picture is free on the public domain - from https://www.goodfreephotos.com
        // specific url: https://www.goodfreephotos.com/other-landscapes/meadow-and-single-tree-in-the-distance.jpg.php
        hill = BitmapFactory.decodeResource(getResources(), R.drawable.hill);
        hillWidth = hill.getWidth();
        hillHeight = hill.getHeight();
        Log.d("hill", "width: " + hillWidth + ", height: " + hillHeight);
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
    public static final float TEXT_SIZE = 40f;
    public static final int RADIUS = 20;
    public static final float ARROW_MULTIPLIER = 2f; // multiplies the init velocity arrow length
    public static final float MIN_ZOOM = 0.2f;
    public static final float MAX_ZOOM = 50.0f;
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
        final double timeOfFlight = Util.computeTimeOfFlight(initialVelocity, angleInRadians, gravity);
        final double ceiling = Util.computeMaxHeight(initialVelocity, angleInRadians, gravity);
        final float screenRange = width - 2 * BOUND;
        double zoom = range / screenRange;
        if(zoom < MIN_ZOOM) zoom = MIN_ZOOM;
        if(zoom > MAX_ZOOM) zoom = MAX_ZOOM;

        // draw zoom text and reference graphics
        paint.setColor(Color.BLACK);
        paint.setTextSize(TEXT_SIZE);
        final double bitmapWidthToHeightRatio = hillWidth * 1d / hillHeight;
        final int computedBitmapHeight = (int) (hillHeight * (1+zoom) * SMOOTHING);
        final int computedBitmapWidth = (int) (computedBitmapHeight * bitmapWidthToHeightRatio);
        srcRect.set(hillWidth - computedBitmapWidth, hillHeight/2 - computedBitmapHeight/2, hillWidth, hillHeight/2 + computedBitmapHeight/2);
        dstRect.set(0, 0, width, height);
        canvas.drawBitmap(hill, srcRect, dstRect, paint);
        canvas.drawText(String.format(Locale.US, "zooom: %.2f", zoom), BOUND, BOUND + TEXT_SIZE, paint);

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