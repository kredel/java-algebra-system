/*
 * $Id$
 */

package edu.jas.arith;


import java.io.Reader;
// import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import edu.jas.kern.StringUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.StarRingElem;


/**
 * Integer BigQuaternion class based on BigRational implementing the RingElem interface
 * and with the familiar MAS static method names. Objects of this class are
 * immutable. The integer quaternion methods are implemented after
 * https://de.wikipedia.org/wiki/Hurwitzquaternion see also
 * https://en.wikipedia.org/wiki/Hurwitz_quaternion
 * @author Heinz Kredel
 */

public final class BigQuaternionInteger extends BigQuaternion 
       // implements StarRingElem<BigQuaternion>, GcdRingElem<BigQuaternion>, RingFactory<BigQuaternion> 
  {


    /**
     * List of all 24 integral units.
     */
    public static List<BigQuaternion> entierUnits = null; //later: unitsOfHurwitzian();


    /*
     * Flag to signal that random elements should be entier.
     */
    //protected boolean entierRandoms = true;


    private static final Logger logger = Logger.getLogger(BigQuaternionInteger.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     * @param k BigRational.
     */
    public BigQuaternionInteger(BigRational r, BigRational i, BigRational j, BigRational k) {
        super(r,i,j,k);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     * @param j BigRational.
     */
    public BigQuaternionInteger(BigRational r, BigRational i, BigRational j) {
        this(r, i, j, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     * @param i BigRational.
     */
    public BigQuaternionInteger(BigRational r, BigRational i) {
        this(r, i, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigRationals.
     * @param r BigRational.
     */
    public BigQuaternionInteger(BigRational r) {
        this(r, BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion from BigComplex.
     * @param r BigComplex.
     */
    public BigQuaternionInteger(BigComplex r) {
        this(r.re, r.im);
    }


    /**
     * Constructor for a BigQuaternion from long.
     * @param r long.
     */
    public BigQuaternionInteger(long r) {
        this(new BigRational(r), BigRational.ZERO);
    }


    /**
     * Constructor for a BigQuaternion with no arguments.
     */
    public BigQuaternionInteger() {
        this(BigRational.ZERO);
    }


    /**
     * The BigQuaternion string constructor accepts the following formats: empty
     * string, "rational", or "rat i rat j rat k rat" with no blanks around i, j
     * or k if used as polynoial coefficient.
     * @param s String.
     * @throws NumberFormatException
     */
    public BigQuaternionInteger(String s) throws NumberFormatException {
        super(s);
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public BigQuaternionInteger factory() {
        return this;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<BigQuaternion> generators() {
        return super.generators();
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public BigQuaternionInteger copy() {
        return new BigQuaternionInteger(re, im, jm, km);
    }


    /**
     * Copy BigQuaternion element c.
     * @param c BigQuaternion.
     * @return a copy of c.
     */
    public BigQuaternionInteger copy(BigQuaternion c) {
        return new BigQuaternionInteger(c.re, c.im, c.jm, c.km);
    }


    /**
     * Get the zero element.
     * @return 0 as BigQuaternion.
     */
    public BigQuaternionInteger getZERO() {
        return copy(ZERO);
    }


    /**
     * Get the one element.
     * @return q as BigQuaternion.
     */
    public BigQuaternionInteger getONE() {
        return copy(ONE);
    }


    /**
     * Query if this ring is commutative.
     * @return false.
     */
    public boolean isCommutative() {
        return false;
    }


    /**
     * Query if this ring is associative.
     * @return true.
     */
    public boolean isAssociative() {
        return true;
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
        return java.math.BigInteger.ZERO;
    }


    /**
     * Get a BigQuaternion element from a BigInteger.
     * @param a BigInteger.
     * @return a BigQuaternion.
     */
    public BigQuaternionInteger fromInteger(java.math.BigInteger a) {
        return new BigQuaternionInteger(new BigRational(a));
    }


    /**
     * Get a BigQuaternion element from a long.
     * @param a long.
     * @return a BigQuaternion.
     */
    public BigQuaternionInteger fromInteger(long a) {
        return new BigQuaternionInteger(new BigRational(a));
    }


    /**
     * Get a BigQuaternion element from a long vector.
     * @param a long vector.
     * @return a BigQuaternion.
     */
    public BigQuaternionInteger fromInteger(long[] a) {
        return new BigQuaternionInteger(new BigRational(a[0]), new BigRational(a[1]), 
                                        new BigRational(a[2]), new BigRational(a[3]));
    }


    /**
     * Is BigQuaternion unit element.
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        return !isZERO() && norm().getRe().abs().isONE();
    }


    /* arithmetic operations: +, -, -
     */


    /* arithmetic operations: *, inverse, / 
     */


    /**
     * Quaternion number inverse.
     * @param A is a non-zero quaternion number.
     * @return S with S * A = A * S = 1.
     */
    public static BigQuaternion QINV(BigQuaternion A) {
        if (A == null)
            return null;
        return A.inverse();
    }


    /**
     * BigQuaternion inverse.
     * @return S with S * this = this * S = 1.
     * @see edu.jas.structure.RingElem#inverse()
     */
    public BigQuaternion inverse() {
        if (!isUnit()) {
            throw new ArithmeticException("not invertible: " + this);
        }
        return super.inverse();
    }


    /**
     * BigQuaternion remainder.
     * @param S BigQuaternion.
     * @return this - this * b**(-1).
     */
    public BigQuaternion remainder(BigQuaternion S) {
        return rightRemainder(S);
    }


    /**
     * Quaternion number quotient.
     * @param A BigQuaternion.
     * @param B BigQuaternion.
     * @return R * B**(-1).
     */
    public static BigQuaternion QQ(BigQuaternion A, BigQuaternion B) {
        if (A == null)
            return null;
        return A.divide(B);
    }


    /**
     * BigQuaternion right divide.
     * @param b BigQuaternion.
     * @return this * b**(-1).
     */
    public BigQuaternion divide(BigQuaternion b) {
        return rightDivide(b);
    }


    /**
     * BigQuaternion right divide.
     * @param b BigQuaternion.
     * @return this * b**(-1).
     */
    @Override
    public BigQuaternion rightDivide(BigQuaternion b) {
        return rightQuotientAndRemainder(b)[0];
    }


    /**
     * BigQuaternion left divide.
     * @param b BigQuaternion.
     * @return b**(-1) * this.
     */
    @Override
    public BigQuaternion leftDivide(BigQuaternion b) {
        return leftQuotientAndRemainder(b)[0];
    }


    /**
     * BigQuaternion divide.
     * @param b BigRational.
     * @return this/b.
     */
    public BigQuaternion divide(BigRational b) {
        BigQuaternion d = super.divide(b);
        if (!d.isEntier()) {
            throw new ArithmeticException("not divisible: " + this + " / " + b);
        }
        return d;
    }


    /**
     * Quotient and remainder by division of this by S.
     * @param S a quaternion number
     * @return [this*S**(-1), this - (this*S**(-1))*S].
     */
    public BigQuaternion[] quotientRemainder(BigQuaternion S) {
        return new BigQuaternion[] { divide(S), remainder(S) };
    }


    /**
     * BigQuaternion random. Random rational numbers A, B, C and D are generated
     * using random(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n) {
        return random(n, random);
    }


    /**
     * BigQuaternion random. Random rational numbers A, B, C and D are generated
     * using RNRAND(n). Then R is the quaternion number with real part A and
     * imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return R, a random BigQuaternion.
     */
    public BigQuaternion random(int n, Random rnd) {
        BigRational r = BigRational.ONE.random(n, rnd);
        BigRational i = BigRational.ONE.random(n, rnd);
        BigRational j = BigRational.ONE.random(n, rnd);
        BigRational k = BigRational.ONE.random(n, rnd);
        BigQuaternionInteger q = new BigQuaternionInteger(r, i, j, k);
        //if (entierRandoms) {
        q = q.roundToHurwitzian();
	//}
        return q;
    }


    /**
     * Quaternion number, random. Random rational numbers A, B, C and D are
     * generated using RNRAND(n). Then R is the quaternion number with real part
     * A and imaginary parts B, C and D.
     * @param n such that 0 &le; A, B, C, D &le; (2<sup>n</sup>-1).
     * @return R, a random BigQuaternion.
     */
    public static BigQuaternion QRAND(int n) {
        return ONE.random(n, random);
    }


    /**
     * Quaternion number greatest common divisor.
     * @param S BigQuaternion.
     * @return gcd(this,S).
     */
    public BigQuaternion gcd(BigQuaternion S) {
        return rightGcd(S);
    }


    /**
     * BigQuaternion extended greatest common divisor.
     * @param S BigQuaternion.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public BigQuaternion[] egcd(BigQuaternion S) {
        if (true) {
            throw new UnsupportedOperationException("not implemented: egcd");
        }
        BigQuaternion[] ret = new BigQuaternion[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if (S == null || S.isZERO()) {
            ret[0] = this;
            return ret;
        }
        if (this.isZERO()) {
            ret[0] = S;
            return ret;
        }
        BigQuaternion half = new BigQuaternion(new BigRational(1, 2));
        ret[0] = ONE;
        ret[1] = this.inverse().multiply(half);
        ret[2] = S.inverse().multiply(half);
        return ret;
    }


    /**
     * BigQuaternion round to next Hurwitz integer. BigQuaternion with all
     * integer or all 1/2 times integer components.
     * @return Hurwitz integer near this.
     */
    public BigQuaternionInteger roundToHurwitzian() {
        BigQuaternion g = this.roundToLipschitzian();
        BigQuaternion d = BigQuaternion.ZERO;
        BigRational half = BigRational.HALF;
        BigQuaternion s = this.subtract(g).norm();
        //System.out.println("s = " + s.toScript());
        if (s.re.compareTo(half) <= 0) {
            //System.out.println("s <= 1/2");
            return copy(g);
        }
        List<BigQuaternion> units = unitsOfHurwitzian();
        for (BigQuaternion ue : units) {
            BigQuaternion t = this.subtract(g).sum(ue).norm();
            if (t.re.compareTo(s.re) < 0) {
                s = t;
                d = ue;
            }
        }
        //System.out.println("s = " + s.toScript());
        g = g.sum(d);
        return copy(g);
    }


    /**
     * BigQuaternion units of the Hurwitzian integers. BigQuaternion units with
     * all integer or all 1/2 times integer components.
     * @return list of all 24 units.
     */
    public static List<BigQuaternion> unitsOfHurwitzian() {
        if (entierUnits != null) {
            return entierUnits;
        }
        BigRational half = BigRational.HALF;
        // Lipschitz integer units
        List<BigQuaternion> units = BigQuaternion.ONE.generators();
        List<BigQuaternion> u = new ArrayList<BigQuaternion>(units);
        for (BigQuaternion ue : u) {
            units.add(ue.negate());
        }
        // Hurwitz integer units
        long[][] comb = new long[][] { { 1, 1, 1, 1 }, { -1, 1, 1, 1 }, { 1, -1, 1, 1 }, { -1, -1, 1, 1 },
                { 1, 1, -1, 1 }, { -1, 1, -1, 1 }, { 1, -1, -1, 1 }, { -1, -1, -1, 1 }, { 1, 1, 1, -1 },
                { -1, 1, 1, -1 }, { 1, -1, 1, -1 }, { -1, -1, 1, -1 }, { 1, 1, -1, -1 }, { -1, 1, -1, -1 },
                { 1, -1, -1, -1 }, { -1, -1, -1, -1 } };
        for (long[] row : comb) {
            BigQuaternion ue = BigQuaternion.ONE.fromInteger(row);
            ue = ue.multiply(half);
            units.add(ue);
        }
        //System.out.println("units = " + units);
        //for (BigQuaternion ue : units) {
        //System.out.println("unit = " + ue + ", norm = " + ue.norm());
        //}
        entierUnits = units;
        return units;
    }


    /**
     * Integral quotient and remainder by left division of this by S. This must
     * be also an integral (Hurwitz) quaternion number.
     * @param b an integral (Hurwitz) quaternion number
     * @return [round(b**(-1)) this, this - b * (round(b**(-1)) this)].
     */
    public BigQuaternion[] leftQuotientAndRemainder(BigQuaternion b) {
        if (!this.isEntier() || !b.isEntier()) {
            throw new IllegalArgumentException("entier elements required");
        }
        BigQuaternion bi = b.inverse();
        BigQuaternion m = bi.multiply(this); // left divide
        //System.out.println("m = " + m.toScript());
        BigQuaternionInteger mh = copy(m).roundToHurwitzian();
        //System.out.println("mh = " + mh.toScript());
        BigQuaternion n = this.subtract(b.multiply(mh));
        BigQuaternion[] ret = new BigQuaternion[2];
        ret[0] = mh;
        ret[1] = n;
        return ret;
    }


    /**
     * Integral quotient and remainder by right division of this by S. This must
     * be also an integral (Hurwitz) quaternion number.
     * @param b an integral (Hurwitz) quaternion number
     * @return [round(b**(-1)) this, this - b * (round(b**(-1)) this)].
     */
    public BigQuaternion[] rightQuotientAndRemainder(BigQuaternion b) {
        if (!this.isEntier() || !b.isEntier()) {
            throw new IllegalArgumentException("entier elements required");
        }
        BigQuaternion bi = b.inverse();
        BigQuaternion m = this.multiply(bi); // right divide
        //System.out.println("m = " + m.toScript());
        BigQuaternion mh = copy(m).roundToHurwitzian();
        //System.out.println("mh = " + mh.toScript());
        BigQuaternion n = this.subtract(mh.multiply(b));
        BigQuaternion[] ret = new BigQuaternion[2];
        ret[0] = mh;
        ret[1] = n;
        return ret;
    }


    /**
     * Left remainder.
     * @param a element.
     * @return r = this - (a/left) * a, where left * a = this.
     */
    @Override
    public BigQuaternion leftRemainder(BigQuaternion a) {
	return leftQuotientAndRemainder(a)[1];
    }


    /**
     * Right remainder.
     * @param a element.
     * @return r = this - a * (a/right), where a * right = this.
     */
    @Override
    public BigQuaternion rightRemainder(BigQuaternion a) {
	return rightQuotientAndRemainder(a)[1];
    }


    /**
     * Integer quaternion number left greatest common divisor.
     * @param S integer BigQuaternion.
     * @return leftGcd(this,S).
     */
    @Override
    public BigQuaternion leftGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        BigQuaternionInteger q;
        BigQuaternion r;
        q = this;
        r = S;
        while (!r.isZERO()) {
            BigQuaternion u = q.leftQuotientAndRemainder(r)[1];
            //System.out.println("u = " + u.toScript());
            q = copy(r);
            r = u;
        }
        return q;
    }


    /**
     * Integer quaternion number right greatest common divisor.
     * @param S integer BigQuaternion.
     * @return rightGcd(this,S).
     */
    @Override
    public BigQuaternion rightGcd(BigQuaternion S) {
        if (S == null || S.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return S;
        }
        BigQuaternionInteger q;
        BigQuaternion r;
        q = this;
        r = S;
        while (!r.isZERO()) {
            BigQuaternion u = q.rightQuotientAndRemainder(r)[1];
            //System.out.println("u = " + u.toScript());
            q = copy(r);
            r = u;
        }
        return q;
    }

}
