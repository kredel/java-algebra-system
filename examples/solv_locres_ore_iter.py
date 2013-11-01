#
# jruby examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import SolvableRing, SolvPolyRing, PolyRing, RingElem
from jas import QQ, startLog, SLR

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

#fl = [ z**2 + y, y**2 + x];
fl = [ z**2 + y, x];
ff = pz.ideal("",fl);
print "ideal ff: " + str(ff);
print;

ff = ff.twosidedGB();
print "ideal ff: " + str(ff);
print;


f0 = SLR(ff,z + x + y + 1);
print "f0 = " + str(f0);

#f1 = SLR(ff, z-y+1 );
#f1 = SLR(ff, y*z+1  );
f1 = SLR(ff, y*z+x+1  );
print "f1 = " + str(f1);

f2 = f1*f0;
print "f2 = f1*f0: " + str(f2);
print;

fi = 1/f1;
print "fi = " + str(fi);
fi1 = fi*f1;
f1i = f1*fi;
print "fi*f1 = " + str(fi1);
print "f1*fi = " + str(f1i);
print;

f2i = f2*fi;
fi2 = fi*f2;
print "f2*fi = " + str(f2i);
print "fi*f2 = " + str(fi2);
print "f2*fi == f0: " + str(f2i == f0);
print "fi*f2 == f0: " + str(fi2 == f0);
print;

#exit(0);

pzc = f0.ring;
print "SolvableLocalResidueRing: " + str(pzc.toScript()) + ", assoz: " + str(pzc.isAssociative());
print "gens =" + str([ str(f) for f in pzc.generators() ]);
print;

pct = PolyRing(pzc,"t,u");
#is automatic: [one,y,z,t] = p.gens(); # no x

#exit(0);

trelations = [t, y,  y * t + y,
              t, z,  z * t - z
             ];

print "trelations: = " + str([ str(f) for f in trelations ]);
print;

#startLog();

pt = SolvPolyRing(pzc, "t,u", PolyRing.lex, trelations);
print "SolvPolyRing: " + str(pt);
print "sp.gens =" + str([ str(f) for f in pt.gens() ]);
#is automatic: one,y,z,t = rp.gens(); # no x
print;

#exit(0);

a = t**2 + y;
b = t + y + 1;
c = z*t**2 - y * t - z;
print "a   = " + str(a);
print "b   = " + str(b);
print "c   = " + str(c);
#c = c.monic();
#print "c   = " + str(c);
print

ff = [ a*c, b*c, (a+b)*c ];
print "ff = " + str([ str(f) for f in ff ]);
print

ii = pt.ideal( "", ff );
print "SolvableIdeal: " + str(ii);
print;

#exit(0);
#startLog();

rgl = ii.leftGB();
print "seq left GB: " + str(rgl);
print "isLeftGB: " + str(rgl.isLeftGB());
print;

#p = RingElem(rgl.list.get(0));
p = RingElem(rgl.list[0]);
print "p     = " + str(p);
print "c     = " + str(c);
print "c-p   = " + str(c-p);
d = c.monic();
print "d     = " + str(d);
print "d-p   = " + str(d-p);
print;

#exit(0);

#no: fl = [ p, p*x ]; # x non existent
#no: fl = [ p, p*z ];
#bad: fl = [ p, p*t, p*p ];
#bad: fl = [ p, p*p ];
#fl = [ p, p*t ];
#fl = [ p ];
#fl = [ t*p, (t*t+1)*p, (t*t-t)*p ];
fl = [ t*c, (t*t+1)*c, (t*t-t)*c ];
print "fl = " + str([ str(f) for f in fl ]);
print

iil = pt.ideal( "", fl );
print "SolvableIdeal_local: " + str(iil);
print;

rgll = iil.leftGB();
print "seq left GB: " + str(rgll);
print "isLeftGB: " + str(rgll.isLeftGB());
print;

#q = RingElem(rgll.list.get(0));
q = RingElem(rgll.list[0]);
print "p     = " + str(p);
print "q     = " + str(q);
print "q-p   = " + str(q-p);
print "c     = " + str(c);
print "c-q   = " + str(c-q);
print "d     = " + str(d);
print "d-q   = " + str(d-q);
print;

