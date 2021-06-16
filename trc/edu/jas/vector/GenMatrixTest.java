/*
 * $Id$
 */

package edu.jas.vector;


import java.util.ArrayList;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * GenMatrix tests with JUnit
 * @author Heinz Kredel
 */

public class GenMatrixTest extends TestCase {


    /**
     * main.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    /**
     * Constructs a <CODE>GenMatrixTest</CODE> object.
     * @param name String.
     */
    public GenMatrixTest(String name) {
        super(name);
    }


    /**
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(GenMatrixTest.class);
        return suite;
    }


    int rl = 5;


    int kl = 10;


    int ll = 10;


    float q = 0.5f;


    int rows = 3 + 20;


    int cols = 3 + 20;


    @Override
    protected void setUp() {
    }


    @Override
    protected void tearDown() {
    }


    /**
     * Test constructor and toString.
     */
    public void testConstruction() {
        BigRational cfac = new BigRational(1);
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, rows, cols);

        assertTrue("#rows = " + rows, mfac.rows == rows);
        assertTrue("#columns = " + cols, mfac.cols == cols);
        assertTrue("cfac == coFac ", cfac == mfac.coFac);

        GenMatrix<BigRational> a;
        a = mfac.getZERO();
        //System.out.println("a = " + a);
        assertTrue("isZERO( a )", a.isZERO());

        GenMatrix<BigRational> b = new GenMatrix<BigRational>(mfac);
        //System.out.println("b = " + b);
        assertTrue("isZERO( b )", b.isZERO());

        assertTrue("a == b ", a.equals(b));

        GenMatrix<BigRational> c = b.copy();
        //System.out.println("c = " + c);
        assertTrue("isZERO( c )", c.isZERO());
        assertTrue("a == c ", a.equals(c));

        GenMatrix<BigRational> d = mfac.copy(b);
        //System.out.println("d = " + d);
        assertTrue("isZERO( d )", d.isZERO());
        assertTrue("a == d ", a.equals(d));

        a = mfac.getONE();
        //System.out.println("a = " + a);
        assertTrue("isONE( a )", a.isONE());

        List<ArrayList<BigRational>> m = a.matrix;
        List<List<BigRational>> ml = new ArrayList<List<BigRational>>(m.size());
        for (ArrayList<BigRational> r : m) {
            ml.add(r);
        }
        b = mfac.fromList(ml);
        assertEquals("a == fromList(a.matrix)", a, b);

        GenMatrix<BigRational> e = mfac.generate((i, j) -> cfac.getZERO());
        //System.out.println("e = " + e);
        assertTrue("e == 0: ", e.isZERO());

        e = mfac.generate((i, j) -> i == j ? cfac.getONE() : cfac.getZERO());
        //System.out.println("e = " + e);
        assertTrue("e == 1: ", e.isONE());

