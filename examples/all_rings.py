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
from jas import ZZ, QQ, ZM, DD, CC, Quat, Oct, RF

print "------- ZZ = BigInteger ------------";
z1 = ZZ(12345678901234567890);
print "z1 = " + str(z1);
z2 = z1**2 + 12345678901234567890;
print "z2 = " + str(z2);

print "------- QQ = BigRational ------------";
r1 = QQ(1,12345678901234567890);
print "r1 = " + str(r1**3);
r2 = r1**2 + (1,12345678901234567890);
print "r2 = " + str(r2);

print "------- ZM = ModInteger ------------";
m1 = ZM(19,12345678901234567890);
print "m1 = " + str(m1);
m2 = m1**2 + 12345678901234567890;
print "m2 = " + str(m2);

print "------- DD = BigDecimal ------------";
d1 = DD(12345678901234567890);
print "d1 = " + str(d1);
d2 = (1/d1)**2;
print "d2 = " + str(d2);

c1 = CC((1,2),(5,));
print "c1 = " + str(c1);
c2 = (1/c1)**2;
print "c2 = " + str(c2);
c3 = CC(0,(1,));
c3 = 1/c3;
print "c3 = " + str(c3);
[one,I] = CC().gens();
print "one = " + str(one);
print "I   = " + str(I);
c4 = c3 + 5 * I;
print "c4 = " + str(c4);

print "------- Quat = BigQuaternion ------------";
[oneQ,IQ,JQ,KQ] = Quat().gens();
print "oneQ = " + str(oneQ);
print "IQ   = " + str(IQ);
print "JQ   = " + str(JQ);
print "KQ   = " + str(KQ);
q1 = 2 + 3 * IQ + 4 * JQ + 5 * KQ;
print "q1 = " + str(q1);
q2 = (1/q1)**2;
print "q2 = " + str(q2);
q3 = q2 * q1 * q1;
print "q3 = " + str(q3);
q4 = (-23,1458) + (-1,243)*IQ + (-4,729)*JQ + (-5,729)*KQ
print "q4 = " + str(q4);

print "------- Oct = BigOctonion ------------";
#print [ str(g) for g in Oct().gens() ];
[oneOR,IOR,JOR,KOR,oneOI,IOI,JOI,KOI] = Oct().gens();
print "oneOR = " + str(oneOR);
print "IOR   = " + str(IOR);
print "JOR   = " + str(JOR);
print "KOR   = " + str(KOR);
print "oneOI = " + str(oneOI);
print "IOI   = " + str(IOI);
print "JOI   = " + str(JOI);
print "KOI   = " + str(KOI);

o1 = 2 * oneOR + 3 * IOR + 4 * JOR + 5 * KOR + 6 * oneOI + 7 * IOI + 8 * JOI + 9 * KOI;
print "o1 = " + str(o1);
o2 = (1/o1)**2;
print "o2 = " + str(o2);
o3 = o2 * o1 * o1;
print "o3 = " + str(o3);

o4 = (-69,20164)*oneOR + (-3,20164)*IOR + (-1,5041)*JOR + (-5,20164)*KOR  + (-3,10082)*oneOI + (-7,20164)*IOI + (-2,5041)*JOI + (-9,20164)*KOI;
print "o4 = " + str(o4);


print "------------------------------------";

sys.exit();

#r = Ring( "Mod 19 (B,S,T,Z,P,W) L" );
#r = Ring( "Mod 1152921504606846883 (B,S,T,Z,P,W) L" ); # 2^60-93
#r = Ring( "Quat(B,S,T,Z,P,W) L" );
#r = Ring( "Z(B,S,T,Z,P,W) L" );
#r = Ring( "C(B,S,T,Z,P,W) L" );
#r = Ring( "Z(B,S,T,Z,P,W) L" );
#r = Ring( "IntFunc(e,f)(B,S,T,Z,P,W) L" );
#r = Ring( "Z(B,S,T,Z,P,W) L" );
#r = Ring( "Q(B,S,T,Z,P,W) L" );
#print "Ring: " + str(r);
#print;

r = PolyRing(ZZ(),"B,S,T",PolyRing.lex);
print "Ring: " + str(r);
print;
pr = PolyRing(r,"Z,P,W",PolyRing.lex);
print "PolyRing: " + str(pr);
#    PolyRing(ZZ(),"B,S,T,Z,P,W",PolyRing.lex)
print;

r = pr;

# sage like: with generators for the polynomial ring
print "r.gens() = ", [ str(f) for f in r.gens() ];
print;
#[one,e,f,B,S,T,Z,P,W] = r.gens();
[one,B,S,T,Z,P,W] = r.gens();

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 * B * S + 3 * B**2;
f7 = 10000.0 * B**2 + 6600 * B + 2673;
#all ok:
#f7 = f7 + e * f6**0;
#f7 = f7 + 5959345574908321469098512640906154241024000000**2 * f6;
#f7 = f7 + 35555./332 * f1;

F = [ f1, f2, f3, f4, f5, f6, f7 ];
#F = [ f1, f2, f3, f4, f5, f6 ]; 
print "F = ", [ str(f) for f in F ];

I = r.ideal( "", list=F );
print "Ideal: " + str(I);
print;

print repr(f1);

print str(f1);

L = \
[\
( ( 10000 * B**2 + 6600 * B + 2673 ) ),\
( ( 45 ) * P + ( 35 * S - 165 * B - 36 ) ),\
( ( 35 ) * P + ( 40 ) * Z + ( 25 * T - 27 * S ) ),\
( ( 15 ) * W + ( 25 * S ) * P + ( 30 ) * Z - ( 18 * T + 165 * B**2 ) ),\
( ( -9 ) * W + ( 15 * T ) * P + ( 20 * S ) * Z ),\
( ( 99 ) * W - ( 11 * B * S - 3 * B**2 ) ),\
( P * W + ( 2 * T ) * Z - ( 11 * B**3 ) )\
];

print [ str(p) for p in L ];
print;
print [ str(p) for p in F ];
print;

sys.exit();

rg = I.GB();
print "seq Output:", rg;
print;

terminate();
sys.exit();

ps = """
( 
 ( 45 P + 35 S - 165 B - 36 ), 
 ( 35 P + 40 Z + 25 T - 27 S ), 
 ( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), 
 ( - 9 W + 15 T P + 20 S Z ), 
 ( P W + 2 T Z - 11 B**3 ), 
 ( 99 W - 11 B S + 3 B**2 ),
 ( 10000 B**2 + 6600 B + 2673 )
) 
""";

# ( 10000 B**2 + 6600 B + 2673 )
# ( B**2 + 33/50 B + 2673/10000 )

#f = Ideal( r, ps );
#print "Ideal: " + str(f);
#print;

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

#startLog();

rg = f.GB();
#print "seq Output:", rg;
#print;

terminate();
#sys.exit();

