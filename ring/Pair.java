
package edu.jas.ring;

import java.io.Serializable;

import edu.jas.poly.OrderedPolynomial;

    /**
     * Serializable subclass to hold pairs of polynomials
     */

class Pair implements Serializable {

	public final OrderedPolynomial pi;
	public final OrderedPolynomial pj;
	public final int i;
	public final int j;

	Pair(Object a, OrderedPolynomial b, int i, int j) {
	    this( (OrderedPolynomial)a, b, i, j); 
	}

	Pair(OrderedPolynomial a, OrderedPolynomial b, int i, int j) {
	    pi = a; 
            pj = b; 
            this.i = i; 
            this.j = j;
	}

	public String toString() {
	    return "pair(" + i + "," + j + ")";
	}

	public MiniPair toMiniPair() {
	    return new MiniPair(i,j);
	}

}
