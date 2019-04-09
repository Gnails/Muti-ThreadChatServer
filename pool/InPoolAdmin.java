package pool;

import java.util.concurrent.LinkedBlockingQueue;

import util.Listener;

public class InPoolAdmin extends Thread {
	Listener ls;
	LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>();
	boolean exitTag = false;
	Object lock = new Object();

	public InPoolAdmin(Listener ls) {
		this.ls = ls;
		setName("InPoolAdmin");
	}

	@Override
	public void run() {
		ls.start();
		while (!exitTag && ls.isAlive()) {
			String st = ls.getMesg();
			synchronized (lock) {
				try {
					if (st == null) {
						throw new InterruptedException("st is null");
					}
					lbq.offer(st);
					lock.notify();
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					exitTag = true;
					System.out.println("***INFO***:EXIT INPOOLADMIN");
					return;
				} finally {
					lock.notify();
				}
			}
		}
		System.out.println("***INFO***:EXIT INPOOLADMIN");
	}

	public String getMesg() {
		while (!hasMesg()) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}
		try {
			return lbq.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean hasMesg() {
		return lbq.size() == 0 ? false : true;
	}

	public void shutDown() {
		ls.shutDown();
		if (currentThread().isAlive())
			interrupt();
	}

}
