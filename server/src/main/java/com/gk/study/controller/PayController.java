package com.gk.study.controller;

/**
 * 支付
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/5/17 21:11
 */
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gk.study.entity.Order;
import com.gk.study.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.alipay.api.internal.util.AlipaySignature.getSignCheckContentV1;
import static com.alipay.api.internal.util.AlipaySignature.rsaCheck;

/**
 * Created by jiafa
 * on 2021/8/6 23:20
 */
@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {
    private final String APP_ID = "9021000137616185";
    private final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCsqPh1CEVrYV2QXWJWce5xqnLT3jI5cST/5+J3j1vXIte2TZmcxNh1s8eODWyckKnoILqOQEWjDxJ+kEDgylP6EyUMQa0yvWywAsU6kFI3skElpH7IIAsvUAfjE+x/mTnyRof4oQojCiu1KMNVkG/1cLtM+PBW92gnBj7AhVA3Zj8VXiTV7vckMOErrjE1OAjvLxeeHEBVyDUJ80TuXvGr70s8nnNbcNVRrqSEVVWj5wTE3AophKE9tUiE9Jq3Sb9GH8WdlXgCsTnYhc8Q8Tr7DGLkuXAzW0cDBOj67COpaSuiZTukzloj2k8XHc5RCC2WYbPZ4vm5cWWlB3zc5rLXAgMBAAECggEAVpbq+65pOh/7T2I15gp/PYvwyGY/o+UlXB2LytAfi/YSXn/uUNu7ftPpeUksNhyqyRpY1OxQwCTX7pcjolh14w1k4A+z76Vl86OADA3C3eHMoDctyemgEls9iVk9mwRaNjG/ESaaMdZw8wp9Y9TrARlHJaQWzFaJakDMibqR7yXJ1/FWZy4Eqns8pAnWRod9EJWOjGKIUrERqTw6hms3dq7TUwMZ4v0/7poD91heMds6ticWRsRg/RSgAhM8hLP37OHcb8AP1Sr5SAj3inDnd4MAnBavLpZGq24j1JI7fvQJkP4DzexbLwQ1xM0F8/RVC8lsGLrkySaPRyrYMmK2cQKBgQDb99LnMxJOokZr45nESx5Ylfn8OAVkaLimYXRqKoyRhGWJDdtOUm1741niJTDtxRHtZBjCyZF2ZeDyq+tFXpLhgyaHqePUPCqok8LwD/XplJXhQPlsElNM270Hoo4P6vPOjKwoeF5PNHC8Po/uwoRK7gQVMVzpcUESXJudkyexLwKBgQDI8VMUYbwv7izrof3tr8VGcvfwcW3tK0anVsxDX55kmE32aq/yFjzZHd8YPgFd5Boq0Pbmlxkz6eUirTa9CqJIbMZWr1eCUfEf9GKKW6JQbGL8fowzqblWdnkC0w0QRMZpXCttIvsJN/6l+byf481Z1fNtBptQ0DByVPPvUuIe2QKBgG2ljv+oOYvw3+GPxmpOd4X43G+ZDTcL7F8jXQA+zWsL8ADtY1AbTm/e2EMd/tHG1jTbHGycRWIbTpIomFIv3k+DLIWbbfGxPCJj5Vv82LviUQPDe5EfYV6CeiuAIAhXOcjP3lEeDwS0Qy3SkbSWSZOAKZDW1lWXGrSobPmTDMoJAoGAbfi86iuvDWBBT1ghm6KVyb+D+CBr89+6VDzJn5nGGvHLwS+USV5oQL77aM4Oe28p7wLcDUjmbyXz1C2OBpm6RP7a8XgW4WitGTrrbyNFStR2vA8Z9fBOUYaXJbeRr7SkDed2kM0TLHFruTRbSfonWaIhDisOd4To7rmVowXuYMkCgYEA1VPS6VfuWz+T62xMFWkwta1z4OVIjIpP9ZQLgbefviI+R8e5DRJL0eIdd0M4lUMmZgLvbwEIJJ+USYvcCsc4he+nHPhwPAGrMj3sovRefd5UfJJr5DduKTnS8cKgKLTE3ZKzkBsYnJu986th9+fVzWJ7Rxtbe2Q1R2rcpzhvzgo=";
    private final String CHARSET = "UTF-8";
    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArKj4dQhFa2FdkF1iVnHucapy094yOXEk/+fid49b1yLXtk2ZnMTYdbPHjg1snJCp6CC6jkBFow8SfpBA4MpT+hMlDEGtMr1ssALFOpBSN7JBJaR+yCALL1AH4xPsf5k58kaH+KEKIwortSjDVZBv9XC7TPjwVvdoJwY+wIVQN2Y/FV4k1e73JDDhK64xNTgI7y8XnhxAVcg1CfNE7l7xq+9LPJ5zW3DVUa6khFVVo+cExNwKKYShPbVIhPSat0m/Rh/FnZV4ArE52IXPEPE6+wxi5LlwM1tHAwTo+uwjqWkromU7pM5aI9pPFx3OUQgtlmGz2eL5uXFlpQd83Oay1wIDAQAB";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private final String FORMAT = "JSON";
    //签名方式
    private final String SIGN_TYPE = "RSA2";
    //支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    private final String NOTIFY_URL = "http://tki8v7.natappfree.cc/api/pay/returnUrl";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private final String RETURN_URL = "http://localhost:3000/#/index/usercenter/orderView";

    @Autowired
    private OrderService orderService;

    @RequestMapping("alipay")
    public void alipay(Long orderId, HttpServletResponse httpResponse) throws IOException {
        //实例化客户端,填入所需参数
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //在公共参数中设置回跳和通知地址
        request.setReturnUrl(RETURN_URL);
        request.setNotifyUrl(NOTIFY_URL);
        //根据订单编号,查询订单相关信息
        Order order = orderService.getOrderById(orderId);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = orderId.toString();
        //付款金额，必填
        String total_amount = order.price.toString();
        //订单名称，必填
        String subject = "订单测试";
        //商品描述，可空
        String body = order.goodId.toString();
        request.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    @RequestMapping(value = "/returnUrl", method = RequestMethod.POST)
    public String returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        System.out.println("=================================同步回调=====================================");
        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
            params.put(name, valueStr);
        }

        System.out.println(params);//查看参数都有哪些
//        boolean signVerified = rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE); // 调用SDK验证签名
//验证签名通过
        if(true){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            // 支付时间
//            Date pay_time = new Date(new String(request.getParameter("gmt_payment").getBytes("ISO-8859-1"), "UTF-8"));
            Date pay_time = new Date();
            System.out.println("商户订单号="+out_trade_no);
            System.out.println("支付宝交易号="+trade_no);
            System.out.println("付款金额="+total_amount);

            //支付成功，修复支付状态
             orderService.updateOrderStatus(Long.valueOf(out_trade_no), 2, pay_time);
            return "ok";//跳转付款成功页面
        }else{
            return "no";//跳转付款失败页面
        }
    }
    /**
     * 如果是RSA或RSA2签名，请调用此方法进行验签
     *
     * @param params    待验签的从支付宝接收到的参数Map
     * @param publicKey 支付宝公钥
     * @param charset   参数内容编码集
     * @param signType  指定采用的签名方式，RSA或RSA2
     * @return true：验签通过；false：验签不通过
     * @throws AlipayApiException
     */
    public static boolean rsaCheckV1(Map<String, String> params, String publicKey,
                                     String charset, String signType) throws AlipayApiException {
        String sign = params.get("sign");
        String content = getSignCheckContentV1(params);

        return rsaCheck(content, sign, publicKey, charset, signType);
    }
}
