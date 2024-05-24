package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.GoodsCollect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsCollectMapper extends BaseMapper<GoodsCollect> {

    List<Map> getThingCollectList(String userId);
    GoodsCollect getCollectById(String id);
}
