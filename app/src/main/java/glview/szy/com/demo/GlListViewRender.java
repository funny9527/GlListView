package glview.szy.com.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by szy on 2016/4/27.
 */
public class GlListViewRender implements GLSurfaceView.Renderer {

    private ArrayList<FloatBuffer> mInputVertices;
    private ArrayList<Integer> mInputTextures;
    private FloatBuffer   mColorBuffer;
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint clearPaint = new Paint();
    private Context mContext;
    private static float SIZE = 256f;
    private static int TEXT_SIZE = 30;
    private int mWidth;
    private int mHeight;
    private int mOffsetY = 0;
    private int mTotalOffsetY = 0;

    private int mFirstVisible = 0;
    private int mLastVisible = 0;
    private int ITEM_SIZE = (int) (SIZE + TEXT_SIZE);

    private int mItemSize = 0;
    private BitmapDrawable mBitmapDrawable;
    private Bitmap mItemBmp;
    private Canvas mCanvas;

    private static long mLastTime = 0;

    private static final int[] PICS = new int[] {
            R.drawable.app0,
            R.drawable.app1,
            R.drawable.app2,
            R.drawable.app3,
            R.drawable.app4,
            R.drawable.app5,
            R.drawable.app6,
            R.drawable.app7,
            R.drawable.app8,
            R.drawable.app9,
            R.drawable.app10,
            R.drawable.app11,
            R.drawable.app12,
            R.drawable.app13,
            R.drawable.app14,
            R.drawable.app15,
            R.drawable.app16,
            R.drawable.app17,
            R.drawable.app18,
            R.drawable.app19,
    };

    public static void trimTime(String step) {
        LogUtils.log("time", step + "  " + (System.currentTimeMillis() - mLastTime));
        mLastTime = System.currentTimeMillis();
    }

    public GlListViewRender(Context context) {
        mContext = context;

        trimTime("GlListViewRender");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        trimTime("onSurfaceCreated start ");
        LogUtils.log("onSurfaceCreated");
        mInputVertices = new ArrayList<FloatBuffer> ();
        mInputTextures = new ArrayList<Integer>();

        initTextureRule();
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setAntiAlias(true);
        textPaint.setColor(0xffffffff);
        textPaint.setTextScaleX(1.0f);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mItemBmp = Bitmap.createBitmap((int) SIZE,
                (int) SIZE + TEXT_SIZE,
                Bitmap.Config.ARGB_8888);

        mCanvas = new Canvas(mItemBmp);

        trimTime("onSurfaceCreated stop");
    }

    //order
    private void initTextureRule() {
        float textTureCoors[] = new float[] {0,0, 1,0, 0,1, 1,1};
        ByteBuffer buffer = ByteBuffer.allocateDirect(textTureCoors.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        mColorBuffer = buffer.asFloatBuffer();
        mColorBuffer.put(textTureCoors);
        mColorBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        trimTime("onSurfaceChanged start");
        LogUtils.log("onSurfaceChanged width ==== " + width + "  height ==== " + height);

        mWidth = width;
        mHeight = height;

        gl.glViewport(0, 0, width, height);

        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, width, 0, height, 1, -1);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        test(gl);

        mLastVisible = (mFirstVisible + mHeight / ITEM_SIZE
                + (mHeight % ITEM_SIZE == 0 ? 0 : 1)) * 2;

        LogUtils.log("size", "fist = " + mFirstVisible
                + "  last = " + mLastVisible);
        trimTime("onSurfaceChanged stop");
    }

    private void updateVisible() {
        mFirstVisible = (int) (mTotalOffsetY / ITEM_SIZE);
        mLastVisible = (mFirstVisible + mHeight / ITEM_SIZE
                + (mHeight % ITEM_SIZE == 0 ? 0 : 1)) * 2;

        LogUtils.log("size", "fist = " + mFirstVisible + "  last = " + mLastVisible);
    }

    private void test(GL10 gl) {
        trimTime("test start");
        mItemSize = 1000;
        for (int i = 0; i < mItemSize / 2; i++) {
            addItemInScreen(gl, i, 0, SIZE);
            addItemInScreen(gl, i, 1, SIZE);
        }
        trimTime("test stop");
    }


    @Override
    public synchronized void onDrawFrame(GL10 gl) {
        trimTime("onDrawFrame start");
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl.glTranslatef(0, mOffsetY, 0);
        mTotalOffsetY += mOffsetY;
        updateVisible();

        for (int i = mFirstVisible; i < mLastVisible; i++)
        {
            drawItem(gl, i);
        }

        LogUtils.log("off", "onDrawFrame ======= " + mTotalOffsetY);
        trimTime("onDrawFrame stop");
    }

    private void drawItem(GL10 gl, int i) {
        if (i >= mItemSize || i < 0) {
            return ;
        }

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mInputVertices.get(i));
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mInputTextures.get(i));
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mColorBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 1, 3);
    }

    public int initTextureId(GL10 gl, Drawable drawable, String title)
    {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        int curId = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, curId);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT);

        mBitmapDrawable = (BitmapDrawable) drawable;

        mCanvas.drawPaint(clearPaint);

        mCanvas.drawBitmap(mBitmapDrawable.getBitmap(), null,
                new Rect(0, 0, (int) SIZE, (int) SIZE), null);
        mCanvas.drawText(title, 25, SIZE + TEXT_SIZE, textPaint);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mItemBmp, 0);

        return curId;
    }

    private void addItemInScreen(GL10 gl, int row, int col, float size) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(32);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertices = byteBuffer.asFloatBuffer();
        float x1 = col * size;
        float y1 = mHeight - row * (size + TEXT_SIZE);
        float x2 = (col + 1) * size;
        float y2 = mHeight - (row + 1) * (size + TEXT_SIZE);
        vertices.put(
                new float[]
                        {
                                x1,
                                y1,

                                x2,
                                y1,

                                x1,
                                y2,

                                x2,
                                y2
                        });

        vertices.flip();
        mInputVertices.add(vertices);

        mInputTextures.add(initTextureId(gl,
                mContext.getResources().getDrawable(PICS[(row * 2 + col) % PICS.length]),
                "test-" + row + "-" + col));
    }

    public synchronized void move(int offset) {
        mOffsetY = offset;
//        mTotalOffsetY += mOffsetY;
//        updateVisible();
    }

    public int getOffset() {
        return mTotalOffsetY;
    }

    public int getMaxHeight() {
        return (int) (mInputVertices.size() * (TEXT_SIZE + SIZE) / 2) - mHeight;
    }
}
