#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# logic example from Kreutzer JdM 2008

#r = Ring.new( "Mod 2 (a,f,p,u) G" );
r = PolyRing.new( GF(2), "(a,f,p,u)", PolyRing.grad );
puts "Ring: " + str(r);
puts;

ks = """
(
 ( a^2 - a ),
 ( f^2 - f ),
 ( p^2 - p ),
 ( u^2 - u )
)
""";

ps = """
(
 ( p f + p ),
 ( p u + p + u + 1 ),
 ( a + u + 1 ),
 ( a + p + 1 )
)
""";


k = r.ideal( ks );
p = r.ideal( ps );

f = k.sum( p );

puts "Ideal: " + str(f);
puts;

rg = f.GB();
puts "Output:" + str(rg);
puts;


