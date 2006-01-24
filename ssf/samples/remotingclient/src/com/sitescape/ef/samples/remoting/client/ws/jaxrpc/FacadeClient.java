package com.sitescape.ef.samples.remoting.client.ws.jaxrpc;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import com.sitescape.ef.samples.remoting.client.FacadeClientHelper;
import com.sitescape.ef.samples.remoting.client.ws.jaxrpc.JaxRpcFacade;
import com.sitescape.ef.samples.remoting.client.ws.jaxrpc.JaxRpcFacadeService;
import com.sitescape.ef.samples.remoting.client.ws.jaxrpc.JaxRpcFacadeServiceLocator;

/**
 * This WS client program uses JAX-RPC compliant client binding classes 
 * generated by Axis' WSDL2Java tool.
 * 
 * @author jong
 *
 */
public class FacadeClient {

	public FacadeClient() {
	}

	public static void main(String[] args) throws ServiceException, RemoteException {

		System.out.println("*** This Facade client uses Axis-generated client bindings");

		// Arguments
		// read <binder id> <entry id> 
		// OR
		// add <binder id> <definition id>

		if(args.length < 3) {
			System.out.println("Invalid arguments");
			return;
		}

		// Note: Instead of specifying the wsdd file to the java launch
		// program (see build.xml), we could specify it programmatically
		// by passing config object to the service locator constructor
		// as shown below (which is commented out).

		//EngineConfiguration config = new FileProvider("client_deploy.wsdd");

		if(args[0].equals("read")) {
			System.out.println("*** Reading an entry ***");
			
			int binderId = Integer.parseInt(args[1]);
			int entryId = Integer.parseInt(args[2]);

			JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
			JaxRpcFacade service = locator.getFacade();

			// Invoke getEntry
			com.sitescape.ef.samples.remoting.client.ws.jaxrpc.Entry entry = 
				service.getEntry(binderId, entryId);

			printEntry(entry);
			
			// Invoke getEntryAsXML
			String entryAsXML = service.getEntryAsXML(binderId, entryId);
			
			FacadeClientHelper.printEntryAsXML(entryAsXML);			
		}
		else if(args[0].equals("add")){
			
			System.out.println("*** Adding an entry ***");

			int binderId = Integer.parseInt(args[1]);
			String definitionId = args[2];
			
			JaxRpcFacadeService locator = new JaxRpcFacadeServiceLocator(/*config*/);
			JaxRpcFacade service = locator.getFacade();

			String entryInputDataAsXML = 
				FacadeClientHelper.generateEntryInputDataAsXML(binderId, definitionId);
			
			long entryId = service.addEntry(binderId, definitionId, entryInputDataAsXML);
			
			System.out.println("*** ID of the newly created entry is " + entryId);
		}
		else {
			System.out.println("Invalid arguments");
			return;
		}
	}
	
	public static void printEntry(Entry entry) {
		System.out.println();
		System.out.println("*** Entry(" + entry.getBinderId() + "," + entry.getId() + ")");
		System.out.println("Title: " + entry.getTitle());
		System.out.println();
	}}
