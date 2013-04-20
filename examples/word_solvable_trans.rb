#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"

# non-commutative polynomial examples: solvable polynomials example

r = WordPolyRing.new(QQ(),"a,b,e1,e2,e3");
puts "WordPolyRing: " + str(r);
puts;

one,a,b,e1,e2,e3 = r.gens();
puts "one = " + str(one);
puts "a = " + str(a);
puts "b = " + str(b);
puts "e1 = " + str(e1);
puts "e2 = " + str(e2);
puts "e3 = " + str(e3);
puts;

r1 = e3 * e1 - (e1 * e3 - e1);
r2 = e3 * e2 - (e2 * e3 - e2);
r3 = e1 * a - a * e1;
r4 = e1 * b - b * e1;
r5 = e2 * a - a * e2;
r6 = e2 * b - b * e2;
r7 = e3 * a - a * e3;
r8 = e3 * b - b * e3;

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

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
#is automatic: ?
one,a,b,e1,e2,e3 = p.gens();

relations = [e3, e1, e1*e3 - e1,
             e3, e2, e2*e3 - e2];

puts "relations: = " + relations.join(", ") { |r| r.to_s };
puts;

rp = SolvPolyRing.new( QQ(), "a,b,e1,e2,e3", PolyRing.grad, relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens =" + rp.gens().join(", ") { |r| r.to_s };
one,a,b,e1,e2,e3 = rp.gens();
#one,I,J,K,a,b,e1,e2,e3 = rp.gens();

f1 = - a + e2**10 + e1 * e3**3;
#puts "f1 = " + f1.to_s;

f2 = e1**3 * e2**2 + e3;
#puts "f2 = " + f2.to_s;

f3 = e3**3 + e3**2 - b;
#puts "f3 = " + f3.to_s;
#puts "+ f3 type(#{f3}) = #{f3.elem.class}\n";
#puts "+ f3.val  = " + f3.elem.getMap().map { |e,c| c.to_s + " " + e.to_s }.join(", ");

ff = [ f1, f2, f3 ];
puts "ff = " + ff.join(", ") { |r| r.to_s };
puts

ii = rp.ideal( "", ff );
puts "SolvableIdeal: " + str(ii);
puts;

rgt = ii.twosidedGB();
puts "seq twosided GB:" + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts
puts "rgt: " + rgt.list.join(", ") { |r| r.to_s };
puts;

