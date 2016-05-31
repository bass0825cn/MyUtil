package com.project.model;

import com.sdc.connect.ConnectionPool;
import com.sdc.connect.MyConnection;
import com.sdc.util.StringUtil;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Model基类
 * Created by Administrator on 2016-05-12.
 */
abstract class BaseModel {

    public abstract String[] getPrimaryKeys();
    private Connection connection;
    private boolean ISNEW;

    public BaseModel(ConnectionPool cPool) throws SQLException{
        this.ISNEW = true;
        connection = cPool.getConnection();
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

    private boolean checkPrimaryKeys(){
        boolean isNew = false;
        Map<String, String> cvs = getColumnsValues();
        String[] primaryKeys = getPrimaryKeys();
        for (String pk: primaryKeys){
            if (cvs.get(pk).equals("") || cvs.get(pk) == null){
                isNew = true;
                break;
            }
        }
        return isNew;
    }

    /**
     * 保存数据，如果主键字段对应属性，有任何一个为空，则执行Insert语句，否则执行Update语句。
     * @return  int类型，返回影响的记录数。
     */
    public int save(){
        String saveString;
        if (ISNEW){
            saveString = generatorInsertSQL();
        }else{
            saveString = generatorUpdateSQL();
        }
        return MyConnection.execSQL(saveString);
    }

    /**
     * 删除数据,按主键删除数据。
     * @return  int类型，删除数据的条数。
     */
    public int delete(){
        return MyConnection.execSQL(generatorDeleteSQL());
    }

    /**
     * 从数据库中加载数据，只加载符合主键条件的数据。
     * @return int类型，返回符合条件的记录数。
     */
    public int loadData(){
        if (checkPrimaryKeys()){
            return -1;
        }else{
            int cnt;
            try {
                ResultSet rs = MyConnection.openSQL(generatorSelectSQL());
                try {
                    rs.first();
                    loadData(rs);
                    rs.last();
                    cnt = rs.getRow();
                }catch (NullPointerException en){
                    en.printStackTrace();
                    cnt = 0;
                }
            }catch (SQLException e){
                e.printStackTrace();
                cnt = -2;
            }
            return cnt;
        }
    }

    public void loadData(ResultSet rs){
        try{
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            for (int i = 1; i < resultSetMetaData.getColumnCount(); i++){
                try{
                    Method method = this.getClass().getMethod("set" + StringUtil.convertFirstCharUpper(resultSetMetaData.getColumnName(i)));
                    Object[] objects = new Object[1];
                    objects[0] = rs.getObject(i);
                    method.invoke(this,objects);
                    this.ISNEW = false;
                }catch (Exception en){
                    en.printStackTrace();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String showString = generatorTableName() + "{";
        Map<String, String> cvs = getColumnsValues();
        for (String key: cvs.keySet()){
            showString += key + "='" + cvs.get(key) + "'," ;
        }
        showString = showString.substring(0, showString.lastIndexOf(",")) + "}";
        return showString;
    }

    public void outputSQL(){
        System.out.println("insert SQL--->" + generatorInsertSQL());
        System.out.println("select SQL--->" + generatorSelectSQL());
        System.out.println("delete SQL--->" + generatorDeleteSQL());
        System.out.println("update SQL--->" + generatorUpdateSQL());
        System.out.println(toString());
    }
}
