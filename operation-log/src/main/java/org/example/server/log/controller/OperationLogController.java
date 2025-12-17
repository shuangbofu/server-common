package org.example.server.log.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.server.common.utils.ObjectUtils;
import org.example.server.log.entity.OperationLog;
import org.example.server.log.mapper.SysLogMapper;
import org.example.server.log.param.OperationLogPageFilter;
import org.example.server.log.vo.OperationLogExcelItem;
import org.example.server.log.vo.OperationLogVO;
import org.example.server.web.annotation.ResultController;
import org.example.server.web.domain.request.PageRequest;
import org.example.server.web.domain.request.PageVO;
import org.example.server.web.utils.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.example.server.web.utils.BeanUtils.aToB;
import static org.example.server.web.utils.BeanUtils.listAToListB;

/**
 * 操作日志
 */
@ResultController(value = "/api/sys/operation-log", name = "sysOperationLogController")
@Controller
@RequiredArgsConstructor
public class OperationLogController {
    private final SysLogMapper mapper;

    /**
     * 分页
     * @param request
     * @return
     */
    @PostMapping("page")
    public PageVO<OperationLogVO> page(@RequestBody PageRequest<OperationLogPageFilter> request) {
        Page<OperationLog> page = mapper.selectPage(request, i -> handleFilter(i, request));
        return PageVO.of(page.convert(i->aToB(i, OperationLogVO.class)));
    }

    private void handleFilter(LambdaQueryWrapper<OperationLog> i, PageRequest<OperationLogPageFilter> request) {
        OperationLogPageFilter filter = request.getFilter();
        i.eq(StringUtils.isNotEmpty(filter.getName()), OperationLog::getName, filter.getName())
                .like(StringUtils.isNotEmpty(filter.getMessage()), OperationLog::getMessage, filter.getMessage())
                .eq(StringUtils.isNotEmpty(filter.getType()), OperationLog::getLogType, filter.getType())
                .like(StringUtils.isNotEmpty(filter.getIp()), OperationLog::getIp, filter.getIp())
                .eq(filter.getOperatorId()!=null,
                        OperationLog::getOperatorId, filter.getOperatorId())
                .ge(filter.getStartTime()!=null, OperationLog::getOperateTime, filter.getStartTime())
                .lt(filter.getEndTime()!=null, OperationLog::getOperateTime, filter.getEndTime())
                .orderByDesc(filter.getDesc()!=null && filter.getDesc(), OperationLog::getOperateTime)
                .orderByAsc(filter.getDesc()!=null && !filter.getDesc(), OperationLog::getOperateTime);
    }

    /**
     * 类型列表
     * @return
     */
    @GetMapping("/list/logType")
    public List<String> logTypeList() {
        return mapper.selectList(new QueryWrapper<OperationLog>().select("DISTINCT log_type"))
                .stream().map(OperationLog::getLogType).toList();
    }

    /**
     * 操作用户列表
     * @return
     */
    @GetMapping("/list/user")
    public List<Map<String, Object>> list() {
        return mapper.selectList(new QueryWrapper<OperationLog>().select("DISTINCT operator_id, operator"))
                .stream()
                .map(i-> Map.<String,Object>of
                        ("operator_id", i.getOperatorId(), "operator", i.getOperator()))
                .toList();
    }

    /**
     * 导出
     * @param request
     * @param response
     */
    @PostMapping("export")
    public void exportChartData(@RequestBody PageRequest<OperationLogPageFilter> request, HttpServletResponse response) {
        List<OperationLogExcelItem> list = listAToListB(mapper.selectList(i-> handleFilter(i, request)),
                OperationLogExcelItem.class, (a,b) -> {
                    b.setOperateTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(a.getOperateTime()), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                });
        List<Map<String, Object>> data = ObjectUtils.convertToMapList(list,
                "id", "name", "level", "operatorId");
        WebUtils.export(response, data, "export", "xlsx");
    }
}
