/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
//import java.util.Iterator;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.OrderedPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.util.ExecutableServer;

  /**
   * Simple setup to run a solvable GB example. <br />
   * Usage: RunSGB [seq] &lt;file&gt; 
   * @author Heinz Kredel
   */

// Usage: RunSGB [seq|par|dist|cli] &lt;file&gt; #procs [machinefile]

public class RunSGB {

    /**
     * main method to be called from commandline <br />
     * Usage: RunSGB [seq|par|dist|cli] &lt;file&gt; #procs [machinefile]
     */

  public static void main( java.lang.String[] args ) {

      BasicConfigurator.configure();

      String usage = "Usage: RunSGB "
                    + "[ seq ] "
          //        + "[ seq | par | dist | cli [port] ] "
                    + "<file> ";
          //          + "#procs "
          //          + "[machinefile]";
      if ( args.length < 1 ) {
          System.out.println(usage);
          return;
      }

      String kind = args[0];
      String[] allkinds = new String[] { "seq" };
      // String[] allkinds = new String[] { "seq", "par", "dist", "cli"  };
      boolean sup = false;
      for ( int i = 0; i < allkinds.length; i++ ) {
          if ( kind.equals( allkinds[i] ) ) {
              sup = true;
          }
      }
      if ( ! sup ) {
          System.out.println(usage);
          return;
      }

      String filename = null;
      if ( ! kind.equals("cli") ) {
          if ( args.length < 2 ) {
             System.out.println(usage);
             return;
          }
          filename = args[1];
      }

      Reader problem = null;
      try { 
           problem = new FileReader( filename );
      } catch (FileNotFoundException e) {
           e.printStackTrace();
           System.out.println(usage);
           return;
      }

      OrderedPolynomialTokenizer tok = new OrderedPolynomialTokenizer(problem);
      PolynomialList S = null; 
      try {
          S = tok.nextSolvablePolynomialSet();
      } catch (IOException e) { 
          e.printStackTrace(); 
          return;
      }
      System.out.println("S =\n" + S); 

      if ( kind.equals("seq") ) {
         runSequential( S );
      }

  }


  static void runSequential(PolynomialList S) {
      List L = S.list; 
      List G;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nSolvable Groebner base sequential ..."); 
      // G = SolvableGroebnerBase.leftGB(L);
      G = SolvableGroebnerBase.twosidedGB(L);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

}
