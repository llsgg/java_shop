package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.Vo.GoodsVo;
import com.gk.study.entity.Good;
import com.gk.study.entity.GoodsCollect;
import com.gk.study.entity.SeckillGoods;
import com.gk.study.mapper.GoodsCollectMapper;
import com.gk.study.mapper.GoodsMapper;
import com.gk.study.mapper.GoodsVoMapper;
import com.gk.study.mapper.SeckillGoodsMapper;
import com.gk.study.service.GoodsCollectService;
import com.gk.study.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Good> implements GoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    GoodsVoMapper goodsVoMapper;
    @Autowired
    GoodsCollectMapper goodsCollectMapper;


    @Override
    public List<Good> getThingList(String keyword, String c) {
        QueryWrapper<Good> queryWrapper = new QueryWrapper<>();

        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 根据分类筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }

        List<Good> things = goodsMapper.selectList(queryWrapper);

        return things;
    }

    @Override
    public List<GoodsVo> getThingList2(String keyword, String c) {
        return goodsMapper.getGoodsVoByKeywordAndC(keyword, c);
    }

    @Override
    public void createThing(Good thing) {
        System.out.println(thing);
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        goodsMapper.insert(thing);
        // 更新tag
//        setThingTags(thing);
    }

    @Override
    public void deleteThing(String id) {
        goodsMapper.deleteById(id);
    }

    @Override
    public void updateThing(Good thing) {

        // 更新tag
//        setThingTags(thing);

        goodsMapper.updateById(thing);
    }

    @Override
    public List<GoodsVo> getGoodsVo() {
        return goodsMapper.getGoodsVo();
    }

    @Override
    public GoodsVo getGoodsVoById(Long id) {
        return goodsMapper.getGoodsVoById(id);
    }


    // 收藏数加1
    @Override
    public void addCollectCount(String thingId) {
        Good thing = goodsMapper.selectById(thingId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) + 1));
        goodsMapper.updateById(thing);
    }

    public void subCollectCount(String goodId) {
        Good thing = goodsMapper.selectById(goodId);
        thing.setCollectCount(String.valueOf(Integer.parseInt(thing.getCollectCount()) - 1));
        goodsMapper.updateById(thing);
    }

}
