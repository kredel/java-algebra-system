/*
 * $Id$
 */

package edu.jas;

/**
  * Klasse zur Ausgabe in Dateien
  * hk, 8.12.2001
 **/

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Datei extends PrintWriter {

  public Datei(String name, boolean append) throws IOException { 
      super(new FileWriter(name,append));
  } 

  public Datei(String name) throws IOException { 
      super(new FileWriter(name));
  } 

}
