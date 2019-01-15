package com.huaneng.zhdj.bean;

import java.util.Iterator;
import java.util.List;

/**
 * 民意调查Item分页
 */
public class SurveyItemPager extends PagerBase<SurveyItem> {//

    public String count;

    public void decorate() {
        List<SurveyItem> list = getList();
        if (list != null && !list.isEmpty()) {
            Iterator<SurveyItem> iterator = list.iterator();
            while(iterator.hasNext()){
                SurveyItem item = iterator.next();
                if(!item.valid()) {
                    iterator.remove();
                }
            }
        }
    }

}
