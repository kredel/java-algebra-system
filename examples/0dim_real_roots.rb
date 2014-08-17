#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: real roots over Q for zero dimensional ideals

r = PolyRing.new(QQ(),"x,y,z",PolyRing.lex);

puts "Ring: " + str(r);
puts;

#is automatic: one,x,y,z = r.gens();

f1 = (x**2 - 5)*(x**2 - 3)**2;
f2 = y**2 - 3;
f3 = z**3 - x * y;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

F = r.ideal( "", [f1,f2,f3] );

puts "F = " + str(F);
puts;

startLog();

t = System.currentTimeMillis();
R = F.realRoots();
t = System.currentTimeMillis() - t;
puts "R = " + str(R);
puts;
puts "real roots = ";
F.realRootsPrint()
puts;
puts "real roots time = " + str(t) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
