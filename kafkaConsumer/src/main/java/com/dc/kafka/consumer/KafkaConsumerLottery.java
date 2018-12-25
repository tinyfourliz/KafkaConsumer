package com.dc.kafka.consumer;



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
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.contract.Lottery;
import com.dc.kafka.contract.PaidVote;
import com.dc.kafka.utils.TConfigUtils;
import com.google.gson.Gson;

@Component
public class KafkaConsumerLottery {
    
    @Autowired
    private JdbcTemplate jdbc;
    
    @KafkaListener(topics = {"lotteryBuyTicket"})
    public String processor(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        Integer count = 1;
        return toconsumer(bean, count);
    }
    
    @KafkaListener(topics = {"lotteryIssueSZBReward"})
    public String processorIssueSZBReward(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        Integer count = 1;
        return toconsumerLotteryIssueSZBReward(bean, count);
    }
    
    public String toconsumer(KafkaConsumerBean bean, Integer count) {
    	try {
    		System.out.println("sleep前");
			Thread.sleep(Long.valueOf(TConfigUtils.selectValueByKey("sleep_time")));
			System.out.println("sleep后");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			System.out.println("KafkaConsumerLottery---sleep异常");
		}
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            System.out.println("将交易Hash值更新为异常值");
            jdbc.execute("UPDATE t_paidlottery_details SET hashcode = '0x0',account = '" + getCredentials(bean).getAddress() + "' WHERE id = " + bean.getTransactionDetailId() + "; ");
            return null;
        }

        Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
        Credentials credentials = getCredentials(bean);
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
            transactionManager = new RawTransactionManager(web3j, credentials, KafkaConsumerBean.getChainid(), transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
            toconsumer(bean, count);
            return "error, build transaction manager failed.";
        }
       
        Lottery contract = Lottery.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.paidLottery(new Uint32(bean.getTransactionDetailId()), bean.getTurnBalance()).send();
            
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
                //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
                toconsumer(bean, count);
            }
            System.out.println("将操作日志记录于表中:" + "INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lottery', '" + resultHash + "',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lottery', '" + resultHash + "',sysdate())");
            
            System.out.println("将交易Hash值更新至t_paidlottery_details表中:" + "UPDATE t_paidvote_details SET hashcode = '" + resultHash + "' WHERE id = " + bean.getTransactionDetailId() + "; ");
            jdbc.execute("UPDATE t_paidlottery_details SET hashcode = '" + resultHash + "',account = '" + credentials.getAddress() + "' WHERE id = " + bean.getTransactionDetailId() + "; ");
            
            System.out.println("将交易Hash值更新至system_transactiondetail表中:" + "UPDATE system_transactiondetail SET fromcount = '" + credentials.getAddress() + "', turnhash = '" + resultHash + "', gas = " + Double.valueOf(transactionReceipt.getGasUsed().toString()) + ", flag = 1 WHERE contracttype = 'LotteryBuyTicket' AND contractid = " + bean.getTransactionDetailId() + ";");
            jdbc.execute("UPDATE system_transactiondetail SET fromcount = '" + credentials.getAddress() + "', turnhash = '" + resultHash + "', gas = " + Double.valueOf(transactionReceipt.getGasUsed().toString()) + ", flag = 1 WHERE contracttype = 'LotteryBuyTicket' AND contractid = " + bean.getTransactionDetailId() + ";");
            
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            toconsumer(bean, count);
            return "error, transaction hash is null.";
        }
    }
    
    public String toconsumerLotteryIssueSZBReward(KafkaConsumerBean bean, Integer count) {
    	BigInteger turnBalance = bean.getTurnBalance();
        BigInteger turnBalanceValue = turnBalance.multiply(BigInteger.valueOf(10000000000000000L));
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            System.out.println("将交易Hash值更新为异常值");
            jdbc.execute("UPDATE system_transactiondetail SET turnhash = '0x0',flag = 3,remark='交易未被写入区块链。'" 
            	+ "WHERE contracttype = 'LotteryIssueSZBReward' and contractid = " 
            	+ bean.getTransactionDetailId() + " and value = " + turnBalance);
            return null;
        }
        
        Web3j web3j = Web3j.build(new HttpService(TConfigUtils.selectIp()));
        Credentials credentials = getCredentials(bean);
        TransactionReceiptProcessor transactionReceiptProcessor = new NoOpProcessor(web3j);
        TransactionManager transactionManager = null;
        try{
            transactionManager = new RawTransactionManager(web3j, credentials, KafkaConsumerBean.getChainid(), transactionReceiptProcessor);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.err.println("构建交易管理异常！");
            toconsumerLotteryIssueSZBReward(bean, count);
            return "error, build transaction manager failed.";
        }
       
        Lottery contract = Lottery.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.backToUser(new Uint256(turnBalanceValue), new Uint32(bean.getTransactionDetailId()), BigInteger.valueOf(10L)).send();
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
                //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
            	toconsumerLotteryIssueSZBReward(bean, count);
            }
            System.out.println("将操作日志记录于表中:" + "INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lottery', '" + resultHash + "',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lotteryIssueSZBReward', '" + resultHash + "',sysdate())");
            
            System.out.println("将交易Hash值更新至system_transactiondetail表中:" + "UPDATE system_transactiondetail SET turnhash = '" + resultHash + "',flag = 1" + " WHERE contracttype = 'LotteryIssueSZBReward' and contractid = " + bean.getTransactionDetailId() + " and value = " + turnBalance + ";");
            jdbc.execute("UPDATE system_transactiondetail SET turnhash = '" + resultHash + "',flag = 1,remark='区块链夺宝神州币奖励。'" 
                	+ " WHERE contracttype = 'LotteryIssueSZBReward' and contractid = " 
                	+ bean.getTransactionDetailId() + " and value = " + turnBalance + ";");
            
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            toconsumerLotteryIssueSZBReward(bean, count);
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
        File file = new File(TConfigUtils.selectRootPath("keystore_temp_path_test") + keystoreName);

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
