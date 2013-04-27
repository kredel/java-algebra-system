#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# WA_32 solvable polynomial example

rs = """
# solvable polynomials, Weyl algebra A_3,2:
Rat(a,b,e1,e2,e3) G|3|
RelationTable
(
 ( e3 ), ( e1 ), ( e1 e3 - e1 ),
 ( e3 ), ( e2 ), ( e2 e3 - e2 )
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;


ps = """
(
 ( e1 e3^3 + e2^10 - a ),
 ( e1^3 e2^2 + e3 ),
 ( e3^3 + e3^2 - b )
)
""";

f = r.ideal( ps );
puts "SolvableIdeal: " + str(f);
puts;


rg = f.leftGB();
puts "seq left GB: " + str(rg);
puts;


rg = f.twosidedGB();
puts "seq twosided GB: " + str(rg);
puts;


rg = f.rightGB();
puts "seq right GB: " + str(rg);
puts;

startLog();
terminate();
