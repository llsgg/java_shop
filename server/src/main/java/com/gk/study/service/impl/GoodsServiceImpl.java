package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Good;
import com.gk.study.mapper.GoodsMapper;
import com.gk.study.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Good> implements GoodsService {
    @Autowired
    GoodsMapper mapper;


    @Override
    public List<Good> getThingList(String keyword, String c) {
        QueryWrapper<Good> queryWrapper = new QueryWrapper<>();

        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

//        // 排序
//        if (StringUtils.isNotBlank(sort)) {
//            if (sort.equals("recent")) {
//                queryWrapper.orderBy(true, false, "create_time");
//            } else if (sort.equals("hot") || sort.equals("recommend")) {
//                queryWrapper.orderBy(true, false, "pv");
//            }
//        }else {
//            queryWrapper.orderBy(true, false, "create_time");
//        }

        // 根据分类筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }

        List<Good> things = mapper.selectList(queryWrapper);

        return things;
    }

    @Override
    public void createThing(Good thing) {
        System.out.println(thing);
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        mapper.insert(thing);
        // 更新tag
//        setThingTags(thing);
    }

    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateThing(Good thing) {

        // 更新tag
//        setThingTags(thing);

        mapper.updateById(thing);
    }

    @Override
    public Good getGoodById(String id) {
        return mapper.selectById(id);
    }

    // 收藏数加1
    @Override
    public void addCollectCount(String thingId) {
        Good thing = mapper.selectById(thingId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) + 1));
        mapper.updateById(thing);
    }



}