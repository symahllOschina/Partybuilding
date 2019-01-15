package com.huaneng.zhdj.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.App;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.GlideApp;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Comment;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.GlideCircleTransform;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 评论Adapter
 */
public class CommentAdapter extends BaseAdapter {

    private BaseActivity mContext;

    private List<Comment> mList = null;

    private View.OnClickListener onClickListener;

    // 举报的url
    private String reportUrl;

    public CommentAdapter(BaseActivity context, List<Comment> list, String reportUrl) {
        this.mContext = context;
        this.mList = list;
        this.reportUrl = reportUrl;
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = Integer.valueOf(v.getTag().toString());
                Comment comment = mList.get(pos);
                commentReport(comment.id);
            }
        };
    }

    // 时代先锋-作品展示-评论举报
    public void commentReport(final String id) {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("确定要举报这条评论吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        report(id);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    public void report(String id) {
        HTTP.service.post(reportUrl, MapParam.me().p("id", id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(mContext){

                    @Override
                    public void onSuccess(Response response) {
                        App.toast("举报成功.");
                    }

                    @Override
                    public void onWrong(String msg) {
                        App.toast("举报失败：" + msg);
                    }
                });
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Comment entity = mList.get(position);
        UIUtils.showText(viewHolder.nameTv, entity.username);
        UIUtils.showText(viewHolder.contentTv, entity.content);
        UIUtils.showText(viewHolder.timeTv, entity.create_time);
        GlideApp.with(mContext)
                .load(entity.image)
                .centerCrop()
                .placeholder(R.drawable.ic_head)
                .transform(new GlideCircleTransform(mContext))
                .into(viewHolder.iconIv);

        viewHolder.reportIv.setTag(position);
        viewHolder.reportIv.setOnClickListener(onClickListener);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.reportIv)
        ImageView reportIv;
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
        @ViewInject(R.id.nameTv)
        TextView nameTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.contentTv)
        TextView contentTv;
    }
}
