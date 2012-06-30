#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# 2 univariate polynomials of degree 2 example for comprehensive GB
# integral/rational function coefficients

r = PolyRing.new( PolyRing.new(QQ(),"a",PolyRing.lex),"x,y,z", PolyRing.lex );
puts "Ring: " + str(r);
puts;

#puts "gens: " + str(r.gens());

f1 = ( x**2 + a * y**2 - x );
f2 = ( a * x**2 + y**2 - y );
f3 = ( x - y ) * z - 1;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);

f = r.paramideal( "", list=[f1,f2,f3] );
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

