/*
 * $Id$
 */

package edu.mas.kern;


import java.util.List;
import java.util.LinkedList;
import java.util.Collections;



/**
 * MAS and SAC2/Aldes LIST emulation and adaptor class.
 * @author Heinz Kredel
 */

public class LIST<C> {


    protected LinkedList<C> list = null; // List list not ok


    public LIST() {
	this( new LinkedList<C>() );
    }


    public LIST(List<C> l) {
	list = new LinkedList<C>( l );
    }


    /**Is empty. Test if the list L is empty.
      */
    public static <C> boolean isEmpty(LIST<C> L) {
	if ( L == null || L.list == null || L.list.isEmpty() ) {
	   return true;
	}
	return false;
    }


    /**Length.  L is a list.  Returns length(L). 
      */
    public static <C> int LENGTH(LIST<C> L) {
	if ( isEmpty( L ) ) {
	   return 0;
	}
	return L.list.size();
    }


    /**First.  L is a non-null list.  a is the first element of L. 
     */
    public static <C> C FIRST(LIST<C> L) {
        C x = null;
	if ( ! isEmpty( L ) ) {
           x = L.list.getFirst();
	}
        return x;
    }


    /**Set first.  L is a non-null list. a is a list. The first
       element of L is changed to a. 
     */
    public static <C> void SFIRST(LIST<C> L, C a) {
	if ( ! isEmpty( L ) ) {
	    L.list.set(0,a);
	}
    }


    /**Reductum.  L is a non-null list.  Returns the reductum of L. 
     */
    public static <C> LIST<C> RED(LIST<C> L) {
	LIST<C> LP = null;
	if ( ! isEmpty( L ) ) {
	    LP = new LIST<C>( L.list.subList(1,L.list.size()) );
	}
	return LP;
    }


    /**Set reductum.  L is a non-null list.  LP is a list.  The reductum
       of L is changed to LP. 
     */
    public static <C> void SRED(LIST<C> L, LIST<C> LP) {
	if ( ! isEmpty( L ) ) {
	   L.list.subList(1,L.list.size()).clear(); 
           if ( ! isEmpty( LP ) ) {
	      L.list.addAll( LP.list );
	   }
	}

    }


    /**To string.
      */
    @Override
    public String toString() {
	if ( isEmpty(this) ) {
	   return "[]";
	}
	return list.toString();
    }


    /**Reductum.  A is a list.  i is a non-negative beta-integer not less
       than LENGTH(A).  B=A, if i=0.  Otherwise, B is the i-th reductum of
       A.
      */
    public static <C> LIST<C> REDUCT(LIST<C> L, int i) {
	LIST<C> LP = null;
	if ( ! isEmpty( L ) ) {
	    LP = new LIST<C>( L.list.subList(i,L.list.size()) );
	}
	return LP;
    }


    /**Reductum 2.  L is a list of length 2 or more.  LP=RED(RED(L)).
      */
    public static <C> LIST<C> RED2(LIST<C> L, int i) {
	LIST<C> LP = null;
	if ( ! isEmpty( L ) ) {
	    LP = new LIST<C>( L.list.subList(2,L.list.size()) );
	}
	return LP;
    }


    /**Reductum 3.  L is a list of length 3 or more.  M is the third
       reductum of L.
      */
    public static <C> LIST<C> RED3(LIST<C> L, int i) {
	LIST<C> LP = null;
	if ( ! isEmpty( L ) ) {
	    LP = new LIST<C>( L.list.subList(3,L.list.size()) );
	}
	return LP;
    }


    /**Reductum 4.  L is a list of length 4 or more.  M is the fourth
       reductum of L.
      */
    public static <C> LIST<C> RED4(LIST<C> L, int i) {
	LIST<C> LP = null;
	if ( ! isEmpty( L ) ) {
	    LP = new LIST<C>( L.list.subList(42,L.list.size()) );
	}
	return LP;
    }


