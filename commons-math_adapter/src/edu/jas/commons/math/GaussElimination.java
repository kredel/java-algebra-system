package edu.jas.commons.math;

import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;

import edu.jas.structure.RingElem;
import edu.jas.vector.GenMatrix;
import edu.jas.vector.GenVector;

/**
 * Algorithms related to Gaussian elimination. Conversion to commons-math classes and
 * delegation to commons-math algorithms.
 * 
 * @param <C>
 *          ring element type
 */
public class GaussElimination<C extends RingElem<C>> {


  /**
   * Determinant of a matrix.
   * 
   * @param a
   *          matrix
   * @return determinant of a
   */
  public C determinant(GenMatrix<C> a) {
    FieldMatrix<CMFieldElement<C>> am = CMFieldElementUtil.<C> toCMFieldMatrix(a);

    final FieldLUDecomposition<CMFieldElement<C>> lu = new FieldLUDecomposition<CMFieldElement<C>>(am);
    CMFieldElement<C> dm = lu.getDeterminant();
    C d = dm.val;
    return d;
  }


  /**
   * Inverse of a matrix.
   * 
   * @param a
   *          matrix
   * @return inverse matrix of a
   */
  public GenMatrix<C> inverse(GenMatrix<C> a) {
    FieldMatrix<CMFieldElement<C>> am = CMFieldElementUtil.<C> toCMFieldMatrix(a);
    final FieldLUDecomposition<CMFieldElement<C>> lu = new FieldLUDecomposition<CMFieldElement<C>>(am);
    FieldDecompositionSolver<CMFieldElement<C>> fds = lu.getSolver();
    FieldMatrix<CMFieldElement<C>> bm = fds.getInverse();
    GenMatrix<C> g = CMFieldElementUtil.<C> matrixFromCMFieldMatrix(a.ring, bm);
    return g;
  }


  /**
   * Test if n is a null space for the linear system: a n = 0.
   * 
   * @param a
   *          matrix
   * @param n
   *          matrix
   * @return true, if n is a nullspace of a, else false
   */
  public boolean isNullSpace(GenMatrix<C> a, GenMatrix<C> n) {
    GenMatrix<C> z = a.multiply(n); // .transpose(n.ring) better not transpose
    // here
    // System.out.println("z = " + z);
    return z.isZERO();
  }


  /**
   * Solve a linear system: a x = b.
   * 
   * @param a
   *          matrix
   * @param b
   *          vector of right hand side
   * @return a solution vector x
   */
  public GenVector<C> solve(GenMatrix<C> a, GenVector<C> b) {
    FieldMatrix<CMFieldElement<C>> am = CMFieldElementUtil.<C> toCMFieldMatrix(a);
    FieldVector<CMFieldElement<C>> bv = CMFieldElementUtil.<C> toCMFieldElementVector(b);

    final FieldLUDecomposition<CMFieldElement<C>> lu = new FieldLUDecomposition<CMFieldElement<C>>(am);
    FieldDecompositionSolver<CMFieldElement<C>> fds = lu.getSolver();
    FieldVector<CMFieldElement<C>> xv = fds.solve(bv);
    GenVector<C> xa = CMFieldElementUtil.<C> vectorFromCMFieldVector(b.modul, xv);
    return xa;
  }


  /**
   * Trace of a matrix.
   * 
   * @param a
   *          matrix
   * @return trace of a
   */
  public C trace(GenMatrix<C> a) {
    FieldMatrix<CMFieldElement<C>> am = CMFieldElementUtil.<C> toCMFieldMatrix(a);
    CMFieldElement<C> dm = am.getTrace();
    C d = dm.val;
    return d;
  }

}
