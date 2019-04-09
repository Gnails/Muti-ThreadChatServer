package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandPool extends Thread {

	LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>();
	String command = null;
	BufferedReader x = new BufferedReader(new InputStreamReader(System.in));
	boolean exitTag = false;
	Object lock = new Object();

	@Override
	public void run() {
		System.out.println("Comander: ");
		try {
			while (!exitTag) {
				lbq.offer(x.readLine());
			}
		} catch (IOException e) {
			System.out.println("Bye~Bye CommandPool");
			return;
		}
	}

	public String getCmd() throws InterruptedException {
		return lbq.take();
	}

	public void shutDown() {
		exitTag = true;
		try {
			x.close();
		} catch (IOException e) {
		}
	}

}
