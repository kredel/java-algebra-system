#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: ideal radical decomposition, example 8.16 in GB book, base field with p-th root

# noThreads(); # must be called very early

prime = 5;
cf = GF(prime);
#cf = QQ();

ca = PolyRing.new(cf,"t",PolyRing.lex);
puts "ca = " + str(ca);
ea,ta = ca.gens();
puts "ea   = " + str(ea);
puts "ta   = " + str(ta);
puts;

Qpt = RF(ca);
#puts Qpt.gens();

ea2,ta2 = Qpt.gens();
puts "ea2  = " + str(ea2);
puts "ta2  = " + str(ta2);
puts;

cr = PolyRing.new(Qpt,"wpt",PolyRing.lex);
puts "polynomial quotient ring: " + str(cr);

et2,t,wpt = cr.gens();
puts "et2  = " + str(et2);
puts "t    = " + str(t);
puts "wpt  = " + str(wpt);
puts;

root = wpt**prime - ta2;
af = AN(root,field=true);
puts "coefficient algebraic quotient ring: " + str(af.ring.toScript());
#puts af.gens();
##xx = AN(( wpt**5 + 4 * t ),True,PolyRing(RF(PolyRing(ZM(5),"t",PolyRing.lex)),"wpt",PolyRing.lex))
##puts "xx: " + str(xx.ring.toScript());

one,t,wpt = af.gens();
puts "one  = " + str(one);
puts "t    = " + str(t);
puts "wpt  = " + str(wpt);
#puts one,t,wpt;
puts;


r = PolyRing.new(af,"x,y",PolyRing.lex);
puts "polynomial ring: " + str(r);
puts;

#automatic: one,t,wpt,x,y = r.gens();
#puts one,t,wpt,x,y;
puts "one  = " + str(one);
puts "t    = " + str(t);
puts "wpt  = " + str(wpt);
puts "x    = " + str(x);
puts "y    = " + str(y);
puts;


f1 = x**prime - t;
f2 = y**prime - t;

f2 = f2**3;

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
