package com.dc.kafka.component;

import java.math.BigInteger;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

/*
 *该类为kafka消费者Bean实体类，包含构建一个交易所需的全部信息。
*/
public class KafkaConsumerBean {
    //链ID
    private static final byte chainId = (byte) 10;
    //交易记录表主键字段（update交易hash使用）
    private Integer transactionDetailId;
	//合约java类
	private String contractName;
	//用户凭证
	//private Credentials credentials;
	private String keystoreFile;
	private String password;
	
	//web3j连接
    //private Web3j web3j;
    //合约地址
	private String address;
	//gas价格
    private BigInteger gasPrice;
    //gas限制
    private BigInteger gasLimit;
    //转账金额
    private BigInteger turnBalance;
    
   public KafkaConsumerBean(Integer transactionDetailId, String contractName,   String address, BigInteger gasPrice, BigInteger gasLimit, BigInteger turnBalance,
		   String password,String keystoreFile){
    	this.transactionDetailId = transactionDetailId;
    	this.contractName = contractName;
    	//this.credentials = credentials;
    	//this.web3j = web3j;
    	this.address = address;
    	this.gasPrice = gasPrice;
    	this.gasLimit = gasLimit;
    	this.turnBalance = turnBalance;
    	this.keystoreFile = keystoreFile;
    	this.password = password;
    }
    
   public KafkaConsumerBean(Integer transactionDetailId, String contractName,  String address, BigInteger turnBalance,String password,String keystoreFile){
    	this.transactionDetailId = transactionDetailId;
    	this.contractName = contractName;
	   	//this.credentials = credentials;
	    //this.web3j = web3j;
    	this.address = address;
    	this.gasPrice = ManagedTransaction.GAS_PRICE;
    	this.gasLimit = Contract.GAS_LIMIT;
    	this.turnBalance = turnBalance;
    	this.keystoreFile = keystoreFile;
    	this.password = password;
    }
    
	public Integer getTransactionDetailId() {
		return transactionDetailId;
	}
	public void setTransactionDetailId(Integer transactionDetailId) {
		this.transactionDetailId = transactionDetailId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public BigInteger getGasPrice() {
		return gasPrice;
	}
	public void setGasPrice(BigInteger gasPrice) {
		this.gasPrice = gasPrice;
	}
	public BigInteger getGasLimit() {
		return gasLimit;
	}
	public void setGasLimit(BigInteger gasLimit) {
		this.gasLimit = gasLimit;
	}
	public static byte getChainid() {
		return chainId;
	}

	public BigInteger getTurnBalance() {
		return turnBalance;
	}

	public void setTurnBalance(BigInteger turnBalance) {
		this.turnBalance = turnBalance;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
