package ru.raid_7.raidrace.client;

import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.Arrays;

public class KeyManager implements KeyListener, Runnable {
  private boolean[] keys;
  private JFrame frame;
  
  public KeyManager(JFrame fr) {
    frame = fr;
    SwingUtilities.invokeLater(this);
  }
  
  public void keyPressed(KeyEvent ev) {
    int code = ev.getKeyCode();
    keys[code] = true;
  }
  public void keyReleased(KeyEvent ev) {
    int code = ev.getKeyCode();
    keys[code] = false;
  }
  public void keyTyped(KeyEvent ev) {}
  public void run() {
    keys = new boolean[65536];
    Arrays.fill(keys, false);
    frame.addKeyListener(this);
  }

  
  public boolean is(int c) {
    return keys[c];
  }
  public void release(int c) {
    keys[c] = false;
  }

}