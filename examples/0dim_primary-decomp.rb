#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: zero dimensional ideals prime and primary decomposition

r = PolyRing.new(QQ(),"x,y,z",PolyRing.lex);

puts "Ring: " + str(r);
puts;

one,x,y,z = r.gens();

f1 = (x**2 - 5)**2;
f2 = y**2 - 5;
f3 = z**3 - y * x ;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

F = r.ideal( "", [f1,f2,f3] );

puts "F = ", F;
puts;

startLog();

t = System.currentTimeMillis();
Q = F.primaryDecomp();
t = System.currentTimeMillis() - t;
puts "Q = " + str(Q);
puts;
puts "primary decomp time = " +str(t) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
