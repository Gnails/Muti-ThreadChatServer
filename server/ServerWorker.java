package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import pool.InPoolAdmin;
import pool.OutPoolAdmin;
import util.Listener;
import util.Sender;

public class ServerWorker extends ServerSocket implements Runnable {
	Socket thisSocket;
	Sender sender;
	Listener listener;
	Object lock;
	OutPoolAdmin outPoolAdmin;
	InPoolAdmin inPoolAdmin;

	public void run() {
		try {
			thisSocket = this.accept();
			sender = new Sender(thisSocket);
			sender.setName(thisSocket.getInetAddress().getHostAddress() + ":sender");
			listener = new Listener(thisSocket);
			listener.setName(thisSocket.getInetAddress().getHostAddress() + ":listener");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		outPoolAdmin = new OutPoolAdmin(sender);
		inPoolAdmin = new InPoolAdmin(listener);
		outPoolAdmin.start();
		inPoolAdmin.start();
		Scanner sc = new Scanner(System.in);
		String x;
		while (!(x = sc.nextLine()).equals("#ByeBye#")) {
			try {
				outPoolAdmin.addMesg(x);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		shutDown();

	}

	public void shutDown() {
		outPoolAdmin.shutDown();
		inPoolAdmin.shutDown();
	}

	public ServerWorker(int port) throws IOException {
		super(port);
	}

	public void sendSomething(String mesg) {
		sender.sendData(mesg);
	}

	public String receiveSomething() {
		return listener.getMesg();

	}

}
