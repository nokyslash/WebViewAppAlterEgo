package home.lernestop.webviewappalterego;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeWebviewTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;
    public OnSwipeWebviewTouchListener(Context ctx, TouchListener touchListener) {
        gestureDetector = new GestureDetector(ctx, new GestureListener(touchListener));
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    private static final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final TouchListener touchListener;
        GestureListener(TouchListener touchListener) {
            super();
            this.touchListener = touchListener;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            return false;  // THIS does the trick
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > 500 && Math.abs(velocityX) > 30) {
                        if (diffX > 0) {
                            touchListener.onSwipeRight();
                        } else {
                            touchListener.onSwipeLeft();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}
