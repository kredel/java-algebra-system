#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# pppj 2006 paper examples

r = PolyRing.new( ZZ(), "x1,x2,x3", PolyRing.lex );
puts "Ring: " + str(r);
puts;


f = 3 * x1**2 * x3**4 + 7 * x2**5 - 61;

puts "f = " + str(f);
puts;

id = r.ideal( "", [f] );
puts "Ideal: " + str(id);
puts;

ri = r.ring;
puts "ri = " + str(ri);

pol = id.pset;
puts "pol = " + str(pol);
puts

pol = ri.parse( str(f) );
puts "pol = " + str(pol);
puts;

#startLog();
