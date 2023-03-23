#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# exterior calculus plane intersection example
# after Blonski, 1983.

puts;

#r = ExtPolyRing.new( ZZ, 4, "E" );
r = ExtPolyRing.new( ZZ, 4 );
puts;
puts "ExtPolyRing: " + str(r);

gg = r.gens();
#for g in gg
#  puts "gens(): " + str(g);
#end
#puts;

# constant names (upper case) need explicit assignment
one = gg[0];
E1  = gg[1];
E2  = gg[2];
E3  = gg[3];
E4  = gg[4];

# variable names (lower case) are implicitly assigned
puts "one: " + str(one);
puts "r.E1:  " + str(r.E1);
puts "r.e1:  " + str(r.e1);
puts "E1:  " + str(E1);
puts "e1:  " + str(e1);
puts "e2:  " + str(e2);
puts "e3:  " + str(e3);
puts "e4:  " + str(e4);
puts;

puts "E1*E1: " + str(E1*E1);
puts "e1*e1: " + str(e1*e1);
puts "e2*e1: " + str(e2*e1);
puts;

#p1 = ("1 E(1) + 5 E(2) - 2 E(3) + 1 E(4)");
#p2 = ("4 E(1) + 3 E(2) + 6 E(3) + 1 E(4)");
p1 = r.e1 + 5*e2 - 2* e3 + e4;
p2 = 4 * r.e1 + 3 * e2 + 6 * e3 + e4;
puts "p1:  " + str(p1);
puts "p2:  " + str(p2);
puts;

#q1 = ("3 E(1) - 2 E(2) - 1 E(3) + 1 E(4)");
#q2 = ("1 E(2) + 5 E(3) + 1 E(4)");
q1 = 3 * e1 - 2 * e2 - e3 + e4;
q2 = e2 + 5 * e3 + e4;
puts "q1:  " + str(q1);
puts "q2:  " + str(q2);
puts;

#s = ("1 E(3) + 1 E(4)");
s = e3 + e4;
puts "s:   " + str(s);
puts;

#g1 = p1.multiply(p2).abs();
#g2 = q1.multiply(q2).abs().divide(new BigInteger(3));
g1 = (p1 * p2).abs();
g2 = r.element( (q1 * q2).elem.coeffPrimitivePart() );
puts "g1 = p1 /\\ p2 = " + str(g1);
puts "g2 = q1 /\\ q2 = " + str(g2);
puts;

#e1 = g1.multiply(s).abs().divide(new BigInteger(17));
#e2 = g2.multiply(s);
plane1 = r.element( (g1 * s).elem.coeffPrimitivePart() );
plane2 = g2 * s;
puts "plane1 = g1 /\\ s = " + str(plane1);
puts "plane2 = g2 /\\ s = " + str(plane2);
puts;
