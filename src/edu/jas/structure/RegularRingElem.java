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
                 extends RingElem<C> {


    /** Is idempotent. 
     * @return If this is a idempotent element then true is returned, else false.
     */
    public boolean isIdempotent();


    /** Idempotent.  
     * @return S with this*S = this.
     */
    public RegularRingElem<C> idempotent();


}
