/*
 * $Id$
 */

package edu.jas.util;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
  * Klasse zur Ausgabe in Dateien.
  * @author hk, 8.12.2001.
 **/
public class Datei extends PrintWriter {

/**
 * Datei.
 * @param name A String.
 * @param append Boolean.
 * @throws IOException.
 */
public Datei(String name, boolean append) throws IOException { 
      super(new FileWriter(name,append));
  } 

/**
 * Datei
 * @param name A String.
 * @throws IOException
 */
public Datei(String name) throws IOException { 
      super(new FileWriter(name));
  } 

}
