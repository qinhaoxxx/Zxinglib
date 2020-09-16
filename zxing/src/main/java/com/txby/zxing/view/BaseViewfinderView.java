package com.txby.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.ResultPoint;
import com.txby.zxing.R;
import com.txby.zxing.camera.CameraManager;
import com.txby.zxing.utils.DisplayUtil;

import java.util.Collection;
import java.util.HashSet;

/**
 * 自定义组件实现,扫描功能
 */
public final class BaseViewfinderView extends ViewfinderView {

    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    public BaseViewfinderView(Context context) {
        this(context, null);
    }

    public BaseViewfinderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BaseViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        possibleResultPoints = new HashSet<>(5);

        scanLight = BitmapFactory.decodeResource(resources, R.drawable.scan_light);

        initInnerRect(context, attrs);
    }

    /**
     * 初始化内部框的大小
     *
     * @param context
     * @param attrs
     */
    private void initInnerRect(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView);

        // 扫描框距离顶部
        float innerMarginTop = ta.getDimension(R.styleable.ViewfinderView_inner_marginTop, -1);
        if (innerMarginTop != -1) {
            CameraManager.FRAME_MARGIN_TOP = (int) innerMarginTop;
        }

        // 扫描框的宽度
        CameraManager.FRAME_WIDTH = (int) ta.getDimension(R.styleable.ViewfinderView_inner_width, DisplayUtil.screenWidthPx / 2);

        // 扫描框的高度
        CameraManager.FRAME_HEIGHT = (int) ta.getDimension(R.styleable.ViewfinderView_inner_height, DisplayUtil.screenWidthPx / 2);

        // 扫描bitmap
        Drawable drawable = ta.getDrawable(R.styleable.ViewfinderView_inner_scan_bitmap);
        if (drawable != null) {
        }

        // 扫描控件
        scanLight = BitmapFactory.decodeResource(getResources(), ta.getResourceId(R.styleable.ViewfinderView_inner_scan_bitmap, R.drawable.scan_light));
        // 扫描速度
        SCAN_VELOCITY = ta.getInt(R.styleable.ViewfinderView_inner_scan_speed, 5);


        ta.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            drawScanLight(canvas, frame);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
            }

            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    // 扫描线移动的y
    private int scanLineTop;
    // 扫描线移动速度
    private int SCAN_VELOCITY;
    // 扫描线
    private Bitmap scanLight;
    // 是否展示小圆点
    private boolean isCircle;

    /**
     * 绘制移动扫描线
     *
     * @param canvas
     * @param frame
     */
    private void drawScanLight(Canvas canvas, Rect frame) {

        if (scanLineTop == 0) {
            scanLineTop = frame.top;
        }

        if (scanLineTop >= frame.bottom - 30) {
            scanLineTop = frame.top;
        } else {
            scanLineTop += SCAN_VELOCITY;
        }
        Rect scanRect = new Rect(0, scanLineTop,canvas.getWidth() ,
                scanLineTop + 100);
        canvas.drawBitmap(scanLight, null, scanRect, paint);
    }


    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
