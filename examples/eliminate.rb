#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# ideal elimination example

r = PolyRing.new( QQ(), "(x,y,z)", PolyRing.grad );
puts "Ring: " + str(r);
puts;

ps1 = """
(
 ( x^2 - 2 ),
 ( y^2 - 3 ),
 ( z^3 - x * y )
)
""";

F1 = r.ideal( ps1 );
puts "Ideal: " + str(F1);
puts;

e = PolyRing.new( QQ(), "(x,z)", PolyRing.grad );
puts "Ring: " + str(e);
puts;

#startLog();

rg1 = F1.eliminateRing(e);
puts "rg1 = " + str(rg1);
puts;

rg2 = rg1.intersectRing(e);
puts "rg2 = " + str(rg2);
puts;

terminate();
#sys.exit();
