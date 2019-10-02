package ru.raid_7.raidrace.mapmaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import ru.raid_7.raidrace.Position;

public class MapViewer extends JPanel {
	private static final long serialVersionUID = -775846020220106232L;
	private static final int MAX_SIDE = 710;
	private static final int BORDER_SIZE = 2;
	private static final int POINT_HALF = 3;
	
	private final MouseMotionListener listenerForCoords = new MouseMotionAdapter() {
		public void mouseMoved(MouseEvent ev) {
			mX = ev.getX()-2;
			mY = ev.getY()-2;
			if (mX<0) mX = 0;
			if (mY<0) mY = 0;
			if (mX>width) mX = width;
			if (mY>height) mY = height;
			realMapPosition = scale(new Point(mX, mY));
			repaint();
		}
	};
	private final MouseListener clickListener = new MouseAdapter() {
		public void mouseClicked(MouseEvent ev) {
			if (ev.getButton()==1) {
				//left
				listenerForCoords.mouseMoved(ev);
				path.add(realMapPosition);
			} else if (ev.getButton()==3)  {
				//right
				listenerForCoords.mouseMoved(ev);
				int h = POINT_HALF;
				for (Position p : path) {
					Point po = unscale(p);
					if (mX<po.x-h || mX>po.x+h || mY<po.y-h || mY>po.y+h) continue;
					path.remove(p);
					break;
				}
			}
		}
	};
	
	protected Image pic;
	protected ArrayList<Position> path;
	protected int width, height;
	protected int mX = 0, mY = 0;
	protected Position realMapPosition = new Position(0, 0);
	protected double kh, kv;
	
	public MapViewer(Image img, ArrayList<Position> pth) {
		super();
		path = pth;
		pic = img;
		
		double dw = pic.getWidth(null);
		double dh = pic.getHeight(null);
		
		double k = dw/dh;
    if (k>=1) {
    	width = MAX_SIDE;
    	height = (int)Math.round(MAX_SIDE/k);
    } else {
    	width = (int)Math.round(MAX_SIDE*k);
    	height = MAX_SIDE;
    }
    
		kh = dw/((double)width);
		kv = dh/((double)height);
		
    setBorder(BorderFactory.createLineBorder(new Color(0, 0, 255), BORDER_SIZE));
    addMouseMotionListener(listenerForCoords);
    addMouseListener(clickListener);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(pic, BORDER_SIZE, BORDER_SIZE, width, height, null);
		
		//Points
		Point last = null;
		for (Position p : path) {
			Point po = unscale(p);
			po.x += BORDER_SIZE;
			po.y += BORDER_SIZE;
			g.fillRect(po.x-POINT_HALF, po.y-POINT_HALF, POINT_HALF*2, POINT_HALF*2);
			if (last!=null)	{
				g.drawLine(last.x, last.y, po.x, po.y);
			}
			last = po;
		}

		g.setXORMode(Color.WHITE);
		g.drawString(mX+":"+mY, 20, 20);
		g.drawString(realMapPosition.getX()+":"+realMapPosition.getY(), 20, 40);
	}
	
	private Point unscale(Position p) {
		return new Point((int)Math.round(p.x/kh), (int)Math.round(p.y/kv));
	}
	private Position scale(Point p) {
		return new Position(((double)p.x)*kh, ((double)p.y)*kv);
	}

	@Override
	public Dimension getPreferredSize() {
		int x = BORDER_SIZE*2;
		return new Dimension(width+x, height+x);
	}
}
