package com.yhy.room.sdk.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.yhy.baselib.data.SharedPreferenceUtils;
import com.yhy.baselib.toast.MyToast;
import com.yhy.baselib.util.KeyBoardUtils;
import com.yhy.room.sdk.ctrl.CloudRoomSdkManager;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        View.OnTouchListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "YIMI-Demo";
    private static final String SP_KEY_UID = "bigclass_demo_edt_user_id";
    private static final String SP_KEY_PID = "bigclass_demo_edt_plan_id";
    private static final String SP_KEY_GID = "bigclass_demo_edt_group_id";
    private static final String SP_KEY_APP_ID = "bigclass_demo_radio_app_id";
    public static final int REQUEST_STORAGE_PERMISSIONS = 1002;

    // 巨人大班课
    private static final String APP_ID_JUREN = "***";
    private static final String APP_KEY_JUREN = "***";
    // 精锐1v1
    private static final String APP_ID_JINGRUI = "***";
    private static final String APP_KEY_JINGRUI = "***";

    private String appId = APP_ID_JINGRUI;
    private String appKey = APP_KEY_JINGRUI;

    private Button btnChangeDemo;
    private Button btnPlayRecord;
    private Button btnCourseware;
    private EditText edt_plan_id;
    private EditText edt_user_id;
    private EditText edt_group_id;
    private ImageView txtPlanId, img_close;
    private Button btnGoClass;
    private Button btnSetEnv;
    private long startClickTime;
    private int clickCount = 0;
    private RadioGroup radioGroupAppId;
    private volatile boolean inited = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private void initSDK() {
        if (inited) {
            return;
        }
        CloudRoomSdkManager.getInstance().init(MainActivity.this, true, appId, appKey);
        CloudRoomSdkManager.getInstance().setRoomSDKCallBack(new CloudRoomSdkManager.IRoomSDKCallBack() {
            @Override
            public void onOpenRoomResult(@CloudRoomSdkManager.ErrorCode int code, String errMsg) {
                if (code != CloudRoomSdkManager.LOAD_ROOM_SUCCESS) {
                    //Toast.makeText(MainActivity.this, "Error：" + errMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onOpenRoomResult Error:" + errMsg + ", Code:" + code);
                }
            }
        });
        inited = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        initObligedPermission();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateBtnStatus();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.btn_go_class) {
                btnGoClass.setTextColor(Color.parseColor("#C45629"));
            } else if (v.getId() == R.id.btn_play_record) {
                btnPlayRecord.setTextColor(Color.parseColor("#C45629"));
            } else if (v.getId() == R.id.btn_set_env) {
                btnSetEnv.setTextColor(Color.WHITE);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (v.getId() == R.id.btn_go_class) {
                btnGoClass.setTextColor(Color.WHITE);
            } else if (v.getId() == R.id.btn_play_record) {
                btnPlayRecord.setTextColor(Color.WHITE);
            } else if (v.getId() == R.id.btn_set_env) {
                btnSetEnv.setTextColor(Color.parseColor("#BDBDBD"));
            }
        }
        return false;
    }

    private void initView() {
        btnChangeDemo = (Button) findViewById(R.id.btn_change_demo);
        btnPlayRecord = (Button) findViewById(R.id.btn_play_record);
        btnCourseware = (Button) findViewById(R.id.btn_courseware);
        edt_plan_id = (EditText) findViewById(R.id.edt_plan_id);
        edt_plan_id.setText(SharedPreferenceUtils.getInstance(MainActivity.this).getStringValue(SP_KEY_PID, "360007015298240512"));
        edt_user_id = (EditText) findViewById(R.id.edt_user_id);
        edt_user_id.setText(SharedPreferenceUtils.getInstance(MainActivity.this).getIntValue(SP_KEY_UID, 911911) + "");
        edt_group_id = (EditText) findViewById(R.id.edt_group_id);
        edt_group_id.setText(SharedPreferenceUtils.getInstance(MainActivity.this).getIntValue(SP_KEY_GID, 1) + "");
        txtPlanId = (ImageView) findViewById(R.id.txt_plan_id);
        btnGoClass = (Button) findViewById(R.id.btn_go_class);
        btnSetEnv = (Button) findViewById(R.id.btn_set_env);
        img_close = (ImageView) findViewById(R.id.img_close);
        btnChangeDemo.setOnClickListener(this);
        btnPlayRecord.setOnClickListener(this);
        btnCourseware.setOnClickListener(this);
        btnGoClass.setOnClickListener(this);
        txtPlanId.setOnClickListener(this);
        img_close.setOnClickListener(this);
        btnSetEnv.setOnClickListener(this);
        edt_plan_id.addTextChangedListener(this);
        edt_user_id.addTextChangedListener(this);
        edt_group_id.addTextChangedListener(this);
        btnGoClass.setOnTouchListener(this);
        btnPlayRecord.setOnTouchListener(this);
        btnSetEnv.setOnTouchListener(this);

        // APP_ID选择, 默认精锐
        radioGroupAppId = findViewById(R.id.radio_appid);
        radioGroupAppId.setOnCheckedChangeListener(this);
        radioGroupAppId.check(SharedPreferenceUtils.getInstance(MainActivity.this).getIntValue(SP_KEY_APP_ID, R.id.radio_appid_jingrui));

        updateBtnStatus();
    }

    private void updateBtnStatus() {
        btnGoClass.setEnabled(false);
        btnPlayRecord.setEnabled(false);
        if (edt_plan_id.getText() == null || TextUtils.isEmpty(edt_plan_id.getText().toString())) {
            return;
        }
        btnPlayRecord.setEnabled(true);
        if (edt_user_id.getText() == null || TextUtils.isEmpty(edt_user_id.getText().toString())) {
            return;
        }
        if (edt_group_id.getText() == null || TextUtils.isEmpty(edt_group_id.getText().toString())) {
            return;
        }
        btnGoClass.setEnabled(true);
    }

    private void calClick() {
        long nextClickTime = SystemClock.uptimeMillis();
        if (startClickTime <= 0) {
            startClickTime = SystemClock.uptimeMillis();
            clickCount++;
            return;
        } else {
            if (nextClickTime - startClickTime < 500) {
                //Toast.makeText(this, "被双击了", Toast.LENGTH_SHORT).show();
                clickCount++;
            }
            startClickTime = SystemClock.uptimeMillis();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.btn_change_demo:
                KeyBoardUtils.keyBoardHide(btnGoClass, this);
                //startActivity(new Intent(MainActivity.this, DemoWebActivity.class));
                break;
            case R.id.btn_play_record:
                KeyBoardUtils.keyBoardHide(btnGoClass, this);
                playRecord();
                break;
            case R.id.btn_courseware:
                KeyBoardUtils.keyBoardHide(btnGoClass, this);
                openCourseware();
                break;
            case R.id.btn_go_class:
                KeyBoardUtils.keyBoardHide(btnGoClass, this);
                goClass();
                break;
            case R.id.btn_set_env:
                setApiEnv();
                break;
            case R.id.txt_plan_id:
                calClick();
                if (clickCount > 6) {
                    clickCount = 0;
                    setApiEnv();
                    btnChangeDemo.setVisibility(View.VISIBLE);
                } else {
                    btnChangeDemo.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 打开录播
     */
    private void playRecord() {
        initSDK();
        final String planId = edt_plan_id.getText().toString();
        if (planId.equals("")) {
            Toast.makeText(this, "请输入房间号码", LENGTH_SHORT).show();
        }
        CloudRoomSdkManager.getInstance().playRecord(appId, appKey, planId, 1);
    }

    /**
     * 去上课
     */
    private void goClass() {
        initSDK();
        String roomId = edt_plan_id.getText().toString();
        if (roomId.equals("")) {
            Toast.makeText(this, "请输入房间号", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setStringValue(SP_KEY_PID, roomId);

        int userId = 0;
        try {
            userId = Integer.parseInt(edt_user_id.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "用户ID错误", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_UID, userId);

        int groupId = 1;
        try {
            groupId = Integer.parseInt(edt_group_id.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "班级ID错误", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_GID, groupId);

        CloudRoomSdkManager.getInstance().joinCloudRoom(
                roomId,
                userId,
                "https://imgstatic.juren.cn/B3PcvT04e7.jpg",
                "安卓" + userId,
                2,
                groupId);
    }

    /**
     * 查看课件
     */
    private void openCourseware() {
        initSDK();
        String planId = edt_plan_id.getText().toString();
        if (planId.equals("")) {
            Toast.makeText(this, "请输入房间号码", LENGTH_SHORT).show();
        }
        CloudRoomSdkManager.getInstance().preViewDoc(appId, appKey, planId);
    }

    /**
     * 设置API环境
     */
    private void setApiEnv() {
        initSDK();
        CloudRoomSdkManager.getInstance().openEnvSettingDialog(MainActivity.this, new CloudRoomSdkManager.EnvDialogCallback() {
            @Override
            public void onEnvSetFinish() {
                // 重启APP
                edt_plan_id.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(i);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                finish();
            }
        });
    }

    /**
     * 检查必要权限
     */
    private void initObligedPermission() {
        if (!checkStoragePermission(this)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSIONS);
        }
    }

    private boolean checkStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSIONS:
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    MyToast.showToast(this, "关闭存储权限将无法正常使用APP");
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (inited) {
            Toast.makeText(this, "SDK已经初始化，重启后再选择", LENGTH_SHORT).show();
            handler.postDelayed(() -> restart(), 2000);
            return;
        }

        if (checkedId == R.id.radio_appid_juren) {
            appId = APP_ID_JUREN;
            appKey = APP_KEY_JUREN;
            SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_APP_ID, checkedId);
        } else if (checkedId == R.id.radio_appid_jingrui) {
            appId = APP_ID_JINGRUI;
            appKey = APP_KEY_JINGRUI;
            SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_APP_ID, checkedId);
        }
    }

    private void restart() {
        // 重启APP
        edt_plan_id.post(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(i);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        finish();
    }
}
