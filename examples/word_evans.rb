#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"

# non-commutative polynomial examples: evans example

r = WordPolyRing.new(QQ(),"x,y,z");
puts "WordPolyRing: " + str(r);
puts;

one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x = " + str(x);
puts "y = " + str(y);
puts "z = " + str(z);
puts;

f1 = x * y - z;
f2 = y * z + 2 * x + z;
f3 = y * z + x;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

ff = r.ideal( "", [f1,f2,f3] );
puts "ff = " + str(ff);
puts;

startLog();

gg = ff.GB();
puts "gg = " + str(gg);
puts "isGB(gg) = " + str(gg.isGB());
puts;

#exit(0);

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;

puts "c1 = " + str(c1);
puts "c2 = " + str(c2);
puts "s  = " + str(s);
puts;

ff = r.ideal( "", [f1,f2,f3,c1,c2,s] );
puts "ff = " + str(ff);
puts;

gga = ff.GB();
puts "gga = " + str(gga);
puts "isGB(gga) = " + str(gga.isGB());
puts "gg == gga: " + str(gg.list == gga.list);
puts;
