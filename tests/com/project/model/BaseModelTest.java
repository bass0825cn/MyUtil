package com.project.model;

import org.junit.Test;
import junit.framework.TestCase;

import java.util.Map;

/**
 * Created by Administrator on 2016-05-23.
 */
public class BaseModelTest extends TestCase {

    private Wzlymb wzlymb;

    @org.junit.Before
    public void setUp() throws Exception {
        wzlymb = new Wzlymb();
        wzlymb.setDJID("WZLY16051000");
        wzlymb.setCLASS("1");
        wzlymb.setKDRQ("2016-05-01");
        wzlymb.setLYDEPT("0101");
        wzlymb.setLYR("sdc");
        wzlymb.setZDKS("0201");
        wzlymb.setZDR("sdc");
        wzlymb.setZJE(1000L);
        wzlymb.setRZBZ("1");
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testGetPrimaryKeys() throws Exception {
        String[] pks = wzlymb.getPrimaryKeys();
        assertEquals(true, pks.equals(new String[]{"DJID", "CLASS"}));
    }

    @org.junit.Test
    public void testGeneratorDeleteSQL() throws Exception {

    }

    @org.junit.Test
    public void testGeneratorUpdateSQL() throws Exception {

    }

    @org.junit.Test
    public void testGeneratorTableName() throws Exception{

    }
}