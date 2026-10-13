package net.zjitc.common;

import lombok.Data;

import java.util.List;

/**
 * 通用分页结果封装
 */
@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long current;
    private long size;
    private long pages;

    public PageResult() {}

    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (size > 0) ? (total + size - 1) / size : 0;
    }
}
