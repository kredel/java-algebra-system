#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import PolyRing
from jas import Ideal
from jas import startLog
from jas import terminate
from jas import ZZ, QQ, ZM, DD, RF, CC

#r = Ring( "Mod 19 (B,S,T,Z,P,W) L" );
#r = Ring( "Mod 1152921504606846883 (B,S,T,Z,P,W) L" ); # 2^60-93
#r = Ring( "Quat(B,S,T,Z,P,W) L" );
#r = Ring( "Z(B,S,T,Z,P,W) L" );
#r = Ring( "C(B,S,T,Z,P,W) L" );
#r = Ring( "IntFunc(e,f)(B,S,T,Z,P,W) L" );
#r = Ring( "Z(B,S,T,Z,P,W) L" );
#r = Ring( "Q(B,S,T,Z,P,W) L" );
#print "Ring: " + str(r);
#print;

#r = PolyRing(ZZ(),"B,S,T,Z,P,W",PolyRing.lex);
#r = PolyRing(QQ(),"B,S,T,Z,P,W",PolyRing.lex);
#r = PolyRing(CC(),"B,S,T,Z,P,W",PolyRing.lex);
#r = PolyRing(DD(),"B,S,T,Z,P,W",PolyRing.lex);
#r = PolyRing(ZM(19),"B,S,T,Z,P,W",PolyRing.lex);
#r = PolyRing(ZM(1152921504606846883),"B,S,T,Z,P,W",PolyRing.lex); # 2^60-93
#rc = PolyRing(ZZ(),"e,f",PolyRing.lex);
#rc = PolyRing(QQ(),"e,f",PolyRing.lex);
#r = PolyRing(rc,"B,S,T,Z,P,W",PolyRing.lex);

rqc = PolyRing(ZZ(),"e,f",PolyRing.lex);
print "Q-Ring: " + str(rqc);
print "rqc.gens() = ", [ str(f) for f in rqc.gens() ];
print;
[pone,pe,pf] = rqc.gens();

r = PolyRing(RF(rqc),"B,S,T,Z,P,W",PolyRing.lex);
print "Ring: " + str(r);
print;

# sage like: with generators for the polynomial ring
print "r.gens() = ", [ str(f) for f in r.gens() ];
print;
[one,e,f,B,S,T,Z,P,W] = r.gens();
#[one,B,S,T,Z,P,W] = r.gens();
#[one,I,B,S,T,Z,P,W] = r.gens();

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 *B * S + 3 * B**2;
f7 = 10000 * B**2 + 6600 * B + 2673;
#all ok:
f1 = f1 + e;
#f7 = f7 + e * f6**0;
#f7 = f7 + 5959345574908321469098512640906154241024000000**2 * f6;
#f7 = f7 + 35555./332 * f1;

F = [ f1, f2, f3, f4, f5, f6, f7 ];
#F = [ f1, f2, f3, f4, f5, f6 ]; 
#print "F = ", [ str(f) for f in F ];
I = r.ideal( "", list=F );
print "Ideal: " + str(I);
print;

#sys.exit();

rg = I.GB();
print "seq Output:", rg;
print;

terminate();
sys.exit();

