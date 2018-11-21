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
public class CashBack extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60008054600160a060020a033316600160a060020a03199091161790556101df8061003b6000396000f3006060604052600436106100615763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166341c0e1b58114610066578063713ed42d1461007b57806388e7fb3f146100835780638da5cb5b1461008e575b600080fd5b341561007157600080fd5b6100796100bd565b005b6100796100e6565b61007960043561012c565b341561009957600080fd5b6100a16101a4565b604051600160a060020a03909116815260200160405180910390f35b60005433600160a060020a039081169116146100d857600080fd5b600054600160a060020a0316ff5b7fd990f2c480ce3f4f968ef233333ebc5db36bd112673a74b9e5382b259a9888a43334604051600160a060020a03909216825260208201526040908101905180910390a1565b600160a060020a03331681156108fc0282604051600060405180830381858888f19350505050151561015d57600080fd5b7f61e88e49d49c8f406eebb1aa3186d721da6760e534a8d91e597fc8da8de34fc33382604051600160a060020a03909216825260208201526040908101905180910390a150565b600054600160a060020a0316815600a165627a7a7230582053f5df74a468e49faefdcaf05f6f937d1b87bfff12057007367b6fc4c16398d80029";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_CHARGETOCONTRACT = "chargeToContract";

    public static final String FUNC_BACKTOUSER = "backToUser";

    public static final String FUNC_OWNER = "owner";

    public static final Event CHARGEEVENT_EVENT = new Event("chargeEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BACKEVENT_EVENT = new Event("backEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    protected CashBack(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CashBack(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> chargeToContract(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CHARGETOCONTRACT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> backToUser(Uint256 balance, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_BACKTOUSER, 
                Arrays.<Type>asList(balance), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<Address> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public static RemoteCall<CashBack> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CashBack.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<CashBack> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CashBack.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<ChargeEventEventResponse> getChargeEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(CHARGEEVENT_EVENT, transactionReceipt);
        ArrayList<ChargeEventEventResponse> responses = new ArrayList<ChargeEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ChargeEventEventResponse typedResponse = new ChargeEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
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
                return typedResponse;
            }
        });
    }

    public Observable<ChargeEventEventResponse> chargeEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CHARGEEVENT_EVENT));
        return chargeEventEventObservable(filter);
    }

    public List<BackEventEventResponse> getBackEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BACKEVENT_EVENT, transactionReceipt);
        ArrayList<BackEventEventResponse> responses = new ArrayList<BackEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BackEventEventResponse typedResponse = new BackEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BackEventEventResponse> backEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BackEventEventResponse>() {
            @Override
            public BackEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BACKEVENT_EVENT, log);
                BackEventEventResponse typedResponse = new BackEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                return typedResponse;
            }
        });
    }

    public Observable<BackEventEventResponse> backEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BACKEVENT_EVENT));
        return backEventEventObservable(filter);
    }

    public static CashBack load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CashBack(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static CashBack load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CashBack(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class ChargeEventEventResponse {
        public Log log;

        public Address account;

        public Uint256 value;
    }

    public static class BackEventEventResponse {
        public Log log;

        public Address account;

        public Uint256 value;
    }
}
