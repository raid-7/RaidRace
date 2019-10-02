package ru.raid_7.raidrace.client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Sprite {
  private HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();
  private static Textures texturepack = null;
  private Dimension size;
  private Point position = new Point(0, 0);
  private double rotation = 0,
  							 rotation2 = 0;
  private Point rotationPoint = new Point(0, 0);
  private Dimension imageSize;
  private BufferedImage currentImage;
  
  public Sprite(String ... picname) {
    for (int i=0; i<picname.length; i++) {
      try {
        images.put(picname[i], ImageIO.read(texturepack.getFileFor(picname[i])));
      } catch (IOException exc) {
      	exc.printStackTrace();
      }
    }
    currentImage = images.get(picname[0]);
    size = imageSize = new Dimension(currentImage.getWidth(), currentImage.getHeight());
  }
  public void setSize(int w, int h) {
    size = new Dimension(w==0 ? size.width : w, h==0 ? size.height : h);
  }
  public int width() {
    return size.width;
  }
  public int height() {
    return size.height;
  }
  public void x(int x) {
    position.x = x;
  }
  public void y(int y) {
    position.y = y;
  }
  public int x() {
    return position.x;
  }
  public int y() {
    return position.y;
  }
  public void moveX(int x) {
    position.x += x; 
  }
  public void moveY(int y) {
    position.y += y;
  }
  public void setRotation(double x) {
  	rotation = x;
  	if (rotation<0) rotation = 360+rotation;
  	if (rotation>360) rotation -= 360;
  }
  public void setRotation(double x, int a, int b) {
  	rotation2 = x;
  	rotationPoint.x = a;
  	rotationPoint.y = b;
  	if (rotation<0) rotation = 360+rotation;
  	if (rotation>360) rotation -= 360;
  }
  public void draw(Graphics gs) {
  	Graphics2D g = (Graphics2D)gs;
  	AffineTransform matrix = g.getTransform();
  	g.rotate(rotation2/180*Math.PI, rotationPoint.x, rotationPoint.y);
  	g.rotate(rotation/180*Math.PI, position.x+size.width/2, position.y+size.height/2);
    g.drawImage(currentImage, position.x, position.y, position.x+size.width, position.y+size.height, 0, 0, imageSize.width, imageSize.height, null);
    g.setTransform(matrix);
  }
  public void selectImage(String s) {
    currentImage = images.get(s);
  }
  public boolean touchs(Sprite s) {
    if (x()+width()<s.x() || y()+height()<s.y() ||
        s.x()+s.width()<x() || s.y()+s.height()<y()) {
      return false;
    }
    return true;
  }
  public static void setTextures(Textures d) {
    if (texturepack==null) texturepack = d;
  }
}