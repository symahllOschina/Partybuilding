package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.TimetableCell;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 课表项Adapter
 */
public class TimetableCellAdapter extends BaseAdapter {

    private Context mContext;

    private List<TimetableCell> mList = null;

    public TimetableCellAdapter(Context context, List<TimetableCell> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_timetable_cell, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        TimetableCell entity = mList.get(position);
        UIUtils.showText(viewHolder.monthTv, entity.month);
        UIUtils.showText(viewHolder.courseTv, entity.curriculum);
        UIUtils.showText(viewHolder.teacherTv, entity.lecturer);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.monthTv)
        TextView monthTv;
        @ViewInject(R.id.courseTv)
        TextView courseTv;
        @ViewInject(R.id.teacherTv)
        TextView teacherTv;
    }
}
