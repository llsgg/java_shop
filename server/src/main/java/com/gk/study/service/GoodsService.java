package com.gk.study.service;


import com.gk.study.Vo.GoodsVo;
import com.gk.study.entity.Good;

import java.util.List;

public interface GoodsService {
    List<Good> getThingList(String keyword, String c);
    List<GoodsVo> getThingList2(String keyword, String c);
    void createThing(Good thing);
    void deleteThing(String id);

    void updateThing(Good thing);

    List<GoodsVo> getGoodsVo();

    GoodsVo getGoodsVoById(Long id);

    void addCollectCount(String thingId);
    public void subCollectCount(String goodId);
}
