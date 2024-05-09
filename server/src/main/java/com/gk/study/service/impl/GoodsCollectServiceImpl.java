package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.GoodsCollect;
import com.gk.study.mapper.GoodsCollectMapper;
import com.gk.study.service.GoodsCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class GoodsCollectServiceImpl extends ServiceImpl<GoodsCollectMapper, GoodsCollect> implements GoodsCollectService {
    @Autowired
    GoodsCollectMapper mapper;

    @Override
    public List<Map> getThingCollectList(String userId) {
        return mapper.getThingCollectList(userId);
    }

    @Override
    public void createThingCollect(GoodsCollect goodsCollect) {
        mapper.insert(goodsCollect);;
    }

    @Override
    public void deleteThingCollect(String id) {
        mapper.deleteById(id);
    }

    @Override
    public GoodsCollect getThingCollect(String userId, String thingId) {
        QueryWrapper<GoodsCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
}
