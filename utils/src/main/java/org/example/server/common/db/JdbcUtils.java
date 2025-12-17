package org.example.server.common.db;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Sets;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public class JdbcUtils {
    private static final Map<RdsParam, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    private static DataSource getDataSource(RdsParam rdsParam) {
        return dataSourceMap.computeIfAbsent(rdsParam, DataSourceConfig::createDataSource);
    }

    public static boolean tableExists(DataSource dataSource, String tableName) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();) {
            stmt.executeQuery("SHOW CREATE TABLE " + tableName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int executeBatchUpdate(Connection conn, String sql, List<List<Object>> paramsList) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // 遍历每组参数
            for (List<Object> params : paramsList) {
                // 为 PreparedStatement 设置参数
                for (int i = 0; i < params.size(); i++) {
                    Object param = params.get(i);
                    if (param != null) {
                        stmt.setObject(i + 1, param);
                    } else {
                        stmt.setNull(i + 1, Types.NULL);
                    }
                }
                stmt.addBatch(); // 添加到批处理
            }
            log.info("Execute batch update sql: {}", sql);
            int[] results = stmt.executeBatch();// 执行批处理
            return Arrays.stream(results).sum();
        } catch (Exception e) {
            throw new RuntimeException("ExecuteBatchUpdate error", e);
        }
    }

    public static int executeBatchUpdate(DataSource dataSource, String sql, List<List<Object>> paramsList) {
        // 使用 try-with-resources 语法自动管理资源
        try (Connection conn = dataSource.getConnection()) {
            return executeBatchUpdate(conn, sql, paramsList);
        } catch (SQLException e) {
            throw new RuntimeException("获取Connection或事务处理错误", e);
        }
    }

    public static int executeUpdate(DataSource dataSource, String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();) {
            return executeUpdate(conn, sql, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int executeUpdate(Connection conn, String sql, Object... params) {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            log.info("Execute update sql: {}", sql);
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("SQL执行错误，sql:" + sql, e);
        }
    }

    public static int useTransaction(DataSource dataSource, Function<Connection, Integer> function) {
        try (Connection conn = dataSource.getConnection()) {
            // 关闭自动提交，手动管理事务
            conn.setAutoCommit(false);
            int totalRowsAffected = 0;
            try {
                totalRowsAffected += function.apply(conn);
                // 所有 SQL 执行成功，提交事务
                conn.commit();
                return totalRowsAffected;
            } catch (SQLException e) {
                // 如果出现异常，回滚事务
                conn.rollback();
                throw new RuntimeException("事务执行失败，已回滚", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接或SQL执行失败", e);
        }
    }

    public static List<ColumnInfo> getColumns(DataSource dataSource, String tableName) {
        return getColumns(dataSource, Sets.newHashSet(tableName)).get(tableName);
    }

    public static Map<String, List<ColumnInfo>> getColumns(DataSource dataSource,
                                                           Set<String> tables) {
        Map<String, List<ColumnInfo>> tableColumnInfo = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            String catalog = conn.getCatalog();
            for (String tableName : tables) {
                String query = String.format(
                        "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_COMMENT " +
                                "FROM INFORMATION_SCHEMA.COLUMNS " +
                                "WHERE TABLE_NAME = '%s' AND TABLE_SCHEMA = '%s'", tableName, catalog
                );
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    String columnType = resultSet.getString("COLUMN_TYPE");
                    String columnComment = resultSet.getString("COLUMN_COMMENT");
                    int columnSize = resultSet.getInt("CHARACTER_MAXIMUM_LENGTH");
                    ColumnInfo columnInfo = new ColumnInfo(columnName, columnType, columnComment, columnSize, null);
                    tableColumnInfo.computeIfAbsent(tableName, k -> new ArrayList<>()).add(columnInfo);
                }
                resultSet.close();
                statement.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Get table column info error", e);
        }
        return tableColumnInfo;
    }

    public static boolean testConnection(RdsParam rdsParam) {
        try (Connection conn = getDataSource(rdsParam).getConnection()) {
            return true;
        } catch (SQLException e) {
            log.error("连接失败", e);
            return false;
        }
    }

    public static List<Map<String, Object>> query(RdsParam rdsParam, String sql) {
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = getDataSource(rdsParam)
                .getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    if (value instanceof LocalDateTime) {
                        value = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
                    }
                    row.put(columnName, value);
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询SQL异常", e);
        }
        return result;
    }

    public static void exec(RdsParam rdsParam, String sql) {
        try (Connection conn = getDataSource(rdsParam).getConnection()) {
            conn.createStatement().executeQuery(sql);
        } catch (Exception e) {
            throw new RuntimeException("查询SQL异常", e);
        }
    }
}