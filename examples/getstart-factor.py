#
# jython examples for jas.
# $Id$
#

import operator
from jas import PolyRing, QQ
from jas import startLog, terminate

# simple factorization example

r = PolyRing( QQ(), "x,y,z", PolyRing.lex); # or PolyRing.grad
print "PolyRing: " + str(r);
print;

p = (x+y+z)**11*(x+1)**5*(y+z)**3;
#print "p: " + str(p);
#print;

#startLog();
f = r.factors( p );
print "factors: " + ", ".join( [ str(k) + "**" + str(v) for k,v in f.items() ] );
print;

mp = reduce(operator.mul, [ k**v for k,v in f.items() ] );
#print str(mp);
print "p == mp: " + str(p == mp);
print;

startLog();
terminate();
