package com.yimi.room.sdk.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yimi.baselib.bean.UserInfo;
import com.yimi.baselib.constant.Constants;
import com.yimi.baselib.data.SharedPreferenceUtils;
import com.yimi.baselib.util.ApplicationCacheUtil;
import com.yimi.comp.dialog.ChangeApiEnvDialog;
import com.yimi.room.sdk.ctrl.CloudRoomSdkManager;
import com.yimi.room.sdk.demo.util.KeyBoardUtils;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SP_KEY_UID = "bigclass_demo_edt_user_id";
    private static final String SP_KEY_PID = "bigclass_demo_edt_plan_id";
    private static final String SP_KEY_GID = "bigclass_demo_edt_group_id";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CloudRoomSdkManager.getInstance().init(MainActivity.this, true);
        CloudRoomSdkManager.getInstance().setRoomSDKCallBack(new CloudRoomSdkManager.IRoomSDKCallBack() {
            @Override
            public void onOpenRoomResult(@CloudRoomSdkManager.ErrorCode int code, String errMsg) {
                if (code < 0) {
                    Toast.makeText(MainActivity.this, "Error：" + errMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
        initView();
    }

    private void initView() {
        btnChangeDemo = (Button) findViewById(R.id.btn_change_demo);
        btnPlayRecord = (Button) findViewById(R.id.btn_play_record);
        btnCourseware = (Button) findViewById(R.id.btn_courseware);
        edt_plan_id = (EditText) findViewById(R.id.edt_plan_id);
        edt_plan_id.setText(SharedPreferenceUtils.getInstance(MainActivity.this).getStringValue(SP_KEY_PID, "354937763033780224"));
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
        final String planId = edt_plan_id.getText().toString();
        if (planId.equals("")) {
            Toast.makeText(this, "请输入房间号码", LENGTH_SHORT).show();
        }
        CloudRoomSdkManager.getInstance().playRecord(
                "7169a6c5ab5b4eeba2ca37b831fb9239", planId, 1);
    }

    /**
     * 去上课
     */
    private void goClass() {
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
                "7169a6c5ab5b4eeba2ca37b831fb9239",
                "fTvEfrTdSsP3uqZj",
                roomId,
                userId,
                "https://imgstatic.juren.cn/B3PcvT04e7.jpg",
                "安卓" + userId,
                2,
                groupId);
    }

    private void openCourseware() {
        String planId = edt_plan_id.getText().toString();
        if (planId.equals("")) {
            Toast.makeText(this, "请输入房间号码", LENGTH_SHORT).show();
        }
        CloudRoomSdkManager.getInstance().openCourseWare(planId);
    }

    /**
     * 设置API环境
     */
    private void setApiEnv() {
        /*CloudRoomSdkManager.getInstance().openEnvSettingDialog(MainActivity.this, new CloudRoomSdkManager.EnvDialogCallback() {
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
        });*/

        openEnvSettingDialog(MainActivity.this);
    }

    /**
     * 打开环境设置Dialog
     */
    public void openEnvSettingDialog(Context context) {
        ChangeApiEnvDialog.get(context, new ChangeApiEnvDialog.OnApiEnviChangeListener() {
            @Override
            public void onApiEnviChanged() {
                UserInfo.setUser(null);
                UserInfo.removeRoomUserInfo();
                ApplicationCacheUtil.clearUserInfo();
                SharedPreferences sharedp = context.getSharedPreferences(Constants.SP_SaveUserInfo, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedp.edit();
                editor.putString(Constants.SP_SaveUserPwd, "").commit();

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
        }).show();
    }

}
