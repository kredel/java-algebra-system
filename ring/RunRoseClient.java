/*
 * $Id$
 */

package edu.jas.ring;

import java.io.IOException;

  /**
   * simple setup to run the client in the distributed Rose example.
   */

public class RunRoseClient {


  public static void main( java.lang.String[] args ) {
      final String SERVER = "localhost";
      String host = SERVER;
      if ( args.length > 0 ) {
	      host = args[0];
      }
      final int SPORT = 4711;
      int port = SPORT;
      if ( args.length > 1 ) {
	  try {
	      port = Integer.parseInt( args[1] );
	  } catch (NumberFormatException e) { }
      }

      runClient( host, port );
  }


  static void runClient(String host, int port) {
      long t;

      t = System.currentTimeMillis();
      System.out.println("\nGroebner base distributed server @ " 
                                     + host + ":" +port); 
      System.out.println("Groebner base distributed client ..."); 
      try {
          GroebnerBaseDistributed.DIRPGBClient(host,port);
      } catch (IOException e) {
      }
      t = System.currentTimeMillis() - t;
      System.out.println("time = " + t + " milliseconds" ); 
      System.out.println(""); 
  }

}
