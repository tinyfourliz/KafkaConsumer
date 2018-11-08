package com.dc.kafka.job;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.consumer.KafkaUtil;
import com.dc.kafka.dao.WalletAccountDAO;

@Component
public class BeatCardReward {
    
    @Autowired
    private WalletAccountDAO walletAccountDAO;
    @Autowired
   	private JdbcTemplate jdbc;
    @Autowired
    private KafkaUtil kafkaUtil;
    
    @Transactional
//    @Scheduled(cron="30 30 08 * * ?")
    public void sendAttendanceRewards(){
        System.err.println("开始考勤奖励员工编号获取...");
        String result = "";
        List<String> resultList = walletAccountDAO.selectUserNoAfter21();
        resultList.addAll(walletAccountDAO.selectUserNoBefore8());

        for (String string : resultList) {
            result += (string + ",");
        }
        if(result == ""){
            return;
        }
        result = result.substring(0, result.length() - 1 );
        System.err.println(result);
    
        String sql = "SELECT * FROM am_person a LEFT JOIN am_ethaccount b on a.ITCODE=b.itcode WHERE b.available='3' and a.EMPLOYEENUMBER in "+ "("+result+")";

        List<Map<String,Object>> list = jdbc.queryForList(sql);
    
        for(int i = 0; i < list.size(); i++) {
            String keystoreFile = list.get(i).get("keystore").toString() ;//"{\"address\":\"189abcd4cb82534d9d7b2ee181b28bcc86c64853\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"92030d37f698f10ceb08b65e8cf8fcb048d75b81437d7feda004008eb3fa69c8\",\"cipherparams\":{\"iv\":\"2ef3f82c19ee50efebd9f555f6a22fc5\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"5bee728b4c1a470d9514faab1ab17eaacbdfe8692d042b50ff37f5eacc6d288e\"},\"mac\":\"f094bbc466a969bf939e755ea58900002ffaa7911b36cc4541222f01234b23e4\"},\"id\":\"c337c7c8-df9d-42f8-b59f-176ee9a04d81\",\"version\":3}";
            String password = "mini0823";
            Integer transactionDetailId = 1;
            String contractName = "Qiandao";
            String address = "0xa3bd7ba69b93d2e1f7708fafd14ba5723ae4799a";
            BigInteger turnBalance = BigInteger.valueOf(100000000000000000L);
            KafkaConsumerBean kafkabean = new KafkaConsumerBean(transactionDetailId, contractName, address, turnBalance, password, keystoreFile);
            kafkaUtil.sendMessage("beatcard", "attendanceReward", kafkabean);
        }
    }
}
