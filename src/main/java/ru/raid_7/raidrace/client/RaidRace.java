package ru.raid_7.raidrace.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ru.raid_7.raidrace.IO;
import ru.raid_7.raidrace.Sound;

public class RaidRace extends JPanel {
	private static final long serialVersionUID = -6966867850706113083L;

	public static final RaidRace instance = new RaidRace();
  public static final Textures textures = Textures.load("resources/textures.cfg");
  
  public static final String TITLE = "Raid Race";
  public static final int WIN_SIZE = 700;
  public static final int CENTER_X = WIN_SIZE/2;
  public static final int CENTER_Y = WIN_SIZE/2;
  public static final int CAR_WIDTH = 50;
  public static final int CAR_HEIGHT = 100;
  public static final int START_NO2 = 360;
  public static final Rectangle NO2_RECTANGLE = new Rectangle(WIN_SIZE-26-10, WIN_SIZE/2-30, 26, WIN_SIZE/2);

  protected double CAR_MAX_SPEED;
  protected double CAR_MAX_BACK_SPEED;
  protected double CAR_ACCELERATION;
  protected double CAR_ROTATION_SPEED;
  protected double CAR_FRICTION;
  protected double scaleM;

  protected JFrame fr;
  protected Road road;
  protected Sprite car;
  protected Sprite[] enemies;
  protected double speed = 0;
  protected KeyManager keys;
  protected Sound.SoundSystem sounds;
  protected IO com;
  protected boolean running = false;
  protected boolean blocked = false;
  protected int NO2 = START_NO2;

	public RaidRace() {
		super(true);
		this.setSize(WIN_SIZE, WIN_SIZE);
	}
	public void start(IO communic, int map, int pls) {
		com = communic;

		fr = new JFrame(TITLE);
		fr.add(this);
		fr.setSize(WIN_SIZE, WIN_SIZE);
		fr.setResizable(false);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		keys = new KeyManager(fr);

		road = new Road(map);
		car = new Sprite("car");
		car.setSize(CAR_WIDTH, CAR_HEIGHT);
		car.x(CENTER_X-CAR_WIDTH/2);
		car.y(CENTER_Y-CAR_HEIGHT/2);
		CAR_MAX_SPEED = road.carMaxSpeed;
		CAR_ACCELERATION = road.carAcceleration;
		CAR_MAX_BACK_SPEED = road.carMaxBackSpeed;
		CAR_FRICTION = road.carFriction;
		CAR_ROTATION_SPEED = road.carRotationSpeed;
		scaleM = (double)WIN_SIZE/road.viewArea;
		
		fr.setTitle(TITLE+" - "+road.title);
	
	  enemies = new Sprite[pls-1];
	  for (int i=0; i<pls-1; i++) {
	  	enemies[i] = new Sprite("enemy");
	  	enemies[i].setSize(CAR_WIDTH, CAR_HEIGHT);
	  }
		
		sounds = Sound.loadSystem(new File("resources/sounds"));

		fr.setVisible(true);

		running = true;
		sounds.play("shot");

		com.write(road.pos.x+"|"+road.pos.y+"|"+road.rotation);
		while (running) {
			runner();
		}
	}
	
	public void runner() {
		double[][] data = recieve();
		updEnemies(data);
		upd();
		com.write(road.pos.x+"|"+road.pos.y+"|"+road.rotation);
		repaint();
	}
	private void upd() {
		if (blocked) {
			if (Math.abs(speed)==0) blocked = false;
		} 
		if (!blocked) {
			if (keys.is(KeyEvent.VK_SPACE)) {
				if (speed==0) return;
				sounds.play("shhh");
				if (speed>0) {
					speed -= CAR_ACCELERATION;
					if (speed<0) speed = 0;
				}
				if (speed<0) {
					speed += CAR_ACCELERATION;
					if (speed>0) speed = 0;
				}
			} else {
				if (keys.is(KeyEvent.VK_W) || keys.is(KeyEvent.VK_UP)) {
					double accNP = 0, maxNP = 0;
					int no2dec = 0;
					if (NO2>0 && (keys.is(KeyEvent.VK_SHIFT) || keys.is(KeyEvent.VK_CONTROL))) {
						accNP = CAR_ACCELERATION*0.6;
						maxNP = CAR_MAX_SPEED*0.1;
						no2dec = 1;
					}
					if (speed>=0) {
						sounds.play("motor");
						speed += CAR_ACCELERATION+accNP;
						NO2-=no2dec;
					} else {
						sounds.play("shhh");
						speed += CAR_ACCELERATION;
					}
					if (speed>CAR_MAX_SPEED+maxNP) speed = CAR_MAX_SPEED+maxNP;
				} else if (keys.is(KeyEvent.VK_S) || keys.is(KeyEvent.VK_DOWN)) {
					if (speed<=0) sounds.play("motor");
					else sounds.play("shhh");
					speed -= CAR_ACCELERATION;
					if (speed<-CAR_MAX_BACK_SPEED) speed = -CAR_MAX_BACK_SPEED;
				}
			}
		}
		if (speed>0) {
			speed -= CAR_FRICTION;
			if (speed<0) speed = 0;
		} else if (speed<0) {
			speed += CAR_FRICTION;
			if (speed>0) speed = 0;
		}

		if (!blocked) {
			double spa = Math.abs(speed);
			short spm = (short)(speed/spa);
			if (spa==0) spm = 0;
			if (keys.is(KeyEvent.VK_A) || keys.is(KeyEvent.VK_LEFT)) {
				road.rotate(CAR_ROTATION_SPEED*Math.sqrt(spa)*spm);
			} else if (keys.is(KeyEvent.VK_D) || keys.is(KeyEvent.VK_RIGHT)) {
				road.rotate(-CAR_ROTATION_SPEED*Math.sqrt(spa)*spm);
			}
		}
		
		road.go(speed);
	}

