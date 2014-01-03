/*
 * $Id$
 */

package edu.jas.jlinalg;


import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.polynomial.Polynomial;

import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenVectorModul;
import edu.jas.vector.GenMatrixRing;


/**
 * Example that computes a solution of a linear equation system.
 * 
 * @author Heinz Kredel
 */
public class MatrixExamples {


    public static void main(String[] argv) {
        example1();
        example2();
        example3();
    }


    @SuppressWarnings("unchecked")
    public static void example1() {
        BigRational r1, r2, r3, r4, r5, r6, z;
        r1 = new BigRational(1, 10);
        r2 = new BigRational(6, 5);
        r3 = new BigRational(1, 9);
        r4 = new BigRational(1, 1);
        r5 = r2.sum(r3);
        r6 = r1.multiply(r4);
        z = new BigRational();

        JLAdapter<BigRational> r1a, r2a, r3a, r4a, r5a, r6a, d, t, za;
        r1a = new JLAdapter<BigRational>(r1);
        r2a = new JLAdapter<BigRational>(r2);
        r3a = new JLAdapter<BigRational>(r3);
        r4a = new JLAdapter<BigRational>(r4);
        r5a = new JLAdapter<BigRational>(r5);
        r6a = new JLAdapter<BigRational>(r6);
        za = new JLAdapter<BigRational>(z);

        Matrix<JLAdapter<BigRational>> a = new Matrix<JLAdapter<BigRational>>(new JLAdapter[][] {
                { r1a, r2a, r3a }, { r4a, r5a, r6a } });

        System.out.println("system = " + a);

        Vector<JLAdapter<BigRational>> b = new Vector<JLAdapter<BigRational>>(new JLAdapter[] { r1a, r2a });
        System.out.println("rhs = " + b);

        Vector<JLAdapter<BigRational>> solution = LinSysSolver.solve(a, b);
        System.out.println("x = " + solution);

        System.out.println("solutionSpace = " + LinSysSolver.solutionSpace(a, b));
        Vector<JLAdapter<BigRational>> bz = new Vector<JLAdapter<BigRational>>(new JLAdapter[] { za, za });
        System.out.println("rhs = " + bz);
        System.out.println("nullSolutionSpace = " + LinSysSolver.solutionSpace(a, bz));

        Matrix<JLAdapter<BigRational>> m = new Matrix<JLAdapter<BigRational>>(new JLAdapter[][] {
                { r1a, r2a, r3a }, { r4a, r5a, r6a }, { r2a, r1a, r3a } });
        System.out.println("matrix = " + m);

        m = m.multiply(m);
        System.out.println("matrix^2 = " + m);

        d = m.det();
        System.out.println("det(matrix^2) = " + d);

        t = m.trace();
        System.out.println("trace(matrix^2) = " + t);

        Polynomial<JLAdapter<BigRational>> cp = m.characteristicPolynomial();
        System.out.println("charPol(matrix^2) = " + cp);

        Polynomial<JLAdapter<BigRational>> mp = m.minimalPolynomial();
        System.out.println("minPol(matrix^2) = " + mp);

        Matrix<JLAdapter<BigRational>> mg = m.gausselim();
        System.out.println("matrix, gauss = " + mg);

        Matrix<JLAdapter<BigRational>> m2 = new Matrix<JLAdapter<BigRational>>(new JLAdapter[][] {
                { r1a, r2a, r3a, r4a }, { r4a, r5a, r6a, r1a }, { r2a, r1a, r3a, r5a } });
        System.out.println("matrix_2 = " + m2);
        Matrix<JLAdapter<BigRational>> mj = m2.gaussjord();
        System.out.println("matrix,gauss-jordan = " + mj);

        try {
            System.out.println("matrix,eigenvalue = " + m.eig());
        } catch (org.jlinalg.InvalidOperationException e) {
            System.out.println("" + e);
        }
    }


