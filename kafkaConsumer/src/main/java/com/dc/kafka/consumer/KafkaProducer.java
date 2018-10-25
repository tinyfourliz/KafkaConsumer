package com.dc.kafka.consumer;




import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.dc.kafka.component.KafkaConsumerBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate; 
    
    private Gson gson = new GsonBuilder().create();

    
    public void sendMessage(String topic,String key,KafkaConsumerBean kafkaEntity){
        kafkaTemplate.send(new ProducerRecord(topic,key,gson.toJson(kafkaEntity))); //测试发送对象数据
    }
}