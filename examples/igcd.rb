#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# integer examples: gcd

r = ZZ();
#r = QQ();
#r = GF(19);
#r = ZM(19*61);
puts "Ring: " + str(r);
puts;

#one,x,y,z = r.gens();

a = r.random(251);
b = r.random(171);
c = r.random(211).abs();
#c = 1; 
#a = 0;

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
puts;

ac = a * c;
bc = b * c;

puts "ac = " + str(ac);
puts "bc = " + str(bc);
puts;

#startLog();

t = System.currentTimeMillis();
#d = r.gcd(ac,bc);
d = ac.gcd(bc);
t = System.currentTimeMillis() - t;

puts "d = " + str(d);
puts;

puts "gcd time = " + str(t) + " milliseconds" ;

m = d % c;
#puts "m = " + str(m);
#puts;
if m.isZERO()
   puts "isGcd(c,d): true" ;
else
   puts "isGcd(c,d): " + str(m);
end
puts;

#startLog();
terminate();
