package com.sdc.connect;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2016-05-26.
 * 连接池
 */
public class MyDataSource implements DataSource {
    private Logger logger;
    private String driverClassName;
    private String url;
    private Properties ps = new Properties();

    private LinkedList<Connection> pool = (LinkedList<Connection>) Collections.synchronizedList(new LinkedList<Connection>());
//    private MyDataSource instance = new MyDataSource();

    private MyDataSource(String dbName, String user, String pass, String url){
        logger = Logger.getLogger(this.getClass().getName());
        this.url = url;
        switch (dbName.toUpperCase()){
            case "ORACLE":
                driverClassName = "oracle.jdbc.driver.OracleDriver";
                break;
            default:
                break;
        }
        try{
            Class.forName(driverClassName);
        }catch (ClassNotFoundException e){
            logger.info("Connection Driver Class is not found");
        }
        ps.setProperty("user", user);
        ps.setProperty("password", pass);
    }

    public Connection getConnection() throws SQLException{
        synchronized (pool){
            if (pool.size() > 0){
                return pool.removeFirst();
            }else{
                return makeConnection();
            }
        }
    }

    public void freeConnection(Connection conn){
        pool.addLast(conn);
    }

    private Connection makeConnection() throws SQLException{
        return DriverManager.getConnection(url, ps);
    }

    public Connection getConnection(String username, String password) throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }

    public PrintWriter getLogWriter() throws SQLException{
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException{

    }

    public void setLoginTimeout(int seconds) throws SQLException{

    }

    public int getLoginTimeout() throws SQLException{
        return 0;
    }

    public <T>T unwrap(Class<T> iface) throws SQLException{
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException{
        return false;
    }

    public Logger getParentLogger(){
        return null;
    }
}