    @SuppressWarnings("unchecked")
    public static void example2() {
        BigRational r1, r2, r3, r4, r5, r6, z;
        r1 = new BigRational(1, 10);
        r2 = new BigRational(6, 5);
        r3 = new BigRational(1, 9);
        r4 = new BigRational(1, 1);
        r5 = r2.sum(r3);
        r6 = r1.multiply(r4);
        z = new BigRational();

        JLAdapter<BigRational> d, t;

        Matrix<JLAdapter<BigRational>> a = new Matrix<JLAdapter<BigRational>>(
               JLAdapterUtil.<BigRational>toJLAdapter( 
                   new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 } }
               )
        );
        System.out.println("system = " + a);

        Vector<JLAdapter<BigRational>> b = new Vector<JLAdapter<BigRational>>(
               JLAdapterUtil.<BigRational>toJLAdapter( 
                   new BigRational[] { r1, r2 }
               )
        );
        System.out.println("rhs = " + b);

        Vector<JLAdapter<BigRational>> solution = LinSysSolver.solve(a, b);
        System.out.println("x = " + solution);

        System.out.println("solutionSpace = " + LinSysSolver.solutionSpace(a, b));

        Vector<JLAdapter<BigRational>> bz = new Vector<JLAdapter<BigRational>>(
               JLAdapterUtil.<BigRational>toJLAdapter( 
                   new BigRational[] { z, z }
               )
        );
        System.out.println("rhs = " + bz);
        System.out.println("nullSolutionSpace = " + LinSysSolver.solutionSpace(a, bz));

        Matrix<JLAdapter<BigRational>> m = new Matrix<JLAdapter<BigRational>>(
               JLAdapterUtil.<BigRational>toJLAdapter( 
                   new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 }, { r2, r1, r3 } }
               )
        );
        System.out.println("matrix = " + m);

        m = m.multiply(m);
        System.out.println("matrix^2 = " + m);

        d = m.det();
        System.out.println("det(matrix^2) = " + d);

        t = m.trace();
        System.out.println("trace(matrix^2) = " + t);

        Polynomial<JLAdapter<BigRational>> cp = m.characteristicPolynomial();
        System.out.println("charPol(matrix^2) = " + cp);

        Polynomial<JLAdapter<BigRational>> mp = m.minimalPolynomial();
        System.out.println("minPol(matrix^2) = " + mp);

        Matrix<JLAdapter<BigRational>> mg = m.gausselim();
        System.out.println("matrix, gauss = " + mg);

        Matrix<JLAdapter<BigRational>> m2 = new Matrix<JLAdapter<BigRational>>(
               JLAdapterUtil.<BigRational>toJLAdapter( 
                   new BigRational[][] { { r1, r2, r3, r4 }, { r4, r5, r6, r1 }, { r2, r1, r3, r5 } }
               )
        );
        System.out.println("matrix_2 = " + m2);
        Matrix<JLAdapter<BigRational>> mj = m2.gaussjord();
        System.out.println("matrix,gauss-jordan = " + mj);

        try {
            System.out.println("matrix,eigenvalue = " + m.eig());
        } catch (org.jlinalg.InvalidOperationException e) {
            System.out.println("" + e);
        }
    }


    @SuppressWarnings("unchecked")
    public static void example3() {
        BigRational r1, r2, r3, r4, r5, r6, z, fac;
        r1 = new BigRational(1, 10);
        r2 = new BigRational(6, 5);
        r3 = new BigRational(1, 9);
        r4 = new BigRational(1, 1);
        r5 = r2.sum(r3);
        r6 = r1.multiply(r4);
        z = new BigRational();

        fac = new BigRational();

        JLAdapter<BigRational> d, t;

        BigRational[][] aa  = new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 } };
        GenMatrixRing<BigRational> mfac = new GenMatrixRing<BigRational>(fac,aa.length,aa[0].length);
        GenMatrix<BigRational> a = new GenMatrix<BigRational>(mfac, JLAdapterUtil.<BigRational>toList( aa ) );
        System.out.println("system = " + a);

        BigRational[] ba = new BigRational[] { r1, r2 };
        GenVectorModul<BigRational> vfac = new GenVectorModul<BigRational>(fac,ba.length);
        GenVector<BigRational> b = new GenVector<BigRational>(vfac, JLAdapterUtil.<BigRational>toList( ba ) );
        System.out.println("right hand side = " + b);

        GaussElimination<BigRational> ge = new GaussElimination<BigRational>();
        GenVector<BigRational> x = ge.solve(a, b);
        System.out.println("solution = " + x);

        BigRational[][] am  = new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 }, { r2, r1, r3 } };
        GenMatrixRing<BigRational> mfacm = new GenMatrixRing<BigRational>(fac,am.length,am[0].length);
        GenMatrix<BigRational> m = new GenMatrix<BigRational>(mfacm, JLAdapterUtil.<BigRational>toList( am ) );
        System.out.println("matrix = " + m);
        GenPolynomial<BigRational> p = ge.characteristicPolynomial(m);
        System.out.println("characteristicPolynomial = " + p);

