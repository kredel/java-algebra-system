#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# e-gb and d-gb example to compare with hermit normal form

r = PolyRing.new( ZZ(), "x4,x3,x2,x1", PolyRing.lex );
puts "Ring: " + str(r);
puts;

#is automatic: one,x4,x3,x2,x1 = r.gens();

f1 = x1 + 2 * x2 + 3 * x3 + 4 * x4 + 3;
f2 =      3 * x2 + 2 * x3 +     x4 + 2;
f3 =               3 * x3 + 5 * x4 + 1;
f4 =                        5 * x4 + 4;

L = [f1,f2,f3,f4];
#puts "L = " + str(L);

f = r.ideal( "", L );
puts "Ideal: " + str(f);
puts;

#startLog();

g = f.eGB();
puts "seq e-GB: " + str(g);
puts "is e-GB: " + str(g.iseGB());
puts;

#sys.exit();

d = f.dGB();
puts "seq d-GB: " + str(d);
puts "is d-GB: " + str(d.isdGB());
puts;

#startLog();

puts "d-GB == e-GB:" + str(g.pset.equals(d.pset));
