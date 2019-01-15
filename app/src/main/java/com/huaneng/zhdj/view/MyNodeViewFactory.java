package com.huaneng.zhdj.view;

import android.view.View;

import com.huaneng.zhdj.R;

import me.texy.treeview.base.BaseNodeViewBinder;
import me.texy.treeview.base.BaseNodeViewFactory;

/**
 * Created by mashenghai on 2018/3/10.
 */

public class MyNodeViewFactory extends BaseNodeViewFactory {

    private int[] colors = {R.color.white, R.color.eee, R.color.ddd};

    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {
//        switch (level) {
//            case 0:
//                return new FirstLevelNodeViewBinder(view);
//            case 1:
//                return new SecondLevelNodeViewBinder(view);
//            case 2:
//                return new ThirdLevelNodeViewBinder(view);
//            default:
//                return new SecondLevelNodeViewBinder(view);
//        }
        return new MyNodeViewBinder(view, level, colors[level % colors.length]);
    }
}
