#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# sicora, e-gb example

r = PolyRing.new(ZZ(),"t",Order::INVLEX)
puts "Ring: " + str(r);
puts;
#automatic: one,t = r.gens();

f1 = 2 * t + 1;
f2 = t**2 + 1;

ff = r.ideal( "", [f1,f2] );
puts "ideal: " + str(ff);
puts;

gg = ff.eGB();
puts "seq e-GB: " + str(gg);
puts "is e-GB: " + str(gg.iseGB());
puts;

f = t**3;
n = gg.eReduction(f);
puts "e-Reduction = " + str(n);

