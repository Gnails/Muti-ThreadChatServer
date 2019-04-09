package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Sender extends Thread {
	BufferedWriter bWriter;
	Socket s;
	String mesg;
	boolean endTag = false;
	boolean sccFlag = false;
	Object lock = new Object();
	Object runlock = new Object();
	Object shutDownlock = new Object();

	public Sender(Socket s) {
		this.s = s;
		setName("SENDER");
	}

	@Override
	public void run() {
		endTag = false;
		sccFlag = false;
		if (s == null || s.isClosed()) {
			System.out.println("***INFO***: " + this.getName() + " ____ SOCKET IS CLOSED CAN'T CONTINUE____");
			endTag = true;
			return;
		}
		try {
			bWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (!endTag) {
			synchronized (lock) {
				try {
					lock.notify();
					lock.wait();
					if (!endTag && mesg != null) {
						bWriter.write((mesg + '\n').toCharArray());
						sccFlag = true;
						System.out.println("I wanna say to \""+s.getPort()+"\": " + "<<" + mesg + ">>");
						mesg = null;
						bWriter.flush();
//						bWriter.close();
					} else {
						System.out.println("***INFO***: SENDER HAS EXITED!");
						endTag = true;
						return;
					}
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
					System.out.println("***INFO***: SENDER HAS EXITED!");
					endTag = true;
					return;
				} finally {
					lock.notify();
				}
			}
		}
		System.out.println("***INFO***: SENDER HAS EXITED!");
	}

	public void sendData(String mesg) {
		if (endTag) {
			System.out.println("Cann't send data,the sender has been shutDown!");
			return;
		}
		synchronized (shutDownlock) {
			synchronized (lock) {
				this.mesg = mesg;
				while (!sccFlag && !endTag) {
					lock.notify();
					try {
						lock.wait();
					} catch (InterruptedException e) {
						return;
					}

				}
			}
			sccFlag = false;
		}
	}

	public void shutDown() {
		synchronized (shutDownlock) {
			synchronized (lock) {
				if (endTag == true) {
					lock.notify();
					return;
				}
				endTag = true;
				this.mesg = null;
				lock.notify();
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (currentThread().isAlive()) {
			interrupt();
		}
	}

	public boolean hasMessage() {
		if (mesg == null)
			return false;
		else
			return true;

	}
}
