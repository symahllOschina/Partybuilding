package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Message;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 工作汇报Adapter
 */
public class MessageAdapter extends BaseAdapter {

    private Context mContext;

    private List<Message> mList = null;

    int primaryTextColor;
    int gray_999;

    public MessageAdapter(Context context, List<Message> list) {
        this.mContext = context;
        this.mList = list;
        primaryTextColor = mContext.getResources().getColor(R.color.primaryTextColor);
        gray_999 = mContext.getResources().getColor(R.color.gray_999);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Message entity = mList.get(position);
        if (entity.isRead()) {
            viewHolder.titleTv.setTextColor(gray_999);
        } else {
            viewHolder.titleTv.setTextColor(primaryTextColor);
        }
        UIUtils.showText(viewHolder.titleTv, entity.message);
        UIUtils.showText(viewHolder.timeTv, entity.create_time);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
    }
}
