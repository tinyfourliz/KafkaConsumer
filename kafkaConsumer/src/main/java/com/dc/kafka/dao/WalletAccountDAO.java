package com.dc.kafka.dao;

import java.util.List;


public interface WalletAccountDAO {

	
	List<String> selectUserNoBefore8();
	
	List<String> selectUserNoAfter21();
}

