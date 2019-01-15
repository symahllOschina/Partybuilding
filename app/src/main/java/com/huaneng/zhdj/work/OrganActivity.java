package com.huaneng.zhdj.work;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Organ;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VotePager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.view.EmptyView;
import com.huaneng.zhdj.view.MyNodeViewFactory;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.texy.treeview.TreeNode;
import me.texy.treeview.TreeView;

/**
 * 组织架构
 */
@ContentView(R.layout.activity_organ)
public class OrganActivity extends BaseActivity {

    @ViewInject(R.id.rootView)
    LinearLayout rootView;
    TreeNode root;

    @ViewInject(R.id.emptyView)
    EmptyView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("组织架构");

        root = TreeNode.root();
        root.setExpanded(true);
        getList();
    }

    private void buildTree(List<Organ> list) {
        buildChildrenNode(root, list);
        View treeView = new TreeView(root, this, new MyNodeViewFactory()).getView();
        treeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(treeView);
    }

    private void buildChildrenNode(TreeNode pNode, List<Organ> list) {
        if (list != null && !list.isEmpty()) {
            for (Organ organ: list) {
                TreeNode treeNode = new TreeNode(organ);
                treeNode.setLevel(organ.level);
                pNode.addChild(treeNode);
                treeNode.setParent(pNode);
                if (pNode == root) {
                    treeNode.setExpanded(true);
                }
                if (organ._child != null && !organ._child.isEmpty()) {
                    buildChildrenNode(treeNode, organ._child);
                }
            }
        }

    }


    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("company/tree")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        List<Organ> list = response.getEntityList(Organ.class);
                        if (list == null || list.isEmpty()) {
                            emptyView.empty();
                        } else {
                            buildTree(list);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        emptyView.fail();
                        toast("获取组织机构失败：" + msg);
                    }
                });
    }
}
