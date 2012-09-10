#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"

# non-commutative polynomial examples: solvable polynomials example

#r = WordPolyRing.new(QQ(),"a,b,e1,e2,e3");
r = WordPolyRing.new(QQ(),"a,b,e,f,g");
puts "WordPolyRing: " + str(r);
puts;

one,a,b,e,f,g = r.gens();
puts "one = " + str(one);
puts "a = " + str(a);
puts "b = " + str(b);
puts "e = " + str(e);
puts "f = " + str(f);
puts "g = " + str(g);
puts;

r1 = g * e - (e*g - e);
r2 = g * f - (f*g - f);
r3 = e * a - a * e;
r4 = e * b - b * e;
r5 = f * a - a * f;
r6 = f * b - b * f;
r7 = g * a - a * g;
r8 = g * b - b * g;

f1 = e * g**3 + f**10 - a;
f2 = e**3 * f**2 + g;
f3 = g**3 + g**2 - b;

puts "r1 = " + str(r1);
puts "r2 = " + str(r2);
puts "r3 = " + str(r3);
puts "r4 = " + str(r4);
puts "r5 = " + str(r5);
puts "r6 = " + str(r6);
puts "r7 = " + str(r7);
puts "r8 = " + str(r8);
puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

ff = r.ideal( "", [r1,r2,r3,r4,r5,r6,r7,r8,f1,f2,f3] );
puts "ff = " + str(ff);
puts;

startLog();

gg = ff.GB();
puts "gg = " + str(gg);
puts "isGB(gg) = " + str(gg.isGB());
puts;

#exit(0);


# now as solvable polynomials

p = PolyRing.new(QQ(),"a,b,e1,e2,e3");
#is automatic: [one,a,b,e1,e2,e3] = p.gens();

relations = [e3, e1, e1*e3 - e1,
             e3, e2, e2*e3 - e2];

puts "relations: = " + relations.join(", ") { |r| r.to_s };
puts;

rp = SolvPolyRing.new(QQ(), "a,b,e1,e2,e3", PolyRing.lex, relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens =" + rp.gens().join(", ") { |r| r.to_s };
one,a,b,e1,e2,e3 = rp.gens();
#one,I,J,K,a,b,e1,e2,e3 = rp.gens();

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

F = [ f1, f2, f3 ];
puts "F = " + F.join(", ") { |r| r.to_s };
puts

I = rp.ideal( "", F );
puts "SolvableIdeal: " + str(I);
puts;

rgt = I.twosidedGB();
puts "seq twosided GB:" + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts
puts "rgt: " + rgt.list.join(", ") { |r| r.to_s };
puts;

