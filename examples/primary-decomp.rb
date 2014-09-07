#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: ideal primary decomposition in char 0

r = PolyRing.new(QQ(),"x,y,z",PolyRing.lex);
puts "Ring: " + str(r);
puts;

#automatic: one,x,y,z = r.gens();
puts one,x,y,z;

#exit();

f1 = (x**2 - 2)**2;
f2 = (y**2 - x)**2;
f3 = (z**2 - x); 

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
#puts "f4 = " + str(f4);
puts;

F = r.ideal( "", list=[f1,f2,f3] );
#F = r.ideal( "", list=[f1,f3] );
#F = r.ideal( "", list=[f2,f3] );

puts "F = " + str(F);
puts;

startLog();

t = System.currentTimeMillis();
P = F.primaryDecomp();
t = System.currentTimeMillis() - t;
puts "P = " + str(P);
puts;
puts "decomp time = " + str(t) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
