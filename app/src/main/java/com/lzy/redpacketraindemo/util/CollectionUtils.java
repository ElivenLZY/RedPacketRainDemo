package com.lzy.redpacketraindemo.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CollectionUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2012-7-22
 */
public class CollectionUtils {

    /**
     * default join separator
     **/
    public static final CharSequence DEFAULT_JOIN_SEPARATOR = ",";

    private CollectionUtils() {
        throw new AssertionError();
    }

    /**
     * is null or its size is 0
     * <p>
     * <pre>
     * isEmpty(null)   =   true;
     * isEmpty({})     =   true;
     * isEmpty({1})    =   false;
     * </pre>
     *
     * @param <V>
     * @param c
     * @return if collection is null or its size is 0, return true, else return false.
     */
    public static <V> boolean isEmpty(Collection<V> c) {
        return (c == null || c.size() == 0);
    }

    /**
     * join collection to string, separator is {@link #DEFAULT_JOIN_SEPARATOR}
     * <p>
     * <pre>
     * join(null)      =   "";
     * join({})        =   "";
     * join({a,b})     =   "a,b";
     * </pre>
     *
     * @param collection
     * @return join collection to string, separator is {@link #DEFAULT_JOIN_SEPARATOR}. if collection is empty, return
     * ""
     */
    public static String join(Iterable collection) {
        return collection == null ? "" : TextUtils.join(DEFAULT_JOIN_SEPARATOR, collection);
    }


    /**
     * 将一个List按照固定的大小拆成很多个小的List
     *
     * @param listObj  需要拆分的List
     * @param groupNum 每个List的最大长度
     * @return 拆分后的List的集合
     */
    public static <T> List<List<T>> getSubList(List<T> listObj, int groupNum) {
        if (isEmpty(listObj) || groupNum < 1) return null;
        List<List<T>> resultList = new ArrayList<List<T>>();
        // 获取需要拆分的List个数
        int loopCount = (listObj.size() % groupNum == 0) ? (listObj.size() / groupNum)
                : ((listObj.size() / groupNum) + 1);
        // 开始拆分
        for (int i = 0; i < loopCount; i++) {
            // 子List的起始值
            int startNum = i * groupNum;
            // 子List的终止值
            int endNum = (i + 1) * groupNum;
            // 不能整除的时候最后一个List的终止值为原始List的最后一个
            if (i == loopCount - 1) {
                endNum = listObj.size();
            }
            // 拆分List
            List<T> listObjSub = listObj.subList(startNum, endNum);
            // 保存差分后的List
            resultList.add(listObjSub);
        }
        return resultList;

    }


    /**
     * 截取list数据
     *
     * @param listObj 需要拆分的List
     * @return 拆分后的List的集合
     */
    public static <T> List<T> getSubList(List<T> listObj, int startIndex, int endIndex) {
        try {
            if (isEmpty(listObj)) return null;
            if (listObj.size() < endIndex) endIndex = listObj.size();
            List<List<T>> resultList = new ArrayList<List<T>>();
            // 拆分List
            List<T> listObjSub = listObj.subList(startIndex, endIndex);
            // 保存差分后的List
            resultList.add(listObjSub);

            return resultList.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 交换集合里元素的位置
     * @author zhang
     * @time 2017/6/10 10:00
     */
    public static <T> List<T> indexExChange(List<T> list, int index1, int index2){
        if (isEmpty(list)) return null;
        T t = list.get(index1);
        list.set(index1, list.get(index2));
        list.set(index2, t);
        return list;
    }

}
