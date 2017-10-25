package com.javacodegeeks.examples.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javacodegeeks.examples.bo.FXTransaction;
import com.javacodegeeks.examples.bo.Role;

import org.springframework.http.MediaType;

@RestController
public class Controller {

	
	private static HFClient client = null;

	private static Channel channel = InitChannel();

	public static void main(String[] args) throws Exception {
		
		String newOwner = "2000";
		System.out.println("New owner is '" + newOwner + "'\n");

		queryFabcar(channel, "CAR1");
		updateCarOwner(channel, "CAR1", newOwner, false);

		System.out.println("after request for transaction without commit");
		queryFabcar(channel, "CAR1");
		updateCarOwner(channel, "CAR1", newOwner, true);

		System.out.println("after request for transaction WITH commit");
		queryFabcar(channel, "CAR1");

		System.out.println("Sleeping 5s");
		Thread.sleep(5000); // 5secs
		queryFabcar(channel, "CAR1");
		System.out.println("all done");

	}

	private static Channel InitChannel() {
		try {
			client = HFClient.createNewInstance();
			CryptoSuite cs = CryptoSuite.Factory.getCryptoSuite();
			client.setCryptoSuite(cs);

			//User user = new SampleUser("c:\\data\\creds", "PeerAdmin");
			// "Log in"
			//client.setUserContext(user);
			Channel channel = client.newChannel("mychannel");
			channel.addPeer(client.newPeer("peer", "grpc://192.168.99.100:7051"));
			// It always wants orderer, otherwise even query does not work
			channel.addOrderer(client.newOrderer("orderer", "grpc://192.168.99.100:7050"));
			channel.initialize();
		}
		catch (Exception ex){
		ex.printStackTrace();
		}
		return channel;
	}

	private static void queryFabcar(Channel channel, String key) throws Exception {
		QueryByChaincodeRequest req = client.newQueryProposalRequest();
		ChaincodeID cid = ChaincodeID.newBuilder().setName("fabcar").build();
		req.setChaincodeID(cid);
		req.setFcn("queryCar");
		req.setArgs(new String[] { key });
		System.out.println("Querying for " + key);
		Collection<ProposalResponse> resps = channel.queryByChaincode(req);
		for (ProposalResponse resp : resps) {
			String payload = new String(resp.getChaincodeActionResponsePayload());
			System.out.println("response: " + payload);
		}

	}



	private String getPrnString(ProposalResponse prop){
				String responseStr = "NONE";
				try{
					responseStr = new String(prop.getChaincodeActionResponsePayload());
				}catch (Exception e){
					e.printStackTrace();
				}
				return responseStr;
	}
	
	@RequestMapping("/getFxTransactions")
	public List<FXTransaction> getUsers() {

		List<FXTransaction> users = new ArrayList<FXTransaction>();
		users.add(new FXTransaction("USD/AUD", "AAAAA","AUD",3000));
		users.add(new FXTransaction("JPY/AUD", "BBBBB","AUD",3000));
		users.add(new FXTransaction("EUR/CHF", "CCCCC","CHF",3000));
		users.add(new FXTransaction("USD/EUR", "DDDDD","EUR",3000));
		users.add(new FXTransaction("USD/JPY", "DEEEE","JPY",3000));
		return users;
	}

	@RequestMapping("/getRoles")
	public List<Role> getRoles() {
		
		List<Role> roles = new ArrayList<Role>();
		roles.add(new Role("Manager"));
		roles.add(new Role("Lead"));
		roles.add(new Role("admin"));
		return roles;
	}
	
	@RequestMapping(value = "/submitTransaction/", method = RequestMethod.POST)
	public void saveUser(@ModelAttribute FXTransaction fXTransaction) {
		System.out.println("fXTransaction : " + fXTransaction.getCustomerName());
	}
	
	
	
	@RequestMapping(value = "/submitTransaction", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> createEmployee(@RequestBody FXTransaction fXTransaction) {
		
		System.out.println("fXTransaction : " + fXTransaction.getCustomerName());
		System.out.println("fXTransaction : " + fXTransaction.getAmount());
		System.out.println("fXTransaction : " + fXTransaction.getCurrencyPair());
		return new ResponseEntity<String>(HttpStatus.CREATED);
    }
	
	private static void updateCarOwner(Channel channel, String key, String newOwner, Boolean doCommit)
			throws Exception {
		TransactionProposalRequest req = client.newTransactionProposalRequest();
		ChaincodeID cid = ChaincodeID.newBuilder().setName("fabcar").build();
		req.setChaincodeID(cid);
		req.setFcn("changeCarOwner");
		req.setArgs(new String[] { key });
		System.out.println("Executing for " + key);
		Collection<ProposalResponse> resps = channel.sendTransactionProposal(req);
	    Iterator<ProposalResponse> it = resps.iterator();
	    while(it.hasNext()) {
	    	System.out.println("-------------------");
	    	System.out.println("-------------------");
	    	System.out.println(it.next().isVerified());
	    	System.out.println("-------------------");
	    	System.out.println("-------------------");
	    }
		if (doCommit) {
			// channel.sendTransaction(resps);
			CompletableFuture<TransactionEvent> event = channel.sendTransaction(resps);
			//System.out.println(event.isCompletedExceptionally());
		}
	}
}
