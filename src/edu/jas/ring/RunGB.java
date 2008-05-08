/*
 * $Id$
 */

package edu.jas.ring;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.util.ExecutableServer;

  /**
   * Simple setup to run a GB example. <br />
   * Usage: RunGB [seq|par|dist|cli] &lt;file&gt; #procs [machinefile]
   * @author Heinz Kredel
   */

public class RunGB {

    /**
     * main method to be called from commandline <br />
     * Usage: RunGB [seq|par|dist|cli] &lt;file&gt; #procs [machinefile]
     */

  public static void main( java.lang.String[] args ) {

      BasicConfigurator.configure();

      String usage = "Usage: RunGB "
                    + "[ seq | par | dist | dist1 | cli [port] ] "
                    + "<file> "
                    + "#procs "
                    + "[machinefile]";
      if ( args.length < 1 ) {
          System.out.println(usage);
          return;
      }

      String kind = args[0];
      String[] allkinds = new String[] { "seq", "par", "dist", "dist1", "cli" };
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

      boolean once = false;
      final int GB_SERVER_PORT = 7114; 
      //inal int EX_CLIENT_PORT = GB_SERVER_PORT + 1000; 
      int port = GB_SERVER_PORT;

      if ( kind.equals("cli") ) {
          if ( args.length >=2 ) {
             try {
                 port = Integer.parseInt( args[1] );
             } catch (NumberFormatException e) { 
                 e.printStackTrace();
                 System.out.println(usage);
                 return;
             }
          }
          runClient( port );
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
      if ( kind.equals("par") || kind.startsWith("dist") ) {
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
      if ( kind.startsWith("dist") ) {
          if ( args.length >= 4 ) {
              mfile = args[3];
          } else {
              mfile = "machines";
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

      GenPolynomialTokenizer tok = new GenPolynomialTokenizer(problem);
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
      if ( kind.equals("dist1") ) {
          runMasterOnce( S, threads, mfile, port );
      }

  }


  @SuppressWarnings("unchecked")
  static void runMaster(PolynomialList S, int threads, String mfile, int port) {
      List L = S.list; 
      List G = null;
      long t, t1;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base distributed ..."); 
      GBDist gbd = new GBDist(threads, mfile, port);
      t1 = System.currentTimeMillis();
      G = gbd.execute( L );
      t1 = System.currentTimeMillis() - t1;
      gbd.terminate(false);
      S = new PolynomialList( S.ring, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t1 + " milliseconds, " 
                                   + (t-t1) + "start-up"); 
      System.out.println(""); 
  }


  @SuppressWarnings("unchecked")
  static void runMasterOnce(PolynomialList S, int threads, String mfile, int port) {
      List L = S.list; 
      List G = null;
      long t, t1;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base distributed ..."); 
      GBDist gbd = new GBDist(threads, mfile, port);
      t1 = System.currentTimeMillis();
      G = gbd.execute( L );
      t1 = System.currentTimeMillis() - t1;
      gbd.terminate(true);
      S = new PolynomialList( S.ring, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t1 + " milliseconds, " 
                                   + (t-t1) + "start-up"); 
      System.out.println(""); 
  }


  static void runClient(int port) {
      System.out.println("\nGroebner base distributed client ..."); 

      ExecutableServer es = new ExecutableServer( port );
      es.init();
  }


  @SuppressWarnings("unchecked")
  static void runParallel(PolynomialList S, int threads) {
      List L = S.list; 
      List G;
      long t;
      GroebnerBaseParallel bb = new GroebnerBaseParallel(threads);

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base parallel ..."); 
      G = bb.GB(L);
      S = new PolynomialList( S.ring, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
      bb.terminate();
  }


  @SuppressWarnings("unchecked")
  static void runSequential(PolynomialList S) {
      List L = S.list; 
      List G;
      long t;
      GroebnerBase bb = new GroebnerBaseSeq();

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base sequential ..."); 
      G = bb.GB(L);
      S = new PolynomialList( S.ring, G );
      System.out.println("G =\n" + S ); 
      System.out.println("G.size() = " + G.size() ); 
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

}
