/*
 * $Id$
 */

package edu.jas.arith;

//import edu.jas.arith.BigInteger;

  /**
   * old test class
   * @deprecated
   */

public class testbi {

  static void tuwas() {
             
      BigInteger a;
      BigInteger b;
      BigInteger c;
      BigInteger d;
      BigInteger e;
      BigInteger r;
      int i;
      int x;
      int y;
      int q;
      int z;
      boolean verbose;


      System.out.println( "zero = " + BigInteger.ZERO );
      System.out.println( "one = " + BigInteger.ONE );

      a = new BigInteger( "68" );
      System.out.println( "68 = " + a );
      a = new BigInteger( "61111111111111111111111111111111111111111111" );
      System.out.println( "a = " + a );

      a = (BigInteger)a.random( 500 );
      System.out.println( "a = " + a );
      b = new BigInteger( "" + a );
      System.out.println( "b = " + b );
      System.out.println( "a-b = " + a.subtract(b) );


      long t = System.currentTimeMillis();
      verbose = true;
      i = 0;
      while ( i < 500 ) { // 500
        a = (BigInteger)a.random( (50 + i) );
        System.out.print( "." );
        if (verbose) {
          System.out.println( "i = " + i + ", a = " + a );
        }
        c = (BigInteger)a.add(a);
        if (verbose) {
          System.out.println( "c = " + c );
        }
        e = (BigInteger)c.subtract(a);
        if (verbose) {
          System.out.println( "e = " + e );
        }
        if ( a.compareTo(e) != 0 ) {
          System.out.println( "NOT EQUAL(a,e)" );
        }
        b = (BigInteger)a.multiply(a);
        if (verbose) {
          System.out.println( "b = " + b );
        }
        d = (BigInteger)b.divide(a);
        if (verbose) {
          System.out.println( "d = " + d );
        }
        if ( a.compareTo(d) != 0 ) {
          System.out.println( "NOT EQUAL(a,d)" );
        }
        i = (i + 1);
      }

      t = System.currentTimeMillis() -t;
      System.out.println("time = " + t);

  }



  public static void main( java.lang.String[] _args )
  {
    try
    {
      tuwas();
      if (_args != null)
      {
        java.lang.System.runFinalization();
        java.lang.System.exit(0);
      }
    }
    finally { }
  }
}
