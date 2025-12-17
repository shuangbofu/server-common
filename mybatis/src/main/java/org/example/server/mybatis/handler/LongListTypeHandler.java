package org.example.server.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({List.class})
public class LongListTypeHandler extends NumberListTypeHandler<Long> {

    @Override
    protected Long str2Num(String str) {
        return Long.parseLong(str);
    }

    @Override
    protected String num2Str(Long number) {
        return number.toString();
    }
}
