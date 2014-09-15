#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# sicora, e-gb example

r = PolyRing.new(ZZ(),"t",PolyRing.lex)
puts "Ring: " + str(r);
puts;

#automatic: one,x = r.gens();

f1 = 2 * t + 1;
f2 = t**2 + 1;

ff = r.ideal( "", [f1,f2] );
puts "ideal: " + str(ff);
puts;

#t = System.currentTimeMillis();
gg = ff.eGB();
#t = System.currentTimeMillis() - t;

puts "seq e-GB: " + str(gg);
puts;
puts "is e-GB: " + str(gg.iseGB());
#puts "e-GB time = " + str(t) + " milliseconds";
puts;

#startLog();
terminate();
