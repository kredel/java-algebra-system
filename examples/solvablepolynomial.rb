#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# WA_32 solvable polynomial example

rs = """
# solvable polynomials, Weyl algebra A_3,2:
#Rat(a,b,e1,e2,e3) G|3|
Quat(a,b,e1,e2,e3) G|3|
RelationTable
(
 ( e3 ), ( e1 ), ( e1 e3 - e1 ),
 ( e3 ), ( e2 ), ( e2 e3 - e2 )
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;


one,I,J,K,a,b,e1,e2,e3 = r.gens();

f1 = e1 * e3**3 + e2**10 - a;
f2 = e1**3 * e2**2 + e3;
f3 = e3**3 + e3**2 - b;

f4 = ( e3**2 * e2**3 + e1 )**3;

puts "f1: " + str(f1);
puts "f2: " + str(f2);
puts "f3: " + str(f3);
puts "f4: " + str(f4);
puts;

ff = r.ideal( "", [f1,f2,f3] );
puts "SolvableIdeal: " + str(ff);
puts;

#exit();

rg = ff.leftGB();
puts "seq left GB: " + str(rg);
puts;


rg = ff.twosidedGB();
puts "seq twosided GB: " + str(rg);
puts;


rg = ff.rightGB();
puts "seq right GB: " + str(rg);
puts;

startLog();
terminate();
