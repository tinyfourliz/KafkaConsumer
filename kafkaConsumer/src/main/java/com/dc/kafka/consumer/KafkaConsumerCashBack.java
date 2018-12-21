package com.dc.kafka.consumer;



import org.web3j.tx.response.TransactionReceiptProcessor;

import com.dc.kafka.component.KafkaConsumerBean;
import com.dc.kafka.contract.CashBack;
import com.dc.kafka.contract.Lesson;
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
public class KafkaConsumerCashBack {
    
    @Autowired
    private JdbcTemplate jdbc;
    private Integer count = 1;

    private static String[] ip = {"http://10.7.10.124:8545","http://10.7.10.125:8545","http://10.0.5.217:8545","http://10.0.5.218:8545","http://10.0.5.219:8545" };
//    private static final String rootPath = "/eth/datadir/temp/";

    @KafkaListener(topics = {"cashback"})
    public String processor(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        count = 1;
        return toconsumer(bean);
    }

    public String toconsumer(KafkaConsumerBean bean) {
    	try {
			Thread.sleep(Long.valueOf(TConfigUtils.selectValueByKey("sleep_time")));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			System.out.println("KafkaConsumerCashBack---sleep异常");
		}
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
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
            toconsumer(bean);
            return "error, build transaction manager failed.";
        }
       
        CashBack contract = CashBack.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        String resultHash="";
        try {
        	transactionReceipt = contract.backToUser(new Uint256(bean.getTurnBalance()), BigInteger.ZERO).send();
             resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
                //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
                toconsumer(bean);
            }
            System.out.println("将操作日志记录于表中:" + "INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lessonbuy', '" + resultHash + "',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'cashback', '" + resultHash + "',sysdate())");
            
            System.out.println("将返现信息记录至vm_cashback_details表中:" + "INSERT INTO vm_cashback_details (itcode, account, cashBackValue, limitFlag) VALUES ("+ bean.getContractName() + ", " + transactionManager.getFromAddress() + ", " + bean.getTurnBalance() + ", 1");
            jdbc.execute("INSERT INTO vm_cashback_details (itcode, account, cashBackValue, limitFlag) VALUES ('"+ bean.getContractName() + "', '" + transactionManager.getFromAddress() + "', " + bean.getTurnBalance().doubleValue()/10000000000000000L + ", 1)");
            
//        	fromcount tocount fromitcode toitcode flag turnhash turndate value contracttype remark contractid
		//	system_transactiondetail表，根据contracttype，contractid更新交易哈希，flag，获取gas并更新？
            //jdbc.execute("UPDATE system_transactiondetail SET turnhash = '" + resultHash + "', gas = " + Double.valueOf(transactionReceipt.getGasUsed().toString()) + ", flag = 1 WHERE contracttype = TODO AND contractid = " + bean.getTransactionDetailId());
            
            resultHash="";
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	if(resultHash.equals("")){
        		toconsumer(bean);
        		System.out.println("第"+count+"次重发");
        	}
        		
            e.printStackTrace();
            
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
