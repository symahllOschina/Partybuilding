package com.huaneng.zhdj.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaneng.zhdj.App;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Organ;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

/**
 * 树形结构--x级节点
 */
public class MyNodeViewBinder extends BaseNodeViewBinder {

    TextView textView;
    ImageView viewBtn;
    View itemView;
    int level;
    int bgColor;
    Drawable arrow_up;
    Drawable arrow_down;

    public MyNodeViewBinder(View itemView, int level, int bgColor) {
        super(itemView);
        this.itemView = itemView;
        this.level = level;
        this.bgColor = bgColor;
        if (arrow_up == null){
            arrow_up = itemView.getResources().getDrawable(R.drawable.ic_arrow_up);
        }
        if (arrow_down == null){
            arrow_down = itemView.getResources().getDrawable(R.drawable.ic_arrow_down);
        }
        textView = (TextView) itemView.findViewById(R.id.node_name_view);
        viewBtn = (ImageView) itemView.findViewById(R.id.viewBtn);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_node;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        itemView.setBackgroundColor(itemView.getResources().getColor(bgColor));
        textView.setPadding(level*40,0,0,0);

        initNodeTitle(treeNode);
        viewBtn.setTag(treeNode.getValue());
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Organ organ = (Organ)view.getTag();
                showNode(organ);
            }
        });
    }

    private void showNode(Organ organ) {
        new AlertDialog.Builder(itemView.getContext())
                .setTitle(organ.name)
                .setMessage(organ.getDisplay())
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void initNodeTitle(TreeNode treeNode) {
        Organ organ = (Organ)treeNode.getValue();
        textView.setText(organ.name);
//        if (organ._child == null || organ._child.isEmpty()) {
//            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//        }
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {
        if (expand) {
            textView.setCompoundDrawablesWithIntrinsicBounds(arrow_up, null, null, null);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(arrow_down, null, null, null);
        }
        super.onNodeToggled(treeNode, expand);
    }
}
