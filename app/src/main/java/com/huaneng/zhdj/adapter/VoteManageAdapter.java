package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.supervise.VoteAmendActivity;
import com.huaneng.zhdj.supervise.VoteRecordActivity;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 投票管理Adapter
 */
public class VoteManageAdapter extends BaseAdapter {

    private Context mContext;

    private List<Vote> mList = null;

    View.OnClickListener onClickListener;

    public VoteManageAdapter(Context context, List<Vote> list) {
        this.mContext = context;
        this.mList = list;
        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int index = Integer.valueOf(view.getTag(R.id.titleTv).toString());
                Vote vote = mList.get(index);
                switch (view.getId()) {
                    case R.id.delBtn:
                        break;
                    case R.id.viewBtn:
                        voteRecord(vote);
                        break;
                    case R.id.updBtn:
                        break;
                    case R.id.updResultBtn:
                        resultAmend(vote);
                        break;
                }
            }
        };
    }

    private void voteRecord(Vote vote) {
        Intent intent = new Intent(mContext, VoteRecordActivity.class);
        intent.putExtra("vote", vote);
        mContext.startActivity(intent);
    }

    private void resultAmend(Vote vote) {
        Intent intent = new Intent(mContext, VoteAmendActivity.class);
        intent.putExtra("vote", vote);
        mContext.startActivity(intent);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_vote_manage, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Vote entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);

        viewHolder.delBtn.setTag(R.id.titleTv, position);
        viewHolder.viewBtn.setTag(R.id.titleTv, position);
        viewHolder.updBtn.setTag(R.id.titleTv, position);
        viewHolder.updResultBtn.setTag(R.id.titleTv, position);
        viewHolder.delBtn.setOnClickListener(onClickListener);
        viewHolder.viewBtn.setOnClickListener(onClickListener);
        viewHolder.updBtn.setOnClickListener(onClickListener);
        viewHolder.updResultBtn.setOnClickListener(onClickListener);
        return convertView;
    }

    private class ViewHolder {
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.delBtn)
        TextView delBtn;
        @ViewInject(R.id.viewBtn)
        TextView viewBtn;
        @ViewInject(R.id.updBtn)
        TextView updBtn;
        @ViewInject(R.id.updResultBtn)
        TextView updResultBtn;
    }
}
