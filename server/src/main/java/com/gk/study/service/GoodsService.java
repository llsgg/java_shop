package com.gk.study.service;


import com.gk.study.Vo.GoodsVo;
import com.gk.study.entity.Good;

import java.util.List;

public interface GoodsService {
    List<Good> getThingList(String keyword, String c);
    void createThing(Good thing);
    void deleteThing(String id);

    void updateThing(Good thing);

    GoodsVo getGoodsVoById(Long id);

    void addCollectCount(String thingId);
}
