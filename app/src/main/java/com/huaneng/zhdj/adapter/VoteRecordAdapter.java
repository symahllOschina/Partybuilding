package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.VoteRecord;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 投票记录Adapter
 */
public class VoteRecordAdapter extends BaseAdapter {

    private Context mContext;

    private List<VoteRecord> mList = null;

    public VoteRecordAdapter(Context context, List<VoteRecord> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_vote_record, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        VoteRecord entity = mList.get(position);
        UIUtils.showText(viewHolder.nameTv, entity.name);
        UIUtils.showText(viewHolder.optionTv, entity.option);
        UIUtils.showText(viewHolder.timeTv, entity.time);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.nameTv)
        TextView nameTv;
        @ViewInject(R.id.optionTv)
        TextView optionTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
    }
}
