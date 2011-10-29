#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Raksanyi & Walter example
# rational function coefficients

#r = Ring.new( "RatFunc(a1, a2, a3, a4) (x1, x2, x3, x4) G" );
r = PolyRing.new( RF(PolyRing.new(ZZ(),"a1, a2, a3, a4",PolyRing.lex)), "x1, x2, x3, x4", PolyRing.grad );
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

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

rg = f.GB();
rg = f.GB();
rg = f.GB();
puts "GB:" + str(rg);
puts;

terminate();
