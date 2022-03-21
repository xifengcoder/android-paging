package com.example.android.codelabs.paging.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;

import com.example.android.codelabs.paging.R;
import com.example.android.codelabs.paging.model.ResultBean;

public class FirstActivity extends Activity {

    private static final String TAG = "yxf";

    private static final String KEY_NAME = "key_name";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_RESULT_BEAN = "key_result_bean";

    private ResultBean resultBean;
    private View view;
    private WindowManager wm;
    private boolean showWm = true;//默认是应该显示悬浮通知栏
    private WindowManager.LayoutParams params;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "FirstActivity onCreate");
        setContentView(R.layout.activity_first);


        Button button = findViewById(R.id.btn_go_to_second);

        if (savedInstanceState != null) {
            String savedName = savedInstanceState.getString(KEY_NAME);
            String savedPassword = savedInstanceState.getString(KEY_PASSWORD);
            ResultBean savedResultBean = savedInstanceState.getParcelable(KEY_RESULT_BEAN);
            Log.d(TAG, "FirstActivity onCreate, name: " + savedName +
                    ", password: " + savedPassword +
                    ", savedResultBean: " + savedResultBean);
        }

        Object object = getLastNonConfigurationInstance();
        if (object instanceof ResultBean) {
            ResultBean saved = (ResultBean) object;
            Log.d(TAG, "get ResultBean from saved: " + saved);
        }
        button.setOnClickListener(v -> {
//            resultBean = new ResultBean();
//            resultBean.name = "James";
//            resultBean.password = "123456";
//            startActivity(new Intent(FirstActivity.this, SecondActivity.class));
//            initWindowManager();
//            //initWindowManager();
//            createFloatView("");
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            intent.setData(Uri.parse("package:" + FirstActivity.this.getPackageName()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            FirstActivity.this.startActivityForResult(intent, 1001);
            showAlertWindow();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getApplicationContext())) {
                    //启动Activity让用户授权
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 100);
                } else {
                    showAlertWindow();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showAlertWindow();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showAlertWindow() {
            initWindowManager();
//                    //initWindowManager();
            createFloatView("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "FirstActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "FirstActivity onResume");
        ClassLoader activityLoader = Activity.class.getClassLoader();
        ClassLoader mainClassLoader = getClassLoader();
        Log.d(TAG, "activityLoader: " + activityLoader + ", mainClassLoader: " + mainClassLoader);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "FirstActivity onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "FirstActivity onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FirstActivity onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "FirstActivity onSaveInstanceState");
        if (resultBean != null) {
            if (resultBean.name != null) {
                outState.putString(KEY_NAME, resultBean.name);
            }
            if (resultBean.password != null) {
                outState.putString(KEY_PASSWORD, resultBean.password);
            }
            outState.putParcelable(KEY_RESULT_BEAN, resultBean);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * 如果在AndroidManifest.xml中配置了android:configChanges="screenSize|orientation", 则屏幕旋转时，只会回调
     * onConfigurationChanged()方法。
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "FirstActivity onConfigurationChanged, newConfig: " + newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String savedName = savedInstanceState.getString(KEY_NAME);
        String savedPassword = savedInstanceState.getString(KEY_PASSWORD);
        ResultBean resultBean = savedInstanceState.getParcelable(KEY_RESULT_BEAN);
        Log.d(TAG, "FirstActivity onRestoreInstanceState, name: " + savedName +
                ", password: " + savedPassword +
                ", savedResultBean: " + resultBean);
    }

    /**
     * 在onStop和onDestroy之间被调用。
     *
     * @return
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        //返回一个包含有状态信息的Object.
        Log.d(TAG, "FirstActivity onRetainNonConfigurationInstance");
        return resultBean;
    }

    private void initWindowManager() {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        //注意是TYPE_SYSTEM_ERROR而不是TYPE_SYSTEM_ALERT
        //前面有SYSTEM才可以遮挡状态栏，不然的话只能在状态栏下显示通知栏
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.TRANSPARENT;
        //设置必须触摸通知栏才可以关掉
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        // 设置通知栏的长和宽
        params.width = wm.getDefaultDisplay().getWidth();
        params.height = 200;
        params.gravity = Gravity.TOP;
    }


    private void createFloatView(String str) {

        view = LayoutInflater.from(this).inflate(R.layout.wechat, null);
        //在这里你可以解析你的自定义的布局成一个View
        if (showWm) {
            wm.addView(view, params);
            showWm = false;
        } else {
            wm.updateViewLayout(view, params);
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        wm.removeViewImmediate(view);
                        view = null;
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                }
                return true;
            }
        });

    }

}
