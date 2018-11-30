package com.dc.kafka.consumer;



import org.web3j.tx.response.TransactionReceiptProcessor;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.contract.Qiandao;
import com.dc.kafka.utils.TConfigUtils;
import com.google.gson.Gson;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.NoOpProcessor;

@Component
public class KafkaConsumerQianDao {
    
    @Autowired
    private JdbcTemplate jdbc;
    private Integer count = 1;
    
//    private static final String rootPath = "/eth/datadir/temp/";

    @KafkaListener(topics = {"attendanceReward"})
    public String processorBeatcard(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        count = 1;
        return attendanceReward(bean);
    }
    
    @KafkaListener(topics = {"chargeSigninContract"})
    public String processorChargeToSigninContract(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        count = 1;
        return chargeToSigninContract(bean);
    }
    
    @KafkaListener(topics = {"signinReward"})
    public String processorSigninReward(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        count = 1;
        return signinReward(bean);
    }
    
    @KafkaListener(topics = {"voteReward"})
    public String processorVoteReward(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        count = 1;
        return voteReward(bean);
    }
    public String voteReward(KafkaConsumerBean bean) {
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            return null;
        }

        Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
        Credentials credentials= getCredentials(bean);
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
            transactionManager = new RawTransactionManager(web3j, credentials, KafkaConsumerBean.getChainid(), transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
            voteReward(bean);
            return "error, build transaction manager failed.";
        }
       
        Qiandao contract = Qiandao.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.qiandaoReward(new Uint256(bean.getTurnBalance())).send();
            
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
            	voteReward(bean);
            }

            //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
            System.out.println("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'voteReward', '"+resultHash+"',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'voteReward', '"+resultHash+"',sysdate())");
            
            System.out.println("将交易Hash值更新至t_signinreward表中:" + "UPDATE t_signinreward SET transactionhash='" + resultHash + "', backup1='每日投票奖励！' WHERE id = " + bean.getTransactionDetailId() + "; ");
            jdbc.execute("UPDATE t_signinreward SET transactionhash='" + resultHash + "', backup1='每日投票奖励！' WHERE id = " + bean.getTransactionDetailId() + "; ");
            
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            voteReward(bean);
            return "error, transaction hash is null.";
        }
    }
    
    public String signinReward(KafkaConsumerBean bean) {
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            return null;
        }

        Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
        Credentials credentials= getCredentials(bean);
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
            transactionManager = new RawTransactionManager(web3j, credentials, KafkaConsumerBean.getChainid(), transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
            signinReward(bean);
            return "error, build transaction manager failed.";
        }
       
        Qiandao contract = Qiandao.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.qiandaoReward(new Uint256(bean.getTurnBalance())).send();
            
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
            	signinReward(bean);
            }

            //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
            System.out.println("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'signinReward', '"+resultHash+"',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'signinReward', '"+resultHash+"',sysdate())");
            
            System.out.println("将交易Hash值更新至t_signinreward表中:" + "UPDATE t_signinreward SET transactionhash='" + resultHash + "', backup1='每日签到奖励！' WHERE id = " + bean.getTransactionDetailId() + "; ");
            jdbc.execute("UPDATE t_signinreward SET transactionhash='" + resultHash + "', backup1='每日签到奖励！' WHERE id = " + bean.getTransactionDetailId() + "; ");
            
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            signinReward(bean);
            return "error, transaction hash is null.";
        }
    }
    
    public String chargeToSigninContract(KafkaConsumerBean bean) {
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            return null;
        }

        Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
        Credentials credentials= getCredentials(bean);
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
            transactionManager = new RawTransactionManager(web3j, credentials, KafkaConsumerBean.getChainid(), transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
            chargeToSigninContract(bean);
            return "error, build transaction manager failed.";
        }
       
        Qiandao contract = Qiandao.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.chargeToContract(bean.getTurnBalance()).send();
            
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
            	chargeToSigninContract(bean);
            }

            //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
            System.out.println("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'chargeSigninContract', '"+resultHash+"',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'chargeSigninContract', '"+resultHash+"',sysdate())");

            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            chargeToSigninContract(bean);
            return "error, transaction hash is null.";
        }
    }
    
    public String attendanceReward(KafkaConsumerBean bean) {
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            return null;
        }

        Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
        Credentials credentials= getCredentials(bean);
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
            transactionManager = new RawTransactionManager(web3j, credentials, KafkaConsumerBean.getChainid(), transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
            attendanceReward(bean);
            return "error, build transaction manager failed.";
        }
       
        Qiandao contract = Qiandao.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.qiandaoReward(new Uint256(bean.getTurnBalance())).send();
            
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
            	attendanceReward(bean);
            }

            //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
            System.out.println("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'attendanceReward', '"+resultHash+"',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'attendanceReward', '"+resultHash+"',sysdate())");
            
            System.out.println("将交易Hash值更新至t_signinreward表中:" + "UPDATE t_signinreward SET transactionhash='" + resultHash + "', backup1='每日考勤奖励！' WHERE id = " + bean.getTransactionDetailId() + "; ");
            jdbc.execute("UPDATE t_signinreward SET transactionhash='" + resultHash + "', backup1='每日考勤奖励！' WHERE id = " + bean.getTransactionDetailId() + "; ");
            
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            attendanceReward(bean);
            return "error, transaction hash is null.";
        }
    }
    
    private static  Credentials getCredentials(KafkaConsumerBean bean) {
        File keystoreFile;
        System.out.println("开始解锁。。。");
        Credentials credentials = null;
        try {
            keystoreFile = keystoreToFile(bean.getKeystoreFile(), "temp.json");
            credentials = WalletUtils.loadCredentials(bean.getPassword(), keystoreFile);
            System.out.println("解锁成功。。。");
            keystoreFile.delete();
            System.out.println("删除临时keystore文件成功。。。");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CipherException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
           
        return credentials;
    }

    public static File keystoreToFile(String keystore, String keystoreName) throws IOException {
        File file = new File(TConfigUtils.selectRootPath("keystore_temp_path") + keystoreName);

        if( !file.exists() ){
            file.createNewFile();
        }
        
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(keystore);
        bw.close();
        
        return file;
    }
}
