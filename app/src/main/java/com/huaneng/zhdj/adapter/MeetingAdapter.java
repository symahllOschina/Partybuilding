package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Meeting;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 会议Adapter
 */
public class MeetingAdapter extends BaseAdapter {

    private Context mContext;

    private List<Meeting> mList = null;

    public MeetingAdapter(Context context, List<Meeting> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_meeting_card, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Meeting entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, "会议名称：", entity.title);
        UIUtils.showText(viewHolder.userNameTv, "主会人：", entity.lecturer);
        UIUtils.showText(viewHolder.addressTv, "会议地点：", entity.maddress);
        UIUtils.showText(viewHolder.timeTv, "会议时间：", entity.mdate);
        UIUtils.showText(viewHolder.statusTv, entity.getStatusName());

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.userNameTv)
        TextView userNameTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.addressTv)
        TextView addressTv;
        @ViewInject(R.id.statusTv)
        TextView statusTv;
    }
}