	private void updEnemies(double[][] dt) {
		int i = 0;
		for (Sprite sp : enemies) {
			sp.x(car.x()+(int)Math.round((dt[i][0]-road.pos.x)*scaleM));
			sp.y(car.y()+(int)Math.round((dt[i][1]-road.pos.y)*scaleM));
			sp.setRotation(road.rotation, CENTER_X, CENTER_Y);
			sp.setRotation(-dt[i][2]);
			i++;
		}
	}
	
	private double[][] recieve() {
		String cmd = com.read();
		if (cmd==null) {
			System.exit(0);
		}
		if (cmd.length()>3 && cmd.substring(0, 3).equals("end")) {
			int n = Integer.parseInt(cmd.substring(4));
			stop(n);
		}
		String[] strs = cmd.split(",");
		double[][] data = new double[strs.length][];
		for (int i=0; i<strs.length; i++) {
			if (strs[i].isEmpty()) continue;
			String[] one = strs[i].split("\\|");
			data[i] = new double[3];
			for (int j=0; j<3; j++) {
				data[i][j] = Double.parseDouble(one[j]);
			}
		}
		return data;
	}
	private void stop(int n) {
		String strend = n==13 ? "ым" : String.valueOf(n).endsWith("3") ? "им" : "ым";
		com.close();
		JOptionPane.showMessageDialog(fr, "Вы финишировали "+n+"-"+strend);
		fr.setVisible(false);
		System.exit(0);
	}
	
	public void hitSide() {
		sounds.play("hit");
		double t = Math.sqrt(Math.sqrt(Math.pow(Math.abs(-speed), 3)))/1.8;
		if (speed>0) {
			speed = -t;
		} else {
			speed = t;
		}
		blocked = true;
	}
	
	@Override
	protected void paintComponent(Graphics gSim) {
		super.paintComponent(gSim);
		if (running) {
			Graphics2D g = (Graphics2D)gSim;
	    road.draw(g);
	    for (Sprite sp : enemies) {
	    	sp.draw(g);
	    }
	    car.draw(g);
	    
	    String spd = toFreq(speed, 2)+" px/frame";
	    g.setFont(new Font("Arial", Font.BOLD, 18));
	    FontMetrics fm = g.getFontMetrics();
	    int heit = fm.getHeight();
	    int widt = fm.stringWidth(spd);
	    g.drawString(spd, WIN_SIZE-widt-30-NO2_RECTANGLE.width, WIN_SIZE-heit-20);
	    
	    paintNO2(g);
		}
	}
	
	private void paintNO2(Graphics2D g) {
		int f = Math.round((float)NO2_RECTANGLE.height/START_NO2*NO2);
		int y = NO2_RECTANGLE.y+(NO2_RECTANGLE.height-f);

		g.setColor(Color.BLUE);
		g.fillRoundRect(NO2_RECTANGLE.x, y, NO2_RECTANGLE.width, f, NO2_RECTANGLE.width/2, NO2_RECTANGLE.width/2);
		g.setColor(Color.BLACK);
		g.drawRoundRect(NO2_RECTANGLE.x, NO2_RECTANGLE.y, NO2_RECTANGLE.width, NO2_RECTANGLE.height, NO2_RECTANGLE.width/2, NO2_RECTANGLE.width/2);
	}

	public static void main(String[] args) {
		Sprite.setTextures(textures);
		String h = args.length>0 ? args[0] : "127.0.0.1";
		int p = args.length>1 ? Integer.parseInt(args[1]) : 25565;
		IO tmp = null;
		try {
			tmp = new IO(new Socket(h, p));
			tmp.write(IO.VERSION);
			if (!tmp.isReleased()) throw new IOException("Versions do not match");
		} catch (IOException exc) {
			exc.printStackTrace();
			JOptionPane.showMessageDialog(null, "Connection error: "+exc);
			System.exit(1);
		}
		String[] start = tmp.read().split(":");
		int map = Integer.parseInt(start[1]);
		int players = Integer.parseInt(start[2]);
		instance.start(tmp, map, players);
	}

	public static void sleep(long x) {
		try {
			Thread.sleep(x);
		} catch(InterruptedException exc) {}
	}
  public static String toFreq(double x, int k) {
  	String s = String.valueOf(x);
  	int i = s.indexOf('.');
  	if (i==-1) {
  		StringBuffer sb = new StringBuffer();
  		for (int j=0; j<k; j++) sb.append('0');
  		return s+"."+sb;
  	}
  	int d = k-s.length()+i;
  	if (d==0) return s;
  	if (d>0) {
  		StringBuffer sb = new StringBuffer();
  		for (int j=0; j<d; j++) sb.append('0');
  		return s+sb;
  	}
  	return s.substring(0, i+k);
  }
}
