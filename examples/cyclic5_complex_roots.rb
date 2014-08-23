#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: complex roots over Q for zero dimensional ideal `cyclic5'

r = PolyRing.new(QQ(),"a,b,c,d,e",PolyRing.lex);
puts "Ring: " + str(r);
puts;

#one,q,w,s,x = r.gens();

f1 = a + b + c + d + e;
f2 = a*b + b*c + c*d + d*e + e*a;
f3 = a*b*c + b*c*d + c*d*e + d*e*a + e*a*b;
f4 = a*b*c*d + b*c*d*e + c*d*e*a + d*e*a*b + e*a*b*c;
f5 = a*b*c*d*e - 1;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts "f4 = " + str(f4);
puts "f5 = " + str(f5);
puts;

F = r.ideal( "", list=[f1,f2,f3,f4,f5] );
puts "F = " + str(F);
puts;

startLog();

t = System.currentTimeMillis();
R = F.complexRoots();
#R = F.realRoots();
t = System.currentTimeMillis() - t;
puts "complex decomposition = " + str(R);
puts;
puts "complex roots = ";
F.complexRootsPrint()
puts "complex roots time = " + str(t) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
