#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# U(sl_3) example

rs = """
# solvable polynomials, U(sl_3):
Rat(Xa,Xb,Xc,Ya,Yb,Yc,Ha,Hb) G
RelationTable
(
 ( Xb ), ( Xa ), ( Xa Xb - Xc ),
 ( Ya ), ( Xa ), ( Xa Ya - Ha ),
 ( Yc ), ( Xa ), ( Xa Yc + Yb ),
 ( Ha ), ( Xa ), ( Xa Ha + 2 Xa ),
 ( Hb ), ( Xa ), ( Xa Hb - Xa),

 ( Yb ), ( Xb ), ( Xb Yb - Hb ),
 ( Yc ), ( Xb ), ( Xb Yc - Ya ),
 ( Ha ), ( Xb ), ( Xb Ha - Xb ),
 ( Hb ), ( Xb ), ( Xb Hb + 2 Xb ),

 ( Ya ), ( Xc ), ( Xc Ya + Xb ),
 ( Yb ), ( Xc ), ( Xc Yb - Xa ),
 ( Yc ), ( Xc ), ( Xc Yc - Ha - Hb ),
 ( Ha ), ( Xc ), ( Xc Ha + Xc ),
 ( Hb ), ( Xc ), ( Xc Hb + Xc ),

 ( Yb ), ( Ya ), ( Ya Yb + Yc ),
 ( Ha ), ( Ya ), ( Ya Ha - 2 Ya ),
 ( Hb ), ( Ya ), ( Ya Hb + Ya ),

 ( Ha ), ( Yb ), ( Yb Ha + Yb ),
 ( Hb ), ( Yb ), ( Yb Hb - 2 Yb ),

 ( Ha ), ( Yc ), ( Yc Ha - Yc ),
 ( Hb ), ( Yc ), ( Yc Hb - Yc )
 
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;


ps = """
(
 ( Xa + Hb ),
 ( Xb + Ha Hb )
)
""";

f = SolvableIdeal.new( r, ps );
puts "SolvableIdeal: " + str(f);
puts;

#startLog();

rg = f.leftGB();
puts "seq left Output:" + str(rg);
puts;


rg = f.twosidedGB();
puts "seq twosided Output:" + str(rg);
puts;


rg = f.rightGB();
puts "seq right GB: " + str(rg);
puts;
