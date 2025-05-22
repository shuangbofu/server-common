package org.example.server.common.utils;

import java.io.StringWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CSVUtils {

    /**
     * 将数据导出为 CSV 格式
     * @param data 要导出的数据，List<Map<String, Object>> 形式
     * @return CSV 文件的字节数组
     * @throws IOException
     */
    public static byte[] exportToCSV(List<Map<String, Object>> data) throws IOException {
        if (data == null || data.isEmpty()) {
            return new byte[0]; // 如果数据为空，返回空文件
        }

        StringWriter writer = new StringWriter();

        // 获取列名 (Map 的 key)，假设所有 Map 的 key 集合相同
        Map<String, Object> firstRow = data.get(0);
        List<String> headers = firstRow.keySet().stream().toList();

        // 写入 CSV 头
        writer.append(String.join(",", headers)).append("\n");

        // 写入 CSV 内容
        for (Map<String, Object> row : data) {
            for (String header : headers) {
                Object value = row.get(header);
                writer.append(value != null ? value.toString() : "").append(",");
            }
            writer.append("\n");
        }

        // 返回 CSV 的字节数组
        return writer.toString().getBytes();
    }
}