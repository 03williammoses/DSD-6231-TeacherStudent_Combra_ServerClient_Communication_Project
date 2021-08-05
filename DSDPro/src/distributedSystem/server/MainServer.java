package distributedSystem.server;

import java.io.File;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import distributedSystem.DCMSCorba.DCMSInterface;
import distributedSystem.DCMSCorba.DCMSInterfaceHelper;


public class MainServer  {

	
	public static void main(String args[]) throws AlreadyBoundException, RemoteException {

		buildLogDirectory("./logs");
		startServers(args);
		System.out.println("Server(s) are Started");
	}
	

	public static void buildLogDirectory(String path) {
		File outputDir = new File(path);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}

	public static void startServers(String args[]) throws RemoteException {
		try {
		ORB orb = ORB.init(args, null);
		POA rootpoa;
		rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
		rootpoa.the_POAManager().activate();
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

		MontrealServer montrealServer = new MontrealServer("Montreal");
		montrealServer.setORB(orb);

		Runnable montreal = () -> {
			try {
				montrealServer.serverConnection(8880, montrealServer);
			} catch (RemoteException | AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread t1 = new Thread(montreal);
		t1.start();
		org.omg.CORBA.Object montrealObjectRef = rootpoa.servant_to_reference(montrealServer);
		DCMSInterface dcmsMontrealRef = DCMSInterfaceHelper.narrow(montrealObjectRef);
		NamingContextExt ncRefMontreal = NamingContextExtHelper.narrow(objRef);
		NameComponent pathMontreal[] = ncRefMontreal.to_name("Montreal");
		ncRefMontreal.rebind(pathMontreal, dcmsMontrealRef);

		LavalServer lavalServer = new LavalServer("Laval");
		lavalServer.setORB(orb);
		Runnable laval = () -> {
			try {
				lavalServer.serverConnection(8881, lavalServer);
			} catch (RemoteException | AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread t2 = new Thread(laval);
		t2.start();
		org.omg.CORBA.Object lavalObjectRef = rootpoa.servant_to_reference(lavalServer);
		DCMSInterface dcmsLavalRef = DCMSInterfaceHelper.narrow(lavalObjectRef);
		NamingContextExt ncRefLaval = NamingContextExtHelper.narrow(objRef);
		NameComponent pathLaval[] = ncRefLaval.to_name("Laval");
		ncRefLaval.rebind(pathLaval, dcmsLavalRef);

		DollardServer dollardServer = new DollardServer("Dollard-des-Ormeaux");
		Runnable dollard = () -> {
			try {
				dollardServer.serverConnection(8882, dollardServer);
			} catch (RemoteException | AlreadyBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Thread t3 = new Thread(dollard);
		t3.start();
		org.omg.CORBA.Object dollardObjectRef = rootpoa.servant_to_reference(dollardServer);
		DCMSInterface dcmsDollardRef = DCMSInterfaceHelper.narrow(dollardObjectRef);
		NamingContextExt ncRefDollard = NamingContextExtHelper.narrow(objRef);
		NameComponent pathDollard[] = ncRefDollard.to_name("Dollard-des-Ormeaux");
		ncRefDollard.rebind(pathDollard, dcmsDollardRef);

	} catch (Exception e) {
		e.printStackTrace(System.out);
	}
	}



}