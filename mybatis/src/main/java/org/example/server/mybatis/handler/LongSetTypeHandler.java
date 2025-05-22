package org.example.server.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Set;

@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({Set.class})
public class LongSetTypeHandler extends SetTypeHandler<Long> {
    @Override
    protected ListTypeHandler<Long> getListTypeHandler() {
        return new LongListTypeHandler();
    }
}
