#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, Ideal, QQ, ZZ, GF, ZM, CC
from jas import startLog, terminate

# trinks 6/7 example

# QQ = rational numbers, ZZ = integers, CC = complex rational numbers, GF = finite field
#QQ = QQ(); ZZ = ZZ(); CC = CC();
#r = PolyRing( GF(19),"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing( GF(1152921504606846883),"B,S,T,Z,P,W", PolyRing.lex); # 2^60-93
#r = PolyRing( GF(2**60-93),"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing( CC(),"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing( ZZ(),"B,S,T,Z,P,W", PolyRing.lex); # not for parallel
r = PolyRing( QQ(),"B,S,T,Z,P,W", PolyRing.lex);
print "Ring: " + str(r);
print;

# sage like: with generators for the polynomial ring
#[one,I,B,S,T,Z,P,W] = r.gens(); # is automaticaly included
#[one,B,S,T,Z,P,W] = r.gens(); # is automaticaly included

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 *B * S + 3 * B**2;
f7 = 10000 * B**2 + 6600 * B + 2673;

F = [ f1, f2, f3, f4, f5, f6, f7 ]; # smaller, faster
#F = [ f1, f2, f3, f4, f5, f6 ]; # bigger, needs more time
#print "F = ", [ str(f) for f in F ];
#print;

I = r.ideal( "", list=F );
print "Ideal: " + str(I);
print;

rg = I.GB();
print "seq Output:", rg;
print;

#startLog();

rg = I.parGB(2);
#print "par Output:", rg;
#print;

#sys.exit(); # if using ZZ coefficients

I.distClient(); # starts in background
rg = I.distGB(2);
#print "dist Output:", rg;
#print;

I.distClientStop(); # stops them
terminate();


