package com.yhy.room.sdk.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhy.baselib.bean_sc.SCUser;
import com.yhy.baselib.data.SharedPreferenceUtils;
import com.yhy.baselib.toast.MyToast;
import com.yhy.baselib.util.KeyBoardUtils;
import com.yhy.room.sdk.ICloudRoomEnvType;
import com.yhy.room.sdk.ICloudRoomSdk;
import com.yhy.room.sdk.ICloudRoomSdk.LoginCallBack;
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
//    private static final String APP_ID_YIMI = "7169a6c5ab5b4eeba2ca37b831fb9239";
//    private static final String APP_KEY_YIMI = "fTvEfrTdSsP3uqZj";
    // 精锐1v1
    private static final String APP_ID_JINGRUI = "kiFBIeLYvxOuWFgwWOy1XFFFehdA2ovo";
    private static final String APP_KEY_JINGRUI = "L6X0TIPFLQGkwEKM";
    //溢米
    private static final String APP_ID_YIMI = "3edaf2877c994a4e8739dbe34411e7d7";
    private static final String APP_KEY_YIMI = "a995732df99bc794";


    private String appId = APP_ID_JINGRUI;
    private String appKey = APP_KEY_JINGRUI;

    private Button btnChangeDemo;
    private Button btnPlayRecord;
    private Button btnCourseware;
    private TextView tvPosition, tvRole;
    private RelativeLayout rlPosition, rlRole;
    private EditText edt_plan_id;
    private EditText edt_user_id;
    private EditText edt_group_id;
    private EditText edt_stu_id;
    private ImageView txtPlanId, img_close;
    private Button btnGoClass;
    private Button btnSetEnv;
    private long startClickTime;
    private int clickCount = 0;
    private RadioGroup radioGroupAppId;
    private volatile boolean inited = false;
    private int userType = SCUser.TYPE_STU;
    private Handler handler = new Handler(Looper.getMainLooper());

    private void initSDK() {
        if (inited) {
            return;
        }
        CloudRoomSdkManager.getInstance().init(MainActivity.this, true, appId, appKey);
        CloudRoomSdkManager.getInstance().setRoomSDKCallBack(new CloudRoomSdkManager.IRoomSDKCallBack() {
            @Override
            public void onOpenRoomResult(int code, String errMsg) {
                if (code == CloudRoomSdkManager.LOAD_ROOM_SUCCESS) {
                    //进入教室成功
                }
            }

            @Override
            public void onExitRoomResult(int code, String errMsg) {
                if (code == CloudRoomSdkManager.EXIT_ROOM_SUCCESS) {
                    //退出教室成功
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
        tvPosition = findViewById(R.id.tv_position);
        tvRole = findViewById(R.id.tv_role);
        rlPosition = findViewById(R.id.rl_position);
        rlRole = findViewById(R.id.rl_role);
        edt_plan_id = (EditText) findViewById(R.id.edt_plan_id);
        edt_stu_id = (EditText) findViewById(R.id.edt_stu_id);
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
        rlPosition.setOnClickListener(this);
        rlRole.setOnClickListener(this);
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
                findViewById(R.id.btn_go_class).setEnabled(false);
                goClass();
                findViewById(R.id.btn_go_class).setEnabled(true);
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

            case R.id.rl_role:
                showRoleSelectDialog();
                break;

            case R.id.rl_position:
                showPositionSelectDialog();
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

        // get roomId
        String roomId = edt_plan_id.getText().toString();
        if (roomId.equals("")) {
            Toast.makeText(this, "请输入房间号", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setStringValue(SP_KEY_PID, roomId);

        // get userId
        int userId = 0;
        try {
            userId = Integer.parseInt(edt_user_id.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "用户ID错误", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_UID, userId);
        final int userIdFinal = userId;

        // login
        CloudRoomSdkManager.getInstance().login(userId, new LoginCallBack() {
            public void onLoginFinish(boolean succ, String token, int code, String errMsg) {
                Log.i(TAG, "getToken: succ=" + succ + ", token=" + token
                        + ", code=" + code + ", msg=" + errMsg);
                if (!succ) {
                    Toast.makeText(MainActivity.this, "获取Token失败：" + errMsg, LENGTH_SHORT).show();
                    return;
                }

                // 登录成功，打开录播
                CloudRoomSdkManager.getInstance().playRecord(token, roomId, userIdFinal,
                        MainActivity.this, 0, new ICloudRoomSdk.RecordCallBack() {
                            @Override
                            public void onOpenFinish(boolean succ, int code, String errMsg) {
                                if (!succ) {
                                    Toast.makeText(MainActivity.this, "打开录播失败：" + errMsg, LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    /**
     * 去上课
     */
    private void goClass() {
        initSDK();

        // get roomId
        String roomId = edt_plan_id.getText().toString();
        if (roomId.equals("")) {
            Toast.makeText(this, "请输入房间号", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setStringValue(SP_KEY_PID, roomId);

        // get userId
        int userId = 0;
        try {
            userId = Integer.parseInt(edt_user_id.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "用户ID错误", LENGTH_SHORT).show();
            return;
        }
        int stuId = 0;
        if (userType == SCUser.TYPE_ADT) {
            try {
                stuId = Integer.parseInt(edt_stu_id.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "学生ID错误", LENGTH_SHORT).show();
                return;
            }
        }
        final int stuIdFinal = stuId;
        SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_UID, userId);
        final int userIdFinal = userId;

        // get groupId
        int groupId = 1;
        try {
            groupId = Integer.parseInt(edt_group_id.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "班级ID错误", LENGTH_SHORT).show();
            return;
        }
        SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_GID, groupId);
        final int groupIdFinal = groupId;

        // login
        CloudRoomSdkManager.getInstance().login(userId, new LoginCallBack() {
            public void onLoginFinish(boolean succ, String token, int code, String errMsg) {
                Log.i(TAG, "getToken: succ=" + succ + ", token=" + token
                        + ", code=" + code + ", msg=" + errMsg);
                if (!succ) {
                    Toast.makeText(MainActivity.this, "获取Token失败：" + errMsg, LENGTH_SHORT).show();
                    return;
                }
                CloudRoomSdkManager.getInstance().setRoomBackGroundImage(R.drawable.board_bg);
                // 登录成功，进入教室
                CloudRoomSdkManager.getInstance().joinCloudRoom(
                        token,
                        roomId,
                        userIdFinal,
                        stuIdFinal,
                        "https://imgstatic.juren.cn/B3PcvT04e7.jpg",
                        "安卓" + userIdFinal,
                        2, // 学生角色
                        groupIdFinal);
            }
        });
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
        CloudRoomSdkManager.getInstance().preViewDoc(this,planId, new ICloudRoomSdk.CourseWareCallBack() {
            @Override
            public void onOpenFinish(boolean succ, int code, String errMsg) {
                Log.i(TAG, "onOpenFinish: succ=" + succ + ", code=" + code + ", errMsg=" + errMsg);
                if (!succ) {
                    Toast.makeText(MainActivity.this, errMsg, LENGTH_SHORT).show();
                }
            }
        });
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
     * 设置环境：生产环境，测试环境等
     * @param context  上下文
     * @param url  环境url，必须是以下其中一个：
     *              String PREFIX_PRODUCT = "";
     *              String PREFIX_SIT01 = "sit01-";
     *              String PREFIX_SIT02 = "sit02-";
     *              String PREFIX_SIT03 = "sit03-";
     *              String PREFIX_SIT04 = "sit04-";
     */
    private void setApiEnv(Context context,String url) {
        CloudRoomSdkManager.getInstance().setEnvironment(context, ICloudRoomEnvType.PREFIX_SIT01);
    }

    /**
     * 选择角色
     */
    public void showRoleSelectDialog() {
        final String items[] = {"学生", "旁听"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 0);
        builder.setTitle("选择角色");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tvRole.setText("角色：" + items[which]);
                if (items[which].equals("旁听")) {
                    userType = SCUser.TYPE_ADT;
                } else {
                    userType = SCUser.TYPE_STU;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 选择身份
     */
    public void showPositionSelectDialog() {
        final String items[] = {"学生", "家长"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 0);
        builder.setTitle("选择身份");
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                tvPosition.setText("身份：" + items[which]);
            }
        });
        builder.create().show();
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
            appId = APP_ID_YIMI;
            appKey = APP_KEY_YIMI;
            SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_APP_ID, checkedId);
        } else if (checkedId == R.id.radio_appid_jingrui) {
            appId = APP_ID_JINGRUI;
            appKey = APP_KEY_JINGRUI;
            SharedPreferenceUtils.getInstance(MainActivity.this).setIntValue(SP_KEY_APP_ID, checkedId);
        } else if (checkedId == R.id.radio_appid_yimi) {
            appId = APP_ID_YIMI;
            appKey = APP_KEY_YIMI;
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
