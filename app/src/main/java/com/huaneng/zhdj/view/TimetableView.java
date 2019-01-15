package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.TimetableCellAdapter;
import com.huaneng.zhdj.bean.Timetable;
import com.huaneng.zhdj.bean.TimetableCell;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 课表
 */
public class TimetableView extends LinearLayout {

    @ViewInject(R.id.seasonTv)
    private TextView seasonTv;

    @ViewInject(R.id.listView)
    private ListView listView;

    @ViewInject(R.id.makerTv)
    private TextView makerTv;

    @ViewInject(R.id.timeTv)
    private TextView timeTv;

    TimetableCellAdapter adapter;

    public TimetableView(Context context) {
        this(context, null);
    }

    public TimetableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimetableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimetableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_timetable, this);
        x.view().inject(this);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.layout_timetable_header, null);
        listView.addHeaderView(header);
    }

    public void setData(Timetable timetable) {
        UIUtils.showText(seasonTv, timetable.syllabus);
        UIUtils.showText(makerTv, "制表人：", timetable.username);
        UIUtils.showText(timeTv, "制表时间：", timetable.stime);
        List<TimetableCell> cells = timetable.data;
        adapter = new TimetableCellAdapter(getContext(), cells);
        listView.setAdapter(adapter);
    }
}
