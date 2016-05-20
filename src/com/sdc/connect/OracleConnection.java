package com.sdc.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Oracle连接类
 * Created by Administrator on 2016-05-17.
 */
public class OracleConnection {

    public static Connection connection;

    public OracleConnection(String ip, String port, String sid, String user, String pass){
        try{
            try{
                Class.forName("oracle.jdbc.driver.OracleDriver");
            }catch (ClassNotFoundException ec){
                System.out.println("驱动程序初始化失败！");
            }
            Properties ps = new Properties();
            ps.put("user", user);
            ps.put("password", pass);
            ps.put("remarksReporting", "true");
            String URL = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + sid;
            connection = DriverManager.getConnection(URL, ps);
        }catch (SQLException es){
            System.out.println("数据库连接失败！");
            connection = null;
        }
    }
}
