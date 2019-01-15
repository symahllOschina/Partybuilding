package com.huaneng.zhdj.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.news.NewsActivity;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 新闻中心Adapter
 */
public class NewsCenterAdapter extends BaseAdapter {

    private Context mContext;

    private List<News> mList = null;

    private View.OnClickListener onClickListener;

    public NewsCenterAdapter(Context context, List<News> list) {
        this.mContext = context;
        this.mList = list;
        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int index = Integer.valueOf(view.getTag().toString());
                Intent intent = new Intent(mContext, NewsActivity.class);
                intent.putExtra("news", mList.get(index));
                mContext.startActivity(intent);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_center, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        News entity = mList.get(position);
        UIUtils.showText(viewHolder.titleTv, entity.title);
        UIUtils.showNum(viewHolder.countTv, "阅读:", Utils.zeroIfNull(entity.read_count));
        UIUtils.showText(viewHolder.timeTv, entity.create_time);

        NewsType newsType = getNewsType(entity.catid);
        viewHolder.iconIv.setBackgroundResource(newsType.bg);
//        UIUtils.showText(viewHolder.typeTv, entity.catname);
//        viewHolder.typeTv.setCompoundDrawablesWithIntrinsicBounds(null, newsType.icon, null, null);

        viewHolder.iconBox.setTag(position);
        viewHolder.iconBox.setOnClickListener(onClickListener);
        return convertView;
    }

    private class NewsType {
        public int bg;
        public Drawable icon;
        public String title;

        public NewsType(int bg, int icon, String title) {
            this.bg = bg;
            this.icon = mContext.getResources().getDrawable(icon);
            this.title = title;
        }
    }

    public NewsType getNewsType(int catid) {
        // 7 => '热点要闻', 8 => '党建动态', 9 => '通知公告', 10 => '一线传真'
        NewsType newsType = null;
        switch (catid) {
            case 7:
                newsType = new NewsType(R.drawable.redianyaowen, R.drawable.ic_news, "热点要闻");
                break;
            case 8:
                newsType = new NewsType(R.drawable.dangjiandongtai, R.drawable.ic_party_logo, "党建动态");
                break;
            case 9:
                newsType = new NewsType(R.drawable.tongzhigonggao, R.drawable.ic_notice_white, "通知公告");
                break;
            case 10:
                newsType = new NewsType(R.drawable.yixianchuanzhen, R.drawable.ic_fax, "一线传真");
                break;
            default:
                newsType = new NewsType(R.drawable.redianyaowen, R.drawable.ic_news, "智慧党建");
        }
        return newsType;
    }

    private class ViewHolder {
        @ViewInject(R.id.iconBox)
        FrameLayout iconBox;
        @ViewInject(R.id.iconIv)
        ImageView iconIv;
        @ViewInject(R.id.typeTv)
        TextView typeTv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.timeTv)
        TextView timeTv;
        @ViewInject(R.id.countTv)
        TextView countTv;
    }
}
