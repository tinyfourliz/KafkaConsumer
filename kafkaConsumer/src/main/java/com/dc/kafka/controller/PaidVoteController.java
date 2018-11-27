package com.dc.kafka.controller;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.consumer.KafkaUtil;
import com.dc.kafka.utils.TConfigUtils;

@Controller
@RequestMapping(value = "/paidVotes")
public class PaidVoteController {
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;

	@ResponseBody
	@PostMapping("/insertVoteDetail")
	public void balanceQuery(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "account", required = true) String account,
		@RequestParam(name = "transactionDetailId", required = true) Integer transactionDetailId,
		@RequestParam(name = "turnBalance", required = true) BigInteger turnBalance){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode + "' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = TConfigUtils.selectDefaultPassword();
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(transactionDetailId, account, TConfigUtils.selectContractAddress("paidvote_contract"), turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("paidvote", "paidVote", kafkabean);
	}
}
