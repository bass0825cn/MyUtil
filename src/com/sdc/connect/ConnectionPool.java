package com.sdc.connect;


import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 连接池
 * Created by Administrator on 2016-05-27.
 */
public class ConnectionPool {
    private String driverName = "";                     //数据库驱动
    private String url = "";                            //数据库地址
    private String user = "";                           //用户名
    private String pass = "";                           //密码
    private String testTable = "";                      //测试表名
    private int initialConnections = 10;                //初始连接数
    private int incrementalConnections = 5;             //每次增加连接数
    private int maxConnections = 50;                    //最大连接数，为0或者负数时，为不限制连接数
    private Vector connections = null;                  //存放数据库连接的向量

    /**
     * 构造函数，设置数据库连接参数
     * @param driverName    String  数据库驱动
     * @param url           String  数据库地址
     * @param user          String  用户名
     * @param pass          String  密码
     */
    public ConnectionPool(String driverName, String url, String user, String pass){
        this.driverName = driverName;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    /**
     * 取测试表名
     * @return  String  测试表名
     */
    public String getTestTable() {
        return testTable;
    }

    /**
     * 设置测试表名
     * @param testTable String  测试表名
     */
    public void setTestTable(String testTable) {
        this.testTable = testTable;
    }

    /**
     * 取连接池初始连接数
     * @return  int 初始连接数
     */
    public int getInitialConnections() {
        return initialConnections;
    }

    /**
     * 设置连接池初始连接数
     * @param initialConnections    int 连接数
     */
    public void setInitialConnections(int initialConnections) {
        this.initialConnections = initialConnections;
    }

    /**
     * 获取每次增加连接数
     * @return  int 每次增加连接数
     */
    public int getIncrementalConnections() {
        return incrementalConnections;
    }

    /**
     * 设置每次增加连接数
     * @param incrementalConnections    int 每次增加连接数
     */
    public void setIncrementalConnections(int incrementalConnections) {
        this.incrementalConnections = incrementalConnections;
    }

    /**
     * 获取连接池最大连接数
     * @return  int 连接数
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置连接池最大连接数
     * @param maxConnections    int 最大连接数
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 创建连接池，按初始连接数，创建可用连接
     * @throws Exception
     */
    public synchronized void createPool() throws Exception{
        if (connections != null){
            return;
        }
        Driver driver = (Driver) (Class.forName(this.driverName)).newInstance();
        DriverManager.registerDriver(driver);
        connections = new Vector();
        createConnections(this.initialConnections);
        System.out.println("Connection Pool is Created!");
    }

    /**
     * 按指定的数量创建可用连接，存入connections向量中
     * @param num   创建连接数量
     */
    @SuppressWarnings("unchecked")
    private void createConnections(int num) throws SQLException{
        for (int i = 0; i < num; i++){
            //如果限制连接数，并且当前连接数大于或者等于最大连接数，则退出
            if (this.maxConnections > 0 && this.connections.size() >= this.maxConnections){
                break;
            }
            try {
                connections.addElement(new PooledConnection(newConnection()));
            }catch (SQLException e){
                System.out.println("Database connect error!");
                throw new SQLException();
            }
        }
    }

    /**
     * 创建一个新的数据库连接
     * @return  Connection类型
     * @throws SQLException
     */
    private Connection newConnection() throws SQLException{
        Connection conn = DriverManager.getConnection(url, user, pass);
        if (connections.size() == 0){
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            int dbMaxConnections = databaseMetaData.getMaxConnections();
            if (dbMaxConnections > 0 && this.maxConnections > dbMaxConnections){
                this.maxConnections = dbMaxConnections;
            }
        }
        return conn;
    }

    /**
     * 获取一个可用的数据库连接，如果第一次获取不到，等0.5秒，
     * 再获取一次，如果两次都获取不到，则返回null
     * @return  Connection类型
     * @throws SQLException
     */
    public synchronized Connection getConnection() throws SQLException{
        if (connections == null){
            return null;
        }
        Connection conn = getFreeConnection();
        while (conn == null){
            wait(500);
            conn = getFreeConnection();
        }
        return conn;
    }

    /**
     * 从连接池中返回一个可用连接，如果没有返回可以连接，则按增量创建一些连接，
     * 然后获取连接，如果还是获取不到，则返回null
     * @return  Connection类型
     * @throws SQLException
     */
    private Connection getFreeConnection() throws SQLException{
        Connection conn = findFreeConnection();
        if (conn == null){
            createConnections(this.incrementalConnections);
            conn = findFreeConnection();
            if (conn == null){
                return null;
            }
        }
        return conn;
    }

    /**
     * 从连接池中找一个可用的连接，如果没有可用连接返回null
     * @return  Connection类型
     * @throws SQLException
     */
    private Connection findFreeConnection() throws SQLException{
        Connection conn = null;
        PooledConnection pConn;
        Enumeration enumeration = connections.elements();
        while (enumeration.hasMoreElements()){
            pConn = (PooledConnection)enumeration.nextElement();
            if (!pConn.isBusy()) {
                conn = pConn.getConnection();
                pConn.setBusy(true);
                if (!testConnection(conn)) {
                    try {
                        conn = newConnection();
                    } catch (SQLException e) {
                        System.out.println("Create Database Connection Error!");
                        return null;
                    }
                    pConn.setConnection(conn);
                }
                break;
            }
        }
        return conn;
    }

    /**
     * 测试数据连接是否正常
     * @param conn  Connection类型 数据库连接
     * @return      boolean类型
     */
    private boolean testConnection(Connection conn){
        try{
            if (testTable.equals("")){
                conn.setAutoCommit(true);
            }else{
                Statement stmt = conn.createStatement();
                stmt.execute("select count(*) from " + testTable);
            }
        }catch (SQLException e){
            closeConnection(conn);
            return false;
        }
        return true;
    }

    /**
     * 把一个数据库连接返还到连接池，修改busy为false
     * @param conn  Connection类型    数据库连接
     */
    public void returnConnection(Connection conn){
        if (connections == null){
            System.out.println("ConnectionPool is not exist, Connection cannot return!");
            return;
        }
        PooledConnection pConn;
        Enumeration enumeration = connections.elements();
        while(enumeration.hasMoreElements()){
            pConn = (PooledConnection)enumeration.nextElement();
            if (conn == pConn.getConnection()){
                pConn.setBusy(false);
                break;
            }
        }
    }

    /**
     * 刷新连接池中的数据库连接
     * @throws SQLException
     */
    public synchronized void refreshConnection() throws SQLException{
        if (connections == null){
            System.out.println("ConnectionPool is not exist");
            return;
        }
        PooledConnection pConn;
        Enumeration enumeration = connections.elements();
        while (enumeration.hasMoreElements()){
            pConn = (PooledConnection)enumeration.nextElement();
            //如果连接被占用，等待5秒后，直接刷新
            if (pConn.isBusy()){
                wait(5000);
            }
            closeConnection(pConn.getConnection());
            pConn.setConnection(newConnection());
            pConn.setBusy(false);
        }
    }

    /**
     * 关闭连接池
     * @throws SQLException
     */
    public synchronized void closeConnectionPool() throws SQLException{
        if (connections == null){
            System.out.println("ConnectionPool is not exist");
            return;
        }
        PooledConnection pConn;
        Enumeration enumeration = connections.elements();
        while (enumeration.hasMoreElements()){
            pConn = (PooledConnection)enumeration.nextElement();
            if (pConn.isBusy()){
                wait(5000);
            }
            closeConnection(pConn.getConnection());
            connections.removeElement(pConn);
        }
        connections = null;
    }

    private void closeConnection(Connection conn){
        try{
            conn.close();
        }catch (SQLException e){
            System.out.println("Close Connection Error:" + e.getMessage());
        }
    }

    /**
     * 设置等待时间
     * @param seconds   int类型   等时间，单位毫秒。
     */
    private void wait(int seconds){
        try{
            Thread.sleep(seconds);
        }catch (InterruptedException e){
            System.out.println("wait() Error!");
        }
    }

  /**
     * 保存连接池中连接对象的类
     */
    class PooledConnection{
        Connection connection = null;
        boolean busy = false;

        /**
         * 构造函数，根据Connection构造PooledConnection
         * @param connection    Connection类型
         */
        public PooledConnection(Connection connection){
            this.connection = connection;
        }

        /**
         * 获取回一个连接
         * @return  Connection类型
         */
        public Connection getConnection(){
            return this.connection;
        }

        /**
         * 设置一个连接
         * @param connection    Connection类型
         */
        public void setConnection(Connection connection){
            this.connection = connection;
        }

        /**
         * 获取该对象状态。
         * @return  boolean类型   对象状态
         */
        public boolean isBusy(){
            return busy;
        }

        /**
         * 设置对象状态
         * @param busy  boolean类型   对象状态
         */
        public void setBusy(boolean busy){
            this.busy = busy;
        }
    }
}
