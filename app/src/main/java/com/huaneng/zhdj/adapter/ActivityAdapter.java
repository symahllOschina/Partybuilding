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
 * 活动Adapter
 */
public class ActivityAdapter extends BaseAdapter {

    private Context mContext;

    private List<Meeting> mList = null;

    public ActivityAdapter(Context context, List<Meeting> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_card, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Meeting entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, "活动名称：", entity.title);
        UIUtils.showText(viewHolder.timeTv, "活动时间：", Utils.emptyIfNull(entity.create_time).split(" ")[0]);
        UIUtils.showText(viewHolder.addressTv, "集合地点：", entity.maddress);
        UIUtils.showText(viewHolder.userNameTv, "发起人：", entity.user_name);
        UIUtils.showText(viewHolder.statusTv, Data.meeting_status.get(entity.condition));

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.addressTv)
        TextView addressTv;
        @ViewInject(R.id.userNameTv)
        TextView userNameTv;
        @ViewInject(R.id.statusTv)
        TextView statusTv;
    }
}
