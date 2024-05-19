package com.gk.study.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gk.study.common.APIResponse;
import com.gk.study.entity.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


/**	java.util.List;
 * 生成用户工具类 *
 * @author zhoubin
 * @since 1.0.0 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
//生成用户

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
//            user.setLoginCount(1);
            user.setNickname("user" + i);
//            user.setSlat("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", "abcd1234"));
            users.add(user);


        }
        System.out.println("create user");
//////插入数据库
//        Connection conn = getConn();
//        String sql = "insert into b_user(nickname, password, id)values(?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for (int i = 0; i < users.size(); i++) {
//            User user = users.get(i);
////            pstmt.setInt(1, user.getLoginCount());
//            pstmt.setString(1, user.getNickname());
////            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
////            pstmt.setString(3, user.getSlat());
//            pstmt.setString(2, user.getPassword());
//            pstmt.setLong(3, user.getId());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.close();
//        conn.close();
//        System.out.println("insert to db");
//登录，生成token
        String urlString = "http://localhost:9100/api/user/test" ;
        File file = new File("C:\\Users\\ll\\Desktop\\config_windows2.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection(); co.setRequestMethod("GET");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "userId=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("123456", "abcd1234");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream .read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();

            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            APIResponse respBean = mapper.readValue(response, APIResponse.class);
            String userTicket = ((String) respBean.getData());
            System.out.println("create userTicket : " + user.getId() + " : " + userTicket);

            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();

        System.out.println("over");
    }

    private static Connection getConn() throws Exception {
        String url = "jdbc:mysql://localhost:3306/shop?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager .getConnection(url, username, password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}