package org.example.server.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface BaseMapper<R extends IdEntity<R>> extends IMapper<R> {

    default String getName(Long id) {
        R one = selectOne(createQueryWrapper(i -> i.select("name").eq(IdEntity.ID, id)));
        try {
            Field field = one.getClass().getDeclaredField("name");
            field.setAccessible(true);
            return Optional.ofNullable(field.get(one)).map(Object::toString).orElse(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default <V> void checkUniqueThrow(R entity, SFunction<R,V> mapper,
                                   Supplier<? extends RuntimeException> supplier) {
        boolean exists = checkExist(entity, mapper);
        if(exists) {
            throw supplier.get();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    default <T, X extends Throwable> boolean save(R entity, SFunction<R, T> existCheck,
                                                           Supplier<? extends X> exceptionSupplier) throws X {
        if (existCheck!=null) {
            boolean exist = checkExist(entity, existCheck);
            if(exist) {
                throw exceptionSupplier.get();
            }
        }
        save(entity);
        return true;
    }

    default  <T> boolean checkExist(R entity, SFunction<R, T> mapper) {
        QueryWrapper<R> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(mapper, mapper.apply(entity));
        wrapper.ne(entity.getId() != null, IdEntity.ID, entity.getId());
        return exists(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    default R save(R entity) {
        QueryWrapper<R> wrapper = new QueryWrapper<>();
        wrapper.eq(IdEntity.ID, entity.getId());
        boolean exists = exists(wrapper);
        if(exists) {
            updateById(entity);
        } else {
            insertRt(entity);
        }
        return entity;
    }

    default <V> List<V> selectValuesInIds(SFunction<R,V> mapper, Collection<Long> idList) {
        return selectListInIds(idList, i->i.select(mapper)).stream().map(mapper).toList();
    }

    default List<R> selectListInIds(Collection<Long> idList) {
        return selectListInIds(idList, i->{});
    }

    default List<R> selectListInIds(Collection<Long> idList, Consumer<LambdaQueryWrapper<R>> handler) {
        if(idList==null || idList.isEmpty()) {
            return new ArrayList<>();
        }
        QueryWrapper<R> wrapper = new QueryWrapper<R>();
        wrapper.in(IdEntity.ID, idList);
        handler.accept(wrapper.lambda());
        return selectList(wrapper);
    }

    default boolean updateById(Long id, Consumer<LambdaUpdateWrapper<R>> handler) {
        LambdaUpdateWrapper<R> wrapper = createUpdateWrapper(handler);
        wrapper.eq(IdEntity::getId, id);
        return update(entityNewInstance(), wrapper) > 0;
    }

    default <V> V selectValueById(SFunction<R,V> mapper, Long id) {
        QueryWrapper<R> wrapper = createQueryWrapper(i -> i.eq(IdEntity.ID, id).lambda().select(mapper));
        return Optional.ofNullable(selectOne(wrapper)).map(mapper).orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    default boolean deleteBatchIds(String ids) {
        List<Long> list = Arrays.stream(ids.split(","))
                .map(Long::parseLong).toList();
        return deleteByIds(list) > 0;
    }
}
