(* ----------------------------------------------------------------------------
 * $Id$
 * ----------------------------------------------------------------------------
 * This file is part of MAS.
 * ----------------------------------------------------------------------------
 * Copyright (c) 1989 - 1992 Universitaet Passau
 * ----------------------------------------------------------------------------
 * $Log$
 * Revision 1.1  2000/06/17 20:40:56  kredel
 * Initial revision
 *
 * Revision 1.1.1.1  2000/06/17 20:40:56  kredel
 * Clean import of MAS 1.0 from Uni Passau
 *
 * Revision 1.1  1992/01/30 12:18:07  kredel
 * Initial revision
 *
 * ----------------------------------------------------------------------------
 *)
 
BEGIN 
 CLOUT("Loading specifications ..."); 
 (*OUT("NUL:");*) x:=T; (* define T for parser *) 
 PRAGMA(SLOPPY);  
 (*PRAGMA(GENPARSE);  ensure non-generic parse *) 
 END. 
 
(* read required specifications. *) 
 
IN("spec/ABSTR.SPC").   (* abstract items *) 
 
IN("spec/LISP.SPC").    (* builtin lisp functions *) 
 
IN("spec/ARITH.SPC").   (* basic arithmetic *) 
 
IN("spec/TERM.SPC").    (* elementary term structures *) 
 
IN("spec/POLY.SPC").    (* polynomials *) 
 
CLOUT(" ... exposing units ... "). 
 
(*specifics. *) 
EXPOSE ATOM.  
EXPOSE REPRESENTATION.  
EXPOSE Lists.  
 
EXPOSE INTEGER.  
EXPOSE RATIONAL. 
EXPOSE MODINT.  
EXPOSE FLOATING.  
 
EXPOSE PROPLOG. 
EXPOSE PEANO. 
 
EXPOSE IPOL. 
EXPOSE DIRP. 
EXPOSE DIRL. 
 
(*generics. *) 
EXPOSE OBJECT.  
EXPOSE AGROUP.  
EXPOSE FIELD.  
EXPOSE RING.  
EXPOSE GBRING.  
 
BEGIN  
 PRAGMA(FUSSY);  
 (*SHUT("NUL:");*) 
 (*PRAGMA(GENPARSE); allow generic parse, if required *) 
 CLOUT("... finished."); 
 END. 
 








(* -EOF- *)
