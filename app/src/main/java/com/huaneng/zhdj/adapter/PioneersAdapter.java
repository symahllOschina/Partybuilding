package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.GlideApp;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.utils.GlideCircleTransform;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 新闻Adapter
 */
public class PioneersAdapter extends BaseAdapter {

    private Context mContext;

    private List<News> list = null;

    public PioneersAdapter(Context context, List<News> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pioneers, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        News entity = list.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        UIUtils.showText(viewHolder.typeTv, entity.catname);
        GlideApp.with(mContext)
                .load(Data.decorateUrl(entity.image))
                .centerCrop()
                .placeholder(R.drawable.default_img_v)
                .into(viewHolder.iconIv);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
        @ViewInject(R.id.typeTv)
        TextView typeTv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
    }
}
