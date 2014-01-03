/*
 * $Id$
 */

package edu.jas.jlinalg;


import java.util.ArrayList;

import org.jlinalg.AffineLinearSubspace;
import org.jlinalg.LinSysSolver;
import org.jlinalg.Matrix;
import org.jlinalg.Vector;
import org.jlinalg.polynomial.Polynomial;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.RingElem;
import edu.jas.vector.GenVector;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenVectorModul;
import edu.jas.vector.GenMatrixRing;


/**
 * Algorithms related to Gaussian elimination. Conversion to JLinAlg classes and
 * delegation to JLinAlg algorithms.
 * 
 * @author Heinz Kredel
 */
public class GaussElimination<C extends RingElem<C>> {


    /**
     * Solve a linear system: a x = b.
     * @param <C> ring element type
     * @param a matrix
     * @param b vector of right hand side
     * @return a solution vector x
     */
    public GenVector<C> solve(GenMatrix<C> a, GenVector<C> b) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        Vector<JLAdapter<C>> bv = JLAdapterUtil.<C> toJLAdapterVector(b);

        Vector<JLAdapter<C>> xv = LinSysSolver.solve(am, bv);

        GenVector<C> xa = JLAdapterUtil.<C> vectorFromJLAdapter(b.modul, xv);
        return xa;
    }


    /**
     * Null space, generating system of solutions of a linear system: a x = 0.
     * @param <C> ring element type
     * @param a matrix
     * @return matrix of generating system of solution vectors x
     */
    public GenMatrix<C> nullSpace(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);

        GenVectorModul<C> vfac = new GenVectorModul<C>(a.ring.coFac, a.ring.cols);
        Vector<JLAdapter<C>> bv = JLAdapterUtil.<C> toJLAdapterVector(vfac.getZERO());

        ArrayList<ArrayList<C>> nsl = null;
        int dim = 0;
        try {
            AffineLinearSubspace<JLAdapter<C>> ss = LinSysSolver.solutionSpace(am, bv);
            //System.out.println("ss = " + ss);
            try {
                ss = ss.normalize();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            Vector<JLAdapter<C>>[] nsa = ss.getGeneratingSystem();

            dim = nsa.length;
            nsl = new ArrayList<ArrayList<C>>(nsa.length);
            for (int i = 0; i < nsa.length; i++) {
                nsl.add(JLAdapterUtil.<C> listFromJLAdapter(nsa[i]));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            nsl = new ArrayList<ArrayList<C>>();
        }
        GenMatrixRing<C> nr;
        if (dim > 0) {
            nr = new GenMatrixRing<C>(a.ring.coFac, dim, a.ring.cols);
        } else {
            nr = new GenMatrixRing<C>(a.ring.coFac, a.ring.rows, a.ring.cols);
        }
        GenMatrix<C> ns = new GenMatrix<C>(nr, nsl);
        if (dim > 0) {
            nr = nr.transpose();
            ns = ns.transpose(nr); // column vectors
        }
        return ns;
    }


    /**
     * Test if n is a null space for the linear system: a n = 0.
     * @param <C> ring element type
     * @param a matrix
     * @param n matrix
     * @return true, if n is a nullspace of a, else false
     */
    public boolean isNullSpace(GenMatrix<C> a, GenMatrix<C> n) {
        GenMatrix<C> z = a.multiply(n); // .transpose(n.ring) better not transpose here
        //System.out.println("z = " + z);
        return z.isZERO();
    }


    /**
     * Charactersitic polynomial of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return charactersitic polynomial of a
     */
    public GenPolynomial<C> characteristicPolynomial(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);

        Polynomial<JLAdapter<C>> p = am.characteristicPolynomial();

        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(a.ring.coFac, new String[] { "x" });
        GenPolynomial<C> cp = pfac.parse(p.toString());
        return cp;
    }


    /**
     * Determinant of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return determinant of a
     */
    public C determinant(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        JLAdapter<C> dm = am.det();
        C d = dm.val;
        return d;
    }


    /**
     * Trace of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return trace of a
     */
    public C trace(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        JLAdapter<C> dm = am.trace();
        C d = dm.val;
        return d;
    }


    /**
     * Rank of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return rank of a
     */
    public int rank(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        int r = am.rank();
        return r;
    }


    /**
     * Gauss elimination of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return Gauss elimination of a
     */
    public GenMatrix<C> gaussElimination(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        Matrix<JLAdapter<C>> bm = am.gausselim();
        GenMatrix<C> g = JLAdapterUtil.<C> matrixFromJLAdapter(a.ring, bm);
        return g;
    }


    /**
     * Gauss-Jordan elimination of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return Gauss-Jordan elimination of a
     */
    public GenMatrix<C> gaussJordanElimination(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        Matrix<JLAdapter<C>> bm = am.gaussjord();
        GenMatrix<C> g = JLAdapterUtil.<C> matrixFromJLAdapter(a.ring, bm);
        return g;
    }


    /**
     * Inverse of a matrix.
     * @param <C> ring element type
     * @param a matrix
     * @return inverse matrix of a
     */
    public GenMatrix<C> inverse(GenMatrix<C> a) {
        Matrix<JLAdapter<C>> am = JLAdapterUtil.<C> toJLAdapterMatrix(a);
        Matrix<JLAdapter<C>> bm = am.inverse();
        GenMatrix<C> g = JLAdapterUtil.<C> matrixFromJLAdapter(a.ring, bm);
        return g;
    }

}
