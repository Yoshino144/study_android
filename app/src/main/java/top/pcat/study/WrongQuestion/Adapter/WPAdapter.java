package top.pcat.study.WrongQuestion.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.pcat.study.Pojo.UserProblemData;
import top.pcat.study.Pojo.WrongChapter;
import top.pcat.study.R;
import top.pcat.study.Utils.DisplayUtil;
import top.pcat.study.Utils.GetUser;
import top.pcat.study.WrongQuestion.Pojo.WrongProblem;
import top.pcat.study.WrongQuestion.WProblemActivity;

public class WPAdapter extends RecyclerView.Adapter<WPAdapter.ViewHolder> {

    private List<WrongProblem> mWChapterList;
    private Context context;
    private boolean ff;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView problemContent;
        TextView text_A;
        TextView text_B;
        TextView text_C;
        TextView text_D;
        LinearLayout AAA;
        LinearLayout BBB;
        LinearLayout CCC;
        LinearLayout DDD;
        LinearLayout wrong_item;
        ImageView image_A;
        ImageView image_B;
        ImageView image_C;
        ImageView image_D;
        LinearLayout jiexi;
        TextView jiexitext;
        LinearLayout wrongaa;

        public ViewHolder(View view) {
            super(view);
            problemContent = view.findViewById(R.id.problemContent);
            text_A = view.findViewById(R.id.text_AA);
            text_B = view.findViewById(R.id.text_BB);
            text_C = view.findViewById(R.id.text_CC);
            text_D = view.findViewById(R.id.text_DD);
            AAA = view.findViewById(R.id.AAA);
            BBB = view.findViewById(R.id.BBB);
            CCC = view.findViewById(R.id.CCC);
            DDD = view.findViewById(R.id.DDD);
            wrong_item = view.findViewById(R.id.wrong_item);
            jiexi = view.findViewById(R.id.jiexi);
            image_A = view.findViewById(R.id.image_A);
            image_B = view.findViewById(R.id.image_B);
            image_C = view.findViewById(R.id.image_C);
            image_D = view.findViewById(R.id.image_D);
            wrongaa = view.findViewById(R.id.wrongaa);
            jiexitext = view.findViewById(R.id.jiexitext);
        }

    }

    public WPAdapter(List<WrongProblem> wChapterList,boolean ff) {
        mWChapterList = wChapterList;
        this.ff = ff;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_wp_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        context = view.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        WrongProblem wrongProblem = mWChapterList.get(position);

        if(ff){
            if (position == 0) {
                RecyclerView.LayoutParams layoutParams =
                        new RecyclerView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams = (RecyclerView.LayoutParams) holder.wrong_item.getLayoutParams();
                layoutParams.setMargins(DisplayUtil.dip2px(context, 7), DisplayUtil.dip2px(context, 70), DisplayUtil.dip2px(context, 7), 0);//left,top,right,bottom
                holder.wrong_item.setLayoutParams(layoutParams);
            }
        }

        holder.problemContent.setText(position+1+". "+wrongProblem.getProblemContent());
        holder.text_A.setText(wrongProblem.getA());
        LogUtils.d("选项"+wrongProblem.getA());
        holder.text_B.setText(wrongProblem.getB());
        holder.text_C.setText(wrongProblem.getC());
        holder.text_D.setText(wrongProblem.getD());
        holder.jiexitext.setText(wrongProblem.getProblemAnalysis());

        holder.AAA.setOnClickListener(v->{
            checked("A","A".equals(wrongProblem.getProblemAnswer()),holder,wrongProblem.getProblemAnswer(),wrongProblem);
        });
        holder.BBB.setOnClickListener(v->{
            checked("B","B".equals(wrongProblem.getProblemAnswer()),holder,wrongProblem.getProblemAnswer(),wrongProblem);
        });
        holder.CCC.setOnClickListener(v->{
            checked("C","C".equals(wrongProblem.getProblemAnswer()),holder,wrongProblem.getProblemAnswer(),wrongProblem);
        });
        holder.DDD.setOnClickListener(v->{
            checked("D","D".equals(wrongProblem.getProblemAnswer()),holder,wrongProblem.getProblemAnswer(),wrongProblem);
        });
        holder.jiexi.setOnClickListener(v->{
            //checked("D","D".equals(wrongProblem.getProblemAnswer()),holder,wrongProblem.getProblemAnswer());
            holder.wrongaa.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public int getItemCount() {
        return mWChapterList.size();
    }

    public void checked(String xx,boolean trueFlag, ViewHolder holder,String answer,WrongProblem wrongProblem){
        UserProblemData userProblemData = new UserProblemData();
        if(trueFlag){
            setTrue(holder,xx);
            userProblemData.setTrueFlag(1);
        }else{
            setFlase(holder,xx);
            setTrue(holder,answer);
            userProblemData.setTrueFlag(0);
        }
        holder.jiexi.setVisibility(View.VISIBLE);
        userProblemData.setProblemId(wrongProblem.getProblemId());
        userProblemData.setUserId(GetUser.getUserId(context));
        userProblemData.setAnswer(xx);
        userProblemData.setSubjectId(wrongProblem.getSubjectId());
        userProblemData.setChapterId(wrongProblem.getChapterId());
        sendRes(userProblemData);
    }

    public void setTrue(ViewHolder holder,String xx){

        switch (xx) {
            case "A" -> {
                holder.image_A.setImageResource(R.drawable.ttrue);
                holder.text_A.setTextColor(Color.parseColor("#1cabfa"));
            }
            case "B" -> {
                holder.image_B.setImageResource(R.drawable.ttrue);
                holder.text_B.setTextColor(Color.parseColor("#1cabfa"));
            }
            case "C" -> {
                holder.image_C.setImageResource(R.drawable.ttrue);
                holder.text_C.setTextColor(Color.parseColor("#1cabfa"));
            }
            case "D" -> {
                holder.image_D.setImageResource(R.drawable.ttrue);
                holder.text_D.setTextColor(Color.parseColor("#1cabfa"));
            }
        }
    }

    public void setFlase(ViewHolder holder,String xx){
        switch (xx) {
            case "A" -> {
                holder.image_A.setImageResource(R.drawable.wrong);
                holder.text_A.setTextColor(Color.parseColor("#fd6767"));
            }
            case "B" -> {
                holder.image_B.setImageResource(R.drawable.wrong);
                holder.text_B.setTextColor(Color.parseColor("#fd6767"));
            }
            case "C" -> {
                holder.image_C.setImageResource(R.drawable.wrong);
                holder.text_C.setTextColor(Color.parseColor("#fd6767"));
            }
            case "D" -> {
                holder.image_D.setImageResource(R.drawable.wrong);
                holder.text_D.setTextColor(Color.parseColor("#fd6767"));
            }
        }
    }

    private void sendRes(UserProblemData userProblemData) {

        LogUtils.d("上传做题结果");
        new Thread(() -> {
            try {

                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder.add("subject_id", String.valueOf(userProblemData.getSubjectId()));
                formBodyBuilder.add("chapter_id", String.valueOf(userProblemData.getChapterId()));
                formBodyBuilder.add("problem_id", String.valueOf(userProblemData.getProblemId()));
                formBodyBuilder.add("answer", userProblemData.getAnswer());
                formBodyBuilder.add("true_flag", String.valueOf(userProblemData.getTrueFlag()));

                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                Request request = new Request.Builder()
                        .url(context.getResources().getString(R.string.network_url)+"/userAnswers/" + GetUser.getUserId(context))
                        .post(formBodyBuilder.build())
                        .build();//创建一个Request对象
                Response response = client.newCall(request).execute(); //发送请求获取返回数据
                String responseData = response.body().string(); //处理返回的数据

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}