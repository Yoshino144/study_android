package top.pcat.study.WrongQuestion;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import top.pcat.study.Pojo.WrongChapter;
import top.pcat.study.R;
import top.pcat.study.Utils.GetUser;
import top.pcat.study.WrongQuestion.Adapter.WChapterAdapter;

public class WChapterActivity extends AppCompatActivity {
    private List<WrongChapter> wChapterList = new ArrayList<>();
    private RecyclerView recyclerView;
    private WChapterAdapter adapter;
    private Integer subjectId;
    ActivityResultLauncher<Intent> launcher;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            LogUtils.d("将加到recy中的数据："+wChapterList);
            if (wChapterList.size() == 0){
                findViewById(R.id.wu).setVisibility(View.VISIBLE);
            }
            adapter = new WChapterAdapter(wChapterList,true,launcher);
            recyclerView.setAdapter(adapter);

            return false;
        }
    });

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            Intent intent = new Intent();
            intent.putExtra("data","Hello SecondActivity");
            setResult(RESULT_OK,intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wchapter_question);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        String str = result.getData().getStringExtra("data");
                        Toasty.success(this, "刷新数据", Toast.LENGTH_SHORT).show();
                        wChapterList.clear();
                        initWChapters();
                    }
                });

        BarUtils.setStatusBarLightMode(this,true);
        BarUtils.transparentStatusBar(this);

        Intent intent = getIntent();
        String subjectName = intent.getStringExtra("subjectName");
        subjectId = intent.getIntExtra("subjectId",0);
        TextView viewById = findViewById(R.id.title);
        viewById.setText("错题本-"+subjectName);
        final Drawable windowBackground = getWindow().getDecorView().getBackground();

        RelativeLayout root = findViewById(R.id.root);
        BlurView bottomBlurView = findViewById(R.id.cur_blu);
        bottomBlurView.setupWith(root)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(15f)
                .setHasFixedTransformationMatrix(true);

        initWChapters();
        recyclerView = (RecyclerView) findViewById(R.id.wq_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }



    private void initWChapters() {
        getWChapter();
//        for (int i = 0; i < 20; i++) {
//            WChapter apple = new WChapter("章节i" + i, 5*i);
//            wChapterList.add(apple);
//
//        }
    }

    private void getWChapter() {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.network_url)+"/userAnswers/subject/"+subjectId +"/" + GetUser.getUserId(this))
                        .get()
                        .build();//创建一个Request对象
                LogUtils.d("错题章节，网络请求 "+request.url().toString());
                Response response = client.newCall(request).execute(); //发送请求获取返回数据
                String responseData = response.body().string(); //处理返回的数据

                LogUtils.d("错题章节，网络请求结果 "+responseData);
                wChapterList = gson.fromJson(responseData, new TypeToken<List<WrongChapter>>() {}.getType());
                handler.sendMessage(new Message());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}