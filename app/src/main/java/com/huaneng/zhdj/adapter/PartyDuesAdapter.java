package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.PartyDues;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 党费Adapter
 */
public class PartyDuesAdapter extends BaseAdapter {

    private Context mContext;

    private List<PartyDues> mList = null;

    public PartyDuesAdapter(Context context, List<PartyDues> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_party_dues, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        PartyDues entity = mList.get(position);
        UIUtils.showText(viewHolder.duesTv, entity.membership_dues);
        UIUtils.showText(viewHolder.timeTv, entity.mtime);
        UIUtils.showText(viewHolder.scopeTv, entity.extent);
        UIUtils.showText(viewHolder.verifyNameTv, entity.verify_name);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.duesTv)
        TextView duesTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.scopeTv)
        TextView scopeTv;
        @ViewInject(R.id.verifyNameTv)
        TextView verifyNameTv;
    }
}
