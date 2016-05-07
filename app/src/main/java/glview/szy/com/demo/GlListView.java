package glview.szy.com.demo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.Scroller;

/**
 * Created by szy on 2016/4/27.
 */
public class GlListView extends GLSurfaceView {
    public static final String TAG = "test";

    private GlListViewRender mRender;
    private int mDownY = 0;
    private static final int TOUCH_SLOP = 16;
    private static final int MOVE_STEP = 4;
    private int mCurrentY = -1;
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private static final int VELOCITY_LIMIT = 500;

    public GlListView(Context context) {
        super(context);
        init();
    }

    public GlListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onMeasure(int widthspec, int heightspec) {
//        LogUtils.log("onMeasure " + MeasureSpec.getSize(widthspec)
//                + "  " + MeasureSpec.getSize(heightspec));
        super.onMeasure(widthspec, heightspec);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        LogUtils.log("onlayout " + left  + " " + top + " " + right + " " + bottom);
    }

    private void init() {
        mRender = new GlListViewRender(getContext());
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mScroller = new Scroller(getContext(), new ScrollInterpolator());
    }

    public void setAdapter(BaseAdapter adapter) {
        LogUtils.log("set Adapter ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null)
        {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        mScroller.abortAnimation();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtils.log("onTouchEvent down");
                mDownY = (int) event.getY();
                mCurrentY = mDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.log("onTouchEvent move");
                shouldMove((int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, 5000);
                int velocity = (int) mVelocityTracker.getYVelocity();
                moveWithVelocity(velocity, (int) event.getY());
                break;
        }
        return true;
    }

    private void shouldMove(int y) {
        int offset = y - mDownY;
        LogUtils.log("move ==== " + offset);
        if (Math.abs(offset) > TOUCH_SLOP) {
            move(y);
        }
    }

    private void syncMove(final int moveoffset) {
        mRender.move(moveoffset);
//        queueEvent(new Runnable() {
//            @Override
//            public void run() {
//                mRender.move(moveoffset);
//            }
//        });
    }

    private void move(int y) {
        int moveoffset = mCurrentY - y;
        LogUtils.log("move ==== " + moveoffset + "  " + mDownY);
        if (Math.abs(moveoffset) > MOVE_STEP) {
            syncMove(moveoffset);
            this.requestRender();
            mCurrentY = y;
        }
    }

    private void moveWithVelocity(int v, int y) {
        LogUtils.log("moveWithVelocity " + v + "  " + y);
        move(y);

        int distance = trackBound();
        LogUtils.log("off", "get track bound " + distance);

        if (distance != 0) {
            mCurrentY = 0;
        } else {
            if (Math.abs(v) < VELOCITY_LIMIT) {
                return ;
            }

            if (y > mDownY) {
                distance = (int) Math.abs(v * v / 2 / 1000);
                if (distance > mRender.getOffset()) {
                    distance = (int) mRender.getOffset();
                    mCurrentY = 0;
                }
            } else {
                distance = -(int) Math.abs(v * v / 2 / 1000);

                if (-distance + mRender.getOffset() > mRender.getMaxHeight()) {
                    distance = (int) (mRender.getOffset() - mRender.getMaxHeight());
                    mCurrentY = 0;
                }
            }
        }

        LogUtils.log("off", "distance ================== " + distance + "  " + mRender.getOffset());
        mScroller.startScroll(0, 0, 0, distance, 1000);
        invalidate();
    }


    /**
     * top is exceed
     * @return
     */
    private int trackBound() {
        float offset = mRender.getOffset();
        LogUtils.log("trackBound ============ " + offset
                + "  max == " + mRender.getMaxHeight());
        if (offset < 0) {
            return (int) offset;
        } else if (offset > mRender.getMaxHeight()) {
            return (int) (offset - mRender.getMaxHeight());
        }

        return 0;
    }

    @Override
    public void computeScroll() {
        LogUtils.log("computeScroll");
        if (mScroller.computeScrollOffset()) {
            int currenty = mScroller.getCurrY();
            moveFling(currenty);
            invalidate();
        }
    }

    private void moveFling(int y) {
        int moveoffset = mCurrentY - y;
        LogUtils.log("off", "move ==== " + moveoffset + "   " + mCurrentY + "  " + y);
        if (Math.abs(moveoffset) > 1f) {
            syncMove(moveoffset);
            this.requestRender();
            mCurrentY = y;
        }
    }

    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t*t*t*t*t + 1;
        }
    }
}
