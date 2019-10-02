package ru.raid_7.raidrace.mapmaker;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ru.raid_7.raidrace.Position;

public class Maker extends JFrame {
	private static final long serialVersionUID = -8803232145832688366L;
	
	public String text = null;
	protected Image mapImage;
	protected TreeMap<String, String> info = new TreeMap<String, String>();
	protected ArrayList<Position> path = new ArrayList<Position>();
	
	protected File data, pict; 
	
	MapViewer view;
	TextViewer textView;
	DataViewer dataView;
	
	public Maker() {
		super("Raid Race Mapmaker");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		int num = 0;
		try {
			num = Integer.parseInt(JOptionPane.showInputDialog("Enter map number:"));
		} catch(NumberFormatException exc) {
			exc.printStackTrace();
		}
		
		File root = new File("resources/maps");
		data = new File(root, num+".rrm");
		pict = new File(root, num+".png");
		load();
		
		view = new MapViewer(mapImage, path);
		JPanel viewC = new JPanel();
		viewC.add(view);
		add(viewC, BorderLayout.CENTER);
		
		textView = new TextViewer(this);
		add(textView, BorderLayout.WEST);
		
		dataView = new DataViewer(info, path);
		add(dataView, BorderLayout.EAST);
		
		setVisible(true);
	}	
	
	private void load() {
		if (data.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(data))) {
				text = "";
				String s;
				boolean readPath = false;
				while ((s = br.readLine())!=null) {
					text += s+"\n";
					s = s.trim();
					if (s.length()==0 || s.charAt(0)=='#') continue;
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
						String spl[] = s.split("=");
						info.put(spl[0], spl[1]);
					}
				}
			} catch(IOException exc) {
				text = null;
				exc.printStackTrace();
			}
		}
    if (text==null) {
			text = "";
			try {
				data.createNewFile();
			} catch(IOException exc) {
				exc.printStackTrace();
				System.exit(1);
			}
		}
		try {
			mapImage = ImageIO.read(pict);
		} catch(IOException exc) {
			exc.printStackTrace();
			mapImage = new BufferedImage(2000, 2000, BufferedImage.TYPE_INT_ARGB);
			try {
				ImageIO.write((RenderedImage)mapImage, "PNG", pict);
			} catch(IOException exc2) {
				exc2.printStackTrace();
			}
		}
	}
	
	public void makeText() {
		text = "";
		List<String> keys = new ArrayList<String>(info.keySet());
	  Collections.reverse(keys);
		for (String k : keys) {
			text += k+'='+info.get(k)+"\n";
		}
		if (path.size()>0) {
			text += "path\n";
			for (Position p : path) {
				text += p.getX()+" "+p.getY()+"\n";
			}
			text += "end\n";
		}
	}
	
	public void save() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(data))) {
			bw.write(text);
		} catch(IOException exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Maker();
	}

}
