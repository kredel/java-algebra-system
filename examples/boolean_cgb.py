import sys;

from jas import Ring, PolyRing, ParamIdeal, QQ, ZM, RR
from jas import startLog, terminate


# Boolean coefficient boolean GB
# see S. Inoue and A. Nagai "On the Implementation of Boolean Gröbner Bases" in ASCM 2009
# Z_2 regular ring coefficent example


r = PolyRing(RR(ZM(2),3),"a,x,y",PolyRing.lex);
print "r  = " + str(r);
#print len(r.gens())

[s1,s2,s3,a,x,y] = r.gens();
one = r.one();

print "one = " + str(one);
print "s1  = " + str(s1);
print "s2  = " + str(s2);
print "s3  = " + str(s3);
print "a   = " + str(a);
print "x   = " + str(x);
print "y   = " + str(y);

#brel = [ a**2 - a, x**2 - x, y**2 - y ]; 
brel = [ x**2 - x, y**2 - y ]; 

#print "brel = " + str(brel[0]) + ", " + str(brel[1]) + ", " + str(brel[2]);
print "brel = " + str(brel[0]) + ", " + str(brel[1]);

pl = [ ( one + s1 + s2 ) * ( x*y + x + y ), s1 * x + s1, a * y + a, x * y ];


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

