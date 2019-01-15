package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 民众投票Adapter
 */
public class VoteAdapter extends BaseAdapter {

    private Context mContext;

    private List<Vote> mList = null;

    public VoteAdapter(Context context, List<Vote> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_vote, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Vote entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, "投票主题：", entity.title);
        UIUtils.showText(viewHolder.timeTv, "结束时间：", entity.end_time);
        UIUtils.showText(viewHolder.participateCountTv, "参与人数：", entity.total);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.participateCountTv)
        TextView participateCountTv;
    }
}
