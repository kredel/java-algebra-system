#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# U(sl_2_e) example

rs = """
# solvable polynomials, U(sl_2_e):
Rat(e,h) G
RelationTable
(
 ( h ), ( e ), ( e h + 2 e )
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;


ps = """
(
 ( e^2 + h^3 )
)
""";

f = r.ideal( ps );
puts "SolvableIdeal: " + str(f);
puts;


rg = f.leftGB();
puts "seq left Output: " + str(rg);
puts;


rg = f.twosidedGB();
puts "seq twosided Output: " + str(rg);
puts


rg = f.rightGB();
puts "seq right GB: " + str(rg);
puts;

