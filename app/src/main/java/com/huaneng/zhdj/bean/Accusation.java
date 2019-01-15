package com.huaneng.zhdj.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 我要举报：问题类别、问题细类
 */
public class Accusation {

    public String id;
    public String title;

    public Accusation(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public List<Accusation> children;

    public Accusation addChild(String id, String title) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(new Accusation(id, title));
        return this;
    }

    // 问题类别
    public static final String[] types = {"违反政治纪律行为", "违反组织纪律行为", "违反廉洁纪律行为", "违反群众纪律行为", "违反工作纪律行为", "违反生活纪律行为", "涉法行为"};

    public final static List<Accusation> typeList = new ArrayList<>();
    static {
        for (int i=1; i<= types.length; i++) {
            typeList.add(new Accusation("" + i, types[i-1]));
        }
        // 问题细类
        typeList.get(0).addChild("54", "公开发表危害党的言论");
        typeList.get(0).addChild("55", "参加反对党和政府的活动或组织");
        typeList.get(0).addChild("56", "在党内搞团团伙伙");
        typeList.get(0).addChild("57", "妨碍党和国家方针政策实施");
        typeList.get(0).addChild("58", "对抗组织审查");
        typeList.get(0).addChild("59", "组织参加迷信活动");
        typeList.get(0).addChild("60", "叛逃及涉外活动中损害党和国家利益");
        typeList.get(0).addChild("61", "无原则一团和气和违反政治规矩");

        typeList.get(1).addChild("62", "违反民主集中制原则");
        typeList.get(1).addChild("63", "不按要求请示报告有关事项");
        typeList.get(1).addChild("64", "违规组织参加老乡会校友会战友会");
        typeList.get(1).addChild("65", "侵犯党员权利");
        typeList.get(1).addChild("66", "在投票和选举中搞非组织活动");
        typeList.get(1).addChild("67", "违反干部选拔任用规定");
        typeList.get(1).addChild("68", "在人事劳动工作中违规谋利");
        typeList.get(1).addChild("69", "违规发展党员");
        typeList.get(1).addChild("70", "违规办理出国证件和在境外脱离组织");

        typeList.get(2).addChild("71", "权权交易和纵容特定关系人以权谋私");
        typeList.get(2).addChild("72", "违规接受礼品礼金宴请服务");
        typeList.get(2).addChild("73", "违规操办婚丧喜庆事宜");
        typeList.get(2).addChild("74", "违规从事营利活动");
        typeList.get(2).addChild("75", "违反工作生活待遇规定");
        typeList.get(2).addChild("76", "违规占有使用公私财物");
        typeList.get(2).addChild("77", "违规参与公款宴请消费");
        typeList.get(2).addChild("78", "违规自定薪酬和发放津贴补贴奖金");
        typeList.get(2).addChild("79", "公款旅游");
        typeList.get(2).addChild("80", "违反公务接待管理规定");
        typeList.get(2).addChild("81", "违反公务用车管理规定");
        typeList.get(2).addChild("82", "违反会议活动管理规定");
        typeList.get(2).addChild("83", "违反办公用房管理规定");
        typeList.get(2).addChild("84", "权色钱色交易");
        typeList.get(2).addChild("85", "其他违反廉洁纪律行为");
        
        typeList.get(3).addChild("86", "侵害群众利益");
        typeList.get(3).addChild("87", "漠视群众利益");
        typeList.get(3).addChild("88", "侵犯群众知情权");
        typeList.get(3).addChild("89", "其他违反群众纪律行为");
        
        typeList.get(4).addChild("90", "主体责任落实不力");
        typeList.get(4).addChild("91", "违规干预市场经济活动");
        typeList.get(4).addChild("92", "违规干预执纪执法司法活动");
        typeList.get(4).addChild("93", "泄露扩散窃取私存党的秘密");
        typeList.get(4).addChild("94", "违反考试录取工作规定");
        typeList.get(4).addChild("95", "其他违反工作纪律行为");
        
        typeList.get(5).addChild("96", "生活奢靡");
        typeList.get(5).addChild("97", "不正当性关系");
        typeList.get(5).addChild("98", "其他违反生活纪律行为");
        
        typeList.get(6).addChild("99", "贪污贿赂行为");
        typeList.get(6).addChild("100", "失职渎职行为");
        typeList.get(6).addChild("101", "破坏社会主义市场经济秩序行为");
        typeList.get(6).addChild("102", "侵犯人身权利民主权利行为");
        typeList.get(6).addChild("103", "妨害社会管理秩序行为");
        typeList.get(6).addChild("104", "其他涉法行为");
    }

    public static String[] toArray(List<Accusation> list) {
        String[] arr = new String[list.size()];
        for (int i=0; i< list.size(); i++) {
            arr[i] = list.get(i).title;
        }
        return arr;
    }
}
