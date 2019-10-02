package ru.raid_7.raidrace.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import ru.raid_7.raidrace.IO;
import ru.raid_7.raidrace.Position;

public class Main {
	public static final int FRAME_DELAY = 30;
	
  public static Main instance;

  public final int map, pk, port;
 
  protected ConnectionQueue queue;
  protected boolean plays = false;
  protected ServerSocket ss = null;
  protected Set<IO> socks = new HashSet<IO>();
  protected Set<IO> disconnected = new HashSet<IO>();
  protected String[] players;
  
  protected Position endPosition = new Position(0, 0);
  protected double endRadius = 100;
  protected int end = 0;
	
	public Main(int prt, int ppk, int mp) {
		map = mp;
		pk = ppk;
		port = prt;
		try {
			ss = new ServerSocket(port);
		} catch (IOException exc) {
			exc.printStackTrace();
			System.exit(0);
		}
		queue = new ConnectionQueue(ss);
		queue.start();
		loadInfo();
		accept();
		start();
	}

	private void accept() {
		System.out.println("Begin accepting...");
		while (socks.size()<pk) {
			IO n = queue.get();
			int k = -1;
			try {
				k = Integer.parseInt(n.read());
				if (k!=IO.VERSION) k = -1;
			} catch (NumberFormatException exc) {
				exc.printStackTrace();
				k = -1;
			}
			if (k==-1) {
				n.close();
				continue;
			}
			socks.add(n);
		}
		System.out.println("All accepted. Server closed.");
	}
	private void start() {
		long delay = 0;
		plays = true;
		players = new String[pk];
		for (IO p : socks) {
			p.write("start:"+map+':'+pk);
		}
		while (plays) {
			sleep(delay);
			long tm = System.currentTimeMillis();
			HashMap<IO, String> map = new HashMap<IO, String>();
			for (IO p : socks) {
				String s = p.read();
				if (s==null || disconnected.contains(p)) {
					map.put(p, endPosition.x+"|"+endPosition.y+"|0");
					if (!disconnected.contains(p)) disconnected.add(p);
					if (disconnected.size()>=pk) {
						stop();
						break;
					}
					continue;
				}
				{
					String[] pl = s.split("\\|");
					Position pos = new Position(Double.parseDouble(pl[0]), Double.parseDouble(pl[1]));
					double r = Math.sqrt(Math.pow(pos.x-endPosition.x, 2)+Math.pow(pos.y-endPosition.y, 2));
					if (r<endRadius) {
						end++;
						disconnected.add(p);
						p.write("end:"+end);
					}
					if (disconnected.size()>=pk) {
						stop();
						break;
					}
				}
				map.put(p, s);
			}
			for (IO p : socks) {
				StringBuffer sb = new StringBuffer();
				for (Entry<IO, String> e : map.entrySet()) {
					if (e.getKey()==p) continue;
					sb.append(','+e.getValue());
				}
				p.write(sb.toString().substring(1));
			}
			delay = FRAME_DELAY-System.currentTimeMillis()+tm;
			if (delay<0) delay = 0;
		}
	}
	private void stop() {
		plays = false;
		end = 0;
		for (IO s : socks) {
			s.close();
		}
		socks = new HashSet<IO>();
	  disconnected = new HashSet<IO>();
		accept();
		start();
	}
	
	private void loadInfo() {
		try (BufferedReader r = new BufferedReader(new FileReader("resources/maps/"+map+".rrm"))) {
			int t = 0;
			String s;
			while ((s = r.readLine())!=null && t<3) {
				s = s.trim();
				s = s.toLowerCase();
				if (s.charAt(0)=='#') continue;
				if (s.indexOf('=')<1) continue;
				String[] d = s.split("=");
				double x = 0;
				try {
					x = Double.parseDouble(d[1]);
				} catch(NumberFormatException exc) {
					continue;
				}
				if (d[0].equals("finishx")) endPosition.x = x;
				if (d[0].equals("finishy")) endPosition.y = x;
				if (d[0].equals("finishr")) endRadius = x;
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int p = args.length==0 ? 25565 : Integer.valueOf(args[0]); //port
		int k = args.length>=2 ? Integer.valueOf(args[1]) : 2; //players
		int m = args.length>=3 ? Integer.valueOf(args[2]) : 0; //map
		instance = new Main(p, k, m);
	}
	public static void sleep(long x) {
		try {
			Thread.sleep(x);
		} catch(InterruptedException exc) {}
	}
}
