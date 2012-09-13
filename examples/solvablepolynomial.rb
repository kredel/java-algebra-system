#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# WA_32 solvable polynomial example

p = PolyRing.new(QQ(),"a,b,e1,e2,e3");
#is automatic: [one,a,b,e1,e2,e3] = p.gens();

relations = [e3, e1, e1*e3 - e1,
             e3, e2, e2*e3 - e2];

puts "relations: =" + relations.join(", ") { |r| r.to_s };
puts;


rp = SolvPolyRing.new(QQ(), "a,b,e1,e2,e3", PolyRing.lex, relations);
puts "SolvPolyRing: " + str(rp);
puts;

puts "gens =" + rp.gens().join(", ") { |r| r.to_s };
one,a,b,e1,e2,e3 = rp.gens();

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

#puts "f1 = ", f1;
#puts "f2 = ", f2;
#puts "f3 = ", f3;

F = [ f1, f2, f3 ];
puts "F =" + F.join(", ") { |r| r.to_s };
puts

I = rp.ideal( "", F );
puts "SolvableIdeal: " + str(I);
puts;

rgl = I.leftGB();
puts "seq left GB:" + str(rgl);
puts "isLeftGB: " + str(rgl.isLeftGB());
puts;

rgt = I.twosidedGB();
puts "seq twosided GB:" + str(rgt);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts;

rgr = I.rightGB();
puts "seq right GB:" + str(rgr);
puts "isRightGB: " + str(rgr.isRightGB());
puts;

#exit(0);


rs = """
# solvable polynomials, Weyl algebra A_3,2:
Rat(a,b,e1,e2,e3) L
#Quat(a,b,e1,e2,e3) G|3|
RelationTable
(
 ( e3 ), ( e1 ), ( e1 e3 - e1 ),
 ( e3 ), ( e2 ), ( e2 e3 - e2 )
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;

#one,a,b,e1,e2,e3 = r.gens();

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

puts "f1: " + str(f1);
puts "f2: " + str(f2);
puts "f3: " + str(f3);
puts;

ff = r.ideal( "", [f1,f2,f3] );
puts "SolvableIdeal: " + str(ff);
puts;

#exit();

rg = ff.leftGB();
puts "seq left GB: " + str(rg);
puts "isLeftGB: " + str(rgt.isLeftGB());
puts;
puts "rgl == rg: " + str(rgl.list == rg.list);
puts;

#exit();

rg = ff.twosidedGB();
puts "seq twosided GB: " + str(rg);
puts "isTwosidedGB: " + str(rgt.isTwosidedGB());
puts;
puts "rgt == rg: " + str(rgt.list == rg.list);


rg = ff.rightGB();
puts "seq right GB: " + str(rg);
puts "isRightGB: " + str(rgt.isRightGB());
puts;
puts "rgr == rg: " + str(rgr.list == rg.list);

#startLog();
#terminate();

