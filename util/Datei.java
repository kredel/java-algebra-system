/*
 * $Id$
 */

package edu.jas.util;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
  * Klasse zur Ausgabe in Dateien
  * @author hk, 8.12.2001
 **/
public class Datei extends PrintWriter {

  public Datei(String name, boolean append) throws IOException { 
      super(new FileWriter(name,append));
  } 

  public Datei(String name) throws IOException { 
      super(new FileWriter(name));
  } 

}
