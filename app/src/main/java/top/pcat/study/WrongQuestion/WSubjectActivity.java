package top.pcat.study.WrongQuestion;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apkfuns.logutils.LogUtils;
import com.blankj.utilcode.util.BarUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.pcat.study.R;
import top.pcat.study.Utils.GetUser;
import top.pcat.study.WrongQuestion.Adapter.WrongAdapter;
import top.pcat.study.WrongQuestion.Pojo.Wrong;

public class WSubjectActivity extends AppCompatActivity {
    private List<Wrong> wrongList = new ArrayList<>();
    private RecyclerView recyclerView;
    private WrongAdapter adapter;
    ActivityResultLauncher<Intent> launcher;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            LogUtils.d("将加到recy中的数据："+wrongList);
            if (wrongList.size() == 0){
                findViewById(R.id.wu).setVisibility(View.VISIBLE);
            }
            adapter = new WrongAdapter(wrongList,launcher);
            recyclerView.setAdapter(adapter);

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_question);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String str = result.getData().getStringExtra("data");
                        Toasty.success(this, "刷新数据", Toast.LENGTH_SHORT).show();
                        wrongList.clear();
                        initWrongs();
                    }
                });

        BarUtils.setStatusBarLightMode(this,true);
        BarUtils.transparentStatusBar(this);

        final Drawable windowBackground = getWindow().getDecorView().getBackground();

        RelativeLayout root = findViewById(R.id.root);
        BlurView bottomBlurView = findViewById(R.id.cur_blu);
        bottomBlurView.setupWith(root)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(15f)
                .setHasFixedTransformationMatrix(true);
        initWrongs();
        recyclerView = (RecyclerView) findViewById(R.id.wq_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }



    private void initWrongs() {
        getWrongSbuject();
//        for (int i = 0; i < 20; i++) {
//            Wrong apple = new Wrong("章节i" + i, 5*i);
//            wrongList.add(apple);
//
//        }
    }

    private void getWrongSbuject() {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.network_url)+"/userAnswers/subject/" + GetUser.getUserId(this))
                        .get()
                        .build();//创建一个Request对象
                LogUtils.d("错题章节，网络请求 "+request.url().toString());
                Response response = client.newCall(request).execute(); //发送请求获取返回数据
                String responseData = response.body().string(); //处理返回的数据

                LogUtils.d("错题章节，网络请求结果 "+responseData);
                wrongList = gson.fromJson(responseData, new TypeToken<List<Wrong>>() {}.getType());
                handler.sendMessage(new Message());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            Intent intent2 = new Intent("android.intent.action.CART_BROADCAST");
            intent2.putExtra("data","refresh");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent2);
            sendBroadcast(intent2);
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if(resultCode==10086){
                    String returnData = data.getStringExtra("data_return");
                    LogUtils.d("FirstActivity");
                }
                break;

        }
    }

}