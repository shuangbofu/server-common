package org.example.server.common.utils;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    public static byte[] exportToExcel(List<Map<String, Object>> data) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Data");

            // 1. 创建标题行（确保行高可见）
            if (!data.isEmpty()) {
                createHeaderRow(sheet, data.get(0), workbook);
            }

            // 2. 写入数据行（设置默认行高）
            writeDataRows(sheet, data, workbook);

            // 3. 精确调整列宽（修复隐藏问题）
            adjustColumnWidthsAccurately(sheet, data.isEmpty() ? 0 : data.get(0).size());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private static void createHeaderRow(Sheet sheet, Map<String, Object> headerData, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(20); // 明确设置行高

        CellStyle style = createBoldStyle(workbook);
        int colIdx = 0;
        for (String key : headerData.keySet()) {
            Cell cell = headerRow.createCell(colIdx);
            cell.setCellValue(key);
            cell.setCellStyle(style);
            colIdx++;
        }
    }

    private static CellStyle createBoldStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)12);
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static void writeDataRows(Sheet sheet, List<Map<String, Object>> data, Workbook workbook) {
        if (data.isEmpty()) return;

        CellStyle linkStyle = createLinkStyle(workbook);
        List<String> keys = List.copyOf(data.get(0).keySet());

        for (int rowIdx = 0; rowIdx < data.size(); rowIdx++) {
            Row row = sheet.createRow(rowIdx + 1);
            row.setHeightInPoints(18); // 设置数据行高度
            Map<String, Object> rowData = data.get(rowIdx);

            for (int colIdx = 0; colIdx < keys.size(); colIdx++) {
                Object value = rowData.get(keys.get(colIdx));
                createCell(row, colIdx, value, linkStyle, workbook);
            }
        }
    }

    private static CellStyle createLinkStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setUnderline(Font.U_SINGLE);
        font.setColor(IndexedColors.BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private static void createCell(Row row, int colIdx, Object value, CellStyle linkStyle, Workbook workbook) {
        if (value == null) {
            // 创建空单元格避免列缺失
            row.createCell(colIdx).setCellValue("");
            return;
        }

        Cell cell = row.createCell(colIdx);
        String strValue = value.toString();

        if (strValue.startsWith("http://") || strValue.startsWith("https://")) {
            Hyperlink link = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            link.setAddress(strValue);
            cell.setHyperlink(link);
            cell.setCellValue(strValue);
            cell.setCellStyle(linkStyle);
        } else {
            cell.setCellValue(strValue);
        }
    }

    private static void adjustColumnWidthsAccurately(Sheet sheet, int columnCount) {
        for (int col = 0; col < columnCount; col++) {
            int maxWidth = 0;

            // 遍历所有行（包括标题）
            for (int row = 0; row <= sheet.getLastRowNum(); row++) {
                Row r = sheet.getRow(row);
                if (r != null) {
                    Cell cell = r.getCell(col);
                    if (cell != null) {
                        String value = cell.getStringCellValue();
                        int width = calculateCellWidth(value);
                        maxWidth = Math.max(maxWidth, width);
                    }
                }
            }

            // 处理空列和最小宽度
            if (maxWidth == 0) {
                sheet.setColumnWidth(col, 8 * 256); // 空列默认8字符
            } else {
                sheet.setColumnWidth(col, Math.min(maxWidth, 150 * 256)); // 最大150字符
            }
        }
    }

    private static int calculateCellWidth(String value) {
        int chineseCount = value.replaceAll("[^\\u4E00-\\u9FA5]", "").length();
        int otherCount = value.length() - chineseCount;
        int width = (int)(chineseCount * 2.2 * 256 + otherCount * 1.0 * 256);
        return width + 1024; // 添加1024(4字符)边距
    }
}