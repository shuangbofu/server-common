package org.example.server.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.example.server.domain.PageItem;
import org.springframework.cglib.core.ReflectUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface IMapper<R> extends BaseMapper<R> {

    default boolean exists(Consumer<LambdaQueryWrapper<R>> handler) {
        return exists(createLambdaQueryWrapper(handler));
    }

    default R insertRt(R entity) {
        insert(entity);
        return entity;
    }

    default boolean deleteBy(Consumer<LambdaQueryWrapper<R>> handler) {
        return delete(createLambdaQueryWrapper(handler)) > 0;
    }

    default <A, B> Map<A, List<B>> selectBListInAGroupBy(SFunction<R, A> condition,
                                                         List<A> list, Function<R, B> mapper) {
        if (list.isEmpty()) {
            return new HashMap<>();
        }
        // 根据 `condition` 进行 `in` 查询，然后通过 `groupingBy` 进行分组
        return selectList(i -> i.in(condition, list)).stream()
                // 通过 `condition` 字段进行分组，`Collectors.mapping` 将 `mapper` 映射后的结果收集为列表
                .collect(Collectors.groupingBy(
                        condition,  // 分组条件
                        Collectors.mapping(mapper, Collectors.toList())  // 映射并收集为列表
                ));
    }

    default List<R> selectAll() {
        return selectList(createLambdaQueryWrapper(i->{}));
    }

    default <T extends PageItem> Page<R> selectPage(T pageItem, Consumer<LambdaQueryWrapper<R>> handler) {
        var wrapper = createLambdaQueryWrapper(handler);
        return selectPage(PageDTO.of(pageItem.getPageNum(), pageItem.getPageSize()), wrapper);
    }

    default boolean updateBy(R entity, Consumer<LambdaUpdateWrapper<R>> handler) {
        return update(entity, createUpdateWrapper(handler)) > 0;
    }

    default boolean updateBy(Consumer<LambdaUpdateWrapper<R>> handler) {
        return update(entityNewInstance(), createUpdateWrapper(handler)) > 0;
    }

    default List<R> selectList(Consumer<LambdaQueryWrapper<R>> handler) {
        return selectList(createLambdaQueryWrapper(handler));
    }
    default R selectOne(Consumer<LambdaQueryWrapper<R>> handler) {
        return selectOne(handler, null);
    }

    default long countBy(Consumer<LambdaQueryWrapper<R>> handler) {
        return selectCount(createLambdaQueryWrapper(handler));
    }

    default R selectOne(Consumer<LambdaQueryWrapper<R>> handler,
                        Supplier<? extends RuntimeException> exceptionSupplier) {
        R one = selectOne(createLambdaQueryWrapper(handler));
        if(exceptionSupplier !=null && one == null) {
            throw exceptionSupplier.get();
        }
        return one;
    }

    default List<String> selectStringList(Consumer<LambdaQueryWrapper<R>> handler) {
        return selectList(createLambdaQueryWrapper(handler)).stream()
                .filter(Objects::nonNull)
                .map(Object::toString).toList();
    }

    default <V> List<V> selectValueList(SFunction<R,V> mapper, Consumer<LambdaQueryWrapper<R>> handler) {
        LambdaQueryWrapper<R> wrapper = createLambdaQueryWrapper(handler).select(mapper);
        return selectList(wrapper).stream().map(mapper)
                .toList();
    }

    default <V> V selectValue(SFunction<R,V> mapper, Consumer<LambdaQueryWrapper<R>> handler) {
        LambdaQueryWrapper<R> wrapper = createLambdaQueryWrapper(handler).select(mapper);
        return Optional.ofNullable(selectOne(wrapper)).map(mapper).orElse(null);
    }

    default LambdaQueryWrapper<R> createLambdaQueryWrapper(Consumer<LambdaQueryWrapper<R>> handler) {
        LambdaQueryWrapper<R> wrapper = new LambdaQueryWrapper<>();
        handler.accept(wrapper);
        return wrapper;
    }

    default QueryWrapper<R> createQueryWrapper(Consumer<QueryWrapper<R>> handler) {
        QueryWrapper<R> wrapper = new QueryWrapper<>();
        handler.accept(wrapper);
        return wrapper;
    }

    default LambdaUpdateWrapper<R> createUpdateWrapper(Consumer<LambdaUpdateWrapper<R>> handler) {
        LambdaUpdateWrapper<R> wrapper = new LambdaUpdateWrapper<>();
        handler.accept(wrapper);
        return wrapper;
    }

    default R entityNewInstance() {
        MybatisMapperProxy<?> mybatisMapperProxy = MybatisUtils.getMybatisMapperProxy(this);
        Class<R> rMapperClass = (Class<R>) mybatisMapperProxy.getMapperInterface();
        Class<?> rClass = GenericTypeUtils.resolveTypeArguments(rMapperClass, com.baomidou.mybatisplus.core.mapper.BaseMapper.class)[0];
        R entity = null;
        if(BaseEntity.class.isAssignableFrom(rClass)) {
            entity = (R) ReflectUtils.newInstance(rClass);
        }
        return entity;
    }

    default <V> List<V> selectObjList(Consumer<QueryWrapper<R>> handler, Class<V> clazz) {
        QueryWrapper<R> wrapper = createQueryWrapper(handler);
        List<Map<String, Object>> maps = selectMaps(wrapper);
        ObjectMapper mapper = JacksonTypeHandler.getObjectMapper();
        try {
            String json = mapper.writeValueAsString(maps);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            return mapper.readValue(json, mapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    default <V> V selectObj(Consumer<QueryWrapper<R>> handler, Class<V> clazz) {
        List<V> vList = selectObjList(handler, clazz);
        if(vList.isEmpty()) {
            return null;
        }
        return vList.get(0);
    }
}
