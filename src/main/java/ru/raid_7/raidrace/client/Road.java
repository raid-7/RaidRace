package ru.raid_7.raidrace.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import ru.raid_7.raidrace.Position;

public class Road {
  public final Image image;

  public double carMaxSpeed = 16,
  							carMaxBackSpeed = 4,
  							carRotationSpeed = 0.765,
  							carAcceleration = 0.28,
  							carFriction = 0.09;
  public String title;
  
  protected double rotation = 0;
  protected Position pos = new Position(0, 0);
  protected int viewArea = 500;
  protected Color background = new Color(255, 255, 255);
  protected Position finish = new Position(0, 0);
  protected float finishR = 100;
  protected int bufferImageSizeD;

  protected ArrayList<Position> path = new ArrayList<Position>();
  protected double pathR = 100;
  
  public Road(int m) {
  	Image im = null;
  	try {
  		String s = "resources/maps/"+m+".";
			im = ImageIO.read(new File(s+"png"));
			load(s+"rrm");
			bufferImageSizeD = (int)Math.round(Math.sqrt(Math.pow(viewArea, 2)*2)/2);
		} catch (IOException e) {
			im = null;
			e.printStackTrace();
		}
  	image = im;
  }
  
  public void draw(Graphics2D g) {
  	int sx = pos.getX()-bufferImageSizeD;
  	int sy = pos.getY()-bufferImageSizeD;
  	int sx2 = pos.getX()+bufferImageSizeD;
  	int sy2 = pos.getY()+bufferImageSizeD;
  	Image buf = new BufferedImage(bufferImageSizeD*2, bufferImageSizeD*2, BufferedImage.TYPE_INT_ARGB);
  	Graphics2D bg = (Graphics2D)buf.getGraphics();
  	bg.rotate(rotation/180*Math.PI, bufferImageSizeD, bufferImageSizeD);
  	bg.drawImage(image, 0, 0, bufferImageSizeD*2, bufferImageSizeD*2, sx, sy, sx2, sy2, background, null);
  	int mx = bufferImageSizeD-viewArea/2;
  	int mx2 = mx+viewArea;
  	g.drawImage(buf, 0, 0, RaidRace.WIN_SIZE, RaidRace.WIN_SIZE, mx, mx, mx2, mx2, background, null);
  }

  public void rotate(double x) {  	
  	rotation += x;
  	if (rotation<0) rotation = 360+rotation;
  	if (rotation>=360) rotation -= 360;
  }
  public void go(double x) {
  	double dx = Math.sin(rotation/180*Math.PI)*x;
  	double dy = Math.cos(rotation/180*Math.PI)*x;
    Position last = new Position(pos.x, pos.y);
  	pos.x -= dx;
  	pos.y -= dy;

  	double checking = countCarDistance();
  	if (checking>pathR) {
  		RaidRace.instance.hitSide();
  		pos.x = last.x;
  		pos.y = last.y;
  	}
  }
  
  private void load(String f) throws IOException {
  	try (BufferedReader br = new BufferedReader(new FileReader(f))) {
  		String s;
  		boolean readPath = false;
  		while ((s = br.readLine())!=null) {
  			s = s.trim(); //spaces
  			if (s.length()==0 || s.charAt(0)=='#') continue; //comments
  			String old = s;
  			s = s.toLowerCase();
  			if (readPath) {
  				if (s.equals("end")) {
  					readPath = false;
  					continue;
  				}
  				String[] spl = s.split(" ");
  				int x = Integer.parseInt(spl[0]);
  				int y = Integer.parseInt(spl[1]);
  				path.add(new Position(x, y));
  			} else {
  				if (s.equals("path")) {
  					readPath = true;
  					continue;
  				}
  				if (s.indexOf("=")>0) {
  					String spl[] = s.split("=");
  					String name = spl[0];
  					String v = spl[1];
  					float value = 0;
  					try {
  						value = Float.parseFloat(v);
  					} catch (NumberFormatException exc) {}
 
  					if (name.equals("viewside")) viewArea = (int)value;
  					if (name.equals("startx")) pos.x = value;
  					if (name.equals("starty")) pos.y = value;
  					if (name.equals("startr")) rotation = value;
  					if (name.equals("road")) pathR = value;
  					if (name.equals("background")) {
  						spl = v.split(":");
  						background = new Color(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
  					}
  					if (name.equals("finishx")) finish.x = value;
  					if (name.equals("finishy")) finish.y = value;
  					if (name.equals("finishr")) finishR = value;

  					if (name.equals("carmaxspeed")) carMaxSpeed = value;
  					if (name.equals("carmaxbackspeed")) carMaxBackSpeed = value;
  					if (name.equals("caracceleration")) carAcceleration = value;
  					if (name.equals("carfriction")) carFriction = value;
  					if (name.equals("carrotationspeed")) carRotationSpeed = value;
  					if (name.equals("title")) title = old.split("=")[1];
  				}
  			}
  		}
  	}
  }

  private double countCarDistance() {
  	double r = Double.NaN, min = Double.POSITIVE_INFINITY;

  	for (int i=0; i<path.size()-1; i++) {
  		r = countCarDistance(i);
  		if (r<min) {
  			min = r;
  		}
  	}

  	return min;
  }

  private double countCarDistance(int i) {
  	Position p1 = path.get(i), p2 = path.get(i+1);
  	
  	double a = distance(pos, p1),
  				 b = distance(pos, p2),
  				 c = distance(p1, p2);
  	double p = (a+b+c)/2;
  	double s = Math.sqrt(p*(p-a)*(p-b)*(p-c));
  	double h = s*2/c;

  	double projection = Math.max(Math.sqrt(Math.pow(a, 2)-Math.pow(h, 2)), Math.sqrt(Math.pow(b, 2)-Math.pow(h, 2)));
  	
  	if (projection>c) {
  		h = Math.min(a,  b);
  	}
  	
  	return h;
  }
  private double distance(Position p1, Position p2) {
  	return Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2));
  }
}
