package ru.raid_7.raidrace.mapmaker;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ru.raid_7.raidrace.Position;

@SuppressWarnings("serial")
public class DataViewer extends JPanel implements ActionListener {
	public static final String[] FIELDS = {
		"title",
		"carMaxSpeed",
		"carMaxBackSpeed",
		"carAcceleration",
		"carFriction",
		"carRotationSpeed",
		"viewSide",
		"background",
		"startX",
		"startY",
		"startR",
		"finishX",
		"finishY",
		"finishR",
		"road"
	};
	
	protected ArrayList<Position> path;
	protected Map<String, String> data;
	
	protected HashMap<JTextField, String> fields = new HashMap<JTextField, String>();
	
	public DataViewer(Map<String, String> dt, ArrayList<Position> pth) {
		super();
		path = pth;
		data = dt;
		JPanel forFields = new JPanel(new GridLayout(FIELDS.length+1, 2));
		for (String s : FIELDS) {
			JLabel ttl = new JLabel(s+"  ");
			String snm = s.toLowerCase(); 
			String v = "";
			if (data.containsKey(snm)) v = data.get(snm);
			JTextField fld = new JTextField(v);
			fields.put(fld, snm);
			forFields.add(ttl);
			forFields.add(fld);
		}
		JButton makeText = new JButton("Make data");
		makeText.addActionListener(this);
		forFields.add(makeText);
		add(forFields, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent ev) {
		Set<JTextField> ks = fields.keySet();
		for (JTextField f : ks) {
			String t = f.getText();
			t = t.trim();
			if (t.length()==0) {
				if (data.containsKey(fields.get(f))) {
					data.remove(fields.get(f));
				}
			} else {
				data.put(fields.get(f), t);
			}
		}
	}

}
