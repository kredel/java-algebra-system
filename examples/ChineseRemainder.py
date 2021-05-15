#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import Ring, PolyRing, QQ, ZZ, GF, CC, ZM
from jas import terminate, startLog

# polynomial examples: Chines remainder theorem for polynomials

#r = PolyRing( ZM(1152921504606846883), "(x,y,z)", PolyRing.lex );
#r = PolyRing( CC(), "(x,y,z)", PolyRing.lex );
##r = PolyRing( ZZ(), "(x,y,z)", PolyRing.lex );
r = PolyRing( QQ(), "x,y,z", PolyRing.lex );
print "Ring: " + str(r);
print;

#[one,x,y,z] = r.gens();

a = r.random(3,4);
b = r.random(3,3);
c = abs( r.random(3,3) );

print "a = ", a;
print "b = ", b;
print "c = ", c;
print;


ff = [[a,b,c], [a+1,b,c], [a,b+1,c], [a,b,c+1]];
print "ff = " + str([ str([ str(a) for a in f ]) for f in ff ]);
print;

ar = x;
br = 3*one;
cr = z - y;
dr = 27*one;
rr = [ar, br, cr, dr];
print "rr = " + str([ str(a) for a in rr]);
print;


t = System.currentTimeMillis();
dd = r.CRT("", ff, rr);
t = System.currentTimeMillis() - t;

print "if existing, Chinese remainder = " + str(dd);
print;

print "Chinese remainder time = " + str(t) + " milliseconds" ;
print;

# now, used for interpolation

cr = r.ring.coFac;
print "cr = " + str(cr.toScriptFactory());
print;

a = cr.random(3);
b = cr.random(4);
c = cr.random(3).abs();

print "a = " + str(a);
print "b = " + str(b);
print "c = " + str(c);
print;

oo = cr.getONE();
ff = [[a,b,c], [a.sum(oo),b,c], [a,b.sum(oo),c], [a,b,c.sum(oo)]];
print "ff = " + str([ str([ str(a) for a in f ]) for f in ff ]);
print;

a = (2,3);
b = (3,7);
c = 5;
d = (1,27);
rr = [a, b, c, d];
print "rr = " + str([ str(a) for a in rr]);
print;

t = System.currentTimeMillis();
dd = r.CRTinterpol("", ff, rr);
t = System.currentTimeMillis() - t;

print "if existing, interpolated polynomial = " + str(dd);
print;

print "CRT interpolation time = " + str(t) + " milliseconds" ;
print;

#startLog();
terminate();
