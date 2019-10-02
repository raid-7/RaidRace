package ru.raid_7.raidrace.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;

import ru.raid_7.raidrace.IO;

@SuppressWarnings("serial")
public class ConnectionQueue extends ArrayDeque<IO> implements Runnable {
	private Thread th;
	private boolean works = false;
	private ServerSocket ss;
	
	public ConnectionQueue(ServerSocket sers) {
		super();
		th = new Thread(this);
		ss = sers;
	}
	
	public void run() {
		while (works) {
			try {
				Socket s = ss.accept();
				IO o = new IO(s);
				offer(o);
				synchronized(this) {
					notify();
				}
			} catch (IOException exc) {
				works = false;
			}
		}
	}
	public void start() {
		if (works) return;
		works = true;
		th.start();
	}
	public void stop() {
		if (!works) return;
		works = false;
		try {
			new Socket("localhost", ss.getLocalPort());
		} catch (IOException exc) {}
	}
	public IO get() {
		if (peek()==null) {
			try {
				synchronized(this) {
					wait();
				}
			} catch (InterruptedException exc) {}
		}
		return poll();
	}
}