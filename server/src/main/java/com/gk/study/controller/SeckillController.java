package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 秒杀
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/5/7 22:23
 */
@Controller
@RequestMapping("seckill")
@Slf4j
public class SeckillController {



    /**
     * @description:秒杀
     * Windos优化前QPS：936
     *       缓存QPS：1606
     *       优化QPS：2435
     * Linux优化前QPS：190.7
     * @author: yanhongwei
     * @date: 2024/4/12 20:24
     * @param: [model, user, goodsId]
     * @return: [model, user, goodsId]
     **/
//    @RequestMapping("/doSeckill2")
//    public String doSeckill2(Model model, User user, Long goodsId)  {
//        // 判断登录
//        if (user == null) return "login";
//        model.addAttribute("user", user);
//
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        // 判断库存
//        if (goods.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        // 判断是否重复抢购
//        SeckillOrder seckillOrder =
//                seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().
//                        eq("user_id", user.getId()).eq("goods_id", goodsId));
//
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }
//        Order order = orderService.seckill(user, goods);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goods);
//        return "orderDetail";
//    }
}
