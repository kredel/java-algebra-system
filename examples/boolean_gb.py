import sys;

from jas import Ring, PolyRing, ParamIdeal, QQ, ZM, RR
from jas import startLog, terminate


# Boolean coefficient boolean GB
# Z_2 regular ring coefficent example


r = PolyRing(RR(ZM(2),3),"x,y",PolyRing.lex);
print "r  = " + str(r);
#print len(r.gens())

[s1,s2,s3,x,y] = r.gens();
one = r.one();

print "one = " + str(one);
print "s1  = " + str(s1);
print "s2  = " + str(s2);
print "s2  = " + str(s3);
print "x   = " + str(x);
print "y   = " + str(y);

brel = [ x**2 - x, y**2 - y ]; 

print "brel = " + str(brel[0]) + ", " + str(brel[1]);

pl = [ ( one + s1 + s2 ) * ( x*y + x +y ), s1 * x + s1, s2 * y + s2, x * y ];
#pl = [ ( one ) * ( x*y + x +y ), s1 * x + s1, s2 * y + s2, x * y ];

pl = pl + brel;


startLog();

f = ParamIdeal(r,list=pl);
print "Ideal: " + str(f);

gb = f.regularGB();
print "boolean GB: " + str(gb);

#ss = gb.stringSlice();
#print "regular string slice: " + str(ss);

terminate();
#sys.exit();

