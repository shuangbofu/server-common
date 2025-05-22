package org.example.server.web.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BeanUtils {

    @SafeVarargs
    protected static <T> void arrayForeach(Consumer<T> consumer, T... ts) {
        Optional.ofNullable(ts)
                .filter(i -> i.length > 0)
                .map(Arrays::stream)
                .ifPresent(i -> i.forEach(consumer));
    }

    @SafeVarargs
    public static <A, B> B aToB(A a, Class<B> bClass, BiConsumer<A, B>... customMappers) {
        return aToB(a, bClass, false, customMappers);
    }

    @SafeVarargs
    public static <A, B> B aToBIgnoreId(A a, Class<B> bClass, BiConsumer<A, B>... customMappers) {
        return aToB(a, bClass, true, customMappers);
    }

    @SafeVarargs
    public static <A, B> B aToB(A a, Class<B> bClass, boolean ignoreId, BiConsumer<A, B>... customMappers) {
        B b = null;
        if (a == null) {
            return b;
        }
        try {
            b = bClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException ignored) {
        }
        return copyAToB(a, b, ignoreId, customMappers);
    }

    @SafeVarargs
    public static <A, B> B copyAToB(A a, B b, BiConsumer<A, B>... customMappers) {
        return copyAToB(a, b, false, customMappers);
    }

    @SafeVarargs
    public static <A, B> B copyAToBIgnoreId(A a, B b, BiConsumer<A, B>... customMappers) {
        return copyAToB(a, b, true, customMappers);
    }

    @SafeVarargs
    public static <A, B> B copyAToB(A a, B b, boolean ignoreId, BiConsumer<A, B>... customMappers) {
        if (a != null && b != null) {
            copyProperties(a, b, ignoreId ? "id" : null);
            arrayForeach(consumer -> consumer.accept(a, b), customMappers);
            return b;
        }
        return b;
    }

    @SafeVarargs
    public static <A, B> List<B> listAToListB(List<A> aList, Class<B> bClass, BiConsumer<A, B>... customMappers) {
        if (aList == null) {
            return new ArrayList<>();
        }
        return aList.stream().map(i -> aToB(i, bClass, customMappers)).collect(Collectors.toList());
    }

    @SafeVarargs
    public static <A, B> List<B> listAToListB(Collection<A> aList, Class<B> bClass, BiConsumer<A, B>... customMappers) {
        if (aList == null) {
            return new ArrayList<>();
        }
        return aList.stream().map(i -> aToB(i, bClass, customMappers)).collect(Collectors.toList());
    }

    public static void copyProperties(Object source, Object target, String... ignoredProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoredProperties);
    }
}