    /**Composition.  a is an object.  L is a list.  Returns the 
       composition of a and L. 
     */
    public static <C> LIST<C> COMP(C a, LIST<C> L) {
	LIST<C> LP = L;
        if ( L == null ) {
           LP = new LIST<C>();
	}
	LP.list.addFirst( a );
        return LP;
    }


    /**Clock. Returns the current CPU clock reading in milliseconds. 
       Intervalls are system dependent. 
     */
    public static long CLOCK() {
        return java.lang.System.currentTimeMillis();
    }


    /**List element.  A is a list.  1 le i le LENGTH(A).  a is the i-th
      element of A.
    */
    public static <C> C LELT(LIST<C> L, int i) {
        C x = null;
	if ( ! isEmpty( L ) ) {
           x = L.list.get(i);
	}
        return x;
    }


    /**Second.  L is a list of length 2 or more.  a is the second element
      of L.
     */
    public static <C> C SECOND(LIST<C> L) {
        C x = null;
	if ( ! isEmpty( L ) ) {
           x = L.list.get(2);
	}
        return x;
    }


    /**Third.  L is a list of length 3 or more.  a is the third element
      of L.
     */
    public static <C> C THIRD(LIST<C> L) {
        C x = null;
	if ( ! isEmpty( L ) ) {
           x = L.list.get(3);
	}
        return x;
    }


    /**Fourth.  L is a list of length 4 or more.  a is the fourth element
      of L.
    */
    public static <C> C FOURTH(LIST<C> L) {
        C x = null;
	if ( ! isEmpty( L ) ) {
           x = L.list.get(4);
	}
        return x;
    }


    /**Constructive concatenation.  L1 and L2 are lists.  L is the
      concatenation of L1 and L2.  The list L is constructed.
     */
    public static <C> LIST<C> CCONC(LIST<C> L1, LIST<C> L2) {
	if ( isEmpty( L1 ) ) {
	   return L2;
	}
	if ( isEmpty( L2 ) ) {
	   return L1;
	}
	LinkedList<C> list = new LinkedList<C>( L1.list );
	list.addAll( L2.list );
	return new LIST<C>( list );
    }


    /**Constructive inverse.  L is a list.  M=INV(L).  M is constructed
       using COMP.
      */
    public static <C> LIST<C> CINV(LIST<C> L) {
	if ( isEmpty( L ) ) {
	   return L;
	}
	LinkedList<C> list = new LinkedList<C>(L.list);
        Collections.reverse( list );
	return new LIST<C>( list );
    }


    /**Inverse.  L is a list.  The inverse of L is returned.  The list L is
       modified. 
      */
    public static <C> LIST<C> INV(LIST<C> L) {
	if ( isEmpty( L ) ) {
	   return L;
	}
        Collections.reverse( L.list );
	return L;
    }


    /**Composition 2.  a and b are objects.  L is a list.
       M=COMP(a,COMP(b,L)).
     */
    public static <C> LIST<C> COMP2(C a, C b, LIST<C> L) {
	LIST<C> LP = L;
        if ( L == null ) {
           LP = new LIST<C>();
	}
	LP.list.addFirst( b );
	LP.list.addFirst( a );
        return LP;
    }


    /**Composition 3.  a1, a2 and a3 are objects.  L is a list.
       M=COMP(a1,COMP(a2,COMP(a3,L))).
      */
    public static <C> LIST<C> COMP3(C a, C b, C c, LIST<C> L) {
	LIST<C> LP = L;
        if ( L == null ) {
           LP = new LIST<C>();
	}
	LP.list.addFirst( c );
	LP.list.addFirst( b );
	LP.list.addFirst( a );
        return LP;
    }


