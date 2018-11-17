# jython examples for jas.
# $Id$                                      
#

import sys, operator

from java.lang import System

from jas import FF, GF, PolyRing
from jas import terminate

# finite fields and algebraic field extensions


#print "------- finite field GF(17,5) ---------";
r = FF(17,5);
print "r     = " + str(r.ring.toScript());
e,a = r.gens();
print "e     = " + str(e);
print "a     = " + str(a);

ap = a**5 - a + 1;
print "ap    = " + str(ap);
ai = 1/ap;
print "ai    = " + str(ai) + ", ai*ap = " + str(ai*ap);

q = FF(17,5);
print "q     = " + str(q.ring.toScript());
qe,qa = q.gens();
print "qe    = " + str(qe);
print "qa    = " + str(qa);

qap = qa**5 - qa + 1;
print "qap   = " + str(qap);

s = ap - qap;
print "s     = " + str(s);



ar = PolyRing(r,"beta",PolyRing.lex);
print "ar    = " + str(ar);
e,a,b = ar.gens();
print "e     = " + str(e);
print "a     = " + str(a);
print "b     = " + str(b);

p = a**5 - a + beta**5 - (a**3 + a)*beta;
#p = p*p;
print "p     = " + str(p);
x = p.factors();
print "x     = " + ", ".join([ str(pp)+"**"+str(i) for (pp,i) in x.iteritems() ]);
g = reduce(operator.mul, [ pp**i for (pp,i) in x.iteritems() ], e);
print "g     = " + str(g);
#print "p-g:    " + str(p-g);
print "isFactors(p,x): " + str(p == g);
print;

#mod = r.ring.modul;
#print "mod   = " + str(mod);

pg = r.gens()[1];
#pg = RingElem(r.ring.modul.ring.generators()[1]);
print "pg               = " + str(pg);
pol = pg**(17**5) - pg;
print "pg**(17**5) - pg = " + str(pol);
pol = pg**(17**5-1);
print "pg**(17**5-1)    = " + str(pol);
print "17**5            = " + str(17**5);
print


terminate();
