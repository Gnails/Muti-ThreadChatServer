package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import pool.InPoolAdmin;
import pool.OutPoolAdmin;
import util.Listener;
import util.Sender;

public class PoolClientUser {
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		String tmp = null;
		Socket s = new Socket("127.0.0.1", 9096);
		OutPoolAdmin outPoolAdmin = new OutPoolAdmin(new Sender(s));
		InPoolAdmin inPoolAdmin = new InPoolAdmin(new Listener(s));
		outPoolAdmin.start();
		inPoolAdmin.start();
		while (!inPoolAdmin.hasMesg())
			TimeUnit.SECONDS.sleep(1);
		tmp = inPoolAdmin.getMesg();
		inPoolAdmin.shutDown();
		outPoolAdmin.shutDown();
		s.close();
		s = new Socket("127.0.0.1", Integer.parseInt(tmp));
		OutPoolAdmin outPoolAdminClient = new OutPoolAdmin(new Sender(s));
		InPoolAdmin inPoolAdminClient = new InPoolAdmin(new Listener(s));
		outPoolAdminClient.start();
		inPoolAdminClient.start();
		Scanner sc = new Scanner(System.in);
		String x;
		while (!(x = sc.nextLine()).equals("#ByeBye#")) {
			outPoolAdminClient.addMesg(x);
		}
		sc.close();

		inPoolAdminClient.shutDown();
		outPoolAdminClient.shutDown();
//		s.close();
	}

}
