package com.dc.kafka.consumer;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.utils.TConfigUtils;
import com.google.gson.Gson;

@Component
public class KafkaConsumerEthAccount {
    
    @Autowired
    private JdbcTemplate jdbc;

    @KafkaListener(topics = {"withdrawconfirm"})
    public String processor(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        Integer count = 1;
        return toconsumer(bean, count);
    }
    
//    @KafkaListener(topics = {"withdrawconfirm"})
//    public String processor(ConsumerRecord<?, ?> record){
//        Gson gson = new Gson();
//        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
//        count = 1;
//        return toconsumer(bean);
//    }

    public String toconsumer(KafkaConsumerBean bean, Integer count) {
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            return null;
        }
        
        String resultHash="";
		try {
			Credentials credentials = getCredentials(bean);
	        List<Web3j> web3jList = new ArrayList<>();
			List<String> ipArr = TConfigUtils.selectIpArr();
			for(int i = 0; i < ipArr.size(); i++) {
				web3jList.add(Web3j.build(new HttpService(ipArr.get(i))));
			}
	        
			EthGetTransactionCount ethGetTransactionCount = web3jList.get(new Random().nextInt(5)).ethGetTransactionCount(bean.getContractName(), DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			System.err.println("nonce:" + nonce);
			RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, BigInteger.valueOf(2200000000L), BigInteger.valueOf(2100000L), bean.getAddress(), bean.getTurnBalance());
			//签名Transaction，这里要对交易做签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			System.err.println("hexValue:" + hexValue);
			//发送交易
			String transactionHash = "";
			for(int i = 0; i < web3jList.size(); i++) {
				transactionHash = web3jList.get(i).ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
				if(transactionHash != null) {
					resultHash = transactionHash;
				}
			}
			if(resultHash.equals("") || resultHash == null){
        		toconsumer(bean,count);
        	}
			
			System.out.println("将操作日志记录于表中:" + "INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'withdrawConfirm', '" + resultHash + "',sysdate())");
			jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (" + bean.getTransactionDetailId() + ", 'withdrawConfirm', '" + resultHash + "',sysdate())");
			  
			System.out.println("将交易哈希记录至am_wallettransaction表中:" + "UPDATE am_wallettransaction SET transactionHash = '" + resultHash + "' WHERE id = " + bean.getTransactionDetailId());
			jdbc.execute("UPDATE am_wallettransaction SET transactionHash = '" + resultHash + "' WHERE id = " + bean.getTransactionDetailId());
			
			//	fromcount tocount fromitcode toitcode flag turnhash turndate value gas contracttype remark contractid
			//	system_transactiondetail表，根据contracttype，contractid更新交易哈希，flag，获取gas并更新？
			jdbc.execute("UPDATE system_transactiondetail SET turnhash = '" + resultHash + "', gas = " + Double.valueOf(rawTransaction.getGasPrice().toString()) + ", flag = 1 WHERE contracttype = TODO AND contractid = " + bean.getTransactionDetailId());
			return resultHash;
		} catch (Exception e) {
			e.printStackTrace();
			if(resultHash.equals("")){
        		toconsumer(bean,count);
        		System.out.println("第"+count+"次重发");
        	}
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
