#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# trinks 7 example

r = PolyRing.new(QQ(), "B,S,T,Z,P,W", PolyRing.lex );
puts "Ring: " + str(r);
puts;
one,B,S,T,Z,P,W = r.gens(); # capital letter variables not automaticaly included

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 * B * S + 3 * B**2;
f7 = 10000 * B**2 + 6600 * B + 2673;

f = r.ideal( "", [f1,f2,f3,f4,f5,f6,f7] );
puts "Ideal: " + str(f);
puts;

#exit()

rg = f.GB();
puts "GB: " + str(rg);
puts;

#startLog();

s = rg.syzygy();
puts "syzygy: " + str(s);
puts;

t = rg.isSyzygy(s);
puts "is syzygy: " + str(t);
puts "is syzygy: " + str(s.isSyzygy(rg));
puts;


sg = s.GB();
puts "seq module GB: sg = " + str(sg);
puts

sm = sg.syzygy();
puts "syzygy: sm = " + str(sm);
puts;

puts "is syzygy: " + str(sm.isSyzygy(sg));
puts;

