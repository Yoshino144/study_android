package top.pcat.study.TabChat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.mob.tools.utils.FileUtils;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.List;

import top.pcat.study.R;
import top.pcat.study.TabChat.Fragment.ClassItemFragment;
import top.pcat.study.Utils.GetUser;

public class DialogActivity extends Activity implements View.OnClickListener {
    private Button btnSubmit;
    private EditText etComment;
    private int type;

    private Handler mMsgHander ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        type = getIntent().getIntExtra("type", 0);
        //点击空白处让Activity消失，可在Style中设置
//        setFinishOnTouchOutside(true);

        setWindow();

        etComment = (EditText) findViewById(R.id.et_comment);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
//                InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                InputMethodManager inputManager =
                        (InputMethodManager) etComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(etComment, 0);
                return false;
            }
        }).sendEmptyMessageDelayed(0, 300);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        etComment.setHint(type==1?"输入班级名":"输入班级id");

        findViewById(R.id.choicepic).setOnClickListener(v->{

            try {
                String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                int permission = ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
                if (permission != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
                } else {
                    Matisse.from(this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(9)
                            //.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .showPreview(false) // Default is `true`
                            .forResult(23);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
    }

    List<Uri> mSelected;
    @Override      //接收返回的地址
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 23 && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);

            //Glide.with(this).load(mSelected.get(0)).into((ImageView) findViewById(R.id.choicepic));
            FileUtils.deleteFile(getCacheDir()+"SampleCropImage.png");
            UCrop.of(mSelected.get(0), Uri.fromFile(new File(getCacheDir(), "SampleCropImage.png")))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(100, 100)
                    .start(this);
        }else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            LogUtils.d("裁剪成功");
            final Uri resultUri = UCrop.getOutput(data);
            mSelected.set(0,resultUri);
            Glide.with(this).load(resultUri).into((ImageView) findViewById(R.id.choicepic));
        } else if (resultCode == UCrop.RESULT_ERROR) {
            LogUtils.d("裁剪失败");
            final Throwable cropError = UCrop.getError(data);
            Glide.with(this).load(R.drawable.addpic).into((ImageView) findViewById(R.id.choicepic));
        }
    }

    private void setWindow() {
        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                Intent intent = new Intent();
                LogUtils.d("jiangyao"+type);
                intent.putExtra("type",type);
                intent.putExtra("value",etComment.getText().toString());
                if (mSelected != null){
                    intent.putExtra("uri",mSelected.get(0).toString());
                }
                setResult(Activity.RESULT_OK,intent);
                finish();
                break;
        }
    }

}