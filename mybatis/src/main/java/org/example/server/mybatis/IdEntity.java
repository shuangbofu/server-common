package org.example.server.mybatis;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class IdEntity<T extends Model<T>> extends Model<T> {
    public static final String ID = "id";
    @TableId(value = ID,type = IdType.AUTO)
    private Long id;
    public static final ColumnCache columnCache = new ColumnCache(ID,ID);
}
