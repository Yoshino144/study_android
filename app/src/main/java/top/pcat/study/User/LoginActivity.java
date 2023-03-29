package top.pcat.study.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.apkfuns.logutils.LogUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.google.gson.Gson;import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

// 导入对应SMS模块的client
import com.tencentcloudapi.sms.v20210111.SmsClient;

// 导入要请求接口对应的request response类
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import top.pcat.study.MainActivity;
import top.pcat.study.Pojo.LoginReq;
import top.pcat.study.R;
import top.pcat.study.Pojo.Msg;
import top.pcat.study.Utils.StatusBarUtil;


import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, PlatformActionListener {

    //返回到第几页
    private int page = 4;
    private String uuid = "";
    private LinearLayout loginWechat;
    private LinearLayout loginSina;
    private LinearLayout loginQQ;
    RelativeLayout mTouchLayout;
    private LinearLayout phone;
    private LinearLayout userpas;
    private LinearLayout aaaaa;
    private LinearLayout bbbbb;
    private LinearLayout ccccc;
    private LinearLayout ddddd;
    private LinearLayout eeeee;
    TextView yzmbut;
    String ph;
    EditText yzm;
    private String yyyyyy;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    LogUtils.d("获取用户id:" + uuid);
                    try {
                        getUserInfo();
                        getToken();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        StatusBarUtil.setStatusBarMode(this, true, R.color.write_fan);


        TextView wx = findViewById(R.id.wx);
        wx.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        wx.getPaint().setAntiAlias(true);

        TextView wb = findViewById(R.id.wb);
        wb.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        wb.getPaint().setAntiAlias(true);

        phone = findViewById(R.id.phone);
        userpas = findViewById(R.id.userpas);
        aaaaa = findViewById(R.id.aaaaaa);
        bbbbb = findViewById(R.id.bbbbb);
        ccccc = findViewById(R.id.cccccc);
        ddddd = findViewById(R.id.dddddd);
        eeeee = findViewById(R.id.eeeeee);
        yzmbut = findViewById(R.id.yzmbut);
        yzm= findViewById(R.id.yzm);

        phone.setOnClickListener(v -> {
            //sendCode(this);
            phone.setBackgroundResource(R.drawable.shape_sign_son);
            userpas.setBackgroundResource(R.drawable.shape_sign);
            aaaaa.setVisibility(View.GONE);
            ccccc.setVisibility(View.GONE);
            ddddd.setVisibility(View.VISIBLE);
            eeeee.setVisibility(View.VISIBLE);
        });
        userpas.setOnClickListener(v -> {
            phone.setBackgroundResource(R.drawable.shape_sign);
            userpas.setBackgroundResource(R.drawable.shape_sign_son);
            aaaaa.setVisibility(View.VISIBLE);
            ccccc.setVisibility(View.VISIBLE);
            ddddd.setVisibility(View.GONE);
            eeeee.setVisibility(View.GONE);
        });

        findViewById(R.id.yzm).setOnClickListener(v->{
            EditText aa = (EditText)findViewById(R.id.sjh);
            ph = aa.getText().toString();

            String code= randomCode();
            yyyyyy=code;
            yzmbut.setEnabled(false);
            LogUtils.d("发送验证码"+ph+code);
            sendSms(ph,code);
            new TimeCount(60000, 1000).start();

        });

        //空白处左右滑动
        mTouchLayout = findViewById(R.id.main_touch_layout);
        final int[] pageIndex = {0};
        mTouchLayout.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;//没有用到
            float endX;
            float endY;//没有用到

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        int width = size.x;
                        if (startX - endX >= (width / 8)) {// startX - endX 大于0 且大于宽的1/8 可以往后翻页
                            if (pageIndex[0] == 0) {
                                pageIndex[0] = 1;
                                phone.setBackgroundResource(R.drawable.shape_sign_son);
                                userpas.setBackgroundResource(R.drawable.shape_sign);
                                aaaaa.setVisibility(View.GONE);
                                ccccc.setVisibility(View.GONE);
                                ddddd.setVisibility(View.VISIBLE);
                                eeeee.setVisibility(View.VISIBLE);
//                                mVp.setCurrentItem(1);
//                                mTextPager.setCurrentItem(1, true);
                            }
                        } else if (endX - startX >= (width / 8)) { // endX - startX   大于0 且大于宽的1/8 可以往前翻页
                            if (pageIndex[0] == 1) {
//                                mVp.setCurrentItem(0);
                                pageIndex[0] = 0;
                                phone.setBackgroundResource(R.drawable.shape_sign);
                                userpas.setBackgroundResource(R.drawable.shape_sign_son);
                                aaaaa.setVisibility(View.VISIBLE);
                                ccccc.setVisibility(View.VISIBLE);
                                ddddd.setVisibility(View.GONE);
                                eeeee.setVisibility(View.GONE);
//                                mTextPager.setCurrentItem(1, true);
                            }
                        }

                        break;
                }
                return true;
            }
        });

        //获取页数
        Intent intent = getIntent();
        page = intent.getIntExtra("page", 0);

        EditText editText1 = findViewById(R.id.username);
        EditText editText2 = findViewById(R.id.password);
        Button go = findViewById(R.id.gores);
        Button gos = findViewById(R.id.goress);
        go.setOnClickListener(v -> {
            Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent1);
        });

        gos.setOnClickListener(v -> {
            Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent1);
        });

        //点击登录
        Button cc = findViewById(R.id.come);
        cc.setOnClickListener(v -> {
            String phoneCode = editText1.getText().toString();
            String password = editText2.getText().toString();
            try {
                LogUtils.d("手机号+密码登录:code:" + phoneCode + " pw:" + password);
                Login(phoneCode, password);
            } catch (IOException e) {
                Toasty.error(LoginActivity.this, "登录失败 请联系开发者", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        });

        Button cce = findViewById(R.id.comee);
        cce.setOnClickListener(v -> {
            if(yyyyyy.equals(yzm.getText().toString())){
                sendPhone(ph);
            }else{
                Toasty.error(LoginActivity.this, "验证码不正确", Toast.LENGTH_SHORT);
            }
        });

        //返回按钮
        ImageView img = findViewById(R.id.loginback);
        img.setOnClickListener(v -> {
            finish();
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            it.putExtra("page", page);
            startActivity(it);
        });

        //快速登录栏
        loginWechat = findViewById(R.id.login_wechat);
        loginSina = findViewById(R.id.login_sina);
        loginQQ = findViewById(R.id.login_qq);
        loginWechat.setOnClickListener(this);
        loginSina.setOnClickListener(this);
        loginQQ.setOnClickListener(this);

    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长，和计时的时间间隔

        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            yzmbut.setText(millisUntilFinished/1000+"");

            if(yyyyyy.equals(yzm.getText().toString())){
                sendPhone(ph);
                this.onFinish();
                cancel();
            }

        }

        @Override
        public void onFinish() {//计时完毕时触发该方法

            yzmbut.setEnabled(true);
            yzmbut.setText("发送验证码");
        }

    }

    /**
     * 点击监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_wechat: {
                WeChat();
            }
            break;
            case R.id.login_sina: {
                Sina();
            }
            break;
            case R.id.login_qq:
                QQ();
                break;
            default:
                break;
        }
    }

    /**
     * qq登录
     */
    private void QQ() {
        Platform plat = ShareSDK.getPlatform(QQ.NAME);
        ShareSDK.setEnableAuthTag(true);
        plat.removeAccount(true);
        //plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
        if (plat.isClientValid()) {

        }
        if (plat.isAuthValid()) {
        }
        //plat.authorize();	//要功能，不要数据
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }

    /**
     * 微信
     */
    private void WeChat() {
        Platform plat = ShareSDK.getPlatform(Wechat.NAME);
        ShareSDK.setEnableAuthTag(true);
        plat.removeAccount(true);
        //plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
        if (plat.isClientValid()) {

        }
        if (plat.isAuthValid()) {

        }
        //plat.authorize();	//要功能，不要数据
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }

    /**
     * 新浪登录
     */
    private void Sina() {
        ShareSDK.setEnableAuthTag(true);
        Platform plat = ShareSDK.getPlatform("SinaWeibo");
        plat.removeAccount(true);
        //plat.SSOSetting(false);
        plat.setPlatformActionListener(this);
        if (plat.isClientValid()) {

        }
        if (plat.isAuthValid()) {

        }
        //plat.authorize();	//要功能，不要数据
        plat.showUser(null);    //要数据不要功能，主要体现在不会重复出现授权界面
    }

    /**
     * 返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            it.putExtra("page", page);
            startActivity(it);
        }
        return super.onKeyDown(keyCode, event);
    }


    //执行登录
    public void Login(String username, String pass) throws IOException {

       // String pwMd5 = EncryptUtils.encryptMD5ToString(pass);
        LogUtils.d("进行登录：" + getResources().getString(R.string.network_url) + "/users/" + username + "/" + pass);
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.network_url) + "/users/" + username + "/" + pass)
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toasty.error(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                String rr = response.body().string();
                LogUtils.d("登录请求返回内容" + rr);
                Msg msg = gson.fromJson(rr, Msg.class);
                if (msg.getStatus() == 200) {
                    LoginReq loginReq = gson.fromJson(msg.getData().toString(), LoginReq.class);
                    LogUtils.d("用户数据" + loginReq);
                    Looper.prepare();
                    LogUtils.d("登录成功");
                    LogUtils.d(FileIOUtils.writeFileFromString(
                            getFilesDir().getAbsolutePath() + "/userToken", gson.toJson(msg.getData())));

                    Message m = new Message();
                    m.what = 0;
                    uuid = loginReq.getUuid();
                    handler.sendMessage(m);

                    Toasty.success(LoginActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toasty.error(LoginActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    //融云token
    public void getToken() throws IOException {

        LogUtils.d("获取token：" + getResources().getString(R.string.network_url) + "/users/rongToken");
        MediaType mediaType = MediaType.parse("text/x-markdown; charset=utf-8");
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("userId", uuid);
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.network_url) + "/users/rongToken")
                .post(formBodyBuilder.build())
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toasty.error(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rr = response.body().string();
                    LogUtils.d(FileIOUtils.writeFileFromString(
                            getFilesDir().getAbsolutePath() + "/rongToken", rr));

                } else {
                    Looper.prepare();
                    Toasty.error(LoginActivity.this, "额。。。网络有一点点不好。。", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });
    }

    /**
     * 获取用户信息
     *
     * @throws IOException
     */
    public void getUserInfo() throws IOException {
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.network_url) + "/users/" + uuid + "/infos")
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toasty.error(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                String rr = response.body().string();
                LogUtils.d("获取用户信息返回内容" + rr);
                Msg msg = gson.fromJson(rr, Msg.class);
                if (msg.getStatus() == 200) {

                    LogUtils.d(FileIOUtils.writeFileFromString(
                            getFilesDir().getAbsolutePath() + "/userInfo", gson.toJson(msg.getData())));


                    Looper.prepare();
                    LogUtils.d("用户信息成功");
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    it.putExtra("page", page);
                    LogUtils.d("跳转:MainActivity：page：" + page);
                    startActivity(it);
                    finish();
                    Toasty.success(LoginActivity.this, "用户信息" + msg.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    Looper.prepare();
                    Toasty.error(LoginActivity.this, "用户信息" + msg.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

        LogUtils.d("=========", " _QQ: -->> onComplete: Platform:" + platform.toString());
        LogUtils.d("=========", " _QQ: -->> onComplete: hashMap:" + hashMap);
        LogUtils.d("=========", " _QQ: -->> onComplete: token:" + platform.getDb().getToken());
        String userId = platform.getDb().getUserId();
        LogUtils.d("getUserId============", userId);

        LogUtils.d("Sign类登录后信息获取======", String.valueOf(hashMap));

        JSONObject jsonObject = new JSONObject(hashMap);
        String name = null;
        String sex = null;
        String year = null;
        try {
            name = jsonObject.getString("nickname");
            sex = jsonObject.getString("gender");
            year = jsonObject.getString("year") + "-01-01 00:00:00";
            LogUtils.d("-============", name + sex + year);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //checkQQUser(userId, name, sex, year);
        //inent.putExtras(bundle);
        //inent.setClass(this, TagsItemActivity.class);
        //startActivity(inent);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.e("SDK+", " SdkTagsMainActivity onError platform: " + platform +
                " i: " + i + " throwable " + throwable.getMessage());
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Log.e("SDK+", " SdkTagsMainActivity onCancel platform: " + platform +
                " i: " + i);
    }
//
//    void checkQQUser(String userId, String name, String sex, String year) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://192.168.137.1/web/CheckUser.php");
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("POST");
//                    connection.setDoOutput(true);
//                    connection.setDoInput(true);
//                    connection.setUseCaches(false);
//                    connection.connect();
//
//                    String body = "appName=QQ&" + "userAppId=" + userId + "&username=" + name + "&sex=" + sex + "&year=" + year;
//                    //LogUtils.d(username,password);
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
//                    writer.write(body);
//                    writer.close();
//
//                    int responseCode = connection.getResponseCode();
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        InputStream inputStream = connection.getInputStream();
//                        String result = String.valueOf(inputStream);//将流转换为字符串。
//                        //LogUtils.d("kwwl","result============="+result);
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                        StringBuilder response = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        //LogUtils.d("pccp",response.toString());
//                        String username = readSj(response.toString(), 0);
//                        GetImg(username);
//                        save(username);
//                        saveInfo(response.toString());
//                        UpData(username);
//
//                        Intent it = new Intent(LoginActivity.this, MainActivity.class);
//                        it.putExtra("page", 3);
//                        startActivity(it);
//
//                        finish();
//                        Looper.prepare();
//                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    } else {
//                        Looper.prepare();
//                        Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Looper.prepare();
//                    Toast.makeText(LoginActivity.this, "登录失败  请联系开发者", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
//                }
//            }
//        }).start();
//    }


    /**
     * 短信验证码
     *
     * @param context
     */
    public void sendCode(Context context) {
//        RegisterPage page = new RegisterPage();
//        //如果使用我们的ui，没有申请模板编号的情况下需传null
//        page.setTempCode(null);
//        page.setRegisterCallback(new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 处理成功的结果
//                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
//                    // 国家代码，如“86”
//                    String country = (String) phoneMap.get("country");
//                    // 手机号码，如“13800138000”
//                    String phone = (String) phoneMap.get("phone");
//                    // TODO 利用国家代码和手机号码进行后续的操作
//
//                    sendPhone(phone);
//
//
//                } else {
//                    // TODO 处理错误的结果
//                }
//            }
//        });
//        page.show(context);


//        System.out.println("电话："+phone+"验证码"+code);
//        session.setAttribute(phone,code);
//



    }

    public static String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    public void sendSms(String phone, String code) {
        /* 必要步骤：
         * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
         * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
         * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
         * 以免泄露密钥对危及你的财产安全。
         * SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi */
        Credential cred = new Credential("AKIDDE3cttc5IDrGPHsDH4aqpY0B5i49yWnn", "kEn56rLYVCcmfHdiW3KL6cttd6ICpCJB");

        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        // 设置代理（无需要直接忽略）
        // httpProfile.setProxyHost("真实代理ip");
        // httpProfile.setProxyPort(真实代理端口);
        /* SDK默认使用POST方法。
         * 如果你一定要使用GET方法，可以在这里设置。GET方法无法处理一些较大的请求 */
        httpProfile.setReqMethod("POST");
        /* SDK有默认的超时时间，非必要请不要进行调整
         * 如有需要请在代码中查阅以获取最新的默认值 */
        httpProfile.setConnTimeout(60);
        /* 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象
         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8 */
        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
        /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
         * 你可以直接查询SDK源码确定接口有哪些属性可以设置
         * 属性可能是基本类型，也可能引用了另一个数据结构
         * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
        SendSmsRequest req = new SendSmsRequest();

        /* 填充请求参数,这里request对象的成员变量即对应接口的入参
         * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
         * 基本类型的设置:
         * 帮助链接：
         * 短信控制台: https://console.cloud.tencent.com/smsv2
         * 腾讯云短信小助手: https://cloud.tencent.com/document/product/382/3773#.E6.8A.80.E6.9C.AF.E4.BA.A4.E6.B5.81 */

        /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
        // 应用 ID 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看
        String sdkAppId = "1400657671";
        req.setSmsSdkAppId(sdkAppId);

        /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
        // 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
        String signName = "彭程个人技术文章";
        req.setSignName(signName);

        /* 模板 ID: 必须填写已审核通过的模板 ID */
        // 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
        String templateId = "1357203";
        req.setTemplateId(templateId);

        /* 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
        String[] templateParamSet = {code};
        req.setTemplateParamSet(templateParamSet);

        /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
         * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
        String[] phoneNumberSet = {"+86" + phone};
        req.setPhoneNumberSet(phoneNumberSet);

        /* 用户的 session 内容（无需要可忽略）: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
        String sessionContext = "";
        req.setSessionContext(sessionContext);

        /* 短信码号扩展号（无需要可忽略）: 默认未开通，如需开通请联系 [腾讯云短信小助手] */
        String extendCode = "";
        req.setExtendCode(extendCode);

        /* 国际/港澳台短信 SenderId（无需要可忽略）: 国内短信填空，默认未开通，如需开通请联系 [腾讯云短信小助手] */
        String senderid = "";
        req.setSenderId(senderid);

        /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
         * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
        new Thread(() -> {
            SendSmsResponse res = null;
            try {
                res = client.SendSms(req);
            } catch (TencentCloudSDKException e) {
                e.printStackTrace();
            }

            // 输出json格式的字符串回包
            LogUtils.d(SendSmsResponse.toJsonString(res));

            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            // System.out.println(res.getRequestId());

            /* 当出现以下错误码时，快速解决方案参考
             * [FailedOperation.SignatureIncorrectOrUnapproved](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Afailedoperation.signatureincorrectorunapproved-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * [FailedOperation.TemplateIncorrectOrUnapproved](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Afailedoperation.templateincorrectorunapproved-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * [UnauthorizedOperation.SmsSdkAppIdVerifyFail](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Aunauthorizedoperation.smssdkappidverifyfail-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * [UnsupportedOperation.ContainDomesticAndInternationalPhoneNumber](https://cloud.tencent.com/document/product/382/9558#.E7.9F.AD.E4.BF.A1.E5.8F.91.E9.80.81.E6.8F.90.E7.A4.BA.EF.BC.9Aunsupportedoperation.containdomesticandinternationalphonenumber-.E5.A6.82.E4.BD.95.E5.A4.84.E7.90.86.EF.BC.9F)
             * 更多错误，可咨询[腾讯云助手](https://tccc.qcloud.com/web/im/index.html#/chat?webAppId=8fa15978f85cb41f7e2ea36920cb3ae1&title=Sms)
             */
        }).start();


    }

    /**
     * 检查手机号
     *
     * @param phone
     */
    private void sendPhone(String phone) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.network_url) + "/users/" + phone)
                        .get()
                        .build();//创建一个Request对象
                Response response = client.newCall(request).execute();//发送请求获取返回数据
                String responseData = response.body().string();//处理返回的数据
                Msg msg = gson.fromJson(responseData, Msg.class);
                LogUtils.d(responseData);
                if (msg.getStatus() != 200) {
                    LogUtils.d("=======" + phone, "手机号登录失败-未注册" + responseData);
                    Looper.prepare();
                    Toasty.error(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                } else {
                    LogUtils.d("=======" + phone, "手机号登录成功" + responseData);



                    LoginReq loginReq = gson.fromJson(msg.getData().toString(), LoginReq.class);
                    LogUtils.d("用户数据" + loginReq);
                    Looper.prepare();
                    LogUtils.d("登录成功");
                    LogUtils.d(FileIOUtils.writeFileFromString(
                            getFilesDir().getAbsolutePath() + "/userToken", gson.toJson(msg.getData())));

                    Message m = new Message();
                    m.what = 0;
                    uuid = loginReq.getUuid();
                    handler.sendMessage(m);

                    Toasty.success(LoginActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String readSjph(String tempInfo, int postion) {
        try {
            JSONObject jsonObject = new JSONObject(tempInfo);

            return jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tempInfo;
    }
}
