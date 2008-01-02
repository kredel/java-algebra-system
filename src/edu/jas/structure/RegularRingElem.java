/*
 * $Id$
 */

package edu.jas.structure;


/**
 * Regular ring element interface.
 * Defines idempotent and idempotent test.
 * @author Heinz Kredel
 */

public interface RegularRingElem<C extends RegularRingElem<C>> 
                 extends GcdRingElem<C> {


    /* Get component. Not possible.
     * @param i index of component.
     * @return val(i).
    public C get(int i);
     */


    /** Is Product full. 
     * @return If every component occurs, then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isFull();


    /** Is idempotent. 
     * @return If this is a idempotent element then true is returned, else false.
     */
    public boolean isIdempotent();


    /** Idempotent.  
     * @return S with this*S = this.
     */
    public C idempotent();


    /** Product idempotent complement.  
     * @return 1-this.idempotent().
     */
    public C idemComplement();


}
