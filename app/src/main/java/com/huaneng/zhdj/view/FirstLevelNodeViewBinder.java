package com.huaneng.zhdj.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaneng.zhdj.App;
import com.huaneng.zhdj.R;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

/**
 * 树形结构--1级节点
 */
public class FirstLevelNodeViewBinder extends BaseNodeViewBinder {

    TextView textView;
    ImageView viewBtn;

    public FirstLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.node_name_view);
        viewBtn = (ImageView) itemView.findViewById(R.id.viewBtn);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_first_level;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        String status = null;
        if (treeNode.isExpanded()) {
            status = "展开";
        } else {
            status = "收缩";
        }
        textView.setText(status + treeNode.getValue().toString());
        viewBtn.setTag(treeNode);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TreeNode node = (TreeNode)view.getTag();
                App.toast(node.getValue().toString());
            }
        });
    }
}
