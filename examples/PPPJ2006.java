

import edu.jas.arith.BigInteger;
import edu.jas.poly.TermOrder;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;


public class PPPJ2006 {

    public static void main(String[] args) {

        BigInteger z = new BigInteger();

        TermOrder to = new TermOrder();
        String[] vars = new String[] { "x1", "x2", "x3" };
        GenPolynomialRing<BigInteger> ring;
        ring = new GenPolynomialRing<BigInteger>(z,3,to,vars);
        System.out.println("ring = " + ring);

        GenPolynomial<BigInteger> pol;
        pol = ring.parse( "3 x1^2 x3^4 + 7 x2^5 - 61" );
        System.out.println("pol = " + pol);
        System.out.println("pol = " + pol.toString(ring.getVars()));

        GenPolynomial<BigInteger> one;
        one = ring.parse( "1" );
        System.out.println("one = " + one);
        System.out.println("one = " + one.toString(ring.getVars()));

        GenPolynomial<BigInteger> p;
        p = pol.subtract(pol);
        System.out.println("p = " + p);
        System.out.println("p = " + p.toString(ring.getVars()));

        p = pol.multiply(pol);
        System.out.println("p = " + p);
        System.out.println("p = " + p.toString(ring.getVars()));


    }
 
}