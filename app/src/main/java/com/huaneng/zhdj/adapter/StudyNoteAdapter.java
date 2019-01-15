package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.StudyNote;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 学习笔记Adapter
 */
public class StudyNoteAdapter extends BaseAdapter {

    private Context mContext;

    private List<StudyNote> mList = null;

    public StudyNoteAdapter(Context context, List<StudyNote> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_study_note, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        StudyNote entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
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
