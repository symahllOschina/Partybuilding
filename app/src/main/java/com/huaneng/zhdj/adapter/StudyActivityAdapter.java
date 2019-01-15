package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.GlideApp;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 学习活动Adapter
 */
public class StudyActivityAdapter extends BaseAdapter {

    private Context mContext;

    private List<News> mList = null;

    public StudyActivityAdapter(Context context, List<News> list) {
        this.mContext = context;
        this.mList = list;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_study_activity, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        News entity = mList.get(position);
        UIUtils.showText(viewHolder.companyTv, entity.company);
        UIUtils.showText(viewHolder.active_statusTv, entity.active_status);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        UIUtils.showText(viewHolder.timeTv, entity.create_time);
        UIUtils.showNum(viewHolder.countTv, "阅读量：", entity.read_count);
//        UIUtils.showText(viewHolder.contentTv, entity.content);
//        RichText.from(entity.content).autoPlay(true).into(viewHolder.contentTv);
//        if (TextUtils.isEmpty(entity.content)) {
//            viewHolder.contentTv.setVisibility(View.INVISIBLE);
//        } else {
//            viewHolder.contentTv.setVisibility(View.VISIBLE);
//            viewHolder.contentTv.loadDataWithBaseURL("about:blank",entity.content, "text/html", "utf-8",null);
//        }
        GlideApp.with(mContext)
                .load(Data.decorateUrl(entity.image))
//                .centerCrop()
                .placeholder(R.drawable.my_bg)
                .into(viewHolder.iconIv);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.companyTv)
        TextView companyTv;
        @ViewInject(R.id.active_statusTv)
        TextView active_statusTv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.countTv)
        TextView countTv;
//        @ViewInject(R.id.contentTv)
//        WebView contentTv;
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
    }
}
