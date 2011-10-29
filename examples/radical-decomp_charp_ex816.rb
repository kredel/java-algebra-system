#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: ideal radical decomposition, modified from example 8.16 in GB book

# noThreads(); # must be called very early

prime = 5;
cf = ZM(prime);
#cf = QQ();

ca = PolyRing.new(cf,"a",PolyRing.lex);
#puts "ca = " + str(ca);
ea,aa = ca.gens();
puts "ea   = " + str(ea);
puts "aa   = " + str(aa);
puts;

#!#roota = aa**prime + 2;
roota = aa**2 + 2;
puts "roota = " + str(roota);
Q3a = AN(roota,field=true);
puts "Q3a   = " + str(Q3a.factory());

## Q3a = RF(ca);
#puts Q3a.gens();

ea2,aa2 = Q3a.gens();
puts "ea2  = " + str(ea2);
puts "aa2  = " + str(aa2);
puts;

#cr = PolyRing.new(QQ(),"t",PolyRing.lex);
cr = PolyRing.new(Q3a,"t",PolyRing.lex);
puts "coefficient Ring: " + str(cr);
rf = RF(cr);
puts "coefficient quotient Ring: " + str(rf.ring.toScript());

r = PolyRing.new(rf,"x,y",PolyRing.lex);
puts "Ring: " + str(r);
#puts;

one,a,t,x,y = r.gens();
#puts one,a,t,x,y;
puts "one = " + str(one);
puts "a   = " + str(a);
puts "t   = " + str(t);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts;

#sys.exit();

#f1 = x**prime - t;
#f2 = y**prime - t;
##f1 = x**4 + t;
##f2 = y**4 + t;
f1 = x**3 + t;
f2 = y**3 + t;

#f2 = f2**2;

f3 = (y-x);
f3 = f3**prime;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
#puts "f3 = " + str(f3);
puts;

F = r.ideal( "", list=[f1,f2] );

puts "F = " + str(F);
puts;

startLog();

t = System.currentTimeMillis();
R = F.radicalDecomp();
#R = F.primeDecomp();
t = System.currentTimeMillis() - t;
puts "R = " + str(R);
puts;
puts "decomp time = " + str(t) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
