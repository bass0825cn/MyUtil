package com.project.model;

import com.sdc.connect.MyConnection;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Model基类
 * Created by Administrator on 2016-05-12.
 */
abstract class BaseModel {

    public abstract String[] getPrimaryKeys();

    public BaseModel(){

    }

    /**
     * 获取Model对象实例的属性名，及属性值
     * @return  Map类型，把对象实例中的属性名和属性值加入Map中。
     */
    private Map<String, String> getColumnsValues(){
        Map<String, String> cvs = new HashMap<>();
        Class c = this.getClass();
        Method[] methods = c.getMethods();
        for (Method method: methods){
            String mName = method.getName();
            if (mName.startsWith("get") && !mName.startsWith("getClass") && !mName.startsWith("getPrimaryKeys")){
                String fieldName = mName.substring(3, mName.length());
                String fieldValue = "";
                try{
                    Object[] args = new Object[]{};
                    Object v = method.invoke(this, args);
                    if (v instanceof Date){
                        fieldValue = MyConnection.convertDateFormat((Date) v);
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
     * 生成select语句，表名要和Model类名一致，字段名要与属性名相同。
     * @return  String类型，select语句
     */
    private String generatorSelectSQL(){
        String sql = "select ";
        Map<String, String> cvs = getColumnsValues();
        for (String key: cvs.keySet()){
            sql += key + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        String[] primaryKeys = getPrimaryKeys();
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

    /**
     * 获取表名，表名为对象的类名
     * @return  String类型，返回表名
     */
    private String generatorTableName(){
        String tableName = this.getClass().getName();
        return tableName.substring(tableName.lastIndexOf(".") + 1, tableName.length());
    }

    /**
     * 生成Insert语句，要求数据中的表名，与Model的类名相同，表中的字段名与Model中的属性名相同。
     * @return  String类型，Insert语句
     */
    private String generatorInsertSQL(){
        String sql = "insert into ";
        String tableName = generatorTableName();
        sql += tableName + "(";
        Map<String, String> cvs = getColumnsValues();
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
     * 生成delete语句，要求数据库中的表名，与Model类型名相同，表中的字段名与属性名相同。
     * @return  String类型，delete语句 。
     */
    private String generatorDeleteSQL(){
        String tableName = generatorTableName();
        String sql = "delete from " + tableName;
        Map<String, String> cvs = getColumnsValues();
        //获取表的主键
        String[] primaryKeys = getPrimaryKeys();
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
     * 生成update语句，要求数据中的表名，与Model的类名相同，表中的字段名与Model中的属性名相同。
     * @return  String类型，update语句。
     */
    private String generatorUpdateSQL(){
        String tableName = generatorTableName();
        String sql = "update " + tableName + " set ";
        Map<String, String> cvs = getColumnsValues();
        for (String key: cvs.keySet()){
            String value = cvs.get(key);
            sql += key + "=" + value + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        //获取表的主键
        String[] primaryKeys = getPrimaryKeys();
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

    public void outputSQL(){
        System.out.println("insert SQL--->" + generatorInsertSQL());
        System.out.println("select SQL--->" + generatorSelectSQL());
        System.out.println("delete SQL--->" + generatorDeleteSQL());
        System.out.println("update SQL--->" + generatorUpdateSQL());
    }
}
