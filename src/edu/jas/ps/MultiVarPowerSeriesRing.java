/*
 * $Id$
 */

package edu.jas.ps;


import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.jas.kern.PrettyPrint;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.ExpVector;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Multivariate power series ring implementation. Uses lazy evaluated generating
 * function for coefficients.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class MultiVarPowerSeriesRing<C extends RingElem<C>> implements RingFactory<MultiVarPowerSeries<C>> {


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random();


    /**
     * Default truncate.
     */
    public final static int DEFAULT_TRUNCATE = 7;


    /**
     * Truncate.
     */
    int truncate;


    /**
     * Default variable name.
     */
    //public final static String[] DEFAULT_NAMES = new String[] { "x" };


    /**
     * Zero ExpVector.
     */
    public final ExpVector EVZERO;


    /**
     * Coefficient ring factory.
     */
    public final RingFactory<C> coFac;


    /**
     * The number of variables.
     */
    public final int nvar;


    /**
     * The names of the variables. This value can be modified.
     */
    protected String[] vars;


    /**
     * The constant power series 1 for this ring.
     */
    public final MultiVarPowerSeries<C> ONE;


    /**
     * The constant power series 0 for this ring.
     */
    public final MultiVarPowerSeries<C> ZERO;


    /**
     * No argument constructor.
     */
    private MultiVarPowerSeriesRing() {
        throw new IllegalArgumentException("do not use no-argument constructor");
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> coFac, int nv) {
        this(coFac, nv, DEFAULT_TRUNCATE);
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     * @param truncate index of truncation.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> coFac, int nv, int truncate) {
        this(coFac, nv, truncate, null);
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     * @param name of the variable.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> coFac, String[] names) {
        this(coFac, names.length, DEFAULT_TRUNCATE, names);
    }


    /**
     * Constructor.
     * @param cofac coefficient ring factory.
     * @param truncate index of truncation.
     * @param name of the variable.
     */
    public MultiVarPowerSeriesRing(RingFactory<C> cofac, int nv, int truncate, String[] names) {
        this.coFac = cofac;
        this.nvar = nv;
        this.truncate = truncate;
        this.vars = names;
        if (vars == null && PrettyPrint.isTrue()) {
            vars = GenPolynomialRing.newVars("x", nvar);
        } else {
            if (vars.length != nvar) {
                throw new IllegalArgumentException("incompatible variable size " + vars.length + ", " + nvar);
            }
            GenPolynomialRing.addVars(vars);
        }
	EVZERO = ExpVector.create(nvar);
        this.ONE = new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {
            @Override
            public C generate(ExpVector i) {
                if (i.isZERO()) {
                    return coFac.getONE();
                } else {
                    return coFac.getZERO();
                }
            }
        });
        this.ZERO = new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {
            @Override
            public C generate(ExpVector i) {
                return coFac.getZERO();
            }
        });
    }


    /**
     * Fixed point construction.
     * @param map a mapping of power series.
     * @return fix point wrt map.
     */
    // Cannot be a static method because a power series ring is required.
    public MultiVarPowerSeries<C> fixPoint(PowerSeriesMap<C> map) {
        MultiVarPowerSeries<C> ps1 = new MultiVarPowerSeries<C>(this);
        MultiVarPowerSeries<C> ps2 = null; //map.map(ps1);
        ps1.lazyCoeffs = ps2.lazyCoeffs;
        return ps2;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String scf = coFac.getClass().getSimpleName();
        sb.append(scf + "((" + varsToString() + "))");
        return sb.toString();
    }


    /**
     * Get a String representation of the variable names.
     * @return names seperated by commas.
     */
    public String varsToString() {
        String s = "";
        if (vars == null) {
            return s + "#" + nvar;
        }
        for (int i = 0; i < vars.length; i++) {
            if (i != 0) {
                s += ", ";
            }
            s += vars[i];
        }
        return s;
    }

    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("MPS(");
        String f = null;
        try {
            f = ((RingElem<C>) coFac).toScriptFactory(); // sic
        } catch (Exception e) {
            f = coFac.toScript();
        }
        s.append(f + ",\"" + varsToString() + "\"," + truncate + ")");
        return s.toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof MultiVarPowerSeriesRing)) {
            return false;
        }
        MultiVarPowerSeriesRing<C> a = null;
        try {
            a = (MultiVarPowerSeriesRing<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (Arrays.equals(vars,a.vars)) {
            return true;
        }
        return false;
    }


    /**
     * Hash code for this .
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 0;
        h = (Arrays.hashCode(vars) << 27);
        h += truncate;
        return h;
    }


    /**
     * Get the zero element.
     * @return 0 as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as MultiVarPowerSeries<C>.
     */
    public MultiVarPowerSeries<C> getONE() {
        return ONE;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<MultiVarPowerSeries<C>> generators() {
        List<C> rgens = coFac.generators();
        List<MultiVarPowerSeries<C>> gens = new ArrayList<MultiVarPowerSeries<C>>(rgens.size());
        for (final C cg : rgens) {
            MultiVarPowerSeries<C> g = new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {
                @Override
                public C generate(ExpVector i) {
                    if (i.isZERO()) {
                        return cg;
                    } else {
                        return coFac.getZERO();
                    }
                }
            });
            gens.add(g);
        }
        //gens.add(ONE.shift(1));
        return gens;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }


    /*
     * Solve an ordinary differential equation. y' = f(y) with y(0) = c.
     * @param f a MultiVarPowerSeries<C>.
     * @param c integration constant.
     * @return f.integrate(c).
    public MultiVarPowerSeries<C> solveODE(final MultiVarPowerSeries<C> f, final C c) {
        return f.integrate(c);
    }
     */


    /**
     * Is commuative.
     * @return true, if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return coFac.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return coFac.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return false.
     */
    public boolean isField() {
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return coFac.characteristic();
    }


    /**
     * Get a (constant) MultiVarPowerSeries&lt;C&gt; from a long value.
     * @param a long.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromInteger(long a) {
        return ONE.multiply(coFac.fromInteger(a));
    }


    /**
     * Get a (constant) MultiVarPowerSeries&lt;C&gt; from a java.math.BigInteger.
     * @param a BigInteger.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromInteger(java.math.BigInteger a) {
        return ONE.multiply(coFac.fromInteger(a));
    }


    /**
     * Get the corresponding GenPolynomialRing&lt;C&gt;.
     * @return GenPolynomialRing&lt;C&gt;.
     */
    public GenPolynomialRing<C> polyRing() {
        return new GenPolynomialRing<C>(coFac,nvar,vars);
    }


    /**
     * Get a MultiVarPowerSeries&lt;C&gt; from a GenPolynomial&lt;C&gt;.
     * @param a GenPolynomial&lt;C&gt;.
     * @return a MultiVarPowerSeries&lt;C&gt;.
     */
    public MultiVarPowerSeries<C> fromPolynomial(GenPolynomial<C> a) {
        if (a == null || a.isZERO()) {
            return ZERO;
        }
        if (a.isONE()) {
            return ONE;
        }
        GenPolynomialRing<C> pfac = polyRing();
        HashMap<Long, GenPolynomial<C>> cache = new HashMap<Long, GenPolynomial<C>>();
        for (Monomial<C> m : a) {
            ExpVector e = m.exponent();
            long t = e.totalDeg();
            GenPolynomial<C> p = cache.get(t);
            if ( p == null ) {
                p = pfac.getZERO().clone();
                cache.put(t, p);
	    }
            p.doPutToMap(e,m.coefficient());
        }

        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(pfac,cache) {
            @Override
            public C generate(ExpVector e) {
                // cached coefficients returned by get
                return coFac.getZERO();
            }
        });
    }


    /**
     * Generate a random power series with k = 5, d = 0.7.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random() {
        return random(5, 0.7f, random);
    }


    /**
     * Generate a random power series with d = 0.7.
     * @param k bitsize of random coefficients.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(int k) {
        return random(k, 0.7f, random);
    }


    /**
     * Generate a random power series with d = 0.7.
     * @param k bitsize of random coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(int k, Random rnd) {
        return random(k, 0.7f, rnd);
    }


    /**
     * Generate a random power series.
     * @param k bitsize of random coefficients.
     * @param d density of non-zero coefficients.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(int k, float d) {
        return random(k, d, random);
    }


    /**
     * Generate a random power series.
     * @param k bitsize of random coefficients.
     * @param d density of non-zero coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public MultiVarPowerSeries<C> random(final int k, final float d, final Random rnd) {
        return new MultiVarPowerSeries<C>(this, new MultiVarCoefficients<C>(this) {
            @Override
            public C generate(ExpVector i) {
                // cached coefficients returned by get
                C c;
                float f = rnd.nextFloat();
                if (f < d) {
                    c = coFac.random(k, rnd);
                } else {
                    c = coFac.getZERO();
                }
                return c;
            }
        });
    }


    /**
     * Copy power series.
     * @param c a power series.
     * @return a copy of c.
     */
    public MultiVarPowerSeries<C> copy(MultiVarPowerSeries<C> c) {
        return new MultiVarPowerSeries<C>(this, c.lazyCoeffs);
    }


    /**
     * Parse a power series. <b>Note:</b> not implemented.
     * @param s String.
     * @return power series from s.
     */
    public MultiVarPowerSeries<C> parse(String s) {
        throw new UnsupportedOperationException("parse for power series not implemented");
    }


    /**
     * Parse a power series. <b>Note:</b> not implemented.
     * @param r Reader.
     * @return next power series from r.
     */
    public MultiVarPowerSeries<C> parse(Reader r) {
        throw new UnsupportedOperationException("parse for power series not implemented");
    }

}
