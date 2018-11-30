package com.dc.kafka.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface TConfigDAO {
	List<String> selectIpArr();
	
	String selectContractAddress(@Param("cfgKey")String cfgKey);
	
	String selectRootPath(@Param("cfgKey")String cfgKey);
	
	String selectDefaultPassword();
	
	String selectValueByKey(@Param("cfgKey")String cfgKey);
}
