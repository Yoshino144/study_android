package top.pcat.study.TabChat.Fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.rong.imkit.utils.RouteUtils;
import io.rong.imlib.model.Conversation;
import top.pcat.study.Pojo.Clasp;
import top.pcat.study.R;
import top.pcat.study.databinding.FragmentClassItemBinding;


import java.util.List;


public class MyClassItemRecyclerViewAdapter extends RecyclerView.Adapter<MyClassItemRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private final List<Clasp> mValues;

    public MyClassItemRecyclerViewAdapter(List<Clasp> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(FragmentClassItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.className.setText(mValues.get(position).getClassName());
        holder.mContentView.setText("Id: "+mValues.get(position).getClassId());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.groupimg);
        Glide.with(context).load("https://air.pcat.top/users/infos/group.photo/g"+mValues.get(position).getClassId()+".png").apply(options).into(holder.dynamic_headImg);
        //Glide.with(context).load("https://air.pcat.top/users/infos/profile.photo/08f4528245764510aef1c12c86625bcb.png").into(holder.dynamic_headImg);
        holder.linearLayout.setOnClickListener(v -> {
            LogUtils.d("打开班级"+mValues.get(position).getClassName());
            Log.d("打开班级", mValues.get(position).getClassName());
            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(mValues.get(position).getClassName())) {
                bundle.putString(RouteUtils.TITLE, mValues.get(position).getClassName()); //会话页面标题
            }
            RouteUtils.routeToConversationActivity(context, Conversation.ConversationType.GROUP, mValues.get(position).getClassId(),bundle);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView className;
        public final TextView mContentView;
        public Clasp mItem;
        public LinearLayout linearLayout;
        public CircleImageView dynamic_headImg;

        public ViewHolder(FragmentClassItemBinding binding) {
            super(binding.getRoot());
            className = binding.className;
            mContentView = binding.content;
            linearLayout = binding.ci;
            dynamic_headImg=binding.iii;
        }
        
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}