#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Quantum plane example

rsan = """
AN[ (i) (i^2 + 1) ] (Y,X,x,y) G
RelationTable
(
 ( y ), ( x ), ( {i} x y )
 ( X ), ( Y ), ( {i} Y X )
)
""";

rsc = """
C(Y,X,x,y) G |2|
#C(Y,X,x,y) G 
RelationTable
(
 ( y ), ( x ), ( 0i1 x y )
 ( X ), ( Y ), ( 0i1 Y X )
)
""";

r = SolvableModule.new( rsc );
#r = SolvableModule.new( rsan );
puts "SolvableModule: " + str(r);
puts;


ps = """
(
 ( ( x + 1 ), ( y ) ),
 ( ( x y ), ( 0 ) ),
 ( ( x - X ), ( x - X ) ),
 ( ( y - Y ), ( y - Y ) )
)
""";

f = SolvableSubModule.new( r, ps );
puts "SolvableSubModule: " + str(f);
puts;

#exit()

lg = f.leftGB();
puts "seq left GB: " + str(lg);
puts "is left GB: " + str(lg.isLeftGB());
puts;


tg = f.twosidedGB();
puts "seq twosided GB: " + str(tg);
puts "is twosided GB: " + str(tg.isTwosidedGB());
puts "is right GB: " + str(tg.isRightGB());
puts;

#startLog();

# not supported for split term orders:
#rg = f.rightGB();
#puts "seq right GB: " + str(rg);
#puts "is right GB: " + str(rg.isRightGB());
#puts;

#startLog();
terminate();
