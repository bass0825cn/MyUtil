package com.sdc.model;

import com.sdc.connect.OracleConnection;
import com.sdc.util.Enums;
import com.sdc.util.FileUtil;
import com.sdc.util.StringUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by Administrator on 2016-05-12.
 */
public class GetModel {
    private List<Table> tableList = new ArrayList<Table>();
    private Enums.DataBaseName dataBaseName;
    private Connection connection;
    private DatabaseMetaData databaseMetaData;
    private String lineSeparator = StringUtil.lineSeparator;
    private final String authorName = "Song Da Cai";
    private String packageName;

    public void setPackageName(String packageName){
        this.packageName = packageName;
    }

    /**
     * 构造函数，连接数据库，获取数据库元数据
     * @param ip    String类型，数据库IP地址或主机名
     * @param port  String类型，数据库端口号
     * @param sid   String类型，数据库实例名（SID）
     * @param user  String类型，登录数据库的用户名
     * @param pass  String类型，登录数据库密码
     */
    public GetModel(String ip, String port, String sid, String user, String pass, Enums.DataBaseName dataBaseName){
        this.dataBaseName = dataBaseName;
        switch (dataBaseName){
            case Oracle:
                OracleConnection oracleConnection = new OracleConnection(ip, port, sid, user, pass);
                connection = oracleConnection.connection;
                break;
            default:
                connection = null;
                break;
        }
        if (connection != null){
            try {
                databaseMetaData = connection.getMetaData();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取数据库中所有的表，把表的信息添加到tableList。
     * @param schemaName    用户名或者数据库名
     */
    public void setTableList(String schemaName) {
        //表类型有："TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"
        try {
            tableList.clear();
            String[] types = {"TABLE"};
            ResultSet rs = databaseMetaData.getTables(null, schemaName.toUpperCase(), "%", types);
            while (rs.next()){
                Table t = new Table();
                t.setSchema(rs.getString("TABLE_SCHEM"));
                t.setName(rs.getString("TABLE_NAME"));
                t.setType(rs.getString("TABLE_TYPE"));
                t.setRemarks(rs.getString("REMARKS"));
                tableList.add(t);
                System.out.println(t.toString());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * 获取指定表的字段信息。
     * @param schemaName    String类型        数据库名或者用户名
     * @param tableName     String类型        表名
     * @return              List<Field>类型   字段列表
     */
    public List<Field> getTableColumns(String schemaName, String tableName){
        try{
            List<Field> fieldList = new ArrayList<Field>();
            ResultSet rs = databaseMetaData.getColumns(null, schemaName.toUpperCase(), tableName.toUpperCase(), "%");
            while (rs.next()){
                Field f = new Field(dataBaseName);
                f.setTableCat(rs.getString("TABLE_CAT"));
                f.setTableSchema(rs.getString("TABLE_SCHEM"));
                f.setTableName(rs.getString("TABLE_NAME"));
                f.setColumnName(rs.getString("COLUMN_NAME"));
                f.setDataType(rs.getInt("DATA_TYPE"));
                f.setDataTypeName(rs.getString("TYPE_NAME"));
                f.setColumnSize(rs.getInt("COLUMN_SIZE"));
                f.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                f.setNumPrecRadix(rs.getInt("NUM_PREC_RADIX"));
                f.setNullAble(rs.getInt("NULLABLE"));
                f.setRemarks(rs.getString("REMARKS"));
                f.setColumnDef(rs.getString("COLUMN_DEF"));
                f.setSqlDataType(rs.getInt("SQL_DATA_TYPE"));
                f.setSqlDateTimeSub(rs.getInt("SQL_DATETIME_SUB"));
                f.setCharOctetLength(rs.getInt("CHAR_OCTET_LENGTH"));
                f.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
                f.setIsNullAble(rs.getString("IS_NULLABLE"));
//                f.setIsAutoIncrement(rs.getString("IS_AUTOINCREMENT"));
                fieldList.add(f);
                System.out.println(f.toString());
            }
            return fieldList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定表的主键信息
     * @param schemaName    String类型    数据库名或者用户名
     * @param tableName     String类型    表名
     * @return      List<PrimaryKey>类型  主键列表
     */
    public List<PrimaryKey> getPrimaryKeys(String schemaName, String tableName){
        List<PrimaryKey> primaryKeyList = new ArrayList<PrimaryKey>();
        try{
            ResultSet rs = databaseMetaData.getPrimaryKeys(null, schemaName.toUpperCase(), tableName.toUpperCase());
            while (rs.next()){
                PrimaryKey p = new PrimaryKey();
                p.setSchemaName(rs.getString("TABLE_SCHEM"));
                p.setTableName(rs.getString("TABLE_NAME"));
                p.setColumnName(rs.getString("COLUMN_NAME"));
                p.setKeySeq(rs.getInt("KEY_SEQ"));
                p.setPkName(rs.getString("PK_NAME"));
                primaryKeyList.add(p);
                System.out.println(p.toString());
            }
            return primaryKeyList;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成表对应的模型类，类表为表名。
     * @param schemaName    用户名或数据库名
     * @param tableName     表名
     */
    public void generateModelClass(String schemaName, String tableName){
        if (packageName.equals("")){
            System.out.println("PackageName must have value!");
        }
        List<Field> fields = getTableColumns(schemaName, tableName);
        List<PrimaryKey> primaryKeys = getPrimaryKeys(schemaName, tableName);
        boolean importUtil = false;
        boolean importSQL = false;
        for (Field f: fields){
            if (f.getJavaType(dataBaseName).equals("Date")){
                importUtil = true;
            }
            if (f.getColumnName().equalsIgnoreCase("blob") || f.getColumnName().equalsIgnoreCase("char")){
                importSQL = true;
            }
            if (importSQL && importUtil){
                break;
            }
        }
        String classStr = "";
        classStr += "package " + packageName + ";" + lineSeparator + lineSeparator;
        if (importUtil){
            classStr += "import java.util.Date;" + lineSeparator;
        }
        if (importUtil){
            classStr += "import java.sql.*;" + lineSeparator;
        }
        classStr += "/**" + lineSeparator;
        classStr += " * " + tableName + "实体类" + lineSeparator;
        classStr += " * " + new Date() + " " + authorName + " Create" + lineSeparator;
        classStr += " */" + lineSeparator;
        classStr += "public class " + StringUtil.convertFirstCharUpper(tableName) + " extends BaseModel{" +
                lineSeparator + lineSeparator;
        classStr += generateAttrs(fields);
        classStr += generateMethods(fields);
        classStr += generateAbstractMethods(tableName, fields, primaryKeys);
        classStr += "}";
        String outPath = FileUtil.getPackagePath(packageName) + StringUtil.convertFirstCharUpper(tableName) + ".java";
        FileUtil.saveToFile(outPath, classStr);
    }

    private String generateAttrs(List<Field> fields){
        String attrs = "";
        for (Field f: fields){
            if (f.getRemarks() != null && f.getRemarks() != ""){
                attrs += "//  " + f.getRemarks() + lineSeparator;
            }
            attrs += "\tprivate " + f.getJavaType(dataBaseName) + " " + f.getColumnName() + ";" + lineSeparator;
        }
        return attrs + lineSeparator;
    }

    private String generateMethods(List<Field> fields){
        String methods = "";
        for (Field f: fields){
            methods += "\tpublic void set" + StringUtil.convertFirstCharUpper(f.getColumnName()) +
                    "(" + f.getJavaType(dataBaseName) + " " + f.getColumnName() + "){" + lineSeparator;
            methods += "\t\tthis." + f.getColumnName() + " = " + f.getColumnName() + ";" + lineSeparator;
            methods += "\t}" + lineSeparator + lineSeparator;
            methods += "\tpublic " + f.getJavaType(dataBaseName) + " get" + StringUtil.convertFirstCharUpper(f.getColumnName()) + "(){" + lineSeparator;
            methods += "\t\treturn this." + f.getColumnName() + ";" + lineSeparator;
            methods += "}" + lineSeparator + lineSeparator;
        }
        return methods;
    }

    /**
     * 生成实现的抽象方法
     * @param tableName     String类型，表名
     * @param fields        List<Field>类型，字段列表
     * @param primaryKeys   List<PrimaryKey>类型，主键列表
     * @return  String类型
     */
    private String generateAbstractMethods(String tableName, List<Field> fields, List<PrimaryKey> primaryKeys){
        //生成insert()方法
        String methods = "\t@Override" + lineSeparator;
        methods += "\tpublic boolean insert(){" + lineSeparator;
        methods += "\t\tString insertString = \"insert into " + tableName + "(";
        String columns = "";
        String values = "";
        for (Field f: fields){
            columns += f.getColumnName() + ",";
            values += f.getValueStr(Enums.DataBaseName.Oracle) + ",";
        }
        columns = columns.substring(0, columns.length() - 1);
        values = values.substring(0, values.length() - 1);
        methods += columns + ") values(";
        methods += values + ")\";" + lineSeparator;
        methods += "\t\treturn true;" + lineSeparator;
        methods += "\t}" + lineSeparator + lineSeparator;
        //生成update()方法
        methods += "\t@Override" + lineSeparator;
        methods += "\tpublic boolean update(){" + lineSeparator;
        methods += "\t\tString updateString = \"update " + tableName + "set ";
        String setValues = "";
        for (Field f: fields){
            PrimaryKey tmpPK = new PrimaryKey();
            tmpPK.setTableName(tableName);
            tmpPK.setColumnName(f.getColumnName());
            if (!primaryKeys.contains(tmpPK)) {
                setValues += f.getColumnName() + "=" + f.getValueStr(Enums.DataBaseName.Oracle) + ",";
            }
        }
        setValues = setValues.substring(0, setValues.length() - 1);
        String whereStr = " where ";
        for (PrimaryKey pk: primaryKeys){
            whereStr += pk.getColumnName() + "='\" + this." + pk.getColumnName() + " + \"' and ";

        }
        whereStr = whereStr.substring(0, whereStr.lastIndexOf(" and ")) + "\";";
        methods += setValues + whereStr + lineSeparator;
        methods += "\t\treturn true;" + lineSeparator;
        methods += "\t}" + lineSeparator + lineSeparator;
        //生成delete()方法
        methods += "\t@Override" + lineSeparator;
        methods += "\t public boolean delete(){" + lineSeparator;
        methods += "\t\tString deleteString = \"delete from " + whereStr + lineSeparator;
        methods += "\t\treturn true;" +lineSeparator;
        methods += "\t}" + lineSeparator + lineSeparator;
        return methods;
    }

    public static void main(String[] args){
        GetModel g = new GetModel("127.0.0.1", "1521", "orcl", "jdyyfam", "jdyyfam", Enums.DataBaseName.Oracle);
        g.setPackageName("com.project.model");
        g.setTableList("jdyyfam");
        g.getTableColumns("jdyyfam", "wzlymb");
        g.getPrimaryKeys("jdyyfam", "wzlymb");
        g.generateModelClass("jdyyfam", "wzlymb");
    }
}
