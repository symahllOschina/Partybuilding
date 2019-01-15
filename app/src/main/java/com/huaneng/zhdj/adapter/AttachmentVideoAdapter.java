package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Attachment;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 附件-视频Adapter
 */
public class AttachmentVideoAdapter extends BaseAdapter {

    private Context mContext;

    private List<Attachment> mList = null;

    public AttachmentVideoAdapter(Context context, List<Attachment> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_attachment_video, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Attachment entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
    }
}
