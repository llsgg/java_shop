package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.Vo.DetailVo;
import com.gk.study.Vo.GoodsVo;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Good;
import com.gk.study.entity.SeckillGoods;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.GoodsService;
import com.gk.study.service.ISeckillGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/thing")
public class GoodsController {

    private final static Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    GoodsService service;

    @Autowired
    ISeckillGoodsService seckillGoodsService;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String c){
        List<Good> list =  service.getThingList(keyword, c);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/seckillList", method = RequestMethod.GET)
    public APIResponse seckillList(String keyword, String c){
        List<GoodsVo> list =  service.getThingList2(keyword, c);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Long id){
        GoodsVo goodsVo =  service.getGoodsVoById(id);
        

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();

        // 秒杀状态
        int secKillStatus = 0;
        int remainSecond = 0;
        // 秒杀倒计时
        if (nowDate.before(startDate)) {
            remainSecond = (int)((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSecond = -1;
        } else {
            secKillStatus = 1;
            remainSecond = 0;
        }
        DetailVo detailVo = new DetailVo();
//        detailVo.setUser(userId);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSecond);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", detailVo);
    }

//    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(GoodsVo goodsVo) throws IOException {
        Good good = new Good();
        good.setTitle(goodsVo.getTitle());
        good.setPrice(goodsVo.getPrice());
        good.setCount(goodsVo.getCount());
        good.setDescription(goodsVo.getDescription());
        good.setStatus(goodsVo.getStatus());
        good.setCreateTime(new Date().toString());
        good.setClassificationId(goodsVo.getClassificationId());
        String url = saveThing(good); // 存图片
        if(!StringUtils.isEmpty(url)) {
            good.cover = url;
        }
        service.createThing(good);

        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(good.getId());
        seckillGoods.setSeckillPrice(goodsVo.getSeckillPrice());
        seckillGoods.setStockCount(goodsVo.getStockCount());
        seckillGoods.setStartDate(goodsVo.getStartDate());
        seckillGoods.setEndDate(goodsVo.getEndDate());
        seckillGoodsService.save(seckillGoods);

        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }


//    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteThing(id);
            seckillGoodsService.remove(new QueryWrapper<SeckillGoods>().eq("goods_id", id));
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

//    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(GoodsVo goodsVo) throws IOException {

        Good good = new Good();
        good.setId(goodsVo.getId());
        good.setTitle(goodsVo.getTitle());
        good.setPrice(goodsVo.getPrice());
        good.setCount(goodsVo.getCount());
        good.setDescription(goodsVo.getDescription());
        good.setStatus(goodsVo.getStatus());
        good.setCreateTime(new Date().toString());
        good.setClassificationId(goodsVo.getClassificationId());
        String url = saveThing(good); // 存图片
        if(!StringUtils.isEmpty(url)) {
            good.cover = url;
        }
        service.updateThing(good);

        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsVo.getId());
        seckillGoods.setSeckillPrice(goodsVo.getSeckillPrice());
        seckillGoods.setStockCount(goodsVo.getStockCount());
        seckillGoods.setStartDate(goodsVo.getStartDate());
        seckillGoods.setEndDate(goodsVo.getEndDate());
        seckillGoodsService.update(seckillGoods, new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));

        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    public String saveThing(Good thing) throws IOException {
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {

            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        return newFileName;
    }

}
