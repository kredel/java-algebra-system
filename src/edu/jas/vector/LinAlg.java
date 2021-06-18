/*
 * $Id$
 */

package edu.jas.vector;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager; 

import edu.jas.structure.RingElem;


/**
 * Linear algebra methods. Implements linear algebra computations and
 * tests, mainly based on Gauss elimination.  Partly based on
 * <a href="https://en.wikipedia.org/wiki/LU_decomposition">LU_decomposition</a>
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class LinAlg<C extends RingElem<C>> implements Serializable {


    private static final Logger logger = LogManager.getLogger(LinAlg.class);


    //private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public LinAlg() {
    }


    /**
     * Matrix LU decomposition. Matrix A is replaced by its LU decomposition.
     * A contains a copy of both matrices L-E and U as A=(L-E)+U such that P*A=L*U.
     * The permutation matrix is not stored as a matrix, but in an integer vector P of size N+1 
     * containing column indexes where the permutation matrix has "1". The last element P[N]=S+N, 
     * where S is the number of row exchanges needed for determinant computation, det(P)=(-1)^S    
     * @param A a n&times;n matrix.
     * @return permutation vector P and modified matrix A.
     */
    public List<Integer> decompositionLU(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        if (N != ring.cols) {
            logger.warn("nosquare matrix");
        }
        int i, imax; 
        C maxA, absA;
        List<Integer> P = new ArrayList<Integer>(N+1);
        for (i = 0; i <= N; i++) {
            P.add(i); //Unit permutation matrix, P[N] initialized with N
        }
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (i = 0; i < N; i++) {
            imax = i;
            maxA = ring.coFac.getZERO();
            for (int k = i; k < N; k++) {
                // absA = fabs(A[k][i])
                absA = mat.get(k).get(i).abs();
                if (absA.compareTo(maxA) > 0) { 
                    maxA = absA;
                    imax = k;
                }
            }
            if (maxA.isZERO()) {
                logger.warn("matrix is degenerate at col " + i);
                mat.get(i).set(i, maxA);
                continue;
                //P.clear();
                //return P; //failure, matrix is degenerate
            }
            if (imax != i) {
                //pivoting P
                int j = P.get(i);
                P.set(i, P.get(imax));
                P.set(imax, j);
                //System.out.println("new pivot " + imax); // + ", P = " + P);
                //pivoting rows of A
                ArrayList<C> ptr = mat.get(i);
                mat.set(i, mat.get(imax));
                mat.set(imax, ptr);
                //counting pivots starting from N (for determinant)
                P.set(N, P.get(N)+1);
            }
            //System.out.println("A(i," + i + ") = " + mat.get(i).get(i));
            for (int j = i + 1; j < N; j++) {
                // A[j][i] /= A[i][i];
                C d = mat.get(j).get(i).divide( mat.get(i).get(i) );
                mat.get(j).set(i, d );

                for (int k = i + 1; k < N; k++) {
                    // A[j][k] -= A[j][i] * A[i][k];
                    C a = mat.get(j).get(i).multiply( mat.get(i).get(k) );
                    mat.get(j).set(k, mat.get(j).get(k).subtract(a) );
                }
            }
        }
        return P;
    }


    /**
     * Solve with LU decomposition. 
     * @param A a n&times;n matrix in LU decomposition.
     * @param P permutation vector.
     * @param b right hand side vector.
     * @return x solution vector of A*x = b.
     */
    public GenVector<C> solveLU(GenMatrix<C> A, List<Integer> P, GenVector<C> b) {
        if (A == null || b == null) {
            return null;
        }
        if (P.size() == 0) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = P.size() - 1;
        GenVectorModul<C> xfac = new GenVectorModul<C>(ring.coFac, N);
        GenVector<C> x = new GenVector<C>(xfac);
        List<C> vec = x.val;
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < N; i++) {
            //x[i] = b[P[i]];
            vec.set(i, b.get( P.get(i) ) );
            C xi = vec.get(i);
            for (int k = 0; k < i; k++) {
                //x[i] -= A[i][k] * x[k];
                C ax = mat.get(i).get(k).multiply(vec.get(k));
                xi = xi.subtract(ax);
            }
            vec.set(i, xi);
        }
        //System.out.println("vec = " + vec);
        for (int i = N - 1; i >= 0; i--) {
            C xi = vec.get(i);
            for (int k = i + 1; k < N; k++) {
                //x[i] -= A[i][k] * x[k];
                C ax = mat.get(i).get(k).multiply(vec.get(k));
                xi = xi.subtract(ax);
            }
            vec.set(i, xi);
            //x[i] /= A[i][i];
            vec.set(i, xi.divide(mat.get(i).get(i)));
        }
        return x;
    }


    /**
     * Solve linear system of equations. 
     * @param A a n&times;n matrix.
     * @param b right hand side vector.
     * @return x solution vector of A*x = b.
     */
    public GenVector<C> solve(GenMatrix<C> A, GenVector<C> b) {
        if (A == null || b == null) {
            return null;
        }
        GenMatrix<C> Ap = A.copy();
        List<Integer> P = decompositionLU(Ap);
        //System.out.println("P = " + P);
        //System.out.println("Ap = " + Ap);
        if (P.size() == 0) {
            System.out.println("undecomposable");
            return b.modul.getZERO();
        }
        GenVector<C> x = solveLU(Ap, P, b);
        return x;
    }


    /**
     * Determinant with LU decomposition. 
     * @param A a n&times;n matrix in LU decomposition.
     * @param P permutation vector.
     * @return d determinant of A.
     */
    public C determinantLU(GenMatrix<C> A, List<Integer> P) {
        if (A == null) {
            return null;
        }
        if (P.size() == 0) {
            return A.ring.coFac.getZERO();
        }
        int N = P.size() - 1;
        ArrayList<ArrayList<C>> mat = A.matrix;
        // det = A[0][0];
        C det = mat.get(0).get(0);
        for (int i = 1; i < N; i++) {
            //det *= A[i][i];
            det = det.multiply( mat.get(i).get(i) );
        }
        //return (P[N] - N) % 2 == 0 ? det : -det
        int s = P.get( N ) - N;
        if (s % 2 != 0) {
            det = det.negate();
        }
        return det;
    }


    /**
     * Rank from LU decomposition.
     * @param A a n&times;n matrix in LU decomposition.
     * @return r rank of A.
     */
    public long rankLU(GenMatrix<C> A) {
        long r = 0;
        if (A == null) {
            return -1l;
        }
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < mat.size(); i++) {
            // A[i][i] == 0?;
            if ( !mat.get(i).get(i).isZERO() ) {
                r++;
            }
        }
        return r;
    }


    /**
     * Inverse with LU decomposition. 
     * @param A a n&times;n matrix in LU decomposition.
     * @param P permutation vector.
     * @return inv(A) with A * inv(A) == 1.
     */
    public GenMatrix<C> inverseLU(GenMatrix<C> A, List<Integer> P) {
        GenMatrixRing<C> ring = A.ring;
        GenMatrix<C> inv = new GenMatrix<C>(ring);
        int N = P.size() - 1;
        ArrayList<ArrayList<C>> mat = A.matrix;
        ArrayList<ArrayList<C>> imat = inv.matrix;
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                //IA[i][j] = P[i] == j ? 1.0 : 0.0;
                C e = (P.get(i) == j) ? ring.coFac.getONE() : ring.coFac.getZERO();
                imat.get(i).set(j, e);
                C b = e; //imat.get(i).get(j);
                for (int k = 0; k < i; k++) {
                    //IA[i][j] -= A[i][k] * IA[k][j];
                    C a = mat.get(i).get(k).multiply( imat.get(k).get(j) );
                    b = b.subtract(a); 
                }
                imat.get(i).set(j, b);
            }
            for (int i = N - 1; i >= 0; i--) {
                C b = imat.get(i).get(j);
                for (int k = i + 1; k < N; k++) {
                    //IA[i][j] -= A[i][k] * IA[k][j];
                    C a = mat.get(i).get(k).multiply( imat.get(k).get(j) );
                    b = b.subtract(a); 
                }
                imat.get(i).set(j, b);
                //IA[i][j] /= A[i][i];
                C e = b; //imat.get(i).get(j);
                e = e.divide( mat.get(i).get(i) );
                imat.get(i).set(j, e);
            }
        }
        return inv;
    }


    /**
     * Matrix Null Space basis, cokernel.
     * @param A a n&times;n matrix.
     * @return V a list of vectors (v_1, ..., v_k) with v_i*A == 0.
     */
    public List<GenVector<C>> nullSpaceBasis(GenMatrix<C> A) {
        if (A == null) {
            return null;
        }
        GenMatrixRing<C> ring = A.ring;
        int N = ring.rows;
        if (N != ring.cols) {
            logger.warn("nosquare matrix");
        }
        List<GenVector<C>> nspb = new ArrayList<GenVector<C>>();
        GenVectorModul<C> vfac = new GenVectorModul<C>(ring.coFac, N);
        ArrayList<ArrayList<C>> mat = A.matrix;
        for (int i = 0; i < N; i++) {
            C maxA, absA;
            // search privot imax
            int imax = i;
            maxA = ring.coFac.getZERO();
            for (int k = i; k < N; k++) { // k = 0 ?
                // absA = fabs(A[k][i])
                absA = mat.get(i).get(k).abs();
                if (absA.compareTo(maxA) > 0 && maxA.isZERO()) {
                    maxA = absA;
                    imax = k;
                }
            }
            logger.info("pivot: " + imax + ", i = " + i + ", maxA = " + maxA);
            if (maxA.isZERO()) {
                // check for complete zero row or left pivot
                int imaxl = i;
                for (int k = 0; k < i; k++) { // k = 0 ?
                    // absA = fabs(A[k][i])
                    absA = mat.get(i).get(k).abs();
                    if (absA.compareTo(maxA) > 0) { // first or last imax: && maxA.isZERO()
                        imaxl = k;
                        // check if upper triangular column is zero
                        boolean iszero = true;
                        for (int m = 0; m < i; m++) {
                            C amm = mat.get(m).get(imaxl).abs();
                            if (!amm.isZERO()) {
                                iszero = false;
				break;
                            }
                        }
                        if (iszero) { // left pivot okay
                            imax = imaxl;
                            //System.out.println("col(" + imax + ") = " + A.getColumn(imax));
                            logger.info("pivot*: " + imax + ", i = " + i + ", absA = " + absA);
                            maxA = ring.coFac.getONE();
                        }
                    }
                }
                if (maxA.isZERO()) { // complete zero row
                    continue; 
                }
            }
            if (imax < N) { //!= i
                //normalize column i
                C mp = mat.get(i).get(imax).inverse();
                for (int k = 0; k < N; k++) { // k = i ?
                    C b = mat.get(k).get(imax);
                    b = b.multiply(mp);
                    mat.get(k).set(imax, b);
                }
                //pivoting columns of A
                if (imax != i) {
                    for (int k = 0; k < N; k++) {
                        C b = mat.get(k).get(i);
                        mat.get(k).set(i, mat.get(k).get(imax));
                        mat.get(k).set(imax, b);
                    }
                }
                //eliminate rest of row i via column operations
                for (int j = 0; j < N; j++) {
                    if (i == j) { // is already normalized
                        continue;
                    }
                    C mm = mat.get(i).get(j);
                    for (int k = 0; k < N; k++) { // or k = 0
                        C b = mat.get(k).get(j);
                        C c = mat.get(k).get(i);
                        C d = b.subtract( c.multiply(mm) );
                        mat.get(k).set(j, d);
                    }
                }
            }
            if (N < 10) System.out.println("mat = " + A);
        }
        // convert to A-I
        for (int i = 0; i < N; i++) {
            C b = mat.get(i).get(i);
            b = b.subtract(ring.coFac.getONE());
            mat.get(i).set(i, b);
        }
        //System.out.println("mat-1 = " + A);
        // read off non zero rows of A
        for (int i = 0; i < N; i++) {
            List<C> row = mat.get(i);
            boolean iszero = true;
            for (int k = 0; k < N; k++) {
                if ( !row.get(k).isZERO() ) {
                    iszero = false;
                    break;
                }
            }
            if (!iszero) {
                GenVector<C> v = new GenVector<C>(vfac, row);
                nspb.add( v );
            }
        }
        return nspb;
    }

}
