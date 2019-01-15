package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.ScoreRecord;
import com.huaneng.zhdj.utils.DateUtils;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 工作汇报Adapter
 */
public class ScoreRecordAdapter extends BaseAdapter {

    private Context mContext;

    private List<ScoreRecord> mList = null;

    public ScoreRecordAdapter(Context context, List<ScoreRecord> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_score_record, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        ScoreRecord entity = mList.get(position);
        UIUtils.showText(viewHolder.timeTv, DateUtils.getDate(entity.create_time));
        UIUtils.showText(viewHolder.descriptionTv, entity.description);
        UIUtils.showText(viewHolder.numTv, entity.num);

        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.descriptionTv)
        TextView descriptionTv;
        @ViewInject(R.id.numTv)
        TextView numTv;
    }
}
