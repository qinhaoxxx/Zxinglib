package com.qinhao.zxinglib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.txby.zxing.activity.AnalyzeCallback;
import com.txby.zxing.activity.CaptureActivity;
import com.txby.zxing.activity.CaptureFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QRCodeUtils.getInstance().CustomUI(this,new CaptureFragment(), new AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {

            }

            @Override
            public void onAnalyzeFailed() {

            }
        });

    }

}