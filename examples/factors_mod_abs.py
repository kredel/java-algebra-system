#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import PolyRing, ZM, QQ
from jas import terminate
from jas import startLog

# polynomial examples: factorization over Z_p

r = PolyRing(ZM(1152921504606846883,field=True),"(x)",PolyRing.lex);
#r = PolyRing(ZM(19),"x",PolyRing.lex );
#r = PolyRing(ZM(23),"x",PolyRing.lex );

print "Ring: " + str(r);
print;

[one,x] = r.gens();


#f = x**4 - 1;
#f = x**3 + 1;
f = x**3 - x - 1;


print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
#G = r.squarefreeFactors(f);
G = r.factorsAbsolute(f);
t = System.currentTimeMillis() - t;
print "G = ", G.toScript();
print "factor time =", t, "milliseconds";

#startLog();
terminate();
