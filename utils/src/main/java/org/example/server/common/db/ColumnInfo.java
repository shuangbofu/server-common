package org.example.server.common.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInfo implements Serializable {
    private String name;
    private String type;
    private String comment;
    private Integer columnSize;
    private Class<?> javaType;

    public ColumnInfo(String name, String comment, Class<?> javaType) {
        this.name = name;
        this.comment = comment;
        columnSize = 0;
        this.javaType = javaType;
    }

    private static String getColumnTypeByJavaType(Class<?> type, int columnSize) {
        if (type == int.class || type == Integer.class) {
            if (columnSize >= 10) {
                return "BIGINT";
            }
            return "INT";
        } else if (type == long.class || type == Long.class) {
            return "BIGINT";
        } else if (type == double.class || type == Double.class) {
            return "DOUBLE";
        } else if (type == float.class || type == Float.class) {
            return "FLOAT";
        } else if (type == short.class || type == Short.class) {
            return "SMALLINT";
        } else if (type == byte.class || type == Byte.class) {
            return "TINYINT";
        } else if (type == boolean.class || type == Boolean.class) {
            return "TINYINT";
        } else if (type == char.class || type == Character.class) {
            return "CHAR(1)";
        } else if (type.isEnum()) {
            // 枚举类型存储为字符串，长度根据需要调整
            return "VARCHAR(" + columnSize + ")";
        } else if (type == String.class) {
            // 字符串类型，长度根据需要调整
            if (columnSize > 65000) {
                return "LONGTEXT";
            } else if (columnSize > 2000) {
                return "TEXT";
            }
            return "VARCHAR(" + columnSize + ")";
        } else if (type == LocalDate.class) {
            return "DATE";
        } else if (type == java.util.Date.class) {
            // 日期时间类型
            return "DATETIME";
        } else if (type == java.time.LocalDateTime.class) {
            // Java 8 日期时间类型，具体取决于数据库支持的类型，这里使用 TIMESTAMP
            return "DATETIME";
        } else if (type == java.math.BigDecimal.class) {
            // 精确小数类型，具体取决于数据库支持的类型，这里使用 DECIMAL
            return "DECIMAL(19, 4)"; // 可根据需要调整精度和小数位数
        } else if (type == java.sql.Clob.class) {
            // 大文本类型，具体取决于数据库支持的类型，这里使用 CLOB
            return "CLOB";
        } else {
            // 复杂对象转成JSON格式
            return "JSON";
        }
    }

    public String getSegment() {
        String segment = String.format("`%s` %s", name, getColumnType());
        if (StringUtils.isNotEmpty(comment)) {
            segment += " COMMENT '" + comment + "'";
        }
        return segment;
    }

    public String getColumnType() {
        if (type == null || type.isEmpty()) {
            return getColumnTypeByJavaType(javaType, columnSize);
        }
        return type;
    }

    public String getPureType() {
        String columnType = getColumnType();
        if (columnType != null) {
            int idx = columnType.indexOf("(");
            if (idx > 0) {
                return columnType.substring(0, idx).trim().toUpperCase();
            }
            return columnType.toUpperCase();
        }
        return columnType;
    }
}