//         m = m.multiply(m);
//         System.out.println("matrix = " + m);
//         p = ge.characteristicPolynomial(m);
//         System.out.println("characteristicPolynomial = " + p);

        m = mfacm.getONE();
        System.out.println("matrix = " + m);
        p = ge.characteristicPolynomial(m);
        System.out.println("characteristicPolynomial = " + p);

        m = mfacm.getZERO();
        System.out.println("matrix = " + m);
        p = ge.characteristicPolynomial(m);
        System.out.println("characteristicPolynomial = " + p);

        m = mfacm.getONE();
        System.out.println("matrix = " + m);
        GenMatrix<BigRational> ns = ge.nullSpace(m);
        System.out.println("nullSpace = " + ns);
        System.out.println("isNullSpace = " + ge.isNullSpace(m,ns));

        am  = new BigRational[][] { { r1, r2, r3 }, { r4, r5, r6 }, { r4, r5, r6 } };
        mfacm = new GenMatrixRing<BigRational>(fac,am.length,am[0].length);
        m = new GenMatrix<BigRational>(mfacm, JLAdapterUtil.<BigRational>toList( am ) );
        System.out.println("matrix = " + m);

        ns = ge.nullSpace(m);
        System.out.println("nullSpace = " + ns);
        System.out.println("isNullSpace = " + ge.isNullSpace(m,ns));
        //System.out.println("isNullSpace = " + ge.isNullSpace(ns,m));
        //System.out.println("isNullSpace = " + ge.isNullSpace(m,ns.transpose(mfacm)));

        m = mfacm.getZERO();
        System.out.println("matrix = " + m);
        ns = ge.nullSpace(m);
        System.out.println("nullSpace = " + ns);
        System.out.println("isNullSpace = " + ge.isNullSpace(m,ns));

        int kl = 3;
        int ll = 10;
        mfac = new GenMatrixRing<BigRational>(fac,ll,ll+1);
        m = mfac.random(kl);
        System.out.println("matrix = " + m);
        m = ge.gaussElimination(m);
        System.out.println("Gauss elimination = " + m);

        m = ge.gaussJordanElimination(m);
        System.out.println("Gauss-Jordan elimination = " + m);

        mfac = new GenMatrixRing<BigRational>(fac,ll,ll);
        m = mfac.random(kl);
        System.out.println("matrix = " + m);
        GenMatrix<BigRational> mi = null;
        try {
            mi = ge.inverse(m);
            System.out.println("inverse = " + mi);
            if ( mi != null ) {
                GenMatrix<BigRational> tt = m.multiply(mi);
                boolean inv = tt.isONE();
                if ( ! inv ) {
                    System.out.println("m * inverse = " + tt);
                }
                System.out.println("isInverse = " + inv);
            }
        } catch (Exception ignored) {
        }
        kl = 3;
        ll = 10; // for ll > 40 must adjust JVM parameters, e.g. -Xms500M -Xmx600M
        mfac = new GenMatrixRing<BigRational>(fac,ll,ll);
        m = mfac.random(kl);
        //System.out.println("matrix = " + m);
        m = ge.gaussElimination(m);
        //System.out.println("Gauss elimination = " + m);

        m = ge.gaussJordanElimination(m);
        //System.out.println("Gauss-Jordan elimination = " + m);
        System.out.println("is Gauss-Jordan elimination = " + m.isONE());
    }

}
