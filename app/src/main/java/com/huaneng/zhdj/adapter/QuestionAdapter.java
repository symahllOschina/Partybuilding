package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.TestQuestion;
import com.huaneng.zhdj.study.ExamActivity;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 试题dapter
 */
public class QuestionAdapter extends BaseAdapter {

    private Context mContext;

    private List<TestQuestion> mList = null;

    private View.OnClickListener onClickListener;

    public QuestionAdapter(final Context context, List<TestQuestion> list) {
        this.mContext = context;
        this.mList = list;
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = Integer.valueOf(view.getTag().toString());
                Intent intent = new Intent(context, ExamActivity.class);
                intent.putExtra("question", mList.get(pos));
                context.startActivity(intent);
            }
        };
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_question, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TestQuestion entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        if(entity.isJoin()) {
            viewHolder.noJoinTv.setVisibility(View.GONE);
            viewHolder.maxScoreTv.setVisibility(View.VISIBLE);
            viewHolder.lastScoreTv.setVisibility(View.VISIBLE);
            UIUtils.showText(viewHolder.maxScoreTv, "最高得分：" , entity.max);
            UIUtils.showText(viewHolder.lastScoreTv, "上次得分：", entity.last);
        } else {
            viewHolder.noJoinTv.setVisibility(View.VISIBLE);
            viewHolder.maxScoreTv.setVisibility(View.GONE);
            viewHolder.lastScoreTv.setVisibility(View.GONE);
        }
        viewHolder.answerBtn.setTag(position);
        viewHolder.answerBtn.setOnClickListener(onClickListener);
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.noJoinTv)
        TextView noJoinTv;
        @ViewInject(R.id.maxScoreTv)
        TextView maxScoreTv;
        @ViewInject(R.id.lastScoreTv)
        TextView lastScoreTv;
        @ViewInject(R.id.answerBtn)
        Button answerBtn;
    }
}
