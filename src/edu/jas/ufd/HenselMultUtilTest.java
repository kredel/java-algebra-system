/*
 * $Id$
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import edu.jas.arith.PrimeList;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.ModularRingFactory;
import edu.jas.structure.Power;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
//import edu.jas.application.Ideal;
//import edu.jas.application.Residue;
//import edu.jas.application.ResidueRing;


/**
 * HenselMultUtil tests with JUnit.
 * Two seperate classes because of package dependency.
 * @see edu.jas.application.HenselMultUtilTest
 * @author Heinz Kredel.
 */

public class HenselMultUtilTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        junit.textui.TestRunner.run(suite());
        ComputerThreads.terminate();
    }


    /**
     * Constructs a <CODE>HenselMultUtilTest</CODE> object.
     * @param name String.
     */
    public HenselMultUtilTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(HenselMultUtilTest.class);
        return suite;
    }


    TermOrder tord = new TermOrder(TermOrder.INVLEX);


    GenPolynomialRing<BigInteger> dfac;


    GenPolynomialRing<BigInteger> cfac;


    GenPolynomialRing<GenPolynomial<BigInteger>> rfac;


    BigInteger ai;


    BigInteger bi;


    BigInteger ci;


    BigInteger di;


    BigInteger ei;


    GenPolynomial<BigInteger> a;


    GenPolynomial<BigInteger> b;


    GenPolynomial<BigInteger> c;


    GenPolynomial<BigInteger> d;


    GenPolynomial<BigInteger> e;


    int rl = 2;


    int kl = 5;


    int ll = 5;


    int el = 3;


    float q = 0.3f;


    @Override
    protected void setUp() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl, tord);
        cfac = new GenPolynomialRing<BigInteger>(new BigInteger(1), rl - 1, tord);
        rfac = new GenPolynomialRing<GenPolynomial<BigInteger>>(cfac, 1, tord);
    }


    @Override
    protected void tearDown() {
        a = b = c = d = e = null;
        ai = bi = ci = di = ei = null;
        dfac = null;
        cfac = null;
        rfac = null;
        ComputerThreads.terminate();
    }


    protected static java.math.BigInteger getPrime1() {
        return PrimeList.getLongPrime(60,93);
    }


    protected static java.math.BigInteger getPrime2() {
        return PrimeList.getLongPrime(30,35);
    }


    /**
     * Test multivariate Hensel lifting monic case list.
     */
    public void xtestHenselLiftingMonicList() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);

        ModIntegerRing pm = new ModIntegerRing(p, false);
        //ModLongRing pl = new ModLongRing(p, false);
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 2, tord, new String[]{ "x", "y" });
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 3, tord, new String[]{ "x", "y", "z" });
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 4, tord, new String[]{ "w", "x", "y", "z" });
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),pfac);

        BigInteger mi = m;
        long k = 5L;
        long d = 3L;
        java.math.BigInteger pk = p.pow((int)k);
        m = new BigInteger(pk);

        ModIntegerRing pkm = new ModIntegerRing(pk, false);
        //ModLongRing pkl = new ModLongRing(pk, false);
        GenPolynomialRing<ModInteger> pkfac = new GenPolynomialRing<ModInteger>(pkm, pfac);
        dfac = new GenPolynomialRing<BigInteger>(mi, pfac);

        //GreatestCommonDivisor<BigInteger> ufd = GCDFactory.getProxy(mi);
        GreatestCommonDivisor<BigInteger> ufd = GCDFactory.getImplementation(mi);

        //ModLong v = pl.fromInteger(3L);
        ModInteger v = pkm.fromInteger(3L);
        List<ModInteger> V = new ArrayList<ModInteger>(1);
        V.add(v);
        if ( pkfac.nvar > 2 ) {
            V.add(pkm.fromInteger(5L));
        }
        if ( pkfac.nvar > 3 ) {
            V.add(pkm.fromInteger(7L));
        }
        //System.out.println("V = " + V);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> rp;

        List<GenPolynomial<BigInteger>> A = new ArrayList<GenPolynomial<BigInteger>>();

        for (int i = 1; i < 2; i++) {
            //a = dfac.random(kl + 7 * i, ll, el + 1, q).abs();
            //b = dfac.random(kl + 7 * i, ll, el + 0, q).abs();
            //c = dfac.random(kl + 7 * i, ll, el + 2, q).abs();
            a = dfac.parse(" ( z^2 + y^2 + 4 x^3 - x + 1 + w ) ");
            b = dfac.parse(" ( z + y + x^2 + 10 + w ) ");
            //c = dfac.parse(" z + x + (y - 2)*(2 + y) ");

            A.add(a);
            A.add(b);
            //A.add(c);
            //System.out.println("A          = " + A);
            A = ufd.coPrime(A);
            //System.out.println("coprime(A) = " + A);
            if ( A.size() == 0 ) {
                continue;
            }
            c = A.get(0).multiply(A.get(1));
            //c = dfac.parse(" y^2 + x^2 ");
            cp = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,c);
            //System.out.println("c          = " + c);

            List<GenPolynomial<ModInteger>> Ap = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<BigInteger> ai : A ) {
                 ap = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,ai);
                 Ap.add(ap);
            }
            //System.out.println("A mod p^k  = " + Ap);
            //System.out.println("v = " + v + ", vp = " + vp);
            GenPolynomialRing<ModInteger> ckfac = pkfac.contract(1);
            v = V.get(2);
            List<GenPolynomial<ModInteger>> Ae = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<ModInteger> a : Ap ) {
                 GenPolynomial<ModInteger> ae = PolyUtil.<ModInteger> evaluateMain(ckfac,a,v);
                 Ae.add(ae);
            }
            //System.out.println("A(v) mod p^k = " + Ae);
            ckfac = ckfac.contract(1);
            v = V.get(1);
            List<GenPolynomial<ModInteger>> Ae1 = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<ModInteger> a : Ae ) {
                 GenPolynomial<ModInteger> ae = PolyUtil.<ModInteger> evaluateMain(ckfac,a,v);
                 Ae1.add(ae);
            }
            Ae = Ae1;
            //System.out.println("A(v,v) mod p^k = " + Ae);
            ckfac = ckfac.contract(1);
            v = V.get(0);
            Ae1 = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<ModInteger> a : Ae ) {
                 GenPolynomial<ModInteger> ae = PolyUtil.<ModInteger> evaluateMain(ckfac,a,v);
                 Ae1.add(ae);
            }
            Ae = Ae1;
            //System.out.println("A(v,v,v) mod p^k = " + Ae);
            
            try {
                List<GenPolynomial<ModInteger>> lift;
                lift = HenselMultUtil.<ModInteger> liftHenselMonic(c, cp, Ae, V, k); // 5 is max
                //System.out.println("\nliftMultiHensel:");
                //System.out.println("lift   = " + lift);
                //System.out.println("A      = " + A);
                boolean t = HenselMultUtil.<ModInteger> isHenselLift(c, cp, Ae, k, lift);
                assertTrue("isHenselLift: ", t);
            } catch (ArithmeticException e) {
                // ok, can happen
            } catch (NoLiftingException e) {
                // can now happen: fail("" + e);
                System.out.println("e = " + e);
            }
        }
    }


    /**
     * Test multivariate Hensel lifting list.
     */
    public void testHenselLiftingList() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);

        ModIntegerRing pm = new ModIntegerRing(p, false);
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 2, tord, new String[]{ "x", "y" });
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 3, tord, new String[]{ "x", "y", "z" });
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 4, tord, new String[]{ "w", "x", "y", "z" });
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),pfac);
        GenPolynomialRing<GenPolynomial<BigInteger>> irfac = ifac.recursive(ifac.nvar-1);

        BigInteger mi = m;
        long k = 5L;
        long d = 3L;
        java.math.BigInteger pk = p.pow((int)k);
        m = new BigInteger(pk);
        System.out.println("m = " + m);

        ModIntegerRing pkm = new ModIntegerRing(pk, false);
        //ModLongRing pkl = new ModLongRing(pk, false);
        GenPolynomialRing<ModInteger> pkfac = new GenPolynomialRing<ModInteger>(pkm, pfac);
        dfac = new GenPolynomialRing<BigInteger>(mi, pfac);

        //GreatestCommonDivisor<BigInteger> ufd = GCDFactory.getProxy(mi);
        GreatestCommonDivisor<BigInteger> ufd = GCDFactory.getImplementation(mi);

        //ModLong v = pl.fromInteger(3L);
        ModInteger v = pkm.fromInteger(3L);
        List<ModInteger> V = new ArrayList<ModInteger>(1);
        V.add(v);
        if ( pkfac.nvar > 2 ) {
            V.add(pkm.fromInteger(5L));
        }
        if ( pkfac.nvar > 3 ) {
            V.add(pkm.fromInteger(7L));
        }
        //System.out.println("V = " + V);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> rp;

        List<GenPolynomial<BigInteger>> A = new ArrayList<GenPolynomial<BigInteger>>();

        for (int i = 1; i < 2; i++) {
            //a = dfac.random(kl + 7 * i, ll, el + 1, q).abs();
            //b = dfac.random(kl + 7 * i, ll, el + 0, q).abs();
            //c = dfac.random(kl + 7 * i, ll, el + 2, q).abs();
            //a = dfac.parse(" ( z^2 + y^2 + 4 x^3 - x + 1 + w ) ");
            //b = dfac.parse(" ( z y x + x^2 + 10 + w ) ");
            a = dfac.parse(" ( x^3 z - 1 ) ");
            //a = dfac.parse(" ( x - y ) ");
            b = dfac.parse(" ( x^2 + z ) ");
            e = dfac.parse(" ( y - 2 x ) ");

            A.add(a);
            A.add(b);
            //A.add(e);
            //System.out.println("A          = " + A);
            A = ufd.coPrime(A);
            System.out.println("coprime(A) = " + A); // polynomials are rearranged
            if ( A.size() == 0 ) {
                continue;
            }
            c = A.get(0).multiply(A.get(1)); //.multiply(A.get(2));
            //c = dfac.parse(" y^2 + x^2 ");
            cp = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,c);
            System.out.println("c          = " + c);
            GenPolynomial<GenPolynomial<BigInteger>> cr = PolyUtil.<BigInteger>recursive(irfac,a);
            GenPolynomial<GenPolynomial<BigInteger>> crr = PolyUtil.<BigInteger>switchVariables(cr);
            GenPolynomial<BigInteger> cl = crr.leadingBaseCoefficient();
            System.out.println("cl         = " + cl);
            List<GenPolynomial<BigInteger>> CL = new ArrayList<GenPolynomial<BigInteger>>(2);
            CL.add( cl.ring.getONE() );
            //CL.add( cl.ring.getONE() );
            CL.add( cl );
            System.out.println("CL         = " + CL);

            List<GenPolynomial<ModInteger>> Ap = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<BigInteger> ai : A ) {
                 ap = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,ai);
                 Ap.add(ap);
            }
            System.out.println("A mod p^k  = " + Ap);
            System.out.println("v = " + v + ", V = " + V);

            GenPolynomialRing<ModInteger> ckfac = pkfac.contract(1);
            //v = V.get(2);
            List<GenPolynomial<ModInteger>> Ae = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            //for ( GenPolynomial<ModInteger> a : Ap ) {
            //     GenPolynomial<ModInteger> ae = PolyUtil.<ModInteger> evaluateMain(ckfac,a,v);
            //     Ae.add(ae);
            //}
            //System.out.println("A(v) mod p^k = " + Ae);
            //ckfac = ckfac.contract(1);

            Ae = Ap;
            v = V.get(1);
            List<GenPolynomial<ModInteger>> Ae1 = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<ModInteger> a : Ae ) {
                 GenPolynomial<ModInteger> ae = PolyUtil.<ModInteger> evaluateMain(ckfac,a,v);
                 Ae1.add(ae);
            }
            Ae = Ae1;
            System.out.println("A(v) mod p^k = " + Ae);
            ckfac = ckfac.contract(1);

            v = V.get(0);
            Ae1 = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<ModInteger> a : Ae ) { // Ap
                 GenPolynomial<ModInteger> ae = PolyUtil.<ModInteger> evaluateMain(ckfac,a,v);
                 Ae1.add(ae);
            }
            Ae = Ae1;
            System.out.println("A(v,v) mod p^k = " + Ae);
          
            try {
                List<GenPolynomial<ModInteger>> lift;
                lift = HenselMultUtil.<ModInteger> liftHensel(c, cp, Ae, V, k, CL); // 5 is max
                //System.out.println("\nliftMultiHensel:");
                System.out.println("lift   = " + lift);
                System.out.println("A      = " + A);
                boolean t = HenselMultUtil.<ModInteger> isHenselLift(c, cp, Ae, k, lift);
                assertTrue("isHenselLift: ", t);
            } catch (ArithmeticException e) {
                // ok, can happen
                System.out.println("e = " + e);
            } catch (NoLiftingException e) {
                // can now happen: 
                fail("" + e);
                System.out.println("e = " + e);
            }
        }
    }

}
