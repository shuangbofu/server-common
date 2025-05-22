package org.example.server.mybatis.handler;

import java.util.List;
import java.util.stream.Collectors;

public abstract class NumberListTypeHandler<T extends Number> extends ListTypeHandler<T> {

    @Override
    protected List<T> str2List(String str) {
        return getStringList(str).stream()
                .map(this::str2Num)
                .collect(Collectors.toList());
    }

    @Override
    protected String list2Str(List<T> list) {
        return strListToString(list.stream()
                .map(this::num2Str)
                .collect(Collectors.toList()));
    }

    protected abstract T str2Num(String str);

    protected abstract String num2Str(T number);
}
