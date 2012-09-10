#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import SolvableRing, SolvPolyRing, PolyRing, SolvableIdeal
from jas import QQ, Quat

# WA_32 solvable polynomial example

p = PolyRing(QQ(),"a,b,e1,e2,e3");
#is automatic: [one,a,b,e1,e2,e3] = p.gens();

relations = [e3, e1, e1*e3 - e1,
             e3, e2, e2*e3 - e2];

print "relations: =", [ str(f) for f in relations ];
print;

#rp = SolvPolyRing(QQ(), "a,b,e1,e2,e3", rel=relations);
rp = SolvPolyRing(QQ(), "a,b,e1,e2,e3", PolyRing.lex, relations);
print "SolvPolyRing: " + str(rp);
print;

print "gens =", [ str(f) for f in rp.gens() ];
#[one,a,b,e1,e2,e3] = rp.gens();
#[one,I,J,K,a,b,e1,e2,e3] = rp.gens();

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

f4 = ( e3**2 * e2**3 + e1 )**3;

#print "f1 = ", f1;
#print "f2 = ", f2;
#print "f3 = ", f3;
#print "f4 = ", f4;

F = [ f1, f2, f3 ];
print "F =", [ str(f) for f in F ];
print

I = rp.ideal( list=F );
print "SolvableIdeal: " + str(I);
print;

rgl = I.leftGB();
print "seq left GB:" + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

rgt = I.twosidedGB();
print "seq twosided GB:" + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print;

rgr = I.rightGB();
print "seq right GB:" + str(rgr);
print "isRightGB: " + str(rgr.isRightGB());
print;


#exit(0);


rs = """
# solvable polynomials, Weyl algebra A_3,2:
Rat(a,b,e1,e2,e3) L
#Quat(a,b,e1,e2,e3) G|3|
RelationTable
(
 ( e3 ), ( e1 ), ( e1 e3 - e1 ),
 ( e3 ), ( e2 ), ( e2 e3 - e2 )
)
""";

r = SolvableRing( rs );
print "SolvableRing: " + str(r);
print;

print "gens =", [ str(f) for f in r.gens() ];
#[one,a,b,e1,e2,e3] = r.gens();
#[one,I,J,K,a,b,e1,e2,e3] = r.gens();

fs1 = e1 * e3**3 + e2**10 - a;
fs2 = e1**3 * e2**2 + e3;
fs3 = e3**3 + e3**2 - b;

fs4 = ( e3**2 * e2**3 + e1 )**3;

#print "fs1 = ", fs1;
#print "fs2 = ", fs2;
#print "fs3 = ", fs3;
print "fs4 = ", fs4;

Fs = [ fs1, fs2, fs3 ];
print "Fs =", [ str(f) for f in Fs ];
print

Is = r.ideal( list=Fs );
print "SolvableIdeal: " + str(Is);
print;

rgsl = I.leftGB();
print "seq left GB:" + str(rgsl);
print "isLeftGB: " + str(rgsl.isLeftGB());
print;

rgst = I.twosidedGB();
print "seq twosided GB:" + str(rgst);
print "isTwosidedGB: " + str(rgst.isTwosidedGB());
print;

rgsr = I.rightGB();
print "seq right GB:" + str(rgsr);
print "isRightGB: " + str(rgsr.isRightGB());
print;


print "rgl == rgsl: " + str(rgl == rgsl);
print "rgr == rgsr: " + str(rgr == rgsr);
print "rgt == rgst: " + str(rgt == rgst);
