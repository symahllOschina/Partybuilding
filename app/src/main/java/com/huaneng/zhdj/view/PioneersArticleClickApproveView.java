package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 时代先锋文章点击量、点赞View
 */
public class PioneersArticleClickApproveView extends LinearLayout {

    @ViewInject(R.id.clickCount)
    protected TextView clickCountTv;
    @ViewInject(R.id.approveView)
    private ApproveView approveView;

    public void init(BaseActivity ctx, String articleId) {
        approveView.init(ctx, articleId, new PioneersArticleApprove());
    }

    public PioneersArticleClickApproveView(Context context) {
        super(context);
    }

    public PioneersArticleClickApproveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PioneersArticleClickApproveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PioneersArticleClickApproveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_click_approve_count, this);
        x.view().inject(this);
    }

    // 点击量
    public void setClickCount(Object count) {
        UIUtils.showNum(clickCountTv, count);
    }

    // 点赞数量
    public void setApproveCount(Object count) {
        approveView.setApproveCount(count);
    }
}
