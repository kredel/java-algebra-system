/*
 * $Id$
 */

package edu.jas.application;


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
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.HenselMultUtil;
import edu.jas.ufd.NoLiftingException;
import edu.jas.ufd.GCDFactory;


/**
 * HenselMultUtil tests with JUnit.
 * Two seperate classes because of package dependency.
 * @see edu.jas.ufd.HenselMultUtilTest
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
     * Test multivariate diophant lifting.
     */
    public void testDiophantLifting() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);

        ModIntegerRing pm = new ModIntegerRing(p, false);
        //ModLongRing pl = new ModLongRing(p, false);
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 2, tord, new String[]{ "x", "y" });
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 3, tord, new String[]{ "x", "y", "z" });
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
        ModInteger v = pkm.fromInteger(5L);
        List<ModInteger> V = new ArrayList<ModInteger>(1);
        V.add(v);
        V.add(pkm.fromInteger(3L));
        //System.out.println("V = " + V);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> bp;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> dp;
        GenPolynomial<ModInteger> sp;
        GenPolynomial<ModInteger> tp;
        GenPolynomial<ModInteger> rp;

        for (int i = 1; i < 2; i++) {
            a = dfac.random(kl + 7 * i, ll, el + 3, q).abs();
            b = dfac.random(kl + 7 * i, ll, el + 2, q).abs();
            //a = dfac.parse(" y^2 + 2 x y - 3 y + x^2 - 3 x - 4 ");
            //b = dfac.parse(" y^2 + 2 x y + 5 y + x^2 + 5 x + 4 ");
            //a = dfac.parse(" (x - 4 + y)*( x + y + 1 ) ");
            //b = dfac.parse(" (x + 4 + y)*( x + y + 1 ) ");
            //a = dfac.parse(" (x - 4 + y) ");
            ///a = dfac.parse(" (x - 13 + y) ");
            ///b = dfac.parse(" (x + 4 + y) ");
            //a = dfac.parse(" (x - 1)*(1 + x) ");
            //b = dfac.parse(" (x - 2)*(3 + x) ");
            //a = dfac.parse(" (x - 1)*(y + x) ");
            //b = dfac.parse(" (x - 2)*(y - x) ");
            //a = dfac.parse(" (x - 1)*(y + 1) ");
            //b = dfac.parse(" (x - 2)*(y - 1) ");
            //a = dfac.parse(" (x - 1)*(y^2 + 1) ");
            //b = dfac.parse(" (x - 2)*(y^2 - 1) ");
            //a = dfac.parse(" z + (y - 1)*(1 + y) ");
            //b = dfac.parse(" z + (y - 2)*(2 + y) ");
            //a = dfac.parse(" (y - 1)*(1 + y) ");
            //b = dfac.parse(" (y - 2)*(2 + y) ");
            ///a = dfac.parse(" (y - 3) "); //2 // tp = 47045880 = -1
            ///b = dfac.parse(" (y - 1) "); // sp = 1
            //a = dfac.parse(" (y - 4) "); // tp = 15681960
            //b = dfac.parse(" (y - 1) "); // sp = 31363921
            //a = dfac.parse(" (x - 3) "); // tp = 15681960,  1238049
            //b = dfac.parse(" (x - 1) "); // sp = 31363921, -1238049
            //a = dfac.parse(" ( y^2 + x^3 - 2 x ) ");
            //b = dfac.parse(" ( y - x^2 + 3 ) ");

            c = ufd.gcd(a,b);
            //System.out.println("\na     = " + a);
            //System.out.println("b     = " + b);
            //System.out.println("c     = " + c);

            if ( ! c.isUnit() ) {
                continue;
            }
            //c = dfac.parse(" x y z ");
            //System.out.println("c     = " + c);

            ap = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,a);
            bp = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,b);
            cp = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,c);
            //if (ap.degree(0) < 1 || bp.degree(0) < 1) {
            //    continue;
            //}
            //System.out.println("\nap     = " + ap);
            //System.out.println("bp     = " + bp);
            //System.out.println("cp     = " + cp);

            List<GenPolynomial<ModInteger>> lift;
            try {
                lift = HenselMultUtil.<ModInteger> liftDiophant(ap, bp, cp, V, d, k); // 5 is max
                sp = lift.get(0);
                tp = lift.get(1);
                //System.out.println("liftMultiDiophant:");
                //System.out.println("sp     = " + sp);
                //System.out.println("tp     = " + tp);
                //System.out.println("isDiophantLift: " +  HenselUtil.<ModInteger> isDiophantLift(bp,ap,sp,tp,cp) );

                GenPolynomialRing<ModInteger> qfac = sp.ring;
                //System.out.println("qfac   = " + qfac.toScript());
                assertEquals("pkfac == qfac: " + qfac, pkfac, qfac);

                rp = bp.multiply(sp).sum( ap.multiply(tp) ); // order
                //System.out.println("\nrp     = " + rp);

                //not true: System.out.println("a s + b t = c: " + cp.equals(rp));
                //assertEquals("a s + b t = c ", dp,rp);

                GenPolynomialRing<ModInteger> cfac = pkfac.contract(1);
                ModInteger vp = pkfac.coFac.fromInteger(V.get(0).getSymmetricInteger().getVal());
                GenPolynomial<ModInteger> ya = pkfac.univariate(1);
                ya = ya.subtract(vp);
                ya = Power.<GenPolynomial<ModInteger>>power(pkfac,ya,d+1);
                //System.out.println("ya     = " + ya);
                List<GenPolynomial<ModInteger>> Y = new ArrayList<GenPolynomial<ModInteger>>();
                Y.add(ya); 
                vp = pkfac.coFac.fromInteger(V.get(1).getSymmetricInteger().getVal());
                GenPolynomial<ModInteger> za = pkfac.univariate(0);
                za = za.subtract(vp);
                za = Power.<GenPolynomial<ModInteger>>power(pkfac,za,d+1);
                //System.out.println("za     = " + za);
                Y.add(za); 
                //System.out.println("\nY      = " + Y);
                Ideal<ModInteger> Yi = new Ideal<ModInteger>(pkfac,Y);
                //System.out.println("Yi     = " + Yi);
                ResidueRing<ModInteger> Yr = new ResidueRing<ModInteger>(Yi);
                //System.out.println("Yr     = " + Yr);

                Residue<ModInteger> apr = new Residue<ModInteger>(Yr,ap);
                Residue<ModInteger> bpr = new Residue<ModInteger>(Yr,bp);
                Residue<ModInteger> cpr = new Residue<ModInteger>(Yr,cp);
                Residue<ModInteger> spr = new Residue<ModInteger>(Yr,sp);
                Residue<ModInteger> tpr = new Residue<ModInteger>(Yr,tp);
                Residue<ModInteger> rpr = bpr.multiply(spr).sum( apr.multiply(tpr) ); // order
                //System.out.println("\napr     = " + apr);
                //System.out.println("bpr     = " + bpr);
                //System.out.println("cpr     = " + cpr);
                //System.out.println("spr     = " + spr);
                //System.out.println("tpr     = " + tpr);
                //System.out.println("rpr     = " + rpr);
                //System.out.println("ar sr + br tr = cr: " + cpr.equals(rpr) + "\n");
                assertEquals("ar sr + br tr = cr ", cpr,rpr);
            } catch (NoLiftingException e) {
                // can happen: fail("" + e);
                System.out.println("e = " + e);
            }
        }
    }


    /**
     * Test multivariate diophant lifting list.
     */
    public void testDiophantLiftingList() {
        java.math.BigInteger p;
        //p = getPrime1();
        p = new java.math.BigInteger("19");
        //p = new java.math.BigInteger("5");
        BigInteger m = new BigInteger(p);

        ModIntegerRing pm = new ModIntegerRing(p, false);
        //ModLongRing pl = new ModLongRing(p, false);
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 2, tord, new String[]{ "x", "y" });
        GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 3, tord, new String[]{ "x", "y", "z" });
        //GenPolynomialRing<ModInteger> pfac = new GenPolynomialRing<ModInteger>(pm, 4, tord, new String[]{ "w", "x", "y", "z" });
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
        ModInteger v = pkm.fromInteger(5L);
        List<ModInteger> V = new ArrayList<ModInteger>(1);
        V.add(v);
        V.add(pkm.fromInteger(3L));
        if ( pkfac.nvar > 3 ) {
            V.add(pkm.fromInteger(7L));
        }
        //System.out.println("V = " + V);

        GenPolynomial<ModInteger> ap;
        GenPolynomial<ModInteger> cp;
        GenPolynomial<ModInteger> rp;

        List<GenPolynomial<BigInteger>> A = new ArrayList<GenPolynomial<BigInteger>>();

        for (int i = 1; i < 2; i++) {
            a = dfac.random(kl + 7 * i, ll, el + 3, q).abs();
            b = dfac.random(kl + 7 * i, ll, el + 2, q).abs();
            c = dfac.random(kl + 7 * i, ll, el + 2, q).abs();
            //a = dfac.parse(" z + x*y + (y - 1)*(1 + y) ");
            //b = dfac.parse(" z - x + (y - 2)*(2 + y) ");
            //c = dfac.parse(" z + x + (y - 2)*(2 + y) ");

            A.add(a);
            A.add(b);
            A.add(c);
            //System.out.println("\nA          = " + A);
            A = ufd.coPrime(A);
            //System.out.println("coprime(A) = " + A);
            if ( A.size() == 0 ) {
                continue;
            }

            List<GenPolynomial<ModInteger>> Ap = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<BigInteger> ai : A ) {
                 ap = PolyUtil.<ModInteger> fromIntegerCoefficients(pkfac,ai);
                 Ap.add(ap);
            }
            //System.out.println("A mod p^k  = " + Ap);
            cp = pkfac.parse(" x y z + x y + x ");
            //cp = pkfac.parse(" x y + x ");
            //cp = Ap.get(0).multiply(Ap.get(1));
            //System.out.println("cp         = " + cp);
            
            GenPolynomial<ModInteger> B = pkfac.getONE();
            for ( GenPolynomial<ModInteger> bp : Ap ) {
                B = B.multiply(bp);
            }
            //System.out.println("B          = " + B);
            List<GenPolynomial<ModInteger>> Bp = new ArrayList<GenPolynomial<ModInteger>>(A.size());
            for ( GenPolynomial<ModInteger> bp : Ap ) {
                 GenPolynomial<ModInteger> b = PolyUtil.<ModInteger> basePseudoDivide(B, bp);
                 if ( b.isZERO() ) {
                     System.out.println("b == 0");
                     return;
                 }
                 Bp.add(b);
            }
            //System.out.println("B mod p^k  = " + Bp);

            try {
                List<GenPolynomial<ModInteger>> lift;
                lift = HenselMultUtil.<ModInteger> liftDiophant(Ap, cp, V, d, k); // 5 is max
                //System.out.println("liftMultiDiophant:");
                //System.out.println("lift   = " + lift);

                GenPolynomialRing<ModInteger> qfac = lift.get(0).ring;
                assertEquals("pkfac == qfac: " + qfac, pkfac, qfac);

                GenPolynomialRing<ModInteger> cfac = pkfac.contract(1);
                List<GenPolynomial<ModInteger>> Y = new ArrayList<GenPolynomial<ModInteger>>();

                for ( int j = 0; j < V.size(); j++ ) {
                    ModInteger vp = pkfac.coFac.fromInteger(V.get(j).getSymmetricInteger().getVal());
                    GenPolynomial<ModInteger> ya = pkfac.univariate(pkfac.nvar-2-j);
                    ya = ya.subtract(vp);
                    //ya = Power.<GenPolynomial<ModInteger>>power(pkfac,ya,d+1);
                    //System.out.println("ya     = " + ya);
                    Y.add(ya); 
                }
                //System.out.println("\nY      = " + Y);
                Ideal<ModInteger> Yi = new Ideal<ModInteger>(pkfac,Y);
                //System.out.println("Yi     = " + Yi);
                Yi = Yi.power((int)d+1);
                //System.out.println("Yi     = " + Yi);
                ResidueRing<ModInteger> Yr = new ResidueRing<ModInteger>(Yi);
                //System.out.println("\nYr     = " + Yr);

                List<Residue<ModInteger>> Bpr = new ArrayList<Residue<ModInteger>>(A.size());
                for ( GenPolynomial<ModInteger> tp : Bp ) {
                     Residue<ModInteger> apr = new Residue<ModInteger>(Yr,tp);
                     Bpr.add(apr);
                }
                List<Residue<ModInteger>> Spr = new ArrayList<Residue<ModInteger>>(A.size());
                for ( GenPolynomial<ModInteger> sp : lift ) {
                     Residue<ModInteger> apr = new Residue<ModInteger>(Yr,sp);
                     if ( apr.isZERO() ) {
                         System.out.println("apr == 0");
                         //return;
                     }
                     Spr.add(apr);
                }
                //System.out.println("\nBpr     = " + Bpr);
                //System.out.println("Spr     = " + Spr);

                Residue<ModInteger> cpr = new Residue<ModInteger>(Yr,cp);
                Residue<ModInteger> rpr = Yr.getZERO();
                int j = 0;
                for ( Residue<ModInteger> r : Bpr ) {
                    rpr = rpr.sum( r.multiply(Spr.get(j++)) ); 
                }
                //System.out.println("cpr     = " + cpr);
                //System.out.println("rpr     = " + rpr);
                assertEquals("sum_i( br sr ) = cr ", cpr,rpr);
            } catch (ArithmeticException e) {
                // ok, can happen
            } catch (NoLiftingException e) {
                // can now happen: fail("" + e);
                System.out.println("e = " + e);
            }
        }
    }

}
