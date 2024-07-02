package io.github.hmzi67.securezone.Widgets;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class LShapeGestureView extends View {

    private PointF startPoint;
    private boolean isHorizontalSwipe;
    private boolean isVerticalSwipe;

    public LShapeGestureView(Context context) {
        super(context);
    }

    public LShapeGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LShapeGestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startPoint = new PointF(event.getX(), event.getY());
                isHorizontalSwipe = false;
                isVerticalSwipe = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - startPoint.x;
                float dy = event.getY() - startPoint.y;

                if (!isHorizontalSwipe && Math.abs(dx) > 100 && Math.abs(dy) < 50) {
                    // Horizontal swipe detected
                    isHorizontalSwipe = true;
                    startPoint.set(event.getX(), event.getY()); // Reset start point for vertical detection
                } else if (isHorizontalSwipe && !isVerticalSwipe && Math.abs(dy) > 100 && Math.abs(dx) < 50) {
                    // Vertical swipe detected after horizontal swipe
                    isVerticalSwipe = true;
                }

                if (isHorizontalSwipe && isVerticalSwipe) {
                    onLShapeDetected();
                    return true;
                }
                return true;

            case MotionEvent.ACTION_UP:
                // Reset on touch up
                startPoint = null;
                isHorizontalSwipe = false;
                isVerticalSwipe = false;
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    private void onLShapeDetected() {
        Toast.makeText(getContext(), "L-Shape Gesture Detected", Toast.LENGTH_SHORT).show();
        // Implement your action for L-shaped gesture here
    }
}
