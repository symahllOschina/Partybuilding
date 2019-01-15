package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import org.jsoup.helper.StringUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 新闻相关信息
 */
public class NewsInfoView extends LinearLayout {

    @ViewInject(R.id.rootView)
    private View rootView;
    @ViewInject(R.id.clickCount)
    private TextView clickCountTv;
    @ViewInject(R.id.author)
    private TextView authorTv;
    @ViewInject(R.id.from)
    private TextView fromTv;
    @ViewInject(R.id.time)
    private TextView timeTv;

    public NewsInfoView(Context context) {
        this(context, null);
    }

    public NewsInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NewsInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, R.layout.layout_news_info, this);
        x.view().inject(this);

    }

    // 设置背景色
    public void setBgColor(int color) {
        rootView.setBackgroundColor(getResources().getColor(color));
    }

    // 点击量
    public void setClickCount(String count) {
        if (TextUtils.isEmpty(count) || "null".equals(count) || "0".equals(count)) {
            count = "1";
        }
        this.clickCountTv.setVisibility(VISIBLE);
        UIUtils.showText(this.clickCountTv, "点击数：", count);

    }

    // 作者
    public void setAuthor(String author) {
        setAuthor("作者：", author);
    }

    public void setAuthor(String authorLabel, String author) {
        if (TextUtils.isEmpty(authorLabel)) {
            authorLabel = "作者：";
        }
        this.authorTv.setVisibility(VISIBLE);
        UIUtils.showText(this.authorTv, authorLabel, author);
    }

    // 来源
    public void setFrom(String from) {
        setFrom("来源：", from);
    }

    public void setFrom(String prefix, String from) {
//        this.fromTv.setVisibility(VISIBLE);
        UIUtils.showText(this.fromTv, prefix, from);
    }

    // 时间
    public void setTime(String time) {
        setTime("时间：", time);
    }
    public void setTime(String prefix, String time) {
        this.timeTv.setVisibility(VISIBLE);
        // 只显示年月日
        UIUtils.showText(this.timeTv, prefix, Utils.emptyIfNull(time).split(" ")[0]);
    }

}
