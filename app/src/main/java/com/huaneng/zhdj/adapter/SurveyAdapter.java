package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Survey;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 民意调查Adapter
 */
public class SurveyAdapter extends BaseAdapter {

    private Context mContext;

    private List<Survey> mList = null;

    public SurveyAdapter(Context context, List<Survey> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_survey, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Survey entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, "问卷标题：", entity.title);
        UIUtils.showText(viewHolder.timeTv, "结束时间：", entity.create_time);
        UIUtils.showText(viewHolder.questionCountTv, "题目数量：", entity.number);
        UIUtils.showText(viewHolder.useCountTv, "使用次数：", entity.count);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.questionCountTv)
        TextView questionCountTv;
        @ViewInject(R.id.useCountTv)
        TextView useCountTv;
    }
}
