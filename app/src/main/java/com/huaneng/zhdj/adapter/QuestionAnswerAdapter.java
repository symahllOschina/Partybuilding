package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.QuestionAnswer;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 问题批复Adapter
 */
public class QuestionAnswerAdapter extends BaseAdapter {

    private Context mContext;

    private List<QuestionAnswer> mList = null;

    public QuestionAnswerAdapter(Context context, List<QuestionAnswer> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_qa, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        QuestionAnswer entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        UIUtils.showText(viewHolder.timeTv, entity.create_time);
        if ("1".equals(entity.status)) {
            viewHolder.statusTv.setText("已读搁置");
        } else if("3".equals(entity.status)) {
            viewHolder.statusTv.setText("弃置");
        } else {
            viewHolder.statusTv.setText(null);
        }
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.statusTv)
        TextView statusTv;
    }
}
