package com.sdc.model;

/**
 * Created by Administrator on 2016-05-12.
 */
public class PrimaryKey {
    private String schemaName;
    private String tableName;
    private String columnName;
    private int keySeq;
    private String pkName;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
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

    public int getKeySeq() {
        return keySeq;
    }

    public void setKeySeq(int keySeq) {
        this.keySeq = keySeq;
    }

    public String getPkName() {
        return pkName;
    }

    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    @Override
    public String toString() {
        return "PrimaryKey{PrimaryKeyName:" + getPkName() + ", " +
                "Schema: " + getSchemaName() + ", " +
                "TableName:" + getTableName() + ", " +
                "ColumnName:" + getColumnName() + ", " +
                "KeySeq:" + getKeySeq() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryKey that = (PrimaryKey) o;

        if (tableName != null ? !tableName.toUpperCase().equals(that.tableName.toUpperCase()) : that.tableName != null) return false;
        return columnName != null ? columnName.toUpperCase().equals(that.columnName.toUpperCase()) : that.columnName == null;

    }

    @Override
    public int hashCode() {
        int result = tableName != null ? tableName.hashCode() : 0;
        result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }
}
