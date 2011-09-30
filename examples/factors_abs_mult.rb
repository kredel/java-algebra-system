#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: absolute factorization over Q

r = Ring.new( "Q(x,y) L" );
puts "Ring: " + str(r);
puts;

one,x,y = r.gens();

f1 = x**2 + y**2;
f2 = x**3 + y**2;
f3 = x**4 + 4;

f = f1**3 * f2**1 * f3**2;

puts "f = " + str(f);
puts;

startLog();

t = System.currentTimeMillis();
#G = r.squarefreeFactors(f);
G = r.factorsAbsolute(f);
t = System.currentTimeMillis() - t;
puts "G = " + str(G.toScript());
puts
puts "factor time = " + str(t) + " milliseconds";
puts

#startLog();
terminate();
