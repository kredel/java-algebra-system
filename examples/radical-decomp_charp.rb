#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: ideal radical decomposition, dim > 0, char p separable

r = PolyRing.new(GF(5),"x,y,z",PolyRing.lex);
puts "Ring: " + str(r);
puts;

#automatic: one,x,y,z = r.gens();

#f1 = (x**2 - 5)**2;
#f1 = (y**10 - x**5)**3;
f2 = y**6 + 2 * x * y**4 + 3 * x**2 * y**2 + 4 * x**3;
f2 = f2**5;
f3 = z**10 - x**5;

f4 = (y**2 - x)**3;

#puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
#puts "f4 = " + str(f4);
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
