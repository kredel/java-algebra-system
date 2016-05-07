#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import SolvableRing, SolvPolyRing, PolyRing, SRF 
from jas import QQ, startLog, SolvableModule, SolvableSubModule, terminate

# Ore extension solvable polynomial example, modules

rp = PolyRing(QQ(),"x,y,z,t",PolyRing.lex);
#is automatic: [one,x,y,z,t] = rp.gens();

trel = [ z, y,  y * z + x,
         t, y,  y * t + y,
         t, z,  z * t - z
       ];

print "trel: = " + str([ str(f) for f in trel ]);
print;

rs = SolvPolyRing(QQ(),"x,y,z,t",PolyRing.lex,trel);

#exit(0)

f = rs.ideal("",[t**2 + z**2 + x**2 + y**2 + 1]);
print "f: " + str(f);
tf = f.twosidedGB();
print "t: " + str(tf);
print;

#exit(0)

r = SolvableModule("",rs);
print "SolvableModule: " + str(r);
print;

subm = [
         [ 0, t**2 + z**2 + x**2 + y**2 + 1],
         [ x**2 + y**2,     z ]
       ];

m = SolvableSubModule( r, "", subm );
print "SolvableSubModule: " + str(m);
print;

#exit()
#startLog();

lg = m.leftGB();
print "seq left GB: " + str(lg);
print "is left GB: " + str(lg.isLeftGB());
print;

tg = m.twosidedGB();
print "seq twosided GB: " + str(tg);
print "is twosided GB: " + str(tg.isTwosidedGB());
print "is right GB: " + str(tg.isRightGB());
print;

#exit()

rg = m.rightGB();
print "seq right GB: " + str(rg);
print "is right GB: " + str(rg.isRightGB());
print;


# as quotients to coefficients
rq = SRF(rs);

rpq = PolyRing(rq,"v,w",PolyRing.lex);
print "PolyRing: rpq = " + str(rpq);

vrel = [ v, t, t * v + x,
         w, t, t * w + y
       ];

print "vrel: = " + str([ str(f) for f in vrel ]);
print;

rsq = SolvPolyRing(rq,"v,w",PolyRing.lex,vrel);
print "SolvPolyRing: rsq = " + str(rsq);
print;


r = SolvableModule("",rsq);
print "SolvableModule: " + str(r);
print;

subm = [
         [     0, v + 1 ],
         [ w + x, w - v ]
       ];

m = SolvableSubModule( r, "", subm );
print "SolvableSubModule: " + str(m);
print;

#startLog();

lg = m.leftGB();
print "seq left GB: " + str(lg);
print "is left GB: " + str(lg.isLeftGB());
print;

#exit();
#startLog();

tg = m.twosidedGB();
print "seq twosided GB: " + str(tg);
print "is twosided GB: " + str(tg.isTwosidedGB());
print "is right GB: " + str(tg.isRightGB());
print;

#exit()

rg = m.rightGB();
print "seq right GB: " + str(rg);
print "is right GB: " + str(rg.isRightGB());
print;


#startLog();
terminate();
