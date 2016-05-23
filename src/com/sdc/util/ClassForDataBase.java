package com.sdc.util;

import com.project.model.Wzlymb;
import com.sdc.connect.OracleConnection;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 利用反射机制，对数据库进行增、删、改、查。
 * Created by Administrator on 2016-05-19.
 */
public class ClassForDataBase {

    private Enums.DataBaseName dataBaseName;
    private Connection connection;

    /**
     * 初始数据库类型
     * @param dbName    String类型，数据库名称，Oracle mysql,PostgreSQL,sql server等。
     */
    public ClassForDataBase(String dbName){
        if (dbName.toUpperCase().equals("MYSQL")){
            this.dataBaseName = Enums.DataBaseName.MySQL;
        }else if (dbName.toUpperCase().equals("SQL SERVER")){
            this.dataBaseName = Enums.DataBaseName.SQLServer;
        }else if (dbName.toUpperCase().equals("POSTGRESQL")){
            this.dataBaseName = Enums.DataBaseName.PostgreSQL;
        }else{
            this.dataBaseName = Enums.DataBaseName.Oracle;
            connection = OracleConnection.connection;
        }
    }

    /**
     * 获取Model对象实例的属性名，及属性值
     * @param o  Object类型，对象实例。
     * @return  Map类型，把对象实例中的属性名和属性值加入Map中。
     */
    private Map<String, String> getColumnsValues(Object o){
        Map<String, String> cvs = new HashMap<>();
        Class c = o.getClass();
        Method[] methods = c.getMethods();
        for (Method method: methods){
            String mName = method.getName();
            if (mName.startsWith("get") && !mName.startsWith("getClass") && !mName.startsWith("getPrimaryKeys")){
                String fieldName = mName.substring(3, mName.length());
                String fieldValue = "";
                try{
                    Object[] args = new Object[]{};
                    Object v = method.invoke(o, args);
                    if (v instanceof Date){
                        fieldValue = convertDateFormat((Date) v);
                    }else{
                        fieldValue = "'" + v + "'";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                cvs.put(fieldName, fieldValue);
            }
        }
        return cvs;
    }

    /**
     * 获取表的主键，存在Model类中的primaryKeys属性中。
     * @param o Object类型，对象实例
     * @return  String[]类型，返回主键字段。
     */
    private String[] getPrimaryKeys(Object o){
        Class c = o.getClass();
        try {
            Class<?>[] cs = new Class<?>[]{};
            Method method = c.getMethod("getPrimaryKeys", cs);
            Object[] args = new Object[]{};
            return (String[]) method.invoke(o, args);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取表名，表名为对象的类名
     * @param o Object类型，对象实例
     * @return  String类型，返回表名
     */
    private String getTableName(Object o){
        String cName = o.getClass().getName();
        return cName.substring(cName.lastIndexOf(".") + 1, cName.length());
    }

    /**
     * 生成Insert语句，要求数据中的表名，与Model的类名相同，表中的字段名与Model中的属性名相同。
     * @param o Object类型，Model对象的实例。
     * @return  String类型，Insert语句
     */
    public String getInsertSQL(Object o){
        String sql = "insert into ";
        String tableName = getTableName(o);
        sql += tableName + "(";
        Map<String, String> cvs = getColumnsValues(o);
        String values = ") values(";
        for (String key: cvs.keySet()){
            String value = cvs.get(key);
            sql += key + ",";
            values += value + ",";
        }
        sql = sql.substring(0, sql.length() - 1) + values.substring(0, values.length() - 1) + ")";
        return sql;
    }

    /**
     * 生成update语句，要求数据中的表名，与Model的类名相同，表中的字段名与Model中的属性名相同。
     * @param o Object类型，Model对象的实例。
     * @return  String类型，update语句。
     */
    public String getUpdateSQL(Object o){
        String tableName = getTableName(o);
        String sql = "update " + tableName + " set ";
        Map<String, String> cvs = getColumnsValues(o);
        for (String key: cvs.keySet()){
            String value = cvs.get(key);
            sql += key + "=" + value + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        //获取表的主键
        String[] primaryKeys = getPrimaryKeys(o);
        if (primaryKeys == null){
            return "have not primary key";
        }
        if (primaryKeys.length <= 0){
            return "have not primary key";
        }
        String whereStr = " where ";
        for (String key: primaryKeys){
            String value = cvs.get(key);
            whereStr += key + "=" + value + " and ";
        }
            whereStr = whereStr.substring(0, whereStr.lastIndexOf(" and ") - 1);
            sql += whereStr;
        return sql;
    }

    /**
     * 生成delete语句，要求数据库中的表名，与Model类型名相同，表中的字段名与属性名相同。
     * @param o Object类型，对象的实例。
     * @return  String类型，delete语句 。
     */
    public String getDeleteSQL(Object o){
        String tableName = getTableName(o);
        String sql = "delete from " + tableName;
        Map<String, String> cvs = getColumnsValues(o);
        //获取表的主键
        String[] primaryKeys = getPrimaryKeys(o);
        if (primaryKeys == null){
            return "have not primary key";
        }
        if (primaryKeys.length <= 0){
            return "have not primary key";
        }
        String whereStr = " where ";
        for (String key: primaryKeys){
            String value = cvs.get(key);
            whereStr += key + "=" + value + " and ";
        }
        whereStr = whereStr.substring(0, whereStr.lastIndexOf(" and "));
        sql += whereStr;
        return sql;
    }

    /**
     * 生成select语句，表名要和Model类名一致，字段名要与属性名相同。
     * @param o Object类型，对象的实例
     * @return  String类型，select语句
     */
    private String getSelectSQL(Object o){
        String sql = "select ";
        Map<String, String> cvs = getColumnsValues(o);
        for (String key: cvs.keySet()){
            sql += key + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        String[] primaryKeys = getPrimaryKeys(o);
        if (primaryKeys == null || primaryKeys.length <= 0){
            return "not have primary key";
        }
        String whereStr = " where ";
        for (String pk: primaryKeys){
            whereStr += pk + "=" + cvs.get(pk) + " and ";
        }
        whereStr = whereStr.substring(0, whereStr.lastIndexOf(" and "));
        sql += whereStr;
        return sql;
    }

    public boolean saveToDataBase(){
        return true;
    }

    /**
     * 根据数据库类型转换日期型的格式。
     * @param date  Date类型，日期型的值
     * @return      String类型，转换后的值
     */
    private String convertDateFormat(Date date){
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

    public int executeSQL(String sql){
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    public ResultSet selectSQL(String sql){
        try{
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args){
        Wzlymb wzlymb = new Wzlymb();
        wzlymb.setDJID("WZLY16051000");
        wzlymb.setCLASS("1");
        wzlymb.setKDRQ("2016-05-01");
        wzlymb.setLYDEPT("0101");
        wzlymb.setLYR("sdc");
        wzlymb.setZDKS("0201");
        wzlymb.setZDR("sdc");
        wzlymb.setZJE(1000L);
        wzlymb.setRZBZ("1");
        ClassForDataBase classForDataBase = new ClassForDataBase("oracle");
        String sql = classForDataBase.getInsertSQL(wzlymb);
        System.out.println("insert sql--->" + sql);
        sql = classForDataBase.getUpdateSQL(wzlymb);
        System.out.println("update sql--->" + sql);
        sql = classForDataBase.getDeleteSQL(wzlymb);
        System.out.println("delete sql--->" + sql);
        sql = classForDataBase.getSelectSQL(wzlymb);
        System.out.println("select sql--->" + sql);
    }
}
