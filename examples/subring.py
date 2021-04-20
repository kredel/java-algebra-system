#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import Ring, PolyRing, QQ, ZZ, GF
from jas import terminate, startLog

# polynomial examples: subring

#r = Ring( "Mod 1152921504606846883 (x,y,z) L" );
#r = Ring( "Rat(x,y,z) L" );
#r = Ring( "C(x,y,z) L" );
##r = Ring( "Z(x,y,z) L" );
r = PolyRing( QQ(), "x,y,z", PolyRing.lex );
print "Ring: " + str(r);
print;

#[one,x,y,z] = r.gens();

a = r.random(3,4);
b = r.random(3,4);
c = abs( r.random(3,3) );

print "a = ", a;
print "b = ", b;
print "c = ", c;
print;



t = System.currentTimeMillis();
dd = r.subring("", [a,b,c]);
t = System.currentTimeMillis() - t;

print "sub ring = " + str([ str(x) for x in dd]);
print;

print "subring time = " + str(t) + " milliseconds" ;
print;

cc = a + b * c;
tt = r.subringmember(dd, cc);
print "sub ring member = " + str(tt);
print;

#startLog();
terminate();
