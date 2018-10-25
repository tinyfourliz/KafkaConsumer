package com.dc.kafka.consumer;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dc.kafka.component.KafkaConsumerBean;



@Component
public class KafkaUtil {
	   @Autowired
	    private KafkaProducer kafkaProducer;
	   
	   public String sendMessage(String topic,String key,KafkaConsumerBean kafkaEntity) {
	    	kafkaProducer.sendMessage( topic, key,kafkaEntity);
	        return "sucess";
	    } 
}
