package org.example.server.web.domain.request;

import lombok.Data;
import org.example.server.domain.PageItem;

import java.io.Serializable;

@Data
public class PageRequest<T> implements Serializable, PageItem {
    /**
     * 分页size
     */
    private int pageSize;
    /**
     * 分页number
     */
    private int pageNum;
    /**
     * 过滤条件
     */
    private T filter;
}
