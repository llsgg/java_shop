package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.Vo.GoodsVo;
import com.gk.study.entity.Good;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsMapper extends BaseMapper<Good> {
//    List<Thing> getList();
//    boolean update(Thing thing);
    List<GoodsVo> getGoodsVo();
    GoodsVo getGoodsVoById(Long id);
}
