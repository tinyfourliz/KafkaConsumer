package com.dc.kafka.controller;

import java.math.BigDecimal;
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

@Controller
@RequestMapping(value = "/cashBack")
public class CashBackController {
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;

    private static String address = "0x8b12979ea5f4e3e57f6886cc765e12d65379221b";
	@ResponseBody
	@PostMapping("/processDeduction")
	public void processLessonBuy(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "turnBalance", required = true) String turnBalance){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode + "' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = "mini0823";
        String contractName = itcode;
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(0, contractName, address, new BigDecimal((Double.parseDouble(turnBalance))*10000000000000000L).toBigInteger(), password, keystoreFile);
        kafkaUtil.sendMessage("cashback", "CashBack", kafkabean);
	}
}
