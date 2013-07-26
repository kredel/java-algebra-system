#
# jruby examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import SolvableRing, SolvPolyRing, PolyRing, RingElem
from jas import QQ, startLog, SRC, SRF

# Ore extension solvable polynomial example, Gomez-Torrecillas, 2003

pcz = PolyRing(QQ(),"x,y,z");
#is automatic: [one,x,y,z] = pcz.gens();

zrelations = [z, y,  y * z + x
             ];

print "zrelations: = " + str([ str(f) for f in zrelations ]);
print;

pz = SolvPolyRing(QQ(), "x,y,z", PolyRing.lex, zrelations);
print "SolvPolyRing: " + str(pz);
print;

#startLog();

fl = [ z**2 + y, y**2 + x];
ff = pz.ideal("",fl);
print "ideal ff: " + str(ff);
print;

ff = ff.twosidedGB();
print "ideal ff: " + str(ff);
print;


f0 = SRC(ff,z + x + y + 1);
print "f0 = " + str(f0);

#f1 = SRC(ff, z-y+1 );
#f1 = SRC(ff, y*z+1  );
f1 = SRC(ff, y*z+x+1  );
print "f1 = " + str(f1);

f2 = f1*f0;
print "f2 = " + str(f2);

fi = 1/f1;
print "fi = " + str(fi);
fi1 = fi*f1;
f1i = f1*fi;
print "fi*f1 = " + str(fi1);
print "f1*fi = " + str(f1i);
print;

#exit(0);

pzc = f0.elem.ring;
print "SolvableResidueRing: " + str(pzc.toScript); # + ", assoz: " + str(pzc::ring.isAssociative);
print "gens =" + str([ str(f) for f in pzc.generators() ]);
print;

pct = PolyRing(pzc,"t");
#is automatic: [one,y,z,t] = p.gens(); # no x

#exit(0);

trelations = [t, y,  y * t + y,
              t, z,  z * t - z
             ];

print "trelations: = " + str([ str(f) for f in trelations ]);
print;

startLog();

pt = SolvPolyRing(pzc, "t", PolyRing.lex, trelations);
print "SolvPolyRing: " + str(pt);
print;

print "sp.gens =" + str([ str(f) for f in pt.gens() ]);
#is automatic: one,y,z,t = rp.gens(); # no x

#exit(0);

#yi = 1 / y; # not invertible
#print "yi   = " + str(yi);

a = t**2 + y;
b = t + y + 1;
c = t**2 - y * t - z;
print "a   = " + str(a);
print "b   = " + str(b);
print "c   = " + str(c);
print

ff = [ a*c, b*c ];
print "ff = " + str([ str(f) for f in ff ]);
print

ii = pt.ideal( "", ff );
print "SolvableIdeal: " + str(ii);
print;

#exit(0);

rgl = ii.leftGB();
print "seq left GB: " + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

p = RingElem(rgl.list.get(0));
print "p     = " + str(p);
print "c-p   = " + str(c-p);
#print "monic(p) = " + str(p.monic());
pp = p * p;
print "p*p   = " + str(pp);
print "p*y*z = " + str(p*y*z);
print "p*t   = " + str(p*t);
print "t*p   = " + str(t*p);
print;

#no: fl = [ p, p*x ]; # x non existent
#fl = [ p, p*y ];
#no: fl = [ p, p*z ];
#fl = [ p, p*t, p*y ];
#bad: fl = [ p, p*t, p*p ];
#bad: fl = [ p, p*p ];
#fl = [ p, p*t ];
#fl = [ p, p*(t+1) ];
fl = [ p*(t*t+1), p*(t*t*t), p*(t-3) ];
#fl = [ p ];
print "fl = " + str([ str(f) for f in fl ]);
print

iil = pt.ideal( "", fl );
print "SolvableIdeal_res: " + str(iil);
print;

iiq = iil.toQuotientCoefficients(); # beware of redefined generators
print "SolvableIdeal_quot: " + str(iiq);
print;

#exit(0);

rgll = iiq.leftGB();
print "seq left GB: " + str(rgll);
print "isLeftGB: " + str(rgll.isLeftGB());
print;

#rgr = rgl.rightGB();
#print "seq right GB: " + str(rgr);
#print "isRightGB: " + str(rgr.isRightGB());
#print;

#startLog();

rgt = iiq.twosidedGB();
print "seq twosided GB: " + str(rgt);
print "isTwosidedGB: " + str(rgt.isTwosidedGB());
print;

#terminate();
