/*
 * $Id$
 */

package edu.jas.arith;

//import edu.jas.arith.BigRational;

public class testbr
{

  static void tuwas()
  {
             
      BigRational a;
      BigRational b;
      BigRational c;
      BigRational d;
      BigRational e;
      BigRational r;
      int i;
      int x;
      int y;
      int q;
      int z;
      boolean verbose;


      System.out.println( "rnzero = " + BigRational.RNZERO );
      System.out.println( "rnone = " + BigRational.RNONE );

      a = new BigRational( "6/8" );
      System.out.println( "6/8 = " + a );
      a = new BigRational( "6/1111111111111111111111111111111111111111111" );
      System.out.println( "a = " + a );

      a = BigRational.RNRAND( 500 );
      System.out.println( "a = " + a );
      b = new BigRational( "" + a );
      System.out.println( "b = " + b );
      System.out.println( "a-b = " + BigRational.RNDIF(a,b) );

      System.out.print( "decimal = " );
      BigRational.RNDWR( a, 10 );
      System.out.println();


      long t = System.currentTimeMillis();
      verbose = true;
      i = 0;
      while ( i < 5 ) // 500
      {
        a = BigRational.RNRAND( (20 + i) );
        System.out.print( "." );
        if (verbose)
        {
          System.out.println( "i = " + i + ", a = " + a );
        }
        c = BigRational.RNSUM( a, a );
        if (verbose)
        {
          System.out.println( "c = " + c );
        }
        e = BigRational.RNDIF( c, a );
        if (verbose)
        {
          System.out.println( "e = " + e );
        }
        if ( BigRational.RNCOMP(a,e) != 0 )
        {
          System.out.println( "NOT EQUAL(a,e)" );
        }
        b = BigRational.RNPROD( a, a );
        if (verbose)
        {
          System.out.println( "b = " + b );
        }
        d = BigRational.RNQ( b, a );
        if (verbose)
        {
          System.out.println( "d = " + d );
        }
        if ( BigRational.RNCOMP(a,d) != 0 )
        {
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
