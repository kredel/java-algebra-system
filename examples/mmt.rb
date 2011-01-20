#
# jruby examples for jas.
# $Id: $
#

require "examples/jas"

#startLog();

# example form Moeller, Mora, Traverso

#r = PolyRing.new(QQ(),"t,z,y,x",PolyRing.grad);
r = PolyRing.new(QQ(),"z,y,x",PolyRing.grad);
#r = PolyRing.new(QQ(),"x,y,z",PolyRing.grad);
print "Ring: " + str(r);
print;

#one,t,z,y,x = r.gens();
one,z,y,x = r.gens();
#one,x,y,z = r.gens();

f0 = x**2 * y - z**2;
f1 = x * z**2 - y**2;
f2 = y * z**3 - x**2;

#f0h = x**2 * y - z**2 * t;
#f1h = x * z**2 - y**2 * t;
#f2h = y * z**3 - x**2 * t**2;

f = r.ideal("",[f0,f1,f2]);
#f = r.ideal("",[f0h,f1h,f2h]);
print "Ideal: " + str(f);
print;

startLog();

rg = f.GB();
print "seq Output: ", rg;
print "\n";

#terminate();

