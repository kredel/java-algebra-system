package edu.jas.commons.math;

import java.util.List;
import java.math.MathContext;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigDecimal;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenMatrixRing;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenVectorModul;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.PolyUtil;


/**
 * Example that computes a solution of a linear equation system.
 * 
 * 
 */
public class MatrixExamples {

  public static void main(String[] argv) {
    example1();
    example2();
  }

  public static void example1() {
    BigRational r1, r2, r3, r4, r5, r6, fac;
    r1 = new BigRational(1, 10);
    r2 = new BigRational(6, 5);
    r3 = new BigRational(1, 9);
    r4 = new BigRational(1, 1);
    r5 = r2.sum(r3);
    r6 = r1.multiply(r4);

    fac = new BigRational();

    BigRational[][] aa = new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 },
        { r2, r1, r3 } };
    GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(fac,
        aa.length, aa[0].length);
    GenMatrix<BigRational> a = new GenMatrix<BigRational>(mfac, CMFieldElementUtil
        .<BigRational> toList(aa));
    System.out.println("system = " + a);

    BigRational[] ba = new BigRational[] { r1, r2, r3 };
    GenVectorModul<BigRational> vfac = new GenVectorModul<BigRational>(fac,
        ba.length);
    GenVector<BigRational> b = new GenVector<BigRational>(vfac, CMFieldElementUtil
        .<BigRational> toList(ba));
    System.out.println("right hand side = " + b);

    GaussElimination<BigRational> ge = new GaussElimination<BigRational>();
    GenVector<BigRational> x = ge.solve(a, b);
    System.out.println("solution = " + x);
  }


  public static void example2() {
      BigRational cfac = new BigRational();
      GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, new String[] { "x"} );

      GenPolynomial<BigRational> p = pfac.parse("(x^2 + 2)(x^2 - 3)"); // (x^2 - 2)
      System.out.println("p = " + p);
      //p = p.multiply(p); // not square-free to fail complexRoots
      //System.out.println("p = " + p);

      Roots<BigRational> rf = new Roots<BigRational>();
      List<Complex<BigDecimal>> r = rf.complexRoots(p);
      System.out.println("r = " + r);
      if (r.size() != p.degree(0) ) {
          System.out.println("#r != deg(p)");
      }

      ComplexRing<BigDecimal> cc = new ComplexRing<BigDecimal>(new BigDecimal(0.0,MathContext.DECIMAL64));
      GenPolynomialRing<Complex<BigDecimal>> dfac = new GenPolynomialRing<Complex<BigDecimal>>(cc, new String[] { "x"} );
      GenPolynomial<Complex<BigDecimal>> cp = dfac.parse(p.toString());
      //System.out.println("cp = " + cp);
      for ( Complex<BigDecimal> cd : r ) {
          //System.out.println("cd = " + cd);
	  Complex<BigDecimal> ev = PolyUtil.<Complex<BigDecimal>> evaluateMain(cc,cp,cd);  
          //System.out.println("ev = " + ev + " == 0.0: " + ev.isZERO());
      }
  }

}
