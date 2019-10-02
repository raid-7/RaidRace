package ru.raid_7.raidrace.client;

import java.util.*;
import java.io.*;

public class Textures {
  private Map<String, File> t;
  private File root;
  private Textures(Map<String, String> mpt) {
    root = new File(mpt.get("root"));
    mpt.remove("root");
    Set<String> spt = mpt.keySet();
    t = new HashMap<String, File>();
    for (String k : spt) {
      t.put(k, new File(root, mpt.get(k)));
    }
  }
  public static Textures load(String file) {
    HashMap<String, String> ml = new HashMap<String, String>();
    ml.put("root", "pictures");
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String nstr;
      while ((nstr=br.readLine())!=null) {
        String s[] = nstr.split("=");
        ml.put(s[0], s[1]);
      }
    } catch(IOException exc) {
    	exc.printStackTrace();
    }
    return new Textures(ml);
  }
  public File getFileFor(String picname) {
    if (t.containsKey(picname)) return t.get(picname);
    else return null;
  }
}