#
# jython examples for jas.
# $Id$
#

from jas import ExtPolyRing, RingElem, ZZ


# exterior calculus plane intersection example
# after Blonski, 1983.

print;

#r = ExtPolyRing( ZZ(), 4, "E" );
r = ExtPolyRing( ZZ(), 4 );
print;
print "ExtPolyRing: " + str(r);

gg = r.gens();
#for g in gg
#  print "gens(): " + str(g);
#end
#print;

# constant names (upper case) need explicit assignment
one = gg[0];
e1  = gg[1];
e2  = gg[2];
e3  = gg[3];
e4  = gg[4];

# variable names (lower case) are implicitly assigned
print "one: " + str(one);
#print "r.E1:  " + str(r.E1);
#print "r.e1:  " + str(r.e1);
print "E1:  " + str(E1);
print "e1:  " + str(e1);
print "e2:  " + str(e2);
print "e3:  " + str(e3);
print "e4:  " + str(e4);
print;

print "E1*E1: " + str(E1*E1);
print "e1*e1: " + str(e1*e1);
print "e2*e1: " + str(e2*e1);
print;

# define points in 4-space as polynomials
#p1 = ("1 E(1) + 5 E(2) - 2 E(3) + 1 E(4)");
#p2 = ("4 E(1) + 3 E(2) + 6 E(3) + 1 E(4)");
p1 = e1 + 5*e2 - 2* e3 + e4;
p2 = 4 * e1 + 3 * e2 + 6 * e3 + e4;
print "p1:  " + str(p1);
print "p2:  " + str(p2);

#q1 = ("3 E(1) - 2 E(2) - 1 E(3) + 1 E(4)");
#q2 = ("1 E(2) + 5 E(3) + 1 E(4)");
q1 = 3 * e1 - 2 * e2 - e3 + e4;
q2 = e2 + 5 * e3 + e4;
print "q1:  " + str(q1);
print "q2:  " + str(q2);

#s = ("1 E(3) + 1 E(4)");
s = e3 + e4;
print "s:   " + str(s);
print;

# compute line(gerade) p1..p2 and q1..q2
#g1 = p1.multiply(p2).abs();
#g2 = q1.multiply(q2).abs().divide(new BigInteger(3));
g1 = abs((p1 * p2));
g2 = (q1 * q2).cpp();
print "g1 = p1 /\\ p2 = " + str(g1);
print "g2 = q1 /\\ q2 = " + str(g2);
#print "cpp(g2) = " + str(g22);
print;

# compute plane g1..s and g2..s
#e1 = g1.multiply(s).abs().divide(new BigInteger(17));
#e2 = g2.multiply(s);
plane1 = (g1 * s).cpp();
plane2 = g2 * s;
print "plane1 = g1 /\\ s = " + str(plane1);
print "plane2 = g2 /\\ s = " + str(plane2);
print;

#emax = r.ring.ixfac.imax;
emax = RingElem( r.ring.getIMAX() );
print "emax = " + str(emax);

# compute dual planes of e1, e2 as e1..emax and e2..emax
e1dual = abs(plane1.innerRightProduct(emax));
e2dual = abs(plane2.innerRightProduct(emax));
print "e1dual = plane1 |_ emax = " + str(e1dual);
print "e2dual = plane2 |_ emax = " + str(e2dual);
print;

# compute intersection of plane plane1, plane2 via dual planes sum
q = (e1dual * e2dual).cpp();
print "q = e1dual /\\ e2dual             = " + str(q);
qs = q.innerRightProduct(emax).cpp();
print "qs = (e1dual /\\ e2dual) |_ emaxd = " + str(qs);
qt = plane1.innerLeftProduct(e2dual).cpp();
print "qt = plane1 _| e2dual            = " + str(qt);
print;

# compute dual line(gerade) of g1, g2
g1dual = g1.innerRightProduct(emax).cpp();
g2dual = g2.innerRightProduct(emax).cpp();
print "g1dual = g1 |_ emax = " + str(g1dual);
print "g2dual = g2 |_ emax = " + str(g2dual);

# compute intersection of g1..e2 and g2..e1
s1 = plane2.innerLeftProduct(g1dual).cpp();
print "s1 = plane2 _| g1dual = " + str(s1);
s2 = plane1.innerLeftProduct(g2dual).cpp();
print "s2 = plane1 _| g2dual = " + str(s2);
print

# check intersection of s..qs, qs..e1 and qs..e2
print " s /\\ qs     =  s \\in qs     = " + str( s * qs);
print "qs /\\ plane1 = qs \\in plane1 = " + str(qs * plane1);
print "qs /\\ plane2 = qs \\in plane2 = " + str(qs * plane2);
print
