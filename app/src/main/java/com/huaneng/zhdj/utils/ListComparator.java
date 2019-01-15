package com.huaneng.zhdj.utils;

import java.util.Comparator;

public class ListComparator implements Comparator<CompareType> {

    @Override
    public int compare(CompareType t1, CompareType t2) {
        return t1.getCompareValue() - t2.getCompareValue();
    }
}
