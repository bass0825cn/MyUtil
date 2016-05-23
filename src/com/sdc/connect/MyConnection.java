package com.sdc.connect;

import com.sdc.util.Enums;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

/**
 * Oracle连接类
 * Created by Administrator on 2016-05-17.
 */
public class MyConnection {

    //使用连接池时，使用dataSource进行连接。
    private DataSource dataSource;
    private static Connection connection;
    private static Enums.DataBaseName dataBaseName;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void setConnection() {

    }

    public static Connection getConnection(){
        String driverString = "";
        String dbName, user, pass, url;
//        try {
//            Properties ps = new Properties();
//            ps.load(this.getClass().getResourceAsStream("/DBSetting.properties"));
//            dbName = ps.getProperty("DatabaseName");
//            user = ps.getProperty("Username");
//            pass = ps.getProperty("Password");
//            url = ps.getProperty("URL");
//        }catch (IOException e){
//            e.printStackTrace();
            dbName = "Oracle";
            user = "jdyyfam";
            pass = "jdyyfam";
            url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
//        }
        switch (dbName.toLowerCase()){
            case "oracle":
                driverString = "oracle.jdbc.driver.OracleDriver";
                dataBaseName = Enums.DataBaseName.Oracle;
                break;
            default:
                break;
        }
        try{
            try{
                Class.forName(driverString);
            }catch (ClassNotFoundException ec){
                System.out.println("驱动程序初始化失败！");
            }
            Properties ps = new Properties();
            ps.put("user", user);
            ps.put("password", pass);
            ps.put("remarksReporting", "true");
            connection = DriverManager.getConnection(url, ps);
        }catch (SQLException es){
            System.out.println("数据库连接失败！");
            connection = null;
        }
        return connection;
    }

    /**
     * 根据数据库类型转换日期型的格式。
     * @param date  Date类型，日期型的值
     * @return      String类型，转换后的值
     */
    public static String convertDateFormat(Date date){
        String resultString;
        switch (dataBaseName){
            case Oracle:
                resultString = "to_date(\'" + date.toString() + "',('yyyy-mm-dd hh24:mi:ss')";
                break;
            default:
                resultString = "'" + date.toString() + "'";
                break;
        }
        return resultString;
    }

    public static int execSQL(String sql){
        try{
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    public static ResultSet openSQL(String sql){
        try{
            connection = getConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
