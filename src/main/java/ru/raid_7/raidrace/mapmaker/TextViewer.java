package ru.raid_7.raidrace.mapmaker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextViewer extends JPanel implements ActionListener {
	private static final long serialVersionUID = -1019191837705672209L;

	private Maker m;
	private JButton sB, uB;
	private JTextArea txt;
	
	public TextViewer(Maker mm) {
		super(new BorderLayout(15, 15));
		m = mm;
		setSize(200, 700);

		sB = new JButton("Save");
		uB = new JButton("Update");
		sB.setActionCommand("save");
		uB.setActionCommand("update");
		sB.addActionListener(this);
		uB.addActionListener(this);

		txt = new JTextArea();
	
		JPanel btns = new JPanel();
		btns.add(uB);
		btns.add(sB);
		JScrollPane txtC = new JScrollPane(txt);

		add(txtC, BorderLayout.CENTER);
		add(btns, BorderLayout.SOUTH);
		updCl();
	}
	
	public void actionPerformed(ActionEvent ev) {
		String c = ev.getActionCommand();
		if (c.equals("save")) {
			svCl();
		} else if (c.equals("update")) {
			updCl();
		}
	}

	private void updCl() {
		m.makeText();
		txt.setText(m.text);
	}
	private void svCl() {
		m.text = txt.getText();
		m.save();
	}
}
