package com.dc.kafka.utils;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.dc.kafka.dao.TConfigDAO;

import scala.util.Random;

@Configuration
public class TConfigUtils {
	@Autowired
	private TConfigDAO tconfigDAO;
	
	private static TConfigDAO newtconfigDAO;
	
	@PostConstruct
	public void init() {
		newtconfigDAO = tconfigDAO;
	}
	
	public static String selectIp() {
		List<String> ipArr = newtconfigDAO.selectIpArr();
		return ipArr.get(new Random().nextInt(5));
	}
	
	public static List<String> selectIpArr() {
		return newtconfigDAO.selectIpArr();
	}
	
	public static String selectContractAddress(String cfgKey) {
		return newtconfigDAO.selectContractAddress(cfgKey);
	}
	
	public static String selectRootPath(String cfgKey) {
		return newtconfigDAO.selectRootPath(cfgKey);
	}
	
	public static String selectDefaultPassword() {
		return newtconfigDAO.selectDefaultPassword();
	}
	
	public static String selectValueByKey(String cfgKey) {
		return newtconfigDAO.selectValueByKey(cfgKey);
	}
}

