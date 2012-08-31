#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# example: Algebraic Statistics
# Drton, Sturmfels, Sullivant, example 2.1.3


#r = PolyRing(QQ(),"l1,l2",PolyRing.grad);
r = PolyRing.new(QQ(),"l1,l2",PolyRing.lex);
puts "Ring: " + r.to_s;
puts;

puts "gens: " + one.to_s + "," + l1.to_s + ", " + l2.to_s;
puts;

u0 = 3; u1 = 5; u2 = 7; u12 = 11;

f1 = (u1+u12)*(l1+l2+2)*(l1+1)*(l1+l2+1)\
     + (u12)*l1*(l1+1)*(l1+l2+1)\
     - (u2+u12)*l1*(l1+l2+2)*(l1+l2+1)\
     - (u0+u1+u2+u12)*l1*(l1+l2+2)*(l1+1)  ;

f2 = (u2+u12)*(l1+l2+2)*(l2+1)*(l1+l2+1)\
     + (u12)*l2*(l2+1)*(l1+l2+1)\
     - (u1+u12)*l2*(l1+l2+2)*(l1+l2+1)\
     - (u0+u1+u2+u12)*l2*(l1+l2+2)*(l2+1)  ;

puts "f1: " + str(f1);
puts;
puts "f2: " + str(f2);
puts

h = l1*l2*(l1+1)*(l2+1)*(l1+l2+1)*(l1+l2+2);
puts "h: " + str(h);
puts

F = r.ideal("",[f1,f2]);
puts "F = " + str(F);
puts

H = r.ideal("",[h]);
puts "H = " + str(H);
puts

G = F.GB();
puts "G = " + str(G);
puts

#startLog();

Q = G.sat(H);
puts "Q = " + str(Q);
puts

#D = Q.radicalDecomp();
#puts "D = " + str(D);
#puts

R = Q.realRoots();
puts "R = " + str(R);
puts

#startLog();

terminate();
#sys.exit(); 

