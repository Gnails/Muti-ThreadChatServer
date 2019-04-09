package pool;

import java.util.concurrent.LinkedBlockingQueue;

import util.Sender;

public class OutPoolAdmin extends Thread {
	Sender sd;
	LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>();
	boolean exitTag = false;
	Object lock = new Object();

	public OutPoolAdmin(Sender sd) {
		this.sd = sd;
		setName("OutPoolAdmin");
	}

	@Override
	public void run() {
		sd.start();
		while (!exitTag && sd.isAlive()) {
			synchronized (lock) {
				try {
					lock.notify();
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					exitTag = true;
					return;
				}
			}
			try {
				while (!exitTag && !lbq.isEmpty() && sd.isAlive()) {
					sd.sendData(lbq.take());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				exitTag = true;
				System.out.println("***INFO***:EXIT OUTPOOLADMIN");
			}
		}
		System.out.println("***INFO***:EXIT OUTPOOLADMIN");
	}

	public void addMesg(String mesg) throws InterruptedException {
		if (!exitTag) {
			lbq.offer(mesg);
		}
		while (!lbq.isEmpty()) {
			synchronized (lock) {
				lock.notify();
				lock.wait();
			}

		}

	}

	public void shutDown() {
		sd.shutDown();
		if (currentThread().isAlive())
			interrupt();
	}

}
