/*
 * $Id$
 */

package edu.jas.ps;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.BinaryFunctor;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.Selector;
import edu.jas.structure.UnaryFunctor;


/**
 * Examples for multivariate power series implementations.
 * @author Heinz Kredel
 */

public class MultiExamples {


    public static void main(String[] args) {
        BasicConfigurator.configure();
        //example1();
        //example2();
        //example3();
        //example4();
        //example5();
        example6();
    }


    public static void example1() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^2  + x y");
        GenPolynomial<BigRational> b = pfac.parse("y^2  + x y");
        GenPolynomial<BigRational> c = pfac.parse("x^3  + x^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L,cp);
        System.out.println("dp = " + dp);
    }


    public static void example2() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x - x y");
        GenPolynomial<BigRational> b = pfac.parse("y^2  + x^3");
        GenPolynomial<BigRational> c = pfac.parse("x^2  + y^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L,cp);
        System.out.println("dp = " + dp);
    }


    public static void example3() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x");
        GenPolynomial<BigRational> b = pfac.parse("y");
        GenPolynomial<BigRational> c = pfac.parse("x^2 + y^2 + z^3 + x^4 + y^5");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L,cp);
        System.out.println("dp = " + dp);
    }


    public static void example4() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x + x^3 + x^5");
        GenPolynomial<BigRational> c = pfac.parse("x + x^2");
        System.out.println("a = " + a);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(11);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L,cp);
        System.out.println("dp = " + dp);
    }


    public static void example5() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("");
        GenPolynomial<BigRational> b = pfac.parse("");
        GenPolynomial<BigRational> c = pfac.parse("x^2  + y^2 - 1");
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        //fac.setTruncate(19);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.getSIN(0);
        MultiVarPowerSeries<BigRational> bp = fac.getCOS(1);
        MultiVarPowerSeries<BigRational> ep = fac.getCOS(0);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("ep = " + ep);
        System.out.println("cp = " + cp);
        System.out.println("ap^2 + ep^2 = " + ap.multiply(ap).sum(ep.multiply(ep)));

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();

        MultiVarPowerSeries<BigRational> dp = red.normalform(L,cp);
        System.out.println("dp = " + dp);
    }


    public static void example6() {
        BigRational br = new BigRational(1);
        String[] vars = new String[] { "x", "y", "z" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(br,vars);
        System.out.println("pfac = " + pfac.toScript());

        GenPolynomial<BigRational> a = pfac.parse("x^5 - x y^6 + z^7");
        GenPolynomial<BigRational> b = pfac.parse("x y + y^3 + z^3");
        GenPolynomial<BigRational> c = pfac.parse("x^2  + y^2 - z^2");
        System.out.println("a = " + a);
        System.out.println("b = " + b);
        System.out.println("c = " + c);

        MultiVarPowerSeriesRing<BigRational> fac = new MultiVarPowerSeriesRing<BigRational>(pfac);
        fac.setTruncate(9);
        System.out.println("fac = " + fac.toScript());

        MultiVarPowerSeries<BigRational> ap = fac.fromPolynomial(a);
        MultiVarPowerSeries<BigRational> bp = fac.fromPolynomial(b);
        MultiVarPowerSeries<BigRational> cp = fac.fromPolynomial(c);
        System.out.println("ap = " + ap);
        System.out.println("bp = " + bp);
        System.out.println("cp = " + cp);

        List<MultiVarPowerSeries<BigRational>> L = new ArrayList<MultiVarPowerSeries<BigRational>>();
        L.add(ap);
        L.add(bp);
        L.add(cp);
        StandardBaseSeq<BigRational> tm = new StandardBaseSeq<BigRational>();

        List<MultiVarPowerSeries<BigRational>> S = tm.STD(L);
        for ( MultiVarPowerSeries<BigRational> ps : S ) {
             System.out.println("ps = " + ps);
        }
        System.out.println("\nS = " + S);

        boolean s = tm.isSTD(S);
        System.out.println("\nisSTD = " + s);

        ReductionSeq<BigRational> red = new ReductionSeq<BigRational>();
        s = red.contains(S,L);
        System.out.println("\nS contains L = " + s);
    }

}