package edu.jas.kern;

import edu.jas.ufd.FactorAbstract;

public class JASConfig {
  /**
   * {@link FactorAbstract#factorsSquarefreeKronecker(edu.jas.poly.GenPolynomial)} will throw an
   * {@link ArithmeticException}, if this parameter is greater than <code>0</code> and the Kronecker
   * substitution is greater than this value.
   */
  public static int MAX_DEGREE_KRONECKER_FACTORIZATION = -1;

  /**
   * {@link FactorAbstract#factorsSquarefreeKronecker(edu.jas.poly.GenPolynomial)} will throw an
   * {@link ArithmeticException}, if this parameter is greater than <code>0</code> and the Kronecker
   * iteration counter is greater than this value.
   */
  public static int MAX_ITERATIONS_KRONECKER_FACTORIZATION = -1;
}
