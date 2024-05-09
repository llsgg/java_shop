package com.gk.study.service;


import com.gk.study.entity.GoodsCollect;

import java.util.List;
import java.util.Map;

public interface GoodsCollectService {
    List<Map> getThingCollectList(String userId);
    void createThingCollect(GoodsCollect goodsCollect);
    void deleteThingCollect(String id);
    GoodsCollect getThingCollect(String userId, String thingId);
}
