package com.project.model;

import com.sdc.connect.ConnectionPool;
import com.sdc.util.Enums;

import java.sql.SQLException;

/**
 * wzlymb实体类
 * Thu May 19 11:09:49 CST 2016 Song Da Cai Create
 */
public class Wzlymb extends BaseModel{

//  单据号
	private String DJID;
//  单据类型
	private String CLASS;
//  开单日期
	private String KDRQ;
//  领用人
	private String LYR;
//  领用部门代码
	private String LYDEPT;
//  制单人
	private String ZDR;
//  制单科室代码
	private String ZDKS;
//  总金额
	private Long ZJE;
//  入帐标志
	private String RZBZ;
//  主键
	private static String[] primaryKeys = {"DJID", "CLASS"};

	public Wzlymb(ConnectionPool cPool) throws SQLException{
        super(cPool);
	}

    @Override
	public String[] getPrimaryKeys() {
        return primaryKeys;
	}

	public void setDJID(String DJID){
		this.DJID = DJID;
	}

	public String getDJID(){
		return this.DJID;
}

	public void setCLASS(String CLASS){
		this.CLASS = CLASS;
	}

	public String getCLASS(){
		return this.CLASS;
}

	public void setKDRQ(String KDRQ){
		this.KDRQ = KDRQ;
	}

	public String getKDRQ(){
		return this.KDRQ;
}

	public void setLYR(String LYR){
		this.LYR = LYR;
	}

	public String getLYR(){
		return this.LYR;
}

	public void setLYDEPT(String LYDEPT){
		this.LYDEPT = LYDEPT;
	}

	public String getLYDEPT(){
		return this.LYDEPT;
}

	public void setZDR(String ZDR){
		this.ZDR = ZDR;
	}

	public String getZDR(){
		return this.ZDR;
}

	public void setZDKS(String ZDKS){
		this.ZDKS = ZDKS;
	}

	public String getZDKS(){
		return this.ZDKS;
}

	public void setZJE(Long ZJE){
		this.ZJE = ZJE;
	}

	public Long getZJE(){
		return this.ZJE;
}

	public void setRZBZ(String RZBZ){
		this.RZBZ = RZBZ;
	}

	public String getRZBZ(){
		return this.RZBZ;
}

	@Override
	public String toString() {
		return "Wzlymb{" +
				"DJID='" + DJID + '\'' +
				", CLASS='" + CLASS + '\'' +
				", KDRQ='" + KDRQ + '\'' +
				", LYR='" + LYR + '\'' +
				", LYDEPT='" + LYDEPT + '\'' +
				", ZDR='" + ZDR + '\'' +
				", ZDKS='" + ZDKS + '\'' +
				", ZJE=" + ZJE +
				", RZBZ='" + RZBZ + '\'' +
				'}';
	}
}
