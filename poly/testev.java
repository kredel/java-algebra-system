/*
 * $Id$
 */

package edu.jas.poly;

/**
 * @deprecated
 */

public class testev
{

  static void tuwas()
  {

      ExpVector U;
      ExpVector V;
      ExpVector W;
      int s;
      boolean t;
      float q = (float) 0.2;
      String[] vars = { "x", "y", "z", "u", "v", "w" };

      U = ExpVector.EVRAND(5,10,q);
      System.out.println("U = " + U );
      System.out.println("U = " + U.toString(vars) );
      System.out.println("U = " + U.toString( U.stdVars() ) );

      V = ExpVector.EVRAND(5,10,q);
      System.out.println("V = " + V);
      System.out.println("V = " + V.toString(vars) );
      System.out.println("V = " + V.toString( V.stdVars() ) );

      System.out.println("hashCode(U) = " + U.hashCode() );
      System.out.println("hashCode(V) = " + V.hashCode() );
      System.out.println("hashCode(U) = " + U.hashCode() );

      String ev = "(1,2,3,4,5,6,7)";
      U = new ExpVector( ev );
      System.out.println(ev + " = " + U );

      U = ExpVector.EVRAND(30,10,q);
      System.out.println("U = " + U);
      U = new ExpVector( U.toString() );
      System.out.println("U = " + U);


      V = ExpVector.EVRAND(30,10,q);
      System.out.println("V = " + V);

      W = ExpVector.EVSUM(U,V);
      System.out.println("W = " + W);

      W = ExpVector.EVLCM(U,V);
      System.out.println("W = " + W);

      s = ExpVector.EVSIGN(W);
      System.out.println("s = " + s);

      t = ExpVector.EVMT(U,V);
      System.out.println("t = " + t);

      s = ExpVector.EVCOMP( ExpVector.INVLEX, U, V);
      System.out.println("s = " + s);

      s = ExpVector.EVCOMP( ExpVector.IGRLEX, U, V);
      System.out.println("s = " + s);

      long ti = System.currentTimeMillis();
      s = 0;
      for (int i = 0; i < 1000; i++ ) {
          U = ExpVector.EVRAND(30,10,q);
          V = ExpVector.EVRAND(30,10,q);
          s += ExpVector.EVCOMP( ExpVector.INVLEX, U, V);
          s += ExpVector.EVCOMP( ExpVector.IGRLEX, U, V);         
      }
      ti = System.currentTimeMillis() -ti;
      System.out.println("time = " + ti);
      System.out.println("s = " + s);

      ti = System.currentTimeMillis();
      s = 0;
      U = ExpVector.EVRAND(300,10,q);
      V = ExpVector.EVRAND(300,10,q);
      for (int i = 0; i < 10000; i++ ) {
          s += ExpVector.EVTDEG( U );
          s += ExpVector.EVTDEG( V );         
      }
      ti = System.currentTimeMillis() -ti;
      System.out.println("time = " + ti);
      System.out.println("s = " + s);

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
