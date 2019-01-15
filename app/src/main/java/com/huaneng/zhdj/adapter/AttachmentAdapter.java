package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Attachment;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 学习资料-附件-Adapter
 */
public class AttachmentAdapter extends BaseAdapter {

    private Context mContext;

    private List<Attachment> mList = null;

    public AttachmentAdapter(Context context, List<Attachment> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_attachment, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Attachment entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        UIUtils.showText(viewHolder.typeTv, entity.souce_type);
        UIUtils.showText(viewHolder.timeTv, entity.create_time);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.typeTv)
        TextView typeTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
    }
}
