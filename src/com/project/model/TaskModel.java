package com.project.model;

import com.sdc.connect.ConnectionPool;
import com.sdc.util.Enums;

import java.sql.SQLException;

/**
 * Created by Administrator on 2016-05-23.
 */
public class TaskModel {
    public static void main(String[] args){
        ConnectionPool oraclePool = new ConnectionPool("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@127.0.0.1:1521:orcl", "jdyyfam", "jdyyfam");
        try {
            Wzlymb wzlymb = new Wzlymb(oraclePool);
            wzlymb.setDJID("WZLY16051000");
            wzlymb.setCLASS("1");
            wzlymb.setKDRQ("2016-05-01");
            wzlymb.setLYDEPT("0101");
            wzlymb.setLYR("sdc");
            wzlymb.setZDKS("0201");
            wzlymb.setZDR("sdc");
            wzlymb.setZJE(1000L);
            wzlymb.setRZBZ("1");
            int count = wzlymb.save();
            count = wzlymb.loadData();
            wzlymb.outputSQL();
            count = wzlymb.delete();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
