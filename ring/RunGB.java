/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
//import java.util.List;
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
   * Simple setup to run a GB example. <br />
   * Usage: RunGB [seq|par|dist|cli] &lt;file&gt; #procs [maschinefile]
   * @author Heinz Kredel
   */

public class RunGB {

    /**
     * main method to be called from commandline <br />
     * Usage: RunGB [seq|par|dist|cli] &lt;file&gt; #procs [maschinefile]
     */

  public static void main( java.lang.String[] args ) {

      BasicConfigurator.configure();

      String usage = "Usage: RunGB "
                    + "[seq|par|dist|cli] "
                    + "<file> "
                    + "#procs "
                    + "[machinefile]";
      if ( args.length < 1 ) {
          System.out.println(usage);
          return;
      }

      String kind = args[0];
      String[] allkinds = new String[] { "seq", "par", "dist", "cli"  };
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

      final int GB_SERVER_PORT = 7114; 
      final int EX_CLIENT_PORT = GB_SERVER_PORT + 1000; 
      int port = GB_SERVER_PORT;

      if ( kind.equals("cli") ) {
          runClient( EX_CLIENT_PORT );
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

      int threads = 0;
      if ( kind.equals("par") || kind.equals("dist") ) {
          if ( args.length < 3 ) {
            System.out.println(usage);
            return;
          }
          try {
              threads = Integer.parseInt( args[2] );
          } catch (NumberFormatException e) { 
              e.printStackTrace();
              System.out.println(usage);
              return;
          }
      }

      String mfile = null;
      if ( kind.equals("dist") ) {
          if ( args.length >= 4 ) {
              mfile = args[3];
          } else {
	      mfile = "maschines";
	  }
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
          S = tok.nextPolynomialSet();
      } catch (IOException e) { 
          e.printStackTrace(); 
          return;
      }
      System.out.println("S =\n" + S); 

      if ( kind.equals("seq") ) {
         runSequential( S );
      }
      if ( kind.equals("par") ) {
         runParallel( S, threads );
      }
      if ( kind.equals("dist") ) {
          runMaster( S, threads, mfile, port );
      }

  }


  static void runMaster(PolynomialList S, int threads, String mfile, int port) {
      ArrayList L = S.list; 
      ArrayList G = null;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base distributed ..."); 

      G = (new GBDist()).execute( L, threads, mfile, port );

      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }


  static void runClient(int port) {
      System.out.println("\nGroebner base distributed client ..."); 

      ExecutableServer es = new ExecutableServer( port );
      es.init();
  }


  static void runParallel(PolynomialList S, int threads) {
      ArrayList L = S.list; 
      ArrayList G;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base parallel ..."); 
      G = GroebnerBaseParallel.DIRPGB(L,threads);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }


  static void runSequential(PolynomialList S) {
      ArrayList L = S.list; 
      ArrayList G;
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base ..."); 
      G = GroebnerBase.DIRPGB(L);
      S = new PolynomialList( S.vars, S.tord, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

}
