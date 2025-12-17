package org.example.server.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CommonUtils {

    public static <T> void notEmpty(List<T> list, Consumer<List<T>> consumer) {
        if(list!=null && !list.isEmpty()) {
            consumer.accept(list);
        }
    }

    public static <T,R> List<T> notEmpty(List<R> list, Supplier<List<T>> supplier) {
        if(list.isEmpty()) {
            return new ArrayList<>();
        }
        return supplier.get();
    }
}
