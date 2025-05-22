package org.example.server.web.domain.request;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.server.domain.PageItem;
import org.example.server.web.utils.BeanUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Serializable {

    /**
     * data
     */
    private List<T> data;

    /**
     * 总条数
     */
    private int totalCount = 0;

    /**
     * 每页数量
     */
    private int pageSize = 1;

    /**
     * 当前页
     */
    private int pageNum = 1;


    public static <T,P extends PageItem> PageVO<T> ofEmpty(P pageItem) {
        PageVO<T> vo = new PageVO<>();
        vo.setTotalCount(0);
        vo.setPageNum(pageItem.getPageNum());
        vo.setData(List.of());
        vo.setPageSize(pageItem.getPageSize());
        return vo;
    }

    public static <T,R> PageVO<T> of(IPage<R> page, List<T> records) {
        PageVO<T> vo = new PageVO<>();
        vo.setData(records);
        vo.setTotalCount((int) page.getTotal());
        vo.setPageNum((int) page.getCurrent());
        vo.setPageSize((int) page.getSize());
        return vo;
    }

    public static <T> PageVO<T> of(IPage<T> page) {
        PageVO<T> vo = new PageVO<>();
        vo.setTotalCount((int) page.getTotal());
        vo.setPageNum((int) page.getCurrent());
        vo.setData(page.getRecords());
        vo.setPageSize((int) page.getSize());
        return vo;
    }


    /**
     * @param page   分页结果
     * @param VOClas vo对象类
     * @param <V>    vo对象类
     * @param <T>    原始对象类
     * @return vo分页
     */
    public static <V, T> PageVO<V> of(IPage<T> page, Class<V> VOClas) {
        PageVO<V> vo = new PageVO<>();
        vo.setTotalCount((int) page.getTotal());
        vo.setPageNum((int) page.getCurrent());
        vo.setData(BeanUtils.listAToListB(page.getRecords(), VOClas));
        vo.setTotalCount((int) page.getTotal());
        return vo;
    }

    public static <V, T> PageVO<V> of(PageVO<T> page, Class<V> VOClas) {
        PageVO<V> vo = new PageVO<>();
        vo.setTotalCount(page.getTotalCount());
        vo.setPageNum(page.getPageNum());
        vo.setData(BeanUtils.listAToListB(page.getData(), VOClas));
        vo.setPageSize(page.getPageSize());
        return vo;
    }

    /**
     * @param page   分页结果
     * @param mapper 转换
     * @param <V>    vo对象
     * @param <T>    原始对象
     * @return vo分页
     */
    public static <V, T> PageVO<V> of(IPage<T> page, Function<T, V> mapper) {
        return of(page.convert(mapper));
    }

    public static <T> PageVO<T> success(int current, int pageSize, int total, List<T> data) {
        PageVO<T> vo = new PageVO<>();
        vo.setPageNum(current);
        vo.setPageSize(pageSize);
        vo.setTotalCount(total);
        vo.setData(data);
        return vo;
    }

}
