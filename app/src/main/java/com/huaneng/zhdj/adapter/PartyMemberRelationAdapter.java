package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.PartyMemberRelation;
import com.huaneng.zhdj.utils.DateUtils;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 转入转出记录Adapter
 */
public class PartyMemberRelationAdapter extends BaseAdapter {

    private Context mContext;

    private List<PartyMemberRelation> mList = null;

    public PartyMemberRelationAdapter(Context context, List<PartyMemberRelation> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_party_member_relation, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        PartyMemberRelation entity = mList.get(position);
        UIUtils.showText(viewHolder.company1Tv, entity.company1);
        UIUtils.showText(viewHolder.company2Tv, entity.company2);
        UIUtils.showText(viewHolder.timeTv, DateUtils.getDate(entity.create_time));
        UIUtils.showText(viewHolder.descriptionTv, entity.description);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.company1Tv)
        TextView company1Tv;
        @ViewInject(R.id.company2Tv)
        TextView company2Tv;
        @ViewInject(R.id.descriptionTv)
        TextView descriptionTv;
    }
}
