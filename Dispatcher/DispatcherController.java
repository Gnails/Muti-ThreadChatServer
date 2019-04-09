package Dispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import pool.*;
import server.ServerWorker;
import util.Listener;
import util.Sender;

public class DispatcherController extends ServerSocket {
	static HashMap<String, ServerWorker> serverListHashMap = new HashMap<String, ServerWorker>();
	boolean exitTag = false;

	public DispatcherController(int port, int backlog) throws IOException {
		super(port, backlog);
	}

	public void startService() {
		Socket s = null;
		Random r = new Random();
		while (true) {
			try {
				s = this.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(
					"**********************************INFO: START PROCESSING REQUEST!**********************************");
			InPoolAdmin inPoolAdmin = new InPoolAdmin(new Listener(s));
			OutPoolAdmin outPoolAdmin = new OutPoolAdmin(new Sender(s));
			inPoolAdmin.start();
			outPoolAdmin.start();
			String id = s.getInetAddress().getHostAddress();
			int port = r.nextInt(65536 - 1500) + 1500;
			try {
				outPoolAdmin.addMesg(Integer.toString(port));
				ServerWorker sw = new ServerWorker(port);
				Thread t = new Thread(sw);
				t.setName("sworker");
				serverListHashMap.put(id, sw);
				t.start();

			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
//			inPoolAdmin.shutDown();
//			outPoolAdmin.shutDown();
			System.out.println(
					"**********************************INFO:REQUEST PROCESS SCC,THE NEXT ONE**********************************");

		}

	}

	public static void main(String[] args) {
		DispatcherController dpc;
		try {
			dpc = new DispatcherController(9096, 4);
			dpc.startService();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
