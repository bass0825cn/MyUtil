package com.sdc.model;

import java.io.File;
import java.sql.*;
import java.util.Date;
import com.sdc.util.FileUtil;

/**
 * 获取数据库表对应的类文件
 * Created by Administrator on 2016-05-10.
 */
public class GetModelOracle {
    private String packageOutPath = "com.project.model";
    private final String authorName = "sdc";
    private String lineSeparator = System.getProperty("line.separator", "/r/n");
    private String separatorChar = String.valueOf(File.separatorChar);

    private static String URL = "jdbc:oracle:thin:@127.0.0.1:1521:ORCL";
    private static final String drive = "oracle.jdbc.driver.OracleDriver";
    private static Connection conn;
    private static Statement pStemt;

    public GetModelOracle(String ip, String port, String sid, String user, String pass){
        setConnectString(ip, port, sid);
        if (!connect(user, pass)){
            System.out.println("连接数据库错误！");
            return;
        }
        generateModelClass("person");
    }
    /**
     * 设置数据库连接
     * @param ip    IP地址
     * @param port  端口号
     * @param sid   数据实例名
     */
    private void setConnectString(String ip, String port, String sid){
        URL = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + sid;
    }

    /**
     * 连接数据库
     * @param user 用户名
     * @param pass 密码
     * @return 连接成功，返回true；连接失败，返回false。
     */
    private boolean connect(String user, String pass){
        try {
            try {
                Class.forName(drive);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
                return false;
            }
            conn = DriverManager.getConnection(URL, user, pass);
            pStemt = conn.createStatement();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成表对应的类文件
     * @param tableName 数据库中的表名
     */
    private void generateModelClass(String tableName){
        String sql = "select * from " + tableName + " where 1=2";
        try {
            ResultSet rs = pStemt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] columnNames = new String[columnCount];
            String[] columnTypes = new String[columnCount];
            int[] columnSizes = new int[columnCount];
            boolean import_util = false;    //是否导入java.util包
            boolean import_sql = false;     //是否导入java.SQL包
            for (int i = 0; i < columnCount; i++){
                columnNames[i] = rsmd.getColumnName(i + 1).toLowerCase();
                columnTypes[i] = rsmd.getColumnTypeName(i + 1);
                columnSizes[i] = rsmd.getColumnDisplaySize(i + 1);
                if (columnTypes[i].equalsIgnoreCase("date") || columnTypes[i].equalsIgnoreCase("timestamp")){
                    import_util = true;
                }
                if (columnTypes[i].equalsIgnoreCase("blob") || columnTypes[i].equalsIgnoreCase("char")){
                    import_sql = true;
                }
            }
            String classStr = getClassString(tableName, columnNames, columnTypes, columnSizes, import_util, import_sql);
            String outPath = FileUtil.getCurrentPath();
            outPath = outPath + separatorChar+ "src" + separatorChar + this.packageOutPath.replace(".", separatorChar) +
                    separatorChar + convertFirstChar(tableName) + ".java";
            FileUtil.saveToFile(outPath, classStr);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {

        }
    }

    private void generateClasses(){

    }

    /**
     * 获取表对应类文件的文本
     * @param tableName     表名
     * @param columnNames   字段名列表
     * @param columnTypes   字段类型列表
     * @param columnSizes   字段长度列表
     * @param import_util   导入Date类型标识
     * @param import_sql    导入SQL包标识
     * @return 返回String类型
     */
    private String getClassString(String tableName, String[] columnNames, String[] columnTypes, int[] columnSizes, boolean import_util, boolean import_sql){
        StringBuffer sb = new StringBuffer();
        //包名
        sb.append("package " + packageOutPath + ";" + lineSeparator + lineSeparator);
        //导入工具包
        if (import_util){
            sb.append("import java.util.Date;" + lineSeparator);
        }
        if (import_sql){
            sb.append("import java.sql.*;" + lineSeparator);
        }
        //类注释
        sb.append("/**" + lineSeparator);
        sb.append(" * " + tableName + "实体类" + lineSeparator);
        sb.append(" * " + new Date() + " " + this.authorName + lineSeparator);
        sb.append(" */" + lineSeparator);
        //类定义
        sb.append(lineSeparator + "public class " + convertFirstChar(tableName) + "{" + lineSeparator + lineSeparator);
        getAttrs(sb, columnNames, columnTypes);
        getMethods(sb, columnNames, columnTypes);
        sb.append("}" + lineSeparator);
        return sb.toString();
    }

    /**
     * 把第一个字母转换为大写
     * @param str 需要转换的字符串
     * @return 返回String类型
     */
    private String convertFirstChar(String str){
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z'){
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 生成类中所有的属性
     * @param sb            StringBuffer类型，把类中所有的属性加入类文本
     * @param columnNames   String[]类型，字段名列表
     * @param columnTypes   String[]类型，字段类型列表
     */
    private void getAttrs(StringBuffer sb, String[] columnNames, String[] columnTypes){
        for (int i = 0; i < columnNames.length; i++){
            sb.append("\tprivate " + convertColumnType(columnTypes[i]) + " " + columnNames[i] + ";" + lineSeparator);
        }
        sb.append(lineSeparator);
    }

    /**
     * 生成类中所有方法
     * @param sb            StringBuffer类型，把类中所有的方法加入类文件
     * @param columnNames   字段名列表
     * @param columnTypes   字段类型列表
     */
    private void getMethods(StringBuffer sb, String[] columnNames, String[] columnTypes){
        for (int i = 0; i < columnNames.length; i++){
            sb.append("\tpublic void set" + convertFirstChar(columnNames[i]) + "(" + convertColumnType(columnTypes[i]) + " " +
                    columnNames[i] + "){" + lineSeparator);
            sb.append("\t\tthis." + columnNames[i] + " = " + columnNames[i] + ";" + lineSeparator);
            sb.append("\t}" + lineSeparator + lineSeparator);
            sb.append("\tpublic " + convertColumnType(columnTypes[i]) + " get" + convertFirstChar(columnNames[i]) + "(){" + lineSeparator);
            sb.append("\t\treturn " + columnNames[i] + ";" + lineSeparator);
            sb.append("\t}" + lineSeparator + lineSeparator);
        }
    }

    /**
     * 把数据库中的字段类型转换为Java的数据类型
     * @param dataType String类型，数据库的字段类型
     * @return String类型Java数据类型
     */
    private String convertColumnType(String dataType){
//        if (dataType.equalsIgnoreCase("binary_double")){
//            return "double";
//        }else if (dataType.equalsIgnoreCase("binary_float")){
//            return "float";
//        }else if (dataType.equalsIgnoreCase("blob")){
//            return "byte[]";
//        }else if (dataType.equalsIgnoreCase("char") || dataType.equalsIgnoreCase("nvarchar2")
//                || dataType.equalsIgnoreCase("varchar2")){
//            return "String";
//        }else if (dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("timestamp")
//                || dataType.equalsIgnoreCase("timestamp with local time zone") || dataType.equalsIgnoreCase("timestamp with time zone")){
//            return "Date";
//        }else if (dataType.equalsIgnoreCase("number")){
//            return "Long";
//        }
        return "String";
    }

    public static void main(String[] args){
//        if (args.length < 5){
//            System.out.println("运行程序至少需要5个参数");
//            System.out.println("第一个参数: 数据库的IP地址或主机名");
//            System.out.println("第二个参数: 数据库端口号");
//            System.out.println("第三个参数: 数据库实例名（SID）");
//            System.out.println("第四个参数: 数据库用户名");
//            System.out.println("第五个参数: 数据库密码");
//            System.out.println("例如: GetModel 127.0.0.1 1521 orcl scrot tiger");
//        }
        new GetModelOracle("127.0.0.1", "1521", "orcl", "jdyyfam", "jdyyfam");
        System.out.print("已生成java类");
    }
}
