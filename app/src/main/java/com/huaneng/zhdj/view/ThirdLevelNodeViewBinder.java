package com.huaneng.zhdj.view;

import android.view.View;
import android.widget.TextView;

import com.huaneng.zhdj.R;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

/**
 * 树形结构--3级节点
 */
public class ThirdLevelNodeViewBinder extends BaseNodeViewBinder {

    TextView textView;

    public ThirdLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.node_name_view);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_third_level;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        textView.setText(treeNode.getValue().toString());
    }
}
