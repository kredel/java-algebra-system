#
# jruby examples for jas.
# $Id$
#

#from java.lang import System
#from java.lang import Integer

from jas import SolvableRing, SolvPolyRing, PolyRing
from jas import QQ, startLog, SRC, SRF

# Weyl coefficient field example

r = PolyRing(QQ(),"p1,q1");
#is automatic: [one,p1,q1] = p.gens();

relations = [q1, p1,  p1 * q1 + 1
            ];

print "relations: = " + str([ str(f) for f in relations ]);
print;

rp = SolvPolyRing(QQ(), "p1,q1", PolyRing.lex, relations);
print "SolvPolyRing: " + str(rp);
print;

print "gens =" + str([ str(f) for f in rp.gens() ]);
#is automatic: one,p1,q1 = rp.gens();


scp = SRF(rp);
print "scp = " + str(scp);


r2 = PolyRing(scp,"p2,q2");
#is automatic: [one,p1,q1,p2,q2] = r2.gens();

relations2 = [q2, p2,  p2 * q2 + 1
             ];

print "relations: = " + str([ str(f) for f in relations2 ]);
print;

rp2 = SolvPolyRing(scp, "p2,q2", PolyRing.lex, relations2);
print "SolvPolyRing: " + str(rp2);
print;

print "gens =" + str([ str(f) for f in rp2.gens() ]);
#is automatic: one,q1,p1,q2,p2 = rp2.gens();


f1 = p2 + p1;
f2 = q2 - q1;
print "f1 = " +str(f1);
print "f2 = " +str(f2);

f3 = f1 * f2;
print "f3 = " +str(f3);

f4 = f2 * f1;
print "f4 = " +str(f4);

ff = [ f4 , f3 ];
print "ff = [" + str([ str(f) for f in ff ]);
print

#exit(0);

ii = rp2.ideal( "", ff );
print "SolvableIdeal: " + str(ii);
print;


rgl = ii.leftGB();
print "seq left GB: " + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

rgr = ii.rightGB();
print "seq right GB: " + str(rgr);
print "isRightGB: " + str(rgr.isRightGB());
print;

#startLog();

rgt = ii.twosidedGB();
print "seq twosided GB: " + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print;


urgt = rgt.univariates();
print "univariate polynomials: " + str([ str(f) for f in urgt ]);
print;

#h = q1;
#h = q2;
#h = p2;
#h = q2 - p2;
h = q1 * q2 + p1 * p2 - p1 + q1**2 + 1;
print "polynomial: " + str(h);
hi = rgt.inverse(h);
print "inverse polynomial: " + str(hi);
hhi = h*hi;
print "h * hi: " + str(hhi);
print "h * hi left-mod rgt: " + str(rgt.leftReduction(hhi));
print "h * hi right-mod rgt: " + str(rgt.rightReduction(hhi));
print;

