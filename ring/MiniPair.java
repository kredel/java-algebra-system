
package edu.jas.ring;

import java.io.Serializable;

    /**
     * subclass to hold pairs of polynomials
     */

    class MiniPair implements Serializable {
	public Integer i;
	public Integer j;

	MiniPair(int i, int j) {
            this.i = new Integer(i); 
            this.j = new Integer(j);
	}

	public String toString() {
	    return "miniPair(" + i + "," + j + ")";
	}

    }
