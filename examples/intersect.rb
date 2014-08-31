#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# ideal intersection example

#r = Ring.new( "Rat(x,y,z) L" );
r = PolyRing.new( QQ(), "(x,y,z)", PolyRing.lex );
puts "Ring: " + str(r);
puts;

ps1 = """
(
 ( x - 1 ),
 ( y - 1 ),
 ( z - 1 )
)
""";

ps2 = """
(
 ( x - 2 ),
 ( y - 3 ),
 ( z - 3 )
)
""";

F1 = r.ideal( ps1 );
#puts "Ideal: " + str(F1);
#puts;

F2 = r.ideal( ps2 );
#puts "Ideal: " + str(F2);
#puts;

#startLog();

rg1 = F1.GB();
puts "rg1 = " + str(rg1);
puts;

rg2 = F2.GB();
puts "rg2 = " + str(rg2);
puts;

#startLog();

ig = F1.intersect(F2);
puts "rg1 intersect rg2 = " + str(ig);
puts;

terminate();
