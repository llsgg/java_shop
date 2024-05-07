package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Thing;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.service.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    @Autowired
    ThingMapper mapper;


    @Override
    public List<Thing> getThingList(String keyword, String c) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();

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

        List<Thing> things = mapper.selectList(queryWrapper);

        return things;
    }

    @Override
    public void createThing(Thing thing) {
        System.out.println(thing);
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        if (thing.getPv() == null) {
            thing.setPv("0");
        }
        if (thing.getScore() == null) {
            thing.setScore("0");
        }
        if (thing.getWishCount() == null) {
            thing.setWishCount("0");
        }
        mapper.insert(thing);
        // 更新tag
//        setThingTags(thing);
    }

    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateThing(Thing thing) {

        // 更新tag
//        setThingTags(thing);

        mapper.updateById(thing);
    }

    @Override
    public Thing getThingById(String id) {
        return mapper.selectById(id);
    }

    // 心愿数加1
    @Override
    public void addWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setWishCount(String.valueOf(Integer.parseInt(thing.getWishCount()) + 1));
        mapper.updateById(thing);
    }

    // 收藏数加1
    @Override
    public void addCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) + 1));
        mapper.updateById(thing);
    }



}
