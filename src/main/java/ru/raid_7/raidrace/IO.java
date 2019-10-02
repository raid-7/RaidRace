package ru.raid_7.raidrace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class IO {
  public static final int VERSION = 0;

	private boolean released = false;
	private BufferedReader reader;
	private PrintWriter writer;
	private Socket s = null;
	
	public IO(Socket s) throws IOException {
		this(s.getInputStream(), s.getOutputStream());
		this.s = s;
	}
	public IO(InputStream is, OutputStream os) {
		reader = new BufferedReader(new InputStreamReader(is));
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
		released = true;
	}
	
	public void close() {
		try {
			if (s==null) {
				reader.close();
				writer.close();
			} else {
				s.close();
			}
		} catch (IOException exc)  {
			exc.printStackTrace();
		}
	}
	
	public void write(String str) {
		if (!isReleased()) return;
		writer.println(str);
		writer.flush();
	}
	public String read() {
		if (!isReleased()) return null;
		try {
			return reader.readLine();
		} catch (IOException exc) {
			return null;
		}
	}
	public <T>void write(T m) {
		write(m.toString());
	}
	public PrintWriter getWriter() {
		return writer;
	}
	public BufferedReader getReader() {
		return reader;
	}
	public boolean isReleased() {
		boolean b = true;
		if (s!=null) b = !s.isClosed();
		return released && b;
	}
}
