/*
 * $Id$
 */

package edu.jas.poly;

import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;


/**
 * @deprecated
 */

public class testtok
{

  static void tuwas_p()
  {
      String[] vars = new String[] { "B", "S", "T", "Z", "P", "W" };
      int evord = ExpVector.INVLEX;
      //int evord = ExpVector.IGRLEX;
      String poly = "( 45 P + 35 S - 165 B - 36, "
                  + "35 P + 40 Z + 25 T - 27 S, "
                  + "15 W + 25 S P + 30 Z - 18 T - 165 B^2, "
                  + "- 9 W + 15 T P + 20 S Z, "
                  + "P W + 2 T Z - 11 B^3, "
                  + "99 W - 11 B S + 3 B^2 )";

      Reader problem = new StringReader( poly );
      PolynomialTokenizer tok = new PolynomialTokenizer(vars,evord,problem);
      System.out.println("start parsing"); 

      ArrayList L = null; 
      try {
          L = tok.nextPolynomialList();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("L = " + L); 
  }

  static void tuwas_l()
  {
      String[] vars = null;
      int evord = ExpVector.INVLEX;
      //int evord = ExpVector.IGRLEX;
      String list = "(B, S  T, Zx, P, W)";

      Reader problem = new StringReader( list );
      PolynomialTokenizer tok = new PolynomialTokenizer(vars,evord,problem);
      System.out.println("start parsing"); 

      try {
          vars = tok.nextVariableList();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("vars = " + vars); 
      System.out.print("(");
      for ( int i = 0; i < vars.length-1; i++ ) {
          System.out.print( vars[i] + ","); 
      }
      System.out.println( vars[vars.length-1] + ")");
  }


  static void tuwas_o()
  {
      String[] vars = null;
      int evord = ExpVector.INVLEX;
      System.out.println("evord = " + evord); 
      //int evord = ExpVector.IGRLEX;
      String list = "IL";

      Reader problem = new StringReader( list );
      PolynomialTokenizer tok = new PolynomialTokenizer(vars,evord,problem);
      System.out.println("start parsing"); 

      try {
          evord = tok.nextExpOrd();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("evord = " + evord); 
  }

  static void tuwas_s()
  {
      String set = "(B,S,T,Z,P,W) L "
                 + "( "
                 + "45 P + 35 S - 165 B - 36, "
                 + "35 P + 40 Z + 25 T - 27 S, "
                 + "15 W + 25 S P + 30 Z - 18 T - 165 B^2, "
                 + "- 9 W + 15 T P + 20 S Z, "
                 + "P W + 2 T Z - 11 B^3, "
                 + "99 W - 11 B S + 3 B^2 "
                 + " )";

      Reader problem = new StringReader( set );
      PolynomialTokenizer tok = new PolynomialTokenizer(problem);
      System.out.println("start parsing"); 

      PolynomialList s = null;
      try {
          s = tok.nextPolynomialSet();
      } catch (IOException e) { e.printStackTrace(); 
      }
      System.out.println("set = \n" + s); 
  }


  public static void main( java.lang.String[] _args )
  {
    try
    {
           tuwas_s();
      //      tuwas_o();
      //      tuwas_l();
      //      tuwas_p();
      if (_args != null)
      {
        java.lang.System.runFinalization();
        java.lang.System.exit(0);
      }
    }
    finally { }
  }
}
