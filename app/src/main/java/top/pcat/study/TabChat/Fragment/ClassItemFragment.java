package top.pcat.study.TabChat.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import top.pcat.study.Pojo.Clasp;
import top.pcat.study.R;
import top.pcat.study.TabChat.DialogActivity;
import top.pcat.study.Utils.GetUser;

/**
 * A fragment representing a list of Items.
 */
public class ClassItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private int mColumnCount = 1;
    public List<Clasp> ITEMS = new ArrayList<>();
    private RecyclerView recyclerView;
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            init();
        }
    };

    private Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                PutFwq2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

//    public static Handler mMsgHander =new Handler(){
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what){
//                case 1:
//                    //新建班级
//                    Bundle b =msg.getData();
//                    try {
//                        createClass(b.getString("type"));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case 2:
//                    //加入班级
//
//                    break;
//            }
//        }
//    };
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClassItemFragment() {
    }

    public void createClassBut(){

        Log.d("dsd","调用其那套framer");
        Toast.makeText(getActivity(), "新建班级", Toast.LENGTH_SHORT).show();

//        Intent intent = new Intent(getActivity(),DialogActivity.class);
//        //在Intent对象当中添加一个键值对
//        intent.putExtra("type",1);
//        startActivityForResult(intent,3);
//        startActivity(intent);

        Intent intent = new Intent(getActivity(), DialogActivity.class);
        intent.putExtra("type",1);
        intentActivityResultLauncher.launch(intent);
    }

    public void joinClassBut(){

        Log.d("dsd","调用其那套framer");
        Toast.makeText(getActivity(), "加入班级", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), DialogActivity.class);
        intent.putExtra("type",2);
        intentActivityResultLauncher.launch(intent);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ClassItemFragment newInstance(int columnCount) {
        ClassItemFragment fragment = new ClassItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                //此处是跳转的result回调方法
                LogUtils.d("进入回调函数");
                if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                    LogUtils.d("判断通过");
                    int type = result.getData().getIntExtra("type",0);
                    String value = result.getData().getStringExtra("value");
                    LogUtils.d("type="+type+",value="+value);
                    if (type==1){
                        Uri uri = Uri.parse(result.getData().getStringExtra("uri"));
                        LogUtils.d("创建课程");
                        try {
                            createClass(value,uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(type == 2){
                        LogUtils.d("加入课程");
                        try {
                            joinClass(Integer.parseInt(value));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    Toasty.error(getContext(), "取消").show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_item_list, container, false);

        if (FileUtils.isFileExists(getActivity().getFilesDir().getAbsolutePath() + "/userToken")) {
            try {
                PutFwq2();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }

    public void init(){
        recyclerView.setAdapter(new MyClassItemRecyclerViewAdapter(ITEMS));
    }


    public void PutFwq2() throws IOException {
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.network_url)+"/classes/"+ GetUser.getUserId(getContext()))
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toasty.error(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rr = response.body().string();
                Gson gson = new Gson();
                ITEMS.clear();
                ITEMS = gson.fromJson(rr, new TypeToken<List<Clasp>>(){}.getType());

                handler2.sendMessage(new Message());
            }
        });
    }

    public void createClass(String className, Uri uri) throws IOException {
//        LogUtils.d("获取的uri"+uri.getEncodedPath());
        File file = new File(getActivity().getCacheDir()+"/SampleCropImage.png");
//
        MediaType mediaType = MediaType.parse("image/png");
//        RequestBody fileBody = RequestBody.create(mediaType, file);
//        RequestBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM) // 表单类型(必填)
//                .addFormDataPart("image", file.getName(), fileBody)
//                .addFormDataPart("className", className)
//                .build();
//
//        URL uu = new URL(getResources().getString(R.string.network_url)+"/classes/"+ GetUser.getUserId(getContext()));
        Bitmap bitMap = BitmapFactory.decodeFile(getActivity().getCacheDir()+"/SampleCropImage.png");
        String imgString = bitmapToBase64(bitMap);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBodyBuilder.add("className", className);
        formBodyBuilder.add("image", imgString);
        LogUtils.d(getResources().getString(R.string.network_url)+"/classes/"+ GetUser.getUserId(getContext()));
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.network_url)+"/classes/"+ GetUser.getUserId(getContext()))
                .post(formBodyBuilder.build())

                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();

                LogUtils.d(e);
                Toasty.error(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rr = response.body().string();
                LogUtils.d("创建课程结果"+rr);
                handler3.sendMessage(new Message());
                Looper.prepare();

                LogUtils.d(rr);
                Toasty.success(getContext(), "创建成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }


    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Bitmap ImageSizeCompress(Uri uri){
        InputStream Stream = null;
        InputStream inputStream = null;
        try {
            //根据uri获取图片的流
            inputStream = getActivity().getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options的in系列的设置了，injustdecodebouond只解析图片的大小，而不加载到内存中去
            options.inJustDecodeBounds = true;
            //1.如果通过options.outHeight获取图片的宽高，就必须通过decodestream解析同options赋值
            //否则options.outheight获取不到宽高
            BitmapFactory.decodeStream(inputStream,null,options);
            //2.通过 btm.getHeight()获取图片的宽高就不需要1的解析，我这里采取第一张方式
//            Bitmap btm = BitmapFactory.decodeStream(inputStream);
            //以屏幕的宽高进行压缩
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int heightPixels = displayMetrics.heightPixels;
            int widthPixels = displayMetrics.widthPixels;
            //获取图片的宽高
            int outHeight = options.outHeight;
            int outWidth = options.outWidth;
            //heightPixels就是要压缩后的图片高度，宽度也一样
            int a = (int) Math.ceil((outHeight/(float)heightPixels));
            int b = (int) Math.ceil(outWidth/(float)widthPixels);
            //比例计算,一般是图片比较大的情况下进行压缩
            int max = Math.max(a, b);
            if(max > 1){
                options.inSampleSize = max;
            }
            //解析到内存中去
            options.inJustDecodeBounds = false;
//            根据uri重新获取流，inputstream在解析中发生改变了
            Stream = getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(Stream, null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(Stream != null){
                    Stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  null;
    }


    public void joinClass(int classId) throws IOException {
        LogUtils.d(getResources().getString(R.string.network_url)+"/classes/"+ GetUser.getUserId(getContext()) + "/"+classId);
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.network_url)+"/classes/"+ GetUser.getUserId(getContext()) + "/"+classId)
                .put(new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {

                    }
                })
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toasty.error(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String rr = response.body().string();
                LogUtils.d("加入班级返回信息"+rr);
                handler3.sendMessage(new Message());
                Looper.prepare();
                Toasty.success(getContext(), "加入成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

}