#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, ParamIdeal, QQ
from jas import startLog, terminate


# Raksanyi & Walter example
# integral/rational function coefficients


r = PolyRing(PolyRing(QQ(),"a1,a2,a3,a4",PolyRing.grad),"x1,x2,x3,x4",PolyRing.lex);
#print "r  = " + str(r);

[one,a1,a2,a3,a4,x1,x2,x3,x4] = r.gens();

pl = [ ( x4 - ( a4 - a2 ) ),
      ( x1 + x2 + x3 + x4 - ( a1 + a3 + a4 ) ),
      ( x1 * x3 + x1 * x4 + x2 * x3 + x3 * x4 - ( a1 * a4 + a1 * a3 + a3 * a4 ) ),
      ( x1 * x3 * x4 - ( a1 * a3 * a4 ) ) 
     ];
f = ParamIdeal(r,list=pl);
print "ParamIdeal: " + str(f);

gs = f.CGBsystem();
#print "CGBsystem: " + str(gs);
#print;

print f.CGB();

print gs.isCGBsystem();

#rs = gs.regularRepresentation();
#print "regular representation: " + str(rs);

rs = gs.regularRepresentationBC();
print "boolean closed regular representation: " + str(rs);

print rs.isRegularGB();


rsg = rs.regularGB();
print "regular GB: " + str(rsg);

print rsg.isRegularGB();

#ss = rsg.stringSlice();
#print "regular string slice: " + str(ss);

startLog();
terminate();
sys.exit();

print "one = " + str(one);
print "a1  = " + str(a1);
print "a2  = " + str(a2);
print "a3  = " + str(a3);
print "a4  = " + str(a4);
print "x1   = " + str(x1);
print "x2   = " + str(x2);
print "x3   = " + str(x3);
print "x4   = " + str(x4);



