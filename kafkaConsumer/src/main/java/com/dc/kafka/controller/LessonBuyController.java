package com.dc.kafka.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
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
@RequestMapping(value = "/lessonBuy")
public class LessonBuyController {
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;

//    private static String address = "0x861b6f2ca079e1cfa5da9b429fa9d82a6645b419";
	@ResponseBody
	@PostMapping("/processDeduction")
	public void processLessonBuy(
		@RequestParam(name = "itcode", required = true) String itcode,
		@RequestParam(name = "transactionDetailId", required = true) Integer transactionDetailId,
		@RequestParam(name = "turnBalance", required = true) BigInteger turnBalance){
		
		String sql = "SELECT * FROM am_ethaccount WHERE itcode = '" + itcode + "' AND available = 3";
        List<Map<String, Object>> list = jdbc.queryForList(sql);
        if(list.size() == 0){
        	return;
        }
		String keystoreFile = list.get(0).get("keystore").toString();
		String password = "mini0823";
        String contractName = "LessonBuy";
        KafkaConsumerBean kafkabean = new KafkaConsumerBean(transactionDetailId, contractName, TConfigUtils.selectContractAddress("lesson_contract"), turnBalance, password, keystoreFile);
        kafkaUtil.sendMessage("lessonbuy", "LessonBuy", kafkabean);
	}
}
