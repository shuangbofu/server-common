package org.example.server.web.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.server.common.utils.CSVUtils;
import org.example.server.common.utils.ExcelUtils;
import org.example.server.web.exception.BizException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class WebUtils {

    /**
     * 通用导出工具方法
     * @param response HttpServletResponse 用于向客户端返回文件
     * @param data 导出的数据列表
     * @param fileName 导出的文件名
     * @param exportType 导出文件的类型（支持 "xlsx", "csv" 等）
     */
    public static void export(HttpServletResponse response,
                              List<Map<String, Object>> data,
                              String fileName, String exportType) {
        byte[] fileContent;
        String fileExtension;
        try {
            // 根据导出类型决定文件内容和扩展名
            switch (exportType.toLowerCase()) {
                case "xls":
                case "xlsx":
                    fileContent = ExcelUtils.exportToExcel(data);
                    fileExtension = "." + exportType.toLowerCase();
                    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                    break;
                case "csv":
                    fileContent = CSVUtils.exportToCSV(data);
                    fileExtension = ".csv";
                    response.setContentType("text/csv");
                    break;
                default:
                    throw new IllegalArgumentException("不支持的文件类型: " + exportType);
            }

            // 动态设置文件名
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + fileExtension);

            // 写入响应输出流
            response.getOutputStream().write(fileContent);
            response.getOutputStream().flush();
        } catch (IOException e) {
            // 记录异常日志
            log.error("文件导出失败: ", e);
            throw new BizException(-2, "导出失败", e);
        } catch (IllegalArgumentException e) {
            // 捕获非法参数异常
            log.error("不支持的导出格式: ", e);
            throw new BizException(-3, "不支持的导出格式", e);
        }
    }
}
