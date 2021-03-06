package com.txby.zxing.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.txby.zxing.R;
import com.txby.zxing.utils.CodeUtils;

/**
 * 默认的二维码扫描 Activity
 */
public class CaptureActivity extends AppCompatActivity implements AnalyzeCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        CaptureFragment captureFragment = new CaptureFragment();
        // 相机初始化回调
        captureFragment.setCameraInitCallBack(new CameraInitCallBack() {
            @Override
            public void callBack(Exception e) {
                if (e != null) {
                    Log.e("TAG", "callBack: ", e);
                }
            }
        });
        // 二维码解析回调函数
        captureFragment.setAnalyzeCallback(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_zxing_container, captureFragment).commit();
    }

    @Override
    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
        bundle.putString(CodeUtils.RESULT_STRING, result);
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onAnalyzeFailed() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
        bundle.putString(CodeUtils.RESULT_STRING, "");
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}