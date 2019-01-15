package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.GlideApp;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.news.NewsActivity;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 新闻Adapter
 */
public class NewsAdapter extends BaseAdapter {

    private Context mContext;

    private List<News> mList = null;

    public NewsAdapter(Context context, List<News> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        News entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        UIUtils.showNum(viewHolder.countTv, "阅读:", Utils.zeroIfNull(entity.read_count));
        UIUtils.showText(viewHolder.timeTv, entity.create_time);
        GlideApp.with(mContext)
                .load(entity.image)
                .centerCrop()
                .placeholder(R.drawable.default_img)
                .into(viewHolder.iconIv);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.countTv)
        TextView countTv;
    }
}
