#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Raksanyi & Walter example
# integral/rational function coefficients

#r = Ring.new( "RatFunc(a1, a2, a3, a4) (x1, x2, x3, x4) L" );
#r = Ring.new( "IntFunc(a1, a2, a3, a4) (x1, x2, x3, x4) L" );
#r = PolyRing.new( PolyRing.new(QQ(),"a1, a2, a3, a4",PolyRing.lex), "x1, x2, x3, x4", PolyRing.grad);
r = PolyRing.new( PolyRing.new(QQ(),"a1, a2, a3, a4",PolyRing.lex), 
                  "x1, x2, x3, x4", PolyRing.lex);
puts "Ring: " + str(r);
puts;

ps = """
(
 ( x4 - { a4 - a2 } ),
 ( x1 + x2 + x3 + x4 - { a1 + a3 + a4 } ),
 ( x1 x3 + x1 x4 + x2 x3 + x3 x4 - { a1 a4 + a1 a3 + a3 a4 } ),
 ( x1 x3 x4 - { a1 a3 a4 } )
) 
""";

f = r.paramideal( ps );
puts "ParamIdeal: " + str(f);
puts;

#sys.exit();

#startLog();

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#sys.exit();

bg = gs.isCGBsystem();
puts "isCGBsystem: " + str(bg);
puts;


rs = gs.regularRepresentation();
puts "regular representation: " + str(rs);
puts;

rs = gs.regularRepresentationBC();
puts "boolean closed regular representation: " + str(rs);
puts;

startLog();

bg = rs.isRegularGB();
puts "pre isRegularGB: " + str(bg);
puts;

rsg = rs.regularGB();
puts "regular GB: " + str(rsg);
puts;

bg = rsg.isRegularGB();
puts "post isRegularGB: " + str(bg);
puts;


ss = rsg.stringSlice();
puts "regular string slice: " + str(ss);
puts;

terminate();

