package com.dc.kafka.controller;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.consumer.KafkaUtil;
import com.dc.kafka.utils.TConfigUtils;

@Controller
@RequestMapping(value = "/signin")
public class SigninController {
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;

	@ResponseBody
	@GetMapping("/chargeToContract")
	public void chargeToContract(
		@RequestParam(name = "value", required = true) long value){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = 'adminAccount' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = "mini0823";
        String contractName = "Qiandao";
        BigInteger turnBalance = BigInteger.valueOf(10000000000000000L).multiply(BigInteger.valueOf(value));
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(null, contractName, TConfigUtils.selectContractAddress("signin_contract"), turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("chargeSigninContract", "SigninCharge", kafkabean);
	}
	
	@ResponseBody
	@GetMapping("/signinReward")
	public void signinReward(
		@RequestParam(name = "itcode", required = true) String itcode, 
		@RequestParam(name = "reward", required = true) int reward,
		@RequestParam(name = "transactionDetailId", required = true) String transactionDetailId){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode +"' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = "mini0823";
        String contractName = "Qiandao";
        BigInteger turnBalance = BigInteger.valueOf(10000000000000000L).multiply(BigInteger.valueOf(reward));
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(Integer.valueOf(transactionDetailId), contractName, TConfigUtils.selectContractAddress("signin_contract"), turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("signinReward", "SigninReward", kafkabean);
	}
	
	@ResponseBody
	@GetMapping("/voteReward")
	public void voteReward(
		@RequestParam(name = "itcode", required = true) String itcode, 
		@RequestParam(name = "reward", required = true) int reward,
		@RequestParam(name = "transactionDetailId", required = true) String transactionDetailId){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode +"' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = "mini0823";
        String contractName = "Qiandao";
        BigInteger turnBalance = BigInteger.valueOf(10000000000000000L).multiply(BigInteger.valueOf(reward));
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(Integer.valueOf(transactionDetailId), contractName, TConfigUtils.selectContractAddress("signin_contract"), turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("voteReward", "VoteReward", kafkabean);
	}
	
	@ResponseBody
	@GetMapping("/attendanceReward")
	public void attendanceReward(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "reward", required = true) int reward,
		@RequestParam(name = "transactionDetailId", required = true) String transactionDetailId){
		
			String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode +"' AND available = 3";
	        List<Map<String, Object>> list = jdbc.queryForList(sql);
	        if(list.size() == 0){
	        	return;
	        }
            String keystoreFile = list.get(0).get("keystore").toString() ;
            String password = "mini0823";
            String contractName = "Qiandao";
            BigInteger turnBalance = BigInteger.valueOf(10000000000000000L).multiply(BigInteger.valueOf(reward));
            KafkaConsumerBean kafkabean = new KafkaConsumerBean(Integer.valueOf(transactionDetailId), contractName, TConfigUtils.selectContractAddress("signin_contract"), turnBalance, password, keystoreFile);
            kafkaUtil.sendMessage("attendanceReward", "AttendanceReward", kafkabean);
	}
}