        e = mfac.generate((i, j) -> i == j + 1 ? cfac.getONE() : cfac.getZERO());
        //System.out.println("e = " + e);
        assertTrue("e**" + mfac.cols + " == 0: ", e.power(mfac.cols).isZERO());
    }


    /**
     * Test random matrix.
     */
    public void testRandom() {
        BigRational cfac = new BigRational(1);
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, rows, cols);
        GenMatrixRing<BigRational> tfac = mfac.transpose();

        if (rows == cols) {
            assertTrue(" mfac = tfac ", mfac.equals(tfac));
        }

        GenMatrix<BigRational> a, b, c;

        for (int i = 0; i < 5; i++) {
            a = mfac.random(kl, q);
            //System.out.println("a = " + a);
            if (a.isZERO()) {
                continue;
            }
            assertTrue(" not isZERO( a" + i + " )", !a.isZERO());
            b = a.transpose(tfac);
            //System.out.println("b = " + b);
            assertTrue(" not isZERO( b" + i + " )", !b.isZERO());
            c = b.transpose(mfac);
            //System.out.println("c = " + c);
            assertEquals(" a^r^r == a ", a, c);
        }
    }


    /**
     * Test addition.
     */
    public void testAddition() {
        BigRational cfac = new BigRational(1);
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, rows, cols);
        GenMatrix<BigRational> a, b, c, d, e;

        a = mfac.random(kl, q);
        b = mfac.random(kl, q);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);

        c = a.sum(b);
        d = c.subtract(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b-b = a", a, d);

        c = a.sum(b);
        d = c.sum(b.negate());
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b+(-b) = a", a, d);

        c = a.sum(b);
        d = b.sum(a);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+b = b+a", c, d);

        c = mfac.random(kl, q);
        d = a.sum(b).sum(c);
        e = a.sum(b.sum(c));
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        assertEquals("a+(b+c) = (a+b)+c", d, e);
    }


    /**
     * Test scalar multiplication.
     */
    public void testScalarMultiplication() {
        BigRational cfac = new BigRational(1);
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, rows, cols);
        BigRational r, s, t;
        GenMatrix<BigRational> a, b, c, d;

        r = cfac.random(kl);
        //System.out.println("r = " + r);
        s = r.inverse();
        //System.out.println("s = " + s);

        a = mfac.random(kl, q);
        //System.out.println("a = " + a);

        c = a.scalarMultiply(r);
        d = c.scalarMultiply(s);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a*b*(1/b) = a", a, d);

        b = mfac.random(kl, q);
        //System.out.println("b = " + b);

        t = cfac.getONE();
        //System.out.println("t = " + t);
        c = a.linearCombination(b, t);
        d = b.linearCombination(a, t);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+1*b = b+1*a", c, d);

        c = a.linearCombination(b, t);
        d = a.sum(b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+1*b = b+1*a", c, d);

        s = t.negate();
        //System.out.println("s = " + s);
        c = a.linearCombination(b, t);
        d = c.linearCombination(b, s);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a+1*b+(-1)*b = a", a, d);

        c = a.linearCombination(t, b, t);
        d = c.linearCombination(t, b, s);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a*1+b*1+b*(-1) = a", a, d);

        t = cfac.getZERO();
        //System.out.println("t = " + t);
        c = a.linearCombination(b, t);
        //System.out.println("c = " + c);
        assertEquals("a+0*b = a", a, c);

        d = a.linearCombination(t, b, t);
        //System.out.println("d = " + d);
        assertEquals("0*a+0*b = 0", mfac.getZERO(), d);
    }


    /**
     * Test (simple) multiplication.
     */
    public void testSimpleMultiplication() {
        BigRational cfac = new BigRational(1);
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, rows, cols);
        GenMatrix<BigRational> a, b, c, d, e, f;

        a = mfac.getZERO();
        b = mfac.getZERO();
        c = a.multiplySimple(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        assertTrue("0*0 = 0 ", c.isZERO());

        a = mfac.getONE();
        b = mfac.getONE();
        c = a.multiplySimple(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        assertTrue("1*1 = 1 ", c.isONE());

        a = mfac.random(kl, q);
        b = mfac.getONE();
        c = a.multiplySimple(b);
        d = a.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("a*1 = a ", a, c);
        assertEquals("a*1 = a*1 ", c, d);

        c = b.multiplySimple(a);
        d = a.multiply(b);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        assertEquals("1*a = a ", a, c);
        assertEquals("a*1 = a*1 ", c, d);

        b = mfac.random(kl, q);
        long s, t;
        s = System.currentTimeMillis();
        c = a.multiplySimple(b);
        s = System.currentTimeMillis() - s;
        assertTrue("nonsense " + s, s >= 0L);
        d = b.multiplySimple(a);
        t = System.currentTimeMillis();
        e = a.multiply(b);
        t = System.currentTimeMillis() - t;
        assertTrue("nonsense " + t, t >= 0L);
        f = b.multiply(a);
        //System.out.println("a = " + a);
        //System.out.println("b = " + b);
        //System.out.println("c = " + c);
        //System.out.println("d = " + d);
        //System.out.println("e = " + e);
        //System.out.println("f = " + e);
        //System.out.println("e = " + e);
        assertTrue("a*b != b*a ", !c.equals(d));
        assertEquals("a*1 = a*1 ", c, e);
        assertEquals("a*1 = a*1 ", d, f);
        //System.out.println("time: s = " + s + ", t = " + t);

        if (!mfac.isAssociative()) {
            return;
        }
        c = mfac.random(kl, q);

        d = a.multiply(b.sum(c));
        e = (a.multiply(b)).sum(a.multiply(c));
        assertEquals("a*(b+c) = a*b+a*c", d, e);

        d = a.multiply(b.multiply(c));
        e = (a.multiply(b)).multiply(c);
        assertEquals("a*(b*c) = (a*b)*c", d, e);
    }


    /**
     * Test parse matrix.
     */
    public void testParse() {
        BigRational cfac = new BigRational(1);
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, rows, cols);

        GenMatrix<BigRational> a, c;

        a = mfac.random(kl, q);
        //System.out.println("a = " + a);
        if (!a.isZERO()) {
            //return;
            assertTrue(" not isZERO( a )", !a.isZERO());
        }
        String s = a.toString();
        //System.out.println("s = " + s);
        c = mfac.parse(s);
        //System.out.println("c = " + c);
        assertEquals("parse(toStirng(a) == a ", a, c);
    }


    /**
     * Test LU decomposition.
     */
    public void testLUdecomp() {
        BigRational cfac = new BigRational(1);
        int n = 10;
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, n, n);//rows, cols);
        GenVectorModul<BigRational> vfac = new GenVectorModul<BigRational>(cfac, n);//rows);

        GenMatrix<BigRational> A, Ap, iA, AiA;
        //A = mfac.getONE().negate(); //.sum(mfac.getONE());
        A = mfac.random(kl, 0.7f);
        //A = mfac.parse("[ [3,4], [1,2] ]");
        //System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();

        LinAlg<BigRational> lu = new LinAlg<BigRational>();
        BasicLinAlg<BigRational> blas = new BasicLinAlg<BigRational>();

        List<Integer> P = lu.decompositionLU(A);
        //System.out.println("P = " + P);
        //System.out.println("A = " + A);
        if (P.size() == 0) {
            System.out.println("undecomposable");
            return;
        }

        GenVector<BigRational> s;
        //b = vfac.random(kl);
        //b = vfac.parse("[5,5]");
        //s = vfac.parse("[1,1]");
        s = vfac.random(kl);
        //System.out.println("s = " + s);
        GenVector<BigRational> b = blas.rightProduct(s, Ap);
        //System.out.println("b = " + b);

        GenVector<BigRational> x = lu.solveLU(A, P, b);
        //System.out.println("x = " + x);
        assertEquals("s == x: ", s, x);

        GenVector<BigRational> r = blas.rightProduct(x, Ap);
        //System.out.println("r = " + r);

        //System.out.println("b == r: " + b.equals(r));
        assertEquals("b == r: ", b, r);

        BigRational det = lu.determinantLU(A, P);
        //System.out.println("det = " + det + " ~= " + det.getDecimal());
        assertFalse("det(A) != 0: ", det.isZERO()); // since P != ()

        iA = lu.inverseLU(A, P);
        //System.out.println("iA = " + iA);
        AiA = Ap.multiply(iA);
        //System.out.println("AiA = " + AiA);
        assertTrue("A*iA == 1: ", AiA.isONE());
    }


    /**
     * Test Null Space basis, modular coeffs.
     */
    public void testNullSpaceMod() {
        ModLongRing cfac = new ModLongRing(11); //11, 32003
        int n = 100;
        GenMatrixRing<ModLong> mfac = new GenMatrixRing<ModLong>(cfac, n, n);//rows, cols);
        //System.out.println("mfac = " + mfac.toScript());
        //GenVectorModul<ModLong> vfac = new GenVectorModul<ModLong>(cfac, n);//rows);
        GenMatrixRing<ModLong> tfac = mfac.transpose();

        GenMatrix<ModLong> A, Ap, B, T;
        //A = mfac.getONE(); //.negate(); //.sum(mfac.getONE());
        A = mfac.random(kl, 0.5f / n);
        //A = mfac.parse("[ [3,4,5], [1,2,3], [2,4,6] ]");
        //A = mfac.parse("[ [1,0,0,0,0], [3,0,0,0,0], [0,0,1,0,0], [2,0,4,0,0], [0,0,0,0,1] ]");
        //A = mfac.parse("[ [0,0,0,0,0,0], [3,4,-3,-3,5,5], [3,-5,5,1,-1,0], [-2,4,-1,2,-4,-2], [-4,-3,-1,0,-1,-3], [-3,-1,-4,-3,-1,-4] ]");
        //A = A.sum( mfac.getONE() );
        if (n < 50)
            System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();
        T = A.transpose(tfac);
        if (n < 10)
            System.out.println("At = " + T);

        LinAlg<ModLong> lu = new LinAlg<ModLong>();
        BasicLinAlg<ModLong> blas = new BasicLinAlg<ModLong>();

        List<GenVector<ModLong>> NSB = lu.nullSpaceBasis(A);
        //System.out.println("NS basis = " + NSB.size());
        if (NSB.size() == 0) {
            System.out.println("no null space basis");
            return;
        }
        if (n < 10)
            System.out.println("mod A-I = " + A);
        for (GenVector<ModLong> v : NSB) {
            GenVector<ModLong> z = blas.leftProduct(v, T);
            //System.out.println("z == 0: " + z.isZERO());
            assertTrue("z == 0: " + z, z.isZERO());
        }
        Ap = A.sum(mfac.getONE());
        B = Ap.multiply(Ap);
        if (!Ap.equals(B)) {
            System.out.println("Ap = " + Ap);
            System.out.println("B = " + B);
        }
        assertEquals("A*A == B: ", Ap, B);
    }


    /**
     * Test Null Space basis.
     */
    public void testNullSpace() {
        BigRational cfac = new BigRational(11);
        int n = 100;
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(cfac, n, n);//rows, cols);
        //System.out.println("mfac = " + mfac.toScript());
        //GenVectorModul<BigRational> vfac = new GenVectorModul<BigRational>(cfac, n);//rows);
        GenMatrixRing<BigRational> tfac = mfac.transpose();

        GenMatrix<BigRational> A, Ap, B, T;
        //A = mfac.getZERO(); //.negate(); //.sum(mfac.getONE());
        //A.setMutate(4,1, cfac.parse("44") );
        //A.setMutate(5,2, cfac.parse("22") );
        //A.setMutate(5,3, cfac.parse("33") );
        A = mfac.random(kl, 0.5f / n);
        //A = mfac.parse("[ [3,4,5], [1,2,3], [2,4,6] ]");
        //A = mfac.parse("[ [1,0,0,0,0], [3,0,0,0,0], [0,0,1,0,0], [2,0,4,0,0], [0,0,0,0,1] ]");
        //A = mfac.parse("[ [0,0,0,0,0,0], [3,4,-3,-3,5,5], [3,-5,5,1,-1,0], [-2,4,-1,2,-4,-2], [-4,-3,-1,0,-1,-3], [-3,-1,-4,-3,-1,-4] ]");
        //A = A.sum( mfac.getONE() ); // subtract
        if (n < 10)
            System.out.println("A = " + A);
        if (A.isZERO()) {
            return;
        }
        assertTrue(" not isZERO( A )", !A.isZERO());
        Ap = A.copy();
        T = A.transpose(tfac);

        LinAlg<BigRational> lu = new LinAlg<BigRational>();
        BasicLinAlg<BigRational> blas = new BasicLinAlg<BigRational>();

        List<GenVector<BigRational>> NSB = lu.nullSpaceBasis(A);
        //System.out.println("NS basis = " + NSB.size());
        if (NSB.size() == 0) {
            System.out.println("no null space basis");
            return;
        }
        if (n < 10)
            System.out.println("mod A-I = " + A);
        if (n < 10)
            System.out.println("T = " + T);
        for (GenVector<BigRational> v : NSB) {
            //System.out.println("v = " + v);
            GenVector<BigRational> z = blas.leftProduct(v, T);
            //System.out.println("z == 0: " + z.isZERO());
            assertTrue("z == 0: " + z, z.isZERO());
        }
        Ap = A.sum(mfac.getONE());
        B = Ap.multiply(Ap);
        if (!Ap.equals(B)) {
            System.out.println("Ap = " + Ap);
            System.out.println("B = " + B);
        }
        assertEquals("A*A == B: ", Ap, B);
    }

}
