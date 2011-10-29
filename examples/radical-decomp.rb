#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: ideal radical decomposition

#r = Ring.new( "Rat(x) L" );
#r = Ring.new( "Q(x) L" );
r = PolyRing.new(QQ(),"x,y,z",PolyRing.lex);

puts "Ring: " + str(r);
puts;

one,x,y,z = r.gens();

f1 = (x**2 - 5)**2;
f2 = (y**2 - 3)**3 * (y**2 - 5);
f3 = z**3 - x * y;

#puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

F = r.ideal( "", list=[f2,f3] );

puts "F = " + str(F);
puts;

startLog();

t = System.currentTimeMillis();
R = F.radicalDecomp();
t = System.currentTimeMillis() - t;
puts "R = " + str(R);
puts;
puts "decomp time = " + str(t) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
