package ru.raid_7.raidrace;

public class Position {	
  public double x, y;
	
	public Position(double tx, double ty) {
		x = tx;
		y = ty;
	}

	public int getX() {
		return (int)Math.round(x);
	}
	public int getY() {
		return (int)Math.round(y);
	}

}
