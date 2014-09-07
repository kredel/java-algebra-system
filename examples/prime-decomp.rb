#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# polynomial examples: ideal prime decomposition

r = PolyRing.new(QQ(),"x,y,z",PolyRing.lex);

puts "Ring: " + str(r);
puts;

#automatic: one,x,y,z = r.gens();

f1 = (x**2 - 5)**2;
f2 = y**3 - x;
f3 = z**2 - y * x;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

#F = r.ideal( "", [f1,f2,f3] );
F = r.ideal( "", [f2,f3] );

puts "F = ", F;
puts;

startLog();

t = System.currentTimeMillis();
P = F.primeDecomp();
t1 = System.currentTimeMillis() - t;
puts "P = " + str(P);
puts;
puts "prime decomp time = " + str(t1) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
