#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# U(sl_2) example

rs = """
# solvable polynomials, U(sl_2):
Rat(e,f,h) G
RelationTable
(
 ( f ), ( e ), ( e f - h ),
 ( h ), ( e ), ( e h + 2 e ),
 ( h ), ( f ), ( f h - 2 f ) 
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;


ps = """
(
 ( e^2 + f^3 )
)
""";

f = SolvableIdeal.new( r, ps );
puts "SolvableIdeal: " + str(f);
puts;

#startLog();

rg = f.leftGB();
puts "seq left GB: " + str(rg);
puts;


rg = f.twosidedGB();
puts "seq twosided GB: " + str(rg);
puts;


rg = f.rightGB();
puts "seq right GB: " + str(rg);
puts;

