package com.sdc.model;

import com.sdc.util.Enums;

/**
 * Created by Administrator on 2016-05-12.
 */
public class Field {
    private Enums.DataBaseName dataBaseName;
    private String tableCat;
    private String tableSchema;
    private String tableName;
    private String columnName;
    private int dataType;
    private String dataTypeName;
    private int columnSize;
    private int decimalDigits;
    private int numPrecRadix;
    private int nullAble;
    private String remarks;
    private String columnDef;
    private int sqlDataType;
    private int sqlDateTimeSub;
    private int charOctetLength;
    private int ordinalPosition;
    private String isNullAble;
    private String isAutoIncrement;

    public String getTableCat() {
        return tableCat;
    }

    public void setTableCat(String tableCat) {
        this.tableCat = tableCat;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public int getNumPrecRadix() {
        return numPrecRadix;
    }

    public void setNumPrecRadix(int numPrecRadix) {
        this.numPrecRadix = numPrecRadix;
    }

    public int getNullAble() {
        return nullAble;
    }

    public void setNullAble(int nullAble) {
        this.nullAble = nullAble;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getColumnDef() {
        return columnDef;
    }

    public void setColumnDef(String columnDef) {
        this.columnDef = columnDef;
    }

    public int getSqlDataType() {
        return sqlDataType;
    }

    public void setSqlDataType(int sqlDataType) {
        this.sqlDataType = sqlDataType;
    }

    public int getSqlDateTimeSub() {
        return sqlDateTimeSub;
    }

    public void setSqlDateTimeSub(int sqlDateTimeSub) {
        this.sqlDateTimeSub = sqlDateTimeSub;
    }

    public int getCharOctetLength() {
        return charOctetLength;
    }

    public void setCharOctetLength(int charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getIsNullAble() {
        return isNullAble;
    }

    public void setIsNullAble(String isNullAble) {
        this.isNullAble = isNullAble;
    }

    public String getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(String isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    public Field(Enums.DataBaseName dataBaseName){
        this.dataBaseName = dataBaseName;
    }

    @Override
    public String toString() {
        return "Field{ColumnName: " + getColumnName() + ", " +
                "TableCat: " + getTableCat() + ", " +
                "TableSchema: " + getTableSchema() + ", " +
                "TableName: " + getTableName() + ", " +
                "DataType: " + getDataType() + ", " +
                "DataTypeName: " + getDataTypeName() + ", " +
                "JavaTypeName: " + getJavaType(dataBaseName) + ", " +
                "ColumnSize: " + getColumnSize() + ", " +
                "DecimalDigits: " + getDecimalDigits() + ", " +
                "NumPrecRadix: " + getNumPrecRadix() + ", " +
                "NullAble: " + getNullAble() + ", " +
                "Remarks: " + getRemarks() + ", " +
                "ColumnDef: " + getColumnDef() + ", " +
                "SQLDataType: " + getSqlDataType() + ", " +
                "SQLDateTimeSub: " + getSqlDateTimeSub() + ", " +
                "CharOctetLength: " + getCharOctetLength() + ", " +
                "OrdinalPosition: " + getOrdinalPosition() + ", " +
                "IsNullAble: " + getIsNullAble() + ", " +
                "IsAutoIncrement: " + getIsAutoIncrement() + "}";
    }

    public String getJavaType(Enums.DataBaseName dataBaseName){
        String javaType;
        switch (dataBaseName){
            case Oracle:
                javaType = convertOracleDataType();
                break;
            default:
                javaType = dataTypeName;
                break;
        }
        return javaType;
    }

    private String convertOracleDataType(){
        if (dataTypeName.equalsIgnoreCase("binary_double")){
            return "double";
        }else if (dataTypeName.equalsIgnoreCase("binary_float")){
            return "float";
        }else if (dataTypeName.equalsIgnoreCase("blob")){
            return "byte[]";
        }else if (dataTypeName.equalsIgnoreCase("char") || dataTypeName.equalsIgnoreCase("nvarchar2")
                || dataTypeName.equalsIgnoreCase("varchar2")){
            return "String";
        }else if (dataTypeName.equalsIgnoreCase("date") || dataTypeName.equalsIgnoreCase("timestamp")
                || dataTypeName.equalsIgnoreCase("timestamp with local time zone") || dataTypeName.equalsIgnoreCase("timestamp with time zone")){
            return "Date";
        }else if (dataTypeName.equalsIgnoreCase("number")){
            return "Long";
        }
        return "String";
    }

    public String getValueStr(Enums.DataBaseName dataBaseName){
        String valueStr;
        switch (dataBaseName){
            case Oracle:
                valueStr = convertOracleValues();
                break;
            default:
                valueStr = "' + this." + columnName + "'";
                break;
        }
        return valueStr;
    }

    private String convertOracleValues(){
        if (dataTypeName.equalsIgnoreCase("date") || dataTypeName.equalsIgnoreCase("timestamp")){
            return "to_date(\'this." + columnName + "',('yyyy-mm-dd hh24:mi:ss')";
        }
        return "'\" + this." + columnName + " + \"'";
    }
}
