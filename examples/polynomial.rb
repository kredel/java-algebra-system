#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: gcd

#r = Ring( "Mod 1152921504606846883 (x,y,z) L" );
#r = Ring( "Rat(x,y,z) L" );
#r = Ring( "C(x,y,z) L" );
r = Ring.new( "Z(x,y,z) L" );
puts "Ring: " + str(r);
puts;

one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
puts;

a = r.random();
b = r.random();
c = r.random().abs();
#c = one; 
#a = 0;

f = x * a + b * y**2 + one * z**7;

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
#puts "f = " + str(f);
puts;

ac = a * c;
bc = b * c;

puts "ac = " + str(ac);
puts "bc = " + str(bc);
puts;

t = System.currentTimeMillis();
g = r.gcd(ac,bc);
t = System.currentTimeMillis() - t;
puts "g = " + str(g);
#puts "gcd time =", t, "milliseconds";

d = r.gcd(g,c);
puts "gcd time = " + str(t) + " milliseconds," + " isGcd(c,d): " + str(c==d);
puts;

#d = g / c;
#m = g % c;
#puts "d = ", d;
#puts "m = ", m;
#puts;

#startLog();
terminate();
