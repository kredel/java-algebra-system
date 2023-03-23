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

# define points in 4-space as polynomials
#p1 = ("1 E(1) + 5 E(2) - 2 E(3) + 1 E(4)");
#p2 = ("4 E(1) + 3 E(2) + 6 E(3) + 1 E(4)");
p1 = r.e1 + 5*e2 - 2* e3 + e4;
p2 = 4 * r.e1 + 3 * e2 + 6 * e3 + e4;
puts "p1:  " + str(p1);
puts "p2:  " + str(p2);

#q1 = ("3 E(1) - 2 E(2) - 1 E(3) + 1 E(4)");
#q2 = ("1 E(2) + 5 E(3) + 1 E(4)");
q1 = 3 * e1 - 2 * e2 - e3 + e4;
q2 = e2 + 5 * e3 + e4;
puts "q1:  " + str(q1);
puts "q2:  " + str(q2);

#s = ("1 E(3) + 1 E(4)");
s = e3 + e4;
puts "s:   " + str(s);
puts;

# compute line(gerade) p1..p2 and q1..q2
#g1 = p1.multiply(p2).abs();
#g2 = q1.multiply(q2).abs().divide(new BigInteger(3));
g1 = (p1 * p2).abs();
g2 = (q1 * q2).cpp();
puts "g1 = p1 /\\ p2 = " + str(g1);
puts "g2 = q1 /\\ q2 = " + str(g2);
#puts "cpp(g2) = " + str(g22);
puts;

# compute plane g1..s and g2..s
#e1 = g1.multiply(s).abs().divide(new BigInteger(17));
#e2 = g2.multiply(s);
plane1 = (g1 * s).cpp();
plane2 = g2 * s;
puts "plane1 = g1 /\\ s = " + str(plane1);
puts "plane2 = g2 /\\ s = " + str(plane2);
puts;

#emax = r.ring.ixfac.imax;
emax = RingElem.new( r.ring.getIMAX() );
puts "emax = " + str(emax);

# compute dual planes of e1, e2 as e1..emax and e2..emax
e1dual = plane1.innerRightProduct(emax).abs();
e2dual = plane2.innerRightProduct(emax).abs();
puts "e1dual = plane1 |_ emax = " + str(e1dual);
puts "e2dual = plane2 |_ emax = " + str(e2dual);
puts;

# compute intersection of plane plane1, plane2 via dual planes sum
q = (e1dual * e2dual).cpp();
puts "q = e1dual /\\ e2dual = " + str(q);
qs = q.innerRightProduct(emax).cpp();
puts "qs = (e1dual /\\ e2dual) |_ emaxd = " + str(qs);
qt = plane1.innerLeftProduct(e2dual).cpp();
puts "qt = e1 _| e2dual                = " + str(qt);
puts;

# compute dual line(gerade) of g1, g2
g1dual = g1.innerRightProduct(emax).cpp();
g2dual = g2.innerRightProduct(emax).cpp();
puts "g1dual = g1 |_ emax = " + str(g1dual);
puts "g2dual = g2 |_ emax = " + str(g2dual);

# compute intersection of g1..e2 and g2..e1
s1 = plane2.innerLeftProduct(g1dual).cpp();
puts "s1 = e2 _| g1dual = " + str(s1);
s2 = plane1.innerLeftProduct(g2dual).cpp();
puts "s2 = e1 _| g2dual = " + str(s2);
puts

# check intersection of s..qs, qs..e1 and qs..e2
puts " s /\\ qs     =  s \\in qs     = " + str( s * qs);
puts "qs /\\ plane1 = qs \\in plane1 = " + str(qs * plane1);
puts "qs /\\ plane2 = qs \\in plane2 = " + str(qs * plane2);
puts
