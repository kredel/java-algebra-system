/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

//import edu.jas.poly.RatPolynomial;
//import edu.jas.poly.IntPolynomial;


public class testbip
{

  static void tuwas_ro()
  {
      System.out.println("Testing RatPolynomial \n"); 

      RatPolynomial A;
      int rl = 3; 
      int kl = 10;
      int ll = 10;
      int el = 5;
      float q = 0.5f;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      RatPolynomial B = A;
      //      System.out.println("B = " + B ); 
      //      System.out.println("B.size() = " + B.length() ); 

      RatPolynomial C;

      C = (RatPolynomial) RatPolynomial.DIRPSM(A,B);
      System.out.println("Sum = " + C ); 
      System.out.println("Sum.size() = " + C.length() ); 

      C = (RatPolynomial) RatPolynomial.DIRPDF(A,B);
      System.out.println("Dif = " + C ); 
      System.out.println("Dif.size() = " + C.length() ); 

      C = (RatPolynomial) RatPolynomial.DIRPPR(A,B);
      System.out.println("Prod = " + C ); 
      System.out.println("Prod.size() = " + C.length() ); 

      BigRational b = BigRational.RNONE;
      b = BigRational.RNNEG(b);

      C = (RatPolynomial) RatPolynomial.DIRPRP(A,b);
      System.out.println("Prod(-1) = " + C ); 
      System.out.println("Prod.size() = " + C.length() ); 

      C = (RatPolynomial) RatPolynomial.DIRPSM(A,C);
      System.out.println("Sum = " + C ); 
      System.out.println("Sum.size() = " + C.length() ); 

      C = RatPolynomial.DIRPMC(A);
      System.out.println("Monic = " + C ); 
      System.out.println("Monic.size() = " + C.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      C = new RatPolynomial( " 6/8 (1,2,3) -3/5 (0,0,3) 8/5 (0,0,3) "   );
      System.out.println("C = " + C ); 
      System.out.println("C.size() = " + C.length() ); 

      RatPolynomial st;
      st = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("st = " + st ); 
      System.out.println("st.size() = " + st.length() ); 
      C = st;
      st = new RatPolynomial( st.toString() );
      System.out.println("st = " + st ); 
      System.out.println("st.size() = " + st.length() ); 
      C = RatPolynomial.DIRPDF(C,st);
      System.out.println("Dif = " + C ); 
      System.out.println("Dif.size() = " + C.length() ); 

  
  }

  static void tuwas_r()
  {
      System.out.println("Testing Polynomial on Rat \n"); 

      RatPolynomial A, B, C;
      int rl = 3; 
      int kl = 10;
      int ll = 10;
      int el = 5;
      float q = 0.5f;

      A = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      B = A; //RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      //      System.out.println("B = " + B ); 
      //      System.out.println("B.size() = " + B.length() ); 


      C = (RatPolynomial) RatPolynomial.DIPSUM(A,B);
      System.out.println("Sum = " + C ); 
      System.out.println("Sum.size() = " + C.length() ); 

      C = (RatPolynomial) RatPolynomial.DIPDIF(A,B);
      System.out.println("Dif = " + C ); 
      System.out.println("Dif.size() = " + C.length() ); 

      C = (RatPolynomial) RatPolynomial.DIPPR(A,B);
      System.out.println("Prod = " + C ); 
      System.out.println("Prod.size() = " + C.length() ); 

      BigRational b = BigRational.RNONE;
      b = BigRational.RNNEG(b);

      C = (RatPolynomial) RatPolynomial.DIPRP(A,b);
      System.out.println("Prod(-1) = " + C ); 
      System.out.println("Prod.size() = " + C.length() ); 

      C = (RatPolynomial) RatPolynomial.DIPSUM(A,C);
      System.out.println("Sum = " + C ); 
      System.out.println("Sum.size() = " + C.length() ); 

      C = RatPolynomial.DIRPMC(A);
      System.out.println("Monic = " + C ); 
      System.out.println("Monic.size() = " + C.length() ); 

      B = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      C = new RatPolynomial( " 6/8 (1,2,3) -3/5 (0,0,3) 8/5 (0,0,3) "   );
      System.out.println("C = " + C ); 
      System.out.println("C.size() = " + C.length() ); 

      RatPolynomial st;
      st = RatPolynomial.DIRRAS(rl, kl, ll, el, q );
      System.out.println("st = " + st ); 
      System.out.println("st.size() = " + st.length() ); 
      C = st;
      st = new RatPolynomial( st.toString() );
      System.out.println("st = " + st ); 
      System.out.println("st.size() = " + st.length() ); 
      C = RatPolynomial.DIRPDF(C,st);
      System.out.println("Dif = " + C ); 
      System.out.println("Dif.size() = " + C.length() ); 

  
  }

  static void tuwas_i()
  {
      System.out.println("Testing Polynomial on Int \n"); 

      IntPolynomial A, B, C, D;
      int rl = 3; 
      int kl = 10;
      int ll = 10;
      int el = 5;
      float q = 0.5f;
      boolean t;

      A = new IntPolynomial(rl);
      System.out.println("A = " + A ); 

      B = new IntPolynomial( new BigInteger("555555555555555555555555555555"), 
                             new ExpVector(rl) );
      System.out.println("B = " + B ); 
      System.out.println("B.NOV = " + B.NOV() ); 
      System.out.println("B.NOV = " + Polynomial.DIPNOV(B) ); 
      System.out.println("B.LBC = " + B.LBC() ); 
      System.out.println("B.LBC = " + Polynomial.DIPLBC(B) ); 
      System.out.println("B.LEV = " + B.LEV() ); 
      System.out.println("B.LEV = " + Polynomial.DIPLEV(B) ); 

      A = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
      System.out.println("A = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      String[] vars = ExpVector.STDVARS(rl);
      System.out.println("vars = " + vars ); 
      System.out.println("A = " + A.toString(vars) ); 

      B = IntPolynomial.DIIRAS(rl, kl, ll, el, q );
      System.out.println("B = " + B ); 
      System.out.println("B.size() = " + B.length() ); 

      C = (IntPolynomial) IntPolynomial.DIPSUM(A,B);
      System.out.println("\nA = " + A ); 
      System.out.println("B = " + B ); 
      System.out.println("Sum = " + C ); 
      System.out.println("Sum.size() = " + C.length() ); 

      C = (IntPolynomial) IntPolynomial.DIPDIF(A,B);
      System.out.println("\nA = " + A ); 
      System.out.println("B = " + B ); 
      System.out.println("Dif = " + C ); 
      System.out.println("Dif.size() = " + C.length() ); 

      C = (IntPolynomial) IntPolynomial.DIPPR(A,B);
      System.out.println("\nA = " + A ); 
      System.out.println("B = " + B ); 
      System.out.println("Prod = " + C ); 
      System.out.println("Prod.size() = " + C.length() ); 

      BigInteger b = BigInteger.ONE;
      b = (BigInteger) b.negate();

      C = (IntPolynomial) IntPolynomial.DIPRP(A,b);
      System.out.println("Prod(-1) = " + C ); 
      System.out.println("Prod.size() = " + C.length() ); 

      System.out.println("A        = " + A ); 
      System.out.println("A.size() = " + A.length() ); 

      D = (IntPolynomial) IntPolynomial.DIPSUM(A,C);
      System.out.println("Sum,D = " + D ); 
      System.out.println("Sum.size() = " + D.length() ); 

      t = D.equals( IntPolynomial.ZERO );
      System.out.println("D.equals(ZERO) " + t ); 
      t = D.isZERO();
      System.out.println("D.isZERO()     " + t ); 

      D = (IntPolynomial) IntPolynomial.DIPNEG(A);
      System.out.println("- A,D = " + D ); 
      System.out.println("- A,C = " + C ); 

      t = D.equals( C );
      System.out.println("D = C " + t ); 

      TreeMap m1, m2;
      m1 = C.getval();
      m2 = D.getval();
      System.out.println("m1 =    " + m1 ); 
      System.out.println("m2 =    " + m2 ); 
      t = m1.equals(m2);
      System.out.println("m1 = m1 " + t ); 
  
      int o1, o2;
      o1 = C.evord;
      o2 = D.evord;
      System.out.println("o1 = " + o1 ); 
      System.out.println("o2 = " + o2 ); 

  }



  public static void main( java.lang.String[] _args )
  {
    try
    {
	//      tuwas_ro();
	//tuwas_r();
      tuwas_i();
      if (_args != null)
      {
        java.lang.System.runFinalization();
        java.lang.System.exit(0);
      }
    }
    finally { }
  }
}
