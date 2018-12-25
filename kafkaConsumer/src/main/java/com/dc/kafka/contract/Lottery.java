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
import org.web3j.abi.datatypes.generated.Uint32;
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
public class Lottery extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60008054600160a060020a033316600160a060020a031990911617905561022e8061003b6000396000f30060606040526004361061006c5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166341c0e1b58114610071578063473d2d94146100865780635b8e6f661461009a578063713ed42d146100ab5780638da5cb5b146100b3575b600080fd5b341561007c57600080fd5b6100846100e2565b005b61008460043563ffffffff6024351661010b565b61008463ffffffff60043516610197565b6100846101f1565b34156100be57600080fd5b6100c66101f3565b604051600160a060020a03909116815260200160405180910390f35b60005433600160a060020a039081169116146100fd57600080fd5b600054600160a060020a0316ff5b600160a060020a03331682156108fc0283604051600060405180830381858888f19350505050151561013c57600080fd5b7ff5a776388956041aeeba8284545da7273711c5e58bf6660a32903eb0d92f41d2338383604051600160a060020a039093168352602083019190915263ffffffff166040808301919091526060909101905180910390a15050565b7f3eef276644a7d9081e6dfb2c91d4519378adc1e36a6b6c9f69f3760f0bf7e264333483604051600160a060020a039093168352602083019190915263ffffffff166040808301919091526060909101905180910390a150565b565b600054600160a060020a0316815600a165627a7a7230582041dd34598304d1430d1529fce8f98349b4c8f10b27f8b3fdce6a2d13e5e8bd050029";

    public static final String FUNC_KILL = "kill";

    public static final String FUNC_BACKTOUSER = "backToUser";

    public static final String FUNC_PAIDLOTTERY = "paidLottery";

    public static final String FUNC_CHARGETOCONTRACT = "chargeToContract";

    public static final String FUNC_OWNER = "owner";

    public static final Event LOTTERYEVENT_EVENT = new Event("lotteryEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
    ;

    public static final Event ISSUEEVENT_EVENT = new Event("issueEvent", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint32>() {}));
    ;

    protected Lottery(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Lottery(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> kill() {
        final Function function = new Function(
                FUNC_KILL, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> backToUser(Uint256 balance, Uint32 lotteryId, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_BACKTOUSER, 
                Arrays.<Type>asList(balance, lotteryId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> paidLottery(Uint32 lotteryId, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_PAIDLOTTERY, 
                Arrays.<Type>asList(lotteryId), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> chargeToContract(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CHARGETOCONTRACT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<Address> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function);
    }

    public static RemoteCall<Lottery> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Lottery.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Lottery> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Lottery.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<LotteryEventEventResponse> getLotteryEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOTTERYEVENT_EVENT, transactionReceipt);
        ArrayList<LotteryEventEventResponse> responses = new ArrayList<LotteryEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LotteryEventEventResponse typedResponse = new LotteryEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.lotteryId = (Uint32) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<LotteryEventEventResponse> lotteryEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, LotteryEventEventResponse>() {
            @Override
            public LotteryEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOTTERYEVENT_EVENT, log);
                LotteryEventEventResponse typedResponse = new LotteryEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.lotteryId = (Uint32) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Observable<LotteryEventEventResponse> lotteryEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOTTERYEVENT_EVENT));
        return lotteryEventEventObservable(filter);
    }

    public List<IssueEventEventResponse> getIssueEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ISSUEEVENT_EVENT, transactionReceipt);
        ArrayList<IssueEventEventResponse> responses = new ArrayList<IssueEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            IssueEventEventResponse typedResponse = new IssueEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
            typedResponse.lotteryId = (Uint32) eventValues.getNonIndexedValues().get(2);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<IssueEventEventResponse> issueEventEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, IssueEventEventResponse>() {
            @Override
            public IssueEventEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ISSUEEVENT_EVENT, log);
                IssueEventEventResponse typedResponse = new IssueEventEventResponse();
                typedResponse.log = log;
                typedResponse.account = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(1);
                typedResponse.lotteryId = (Uint32) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

    public Observable<IssueEventEventResponse> issueEventEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ISSUEEVENT_EVENT));
        return issueEventEventObservable(filter);
    }

    public static Lottery load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Lottery(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Lottery load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Lottery(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class LotteryEventEventResponse {
        public Log log;

        public Address account;

        public Uint256 value;

        public Uint32 lotteryId;
    }

    public static class IssueEventEventResponse {
        public Log log;

        public Address account;

        public Uint256 value;

        public Uint32 lotteryId;
    }
}
