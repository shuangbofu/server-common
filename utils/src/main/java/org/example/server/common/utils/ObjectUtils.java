package org.example.server.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.example.server.common.annotation.DisplayName;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ObjectUtils {

    /**
     * 比较两个对象的字段差异，支持嵌套对象和注解中文提示
     * @param oldObj 修改前的对象
     * @param newObj 修改后的对象
     * @return 字段差异描述的列表
     */
    public static List<String> compareFields(Object oldObj, Object newObj) {
        List<String> changes = new ArrayList<>();
        compareFieldsRecursively(oldObj, newObj, changes, "");
        return changes;
    }

    /**
     * 递归比较对象的字段差异
     * @param oldObj 修改前的对象
     * @param newObj 修改后的对象
     * @param changes 字段差异描述的列表
     * @param parentField 父字段名，用于标识嵌套字段
     */
    private static void compareFieldsRecursively(Object oldObj, Object newObj, List<String> changes, String parentField) {
        if (oldObj != null && newObj != null && oldObj.getClass().equals(newObj.getClass())) {
            Class<?> currentClass = oldObj.getClass();

            // 遍历当前类及其父类的所有字段
            while (currentClass != null) {
                Field[] fields = currentClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        Object oldValue = field.get(oldObj);
                        Object newValue = field.get(newObj);
                        String displayName = getDisplayName(field, parentField);

                        if (isSimpleValueType(field.getType())) {
                            // 简单类型直接比较
                            if (oldValue == null && newValue != null) {
                                changes.add(displayName + " 从 null 修改成 「" + newValue + "」");
                            } else if (oldValue != null && newValue == null) {
                                changes.add(displayName + " 从 「" + oldValue + "」 修改成 null");
                            } else if (oldValue != null && !oldValue.equals(newValue)) {
                                changes.add(displayName + " 从 「" + oldValue + "」 修改成 「" + newValue + "」");
                            }
                        } else if (oldValue instanceof Collection && newValue instanceof Collection) {
                            // 处理集合类型
                            compareCollections((Collection<?>) oldValue, (Collection<?>) newValue, changes, displayName);
                        } else if (oldValue instanceof Map && newValue instanceof Map) {
                            // 处理映射类型
                            compareMaps((Map<?, ?>) oldValue, (Map<?, ?>) newValue, changes, displayName);
                        } else if (oldValue != null && newValue != null) {
                            // 递归比较对象类型
                            compareFieldsRecursively(oldValue, newValue, changes, displayName);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                // 继续处理父类字段
                currentClass = currentClass.getSuperclass();
            }
        }
    }

    // 比较集合的辅助方法
    private static void compareCollections(Collection<?> oldCollection, Collection<?> newCollection, List<String> changes, String parentField) {
        if (!oldCollection.equals(newCollection)) {
            changes.add(parentField + " 集合内容修改: 从 「" + oldCollection + "」 修改成 「" + newCollection + "」");
        }
    }

    // 比较映射的辅助方法
    private static void compareMaps(Map<?, ?> oldMap, Map<?, ?> newMap, List<String> changes, String parentField) {
        if (!oldMap.equals(newMap)) {
            changes.add(parentField + " 映射内容修改: 从 「" + oldMap + "」 修改成 「" + newMap + "」");
        }
    }

    /**
     * 获取字段的显示名称
     * @param field 字段
     * @param parentField 父字段名，用于标识嵌套字段
     * @return 字段的中文显示名称
     */
    private static String getDisplayName(Field field, String parentField) {
        DisplayName displayNameAnnotation = field.getAnnotation(DisplayName.class);
        String fieldName = displayNameAnnotation != null ? displayNameAnnotation.value() : field.getName();
        return parentField.isEmpty() ? fieldName : parentField + "." + fieldName;
    }

    /**
     * 判断一个类型是否是简单数据类型（如基本类型、包装类、String等）
     * @param type 类型
     * @return 是否是简单数据类型
     */
    private static boolean isSimpleValueType(Class<?> type) {
        return type.isPrimitive() ||
               type.equals(String.class) ||
               type.equals(Integer.class) ||
               type.equals(Long.class) ||
               type.equals(Double.class) ||
               type.equals(Float.class) ||
               type.equals(Boolean.class) ||
               type.equals(Byte.class) ||
               type.equals(Short.class) ||
               type.equals(Character.class);
    }

    /**
     * 将对象集合转换为 List<Map<String, Object>>，支持忽略指定字段，并处理异常不抛出
     * @param objects 对象集合
     * @param ignoredFields 需要忽略的字段名称
     * @param <T> 泛型对象
     * @return List<Map<String, Object>>
     */
    public static <T> List<Map<String, Object>> convertToMapList(List<T> objects, String... ignoredFields) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (objects == null || objects.isEmpty()) {
            return mapList;
        }

        // 将忽略的字段转换为 Set，方便查找
        Set<String> ignoredFieldSet = new HashSet<>();
        if (ignoredFields != null) {
            Collections.addAll(ignoredFieldSet, ignoredFields);
        }

        // 遍历对象集合
        for (T obj : objects) {
            Map<String, Object> map = new LinkedHashMap<>();
            Class<?> clazz = obj.getClass();

            // 获取所有字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);  // 设置可访问私有字段

                // 跳过忽略的字段
                if (ignoredFieldSet.contains(field.getName())) {
                    continue;
                }

                try {
                    // 获取注解 FieldDisplayName 的值，如果没有则使用字段名
                    DisplayName annotation = field.getAnnotation(DisplayName.class);
                    String displayName = (annotation != null) ? annotation.value() : field.getName();

                    // 获取字段值
                    Object value = field.get(obj);
                    map.put(displayName, value);
                } catch (IllegalAccessException e) {
                    // 记录异常日志，继续执行
                    log.error("字段访问异常: {}", field.getName(), e);
                }
            }
            mapList.add(map);
        }

        return mapList;
    }
}