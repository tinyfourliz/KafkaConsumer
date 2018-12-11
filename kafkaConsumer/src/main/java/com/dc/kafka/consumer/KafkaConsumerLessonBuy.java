package com.dc.kafka.consumer;



import org.web3j.tx.response.TransactionReceiptProcessor;

import com.dc.kafka.component.KafkaConsumerBean;
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
public class KafkaConsumerLessonBuy {
    
    @Autowired
    private JdbcTemplate jdbc;
    private Integer count = 1;
    
//    private static final String rootPath = "/eth/datadir/temp/";
//    private static final String rootPath = "C:\\temp";

    @KafkaListener(topics = {"lessonbuy"})
    public String processor(ConsumerRecord<?, ?> record){
        Gson gson = new Gson();
        KafkaConsumerBean bean = gson.fromJson(record.value().toString(), KafkaConsumerBean.class);
        count = 1;
        return toconsumer(bean);
    }

    public String toconsumer(KafkaConsumerBean bean) {
        count ++;
        System.out.println(count);
        //默认超过100次则该任务失效。
        if(count > 100) {
            count = 1;
            return null;
        }

        Integer index = (int)(Math.random()*(5));
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
       
        Lesson contract = Lesson.load(bean.getAddress(),web3j, transactionManager, bean.getGasPrice(), bean.getGasLimit());
        
        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = contract.buyChapter(bean.getTurnBalance()).send();
            
            String resultHash = transactionReceipt.getTransactionHash();
            System.out.println(resultHash);

            if(resultHash == null) {
                //FIXME 此处添加根据bean.getTransactionDetailId()更新resultHash。若 resultHash == null则为失败。需重新发送
                toconsumer(bean);
            }
            System.out.println("将操作日志记录于表中:" + "INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lessonbuy', '" + resultHash + "',sysdate())");
            jdbc.execute("INSERT INTO kafka_data (id, topic, hashres,createdate) VALUES (NULL, 'lessonbuy', '" + resultHash + "',sysdate())");
            
            System.out.println("将交易Hash值更新至am_lessonbuy表中:" + "UPDATE am_lessonbuy SET backup1='" + resultHash + "' WHERE id = " + bean.getTransactionDetailId() + "; ");
            jdbc.execute("UPDATE am_lessonbuy SET backup1='" + resultHash + "' WHERE id = " + bean.getTransactionDetailId() + "; ");
            
//        	fromcount tocount fromitcode toitcode flag turnhash turndate value contracttype remark contractid
		//	system_transactiondetail表，根据contracttype，contractid更新交易哈希，flag，获取gas并更新？
            jdbc.execute("UPDATE system_transactiondetail SET turnhash = '" + resultHash + "', gas = " + Double.valueOf(transactionReceipt.getGasUsed().toString()) + ", flag = 1 WHERE contracttype = TODO AND contractid = " + bean.getTransactionDetailId());
            return resultHash;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            toconsumer(bean);
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
