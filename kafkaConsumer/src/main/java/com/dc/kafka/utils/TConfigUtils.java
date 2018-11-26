package com.dc.kafka.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.dc.kafka.dao.TConfigDAO;

import scala.util.Random;

public class TConfigUtils {
	@Autowired
	public static TConfigDAO tconfigDAO;
	
	public static String selectIp() {
		String[] ipArr = (String[]) tconfigDAO.selectIpArr().toArray();
		return ipArr[new Random().nextInt(5)];
	}
	
	public static String[] selectIpArr() {
		return (String[]) tconfigDAO.selectIpArr().toArray();
	}
	
	public static String selectContractAddress(String cfgKey) {
		return tconfigDAO.selectContractAddress(cfgKey);
	}
	
	public static String selectRootPath(String cfgKey) {
		return tconfigDAO.selectRootPath(cfgKey);
	}
}

