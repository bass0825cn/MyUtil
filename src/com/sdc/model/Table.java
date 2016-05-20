package com.sdc.model;

/**
 * Created by Administrator on 2016-05-12.
 */
public class Table {

    private String schema;
    private String name;
    private String type;
    private String remarks;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Table{schem: " + getSchema() + ", name: " + getName() + ", type: " + getType() + ", remarks: " + getRemarks() + "}";
    }
}
