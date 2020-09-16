package com.qinhao.zxinglib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import com.txby.zxing.activity.AnalyzeCallback;
import com.txby.zxing.activity.CaptureFragment;
import com.txby.zxing.utils.CodeUtils;

/**
 * @author QinHao
 * @data 2018/11/21
 * @Content
 */
public class QRCodeUtils {

    private static final int RESULT_OK = -1;

    private Context mContext;

    @SuppressLint("StaticFieldLeak")
    private static QRCodeUtils mInstance;
    private OnQrListener listener;

    public interface OnQrListener {

        void onResult(String result);
    }

    public static QRCodeUtils getInstance() {
        if (mInstance == null) {
            synchronized (QRCodeUtils.class) {
                mInstance = new QRCodeUtils();
            }
        }
        return mInstance;
    }

    private QRCodeUtils() {

    }

    /**
     * 扫描二维码
     *
     * @param activity        扫描界面
     * @param captureFragment 可以自定义的扫描fragment
     */
    public void CustomUI(AppCompatActivity activity, CaptureFragment captureFragment, AnalyzeCallback analyzeCallback) {
        mContext = activity;
        //my_camera 是可以定制化的页面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    /**
     * @param textContent 生成二维码的字符串
     * @param bitmap      Logo
     */
    public Bitmap onGetLogoQR(String textContent, Bitmap bitmap) {
        return CodeUtils.createImage(textContent, 400, 400
                , bitmap);
    }

    /**
     * @param textContent 生成二维码的字符串
     */
    public Bitmap onGetNoLogoQR(String textContent) {
        return CodeUtils.createImage(textContent, 400, 400
                , null);
    }


}
