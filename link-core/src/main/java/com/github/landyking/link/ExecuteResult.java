package com.github.landyking.link;

/**
 * Created by landy on 2018/4/23.
 */
public class ExecuteResult implements ExecutionEndingData {


    /**
     * 操作影响行数
     */
    private int effectCount;
    /**
     * 主键值
     */
    private Object primaryKeyValue;
    /**
     * 查询操作返回的数据
     */
    private Object data;
    /**
     * 查询操作查到的数据总数
     */
    private int totalCount;

    public int getEffectCount() {
        return effectCount;
    }

    public void setEffectCount(int effectCount) {
        this.effectCount = effectCount;
    }

    public Object getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    public void setPrimaryKeyValue(Object primaryKeyValue) {
        this.primaryKeyValue = primaryKeyValue;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "ExecuteResult{" +
                "effectCount=" + effectCount +
                ", primaryKeyValue=" + primaryKeyValue +
                ", data=" + data +
                ", totalCount=" + totalCount +
                '}';
    }

    @Override
    public Object forOutput() {
        return data;
    }

}
