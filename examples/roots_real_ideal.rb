#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: complex roots over Q

r = PolyRing.new(QQ(),"I,x,y,z",PolyRing.lex);
puts "Ring: " + str(r);
puts;

one,I,x,y,z = r.gens();

f1 = z - x - y * I;
f2 = I**2 + 1;

#f3 = z**3 - 2;
f3 = z**3 - 2*I;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

ff = r.ideal( "", list=[f1,f2,f3] );
puts "ff = " + str(ff);
puts;

startLog();

gg = ff.GB();
puts "gg = " + str(gg);
puts;


#terminate();
#sys.exit();

r = PolyRing.new(QQ(),"x,y",PolyRing.lex);
puts "Ring: " + str(r);
puts;


one,x,y = r.gens();

#    y**3 - 3 * I * x * y**2 - 3 * x**2 * y + I * x**3 - 2 * I = z**3 - 2 
#fr = y**3 - 3 * x**2 * y; 
#fi = -3 * x * y**2 + x**3 - 2;

#    y**3 - 3 * I * x * y**2 - 3 * x**2 * y + I * x**3 + 2 = z**3 - 2 I
fr = y**3 - 3 * x**2 * y  - 2; 
fi = -3 * x * y**2 + x**3;

puts "fr = " + str(fr);
puts "fi = " + str(fi);
puts;

ff = r.ideal( "", list=[fr,fi] );
puts "ff = " + str(ff);
puts;

gg = ff.GB();
puts "gg = " + str(gg);
puts;

t = System.currentTimeMillis();
R = gg.realRoots();
t = System.currentTimeMillis() - t;
puts "R = " + str(R);
puts;
puts "real roots: ";
gg.realRootsPrint()
puts;
puts "real roots time = " + str(t) + " milliseconds";
puts;

puts "gg = " + str(gg);
puts;

#startLog();
terminate();

