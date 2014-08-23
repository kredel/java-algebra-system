#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# 2 univariate polynomials of degree 2 example for comprehensive GB
# integral/rational function coefficients

#r = Ring.new( "IntFunc(a3, b3, a2, b2, a1, b1, a0, b0) (x) L" );
r = PolyRing.new( PolyRing.new(ZZ(),"(a3, b3, a2, b2, a1, b1, a0, b0)",PolyRing.lex),"(x)", PolyRing.lex );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( { a3 } x^3 + { a2 } x^2 + { a1 } x + { a0 } ),
 ( { b3 } x^3 + { b2 } x^2 + { b1 } x + { b0 } )
) 
""";

p1 = a3 * x**3 + a2 * x**2 + a1 * x + a0;
p2 = b3 * x**3 + b2 * x**2 + b1 * x + b0;

#f = r.paramideal( ps );
f = r.paramideal( "", [p1,p2] );
puts "ParamIdeal: " + str(f);
puts;

#sys.exit(); # long run time

startLog();

gs = f.CGBsystem();
puts "CGBsystem: " + str(gs);
puts;

#sys.exit();

bg = gs.isCGBsystem();
puts "isCGBsystem: " + str(bg);
puts;

#sys.exit();

gs = f.CGB();
puts "CGB: " + str(gs);
puts;

#sys.exit();

#bg = gs.isCGB();
#puts "isCGB: " + str(bg);
#puts;

terminate();

