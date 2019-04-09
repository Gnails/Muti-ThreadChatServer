package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.text.html.HTML.Tag;

public class Listener extends Thread {
	Socket s;
	public String mesg = null;
	BufferedReader br;
	boolean endTag = false;
	Object lock = new Object();
	Object shutDownLock = new Object();

	@Override
	public void run() {
		endTag = false;
		mesg = null;
		if (s == null) {
			System.out.println("***INFO***: " + this.getName() + " ____ SOCKET IS CLOSED CAN'T CONTINUE____");
			endTag = true;
			return;
		}
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (!endTag) {
			try {
				mesg = br.readLine();
				if (mesg == null) {
					endTag = true;
					return;
				}
				System.out.println("Port \""+s.getPort()+"\" tells you:  <<< " + mesg + ">>>");
			} catch (Exception e) {
				e.printStackTrace();
				endTag = true;
				System.out.println("***INFO***:LISTENER HAS EXITED!");
				return;
			} finally {
				synchronized (lock) {
					lock.notify();
				}
			}
		}
		System.out.println("***INFO***:LISTENER HAS EXITED!");
	}

	public Listener(Socket s) {
		this.s = s;
		setName("Listener");
	}

	public String getMesg() {
		if (endTag) {
			System.out.println("Cann't getMesg,listener has been shutDown!");
			return null;
		}
		synchronized (shutDownLock) {
			String tmp = mesg;
			synchronized (lock) {
				while (mesg == null && !endTag) {
					try {
						lock.notify();
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return null;
					}
				}
				tmp = mesg;
				mesg = null;
			}
			return tmp;
		}
	}

	public void shutDown() {
		if (s != null)
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (currentThread().isAlive())
			interrupt();
	}

}