    /**Composition 4.  a1, a2, a3 and a4 are objects.  L is a list.
       M=COMP(a1,COMP(a2,COMP(a3,COMP(a4,l)))).
      */
    public static <C> LIST<C> COMP3(C a, C b, C c, C d, LIST<C> L) {
	LIST<C> LP = L;
        if ( L == null ) {
           LP = new LIST<C>();
	}
	LP.list.addFirst( d );
	LP.list.addFirst( c );
	LP.list.addFirst( b );
	LP.list.addFirst( a );
        return LP;
    }


    /**Concatenation.  L1 and L2 are lists.  L=CONC(L1,L2).  The list L1 is
       modified.
      */
    public static <C> LIST<C> CONC(LIST<C> L1, LIST<C> L2) {
	if ( isEmpty( L1 ) ) {
	   return L2;
	}
	if ( isEmpty( L2 ) ) {
	   return L1;
	}
	L1.list.addAll( L2.list );
	return L1;
    }


    /**Equal.  a and b are objects.  t=true if a and b are equal and
       otherwise t=false.
      */
    public static <C> boolean EQUAL(LIST<C> L1, LIST<C> L2) {
	if ( isEmpty( L1 ) ) {
           return isEmpty(L2);
	}
	if ( isEmpty( L2 ) ) {
	   return isEmpty(L1);
	}
	return L1.list.equals( L2.list );
    }


    /**Extent.  a is an object.  n=EXTENT(a).
      */


    /**List, 1 element.  a in an object.  L is the list (a).
      */
    public static <C> LIST<C> LIST1(C a) {
	LIST<C> L = new LIST<C>();
	L.list.addFirst( a );
        return L;
    }


    /**List, 10 elements.  a1, a2, a3, a4, a5, a6, a7, a8, a9 and a10 are
       objects.  L is the list (a1,a2,a3,a4,a5,a6,a7,a8,a9,a10).
      */
    public static <C> LIST<C> LIST10(C a1, C a2, C a3, C a4, C a5, C a6, C a7, C a8, C a9, C a10) {
	LIST<C> L = new LIST<C>();
	L.list.addFirst( a10 );
	L.list.addFirst( a9 );
	L.list.addFirst( a8 );
	L.list.addFirst( a7 );
	L.list.addFirst( a6 );
	L.list.addFirst( a5 );
	L.list.addFirst( a4 );
	L.list.addFirst( a3 );
	L.list.addFirst( a2 );
	L.list.addFirst( a1 );
        return L;
    }


    /**List, 2 elements.  a and b are objects.  L is the list (a,b).
      */
    public static <C> LIST<C> LIST2(C a, C b) {
	LIST<C> L = new LIST<C>();
	L.list.addFirst( b );
	L.list.addFirst( a );
        return L;
    }


    /**List, 3 elements.  a1, a2 and a3 are objects.  L=(a1,a2,a3).
      */
    public static <C> LIST<C> LIST3(C a, C b, C c) {
	LIST<C> L = new LIST<C>();
	L.list.addFirst( c );
	L.list.addFirst( b );
	L.list.addFirst( a );
        return L;
    }


    /**List, 4 elements.  a1, a2, a3 and a4 are objects.  L is the list
       (a1,a2,a3,a4).
      */
    public static <C> LIST<C> LIST4(C a, C b, C c, C d) {
	LIST<C> L = new LIST<C>();
	L.list.addFirst( d );
	L.list.addFirst( c );
	L.list.addFirst( b );
	L.list.addFirst( a );
        return L;
    }


    /**List, 5 elements.  a1,a2,a3,a4 and a5 are objects.  L is the list
       (a1,a2,a3,a4,a5).
      */
    public static <C> LIST<C> LIST5(C a, C b, C c, C d, C e) {
	LIST<C> L = new LIST<C>();
	L.list.addFirst( e );
	L.list.addFirst( d );
	L.list.addFirst( c );
	L.list.addFirst( b );
	L.list.addFirst( a );
        return L;
    }


    /**Order.  a is an object.  n=ORDER(a).
      */


}