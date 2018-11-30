package com.dc.kafka.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class Article extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b61020e8061001e6000396000f3006060604052600436106100615763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416638da5cb5b8114610066578063ab1633c214610095578063b1c553fa146100aa578063e466cf18146100b8575b600080fd5b341561007157600080fd5b6100796100c9565b604051600160a060020a03909116815260200160405180910390f35b34156100a057600080fd5b6100a86100d8565b005b6100a860ff60043516610102565b6100a860ff60043516602435610159565b600054600160a060020a031681565b6000805473ffffffffffffffffffffffffffffffffffffffff191633600160a060020a0316179055565b7ffe0bbe7b3c23c1b8195458729b863c04c1cededbe8c12a0c2d00f5568cab594a333483604051600160a060020a039093168352602083019190915260ff166040808301919091526060909101905180910390a150565b600160a060020a03331681156108fc0282604051600060405180830381858888f19350505050151561018a57600080fd5b7fdff7e6a8bba6817f18b1ccdcab87c67ef936613def24bc4377feaca5f66a0645338284604051600160a060020a039093168352602083019190915260ff166040808301919091526060909101905180910390a150505600a165627a7a7230582023e021815e1474f335a85faf22a5637379dabb664f6203cbfe27590b333b490d0029";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_ARTICLE = "article";

    public static final String FUNC_CHARGETOCONTRACT = "chargeToContract";

    public static final String FUNC_WITHDRAWFROMCONTRACT = "withdrawFromContract";

    public static final Event CHARGEEVENT_EVENT = new Event("chargeEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
    ;

    public static final Event WITHDRAWEVENT_EVENT = new Event("withdrawEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
    ;

    protected Article(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Article(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Address> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public RemoteCall<TransactionReceipt> article() {
        final Function function = new Function(
                FUNC_ARTICLE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> chargeToContract(Uint8 articleId, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CHARGETOCONTRACT, 
                Arrays.<Type>asList(articleId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> withdrawFromContract(Uint8 articleId, Uint256 balance, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_WITHDRAWFROMCONTRACT, 
                Arrays.<Type>asList(articleId, balance), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public List<ChargeEventEventResponse> getChargeEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CHARGEEVENT_EVENT, transactionReceipt);
        ArrayList<ChargeEventEventResponse> responses = new ArrayList<ChargeEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChargeEventEventResponse typedResponse = new ChargeEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.articleId = (Uint8) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ChargeEventEventResponse> chargeEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ChargeEventEventResponse>() {
            @Override
            public ChargeEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(CHARGEEVENT_EVENT, log);
                ChargeEventEventResponse typedResponse = new ChargeEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.articleId = (Uint8) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Observable<ChargeEventEventResponse> chargeEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHARGEEVENT_EVENT));
        return chargeEventEventObservable(filter);
    }

    public List<WithdrawEventEventResponse> getWithdrawEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(WITHDRAWEVENT_EVENT, transactionReceipt);
        ArrayList<WithdrawEventEventResponse> responses = new ArrayList<WithdrawEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            WithdrawEventEventResponse typedResponse = new WithdrawEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.articleId = (Uint8) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<WithdrawEventEventResponse> withdrawEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, WithdrawEventEventResponse>() {
            @Override
            public WithdrawEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(WITHDRAWEVENT_EVENT, log);
                WithdrawEventEventResponse typedResponse = new WithdrawEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.articleId = (Uint8) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Observable<WithdrawEventEventResponse> withdrawEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(WITHDRAWEVENT_EVENT));
        return withdrawEventEventObservable(filter);
    }

    public static RemoteCall<Article> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Article.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Article> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Article.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static Article load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Article(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Article load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Article(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ChargeEventEventResponse {
        public Log log;

        public Address account;

        public Uint256 value;

        public Uint8 articleId;
    }

    public static class WithdrawEventEventResponse {
        public Log log;

        public Address account;

        public Uint256 value;

        public Uint8 articleId;
    }
}
