#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# example: Algebraic Statistics
# Drton, Sturmfels, Sullivant, example 2.1.3


r = PolyRing.new(RF(PolyRing.new(QQ(),"u0,u1,u2,u12",PolyRing.lex)),"l1,l2",PolyRing.grad);
puts "Ring: " + str(r);
puts;

puts one, u0, u1, u2, u12, l1 ,l2;
puts;

f1 = (u1+u12)*(l1+l2+2)*(l1+1)*(l1+l2+1)\
     + (u12)*l1*(l1+1)*(l1+l2+1)\
     - (u2+u12)*l1*(l1+l2+2)*(l1+l2+1)\
     - (u0+u1+u2+u12)*l1*(l1+l2+2)*(l1+1)  ;

f2 = (u2+u12)*(l1+l2+2)*(l2+1)*(l1+l2+1)\
     + (u12)*l2*(l2+1)*(l1+l2+1)\
     - (u1+u12)*l2*(l1+l2+2)*(l1+l2+1)\
     - (u0+u1+u2+u12)*l2*(l1+l2+2)*(l2+1)  ;

puts "f1 = " + str(f1);
puts;
puts "f2 = " + str(f2);
puts

#h = l1*l2*(l1+1)*(l2+1)*(l1+l2+1)*(l1+l2+2);
h = l1*l2*(l1+1);
hp = (l2+1);
hpp = (l1+l2+1)*(l1+l2+2);

puts "h = " + str(h);
puts

F = r.ideal("",[f1,f2]);
puts "F = " + str(F);
puts

H = r.ideal("",[h]);
puts "H = " + str(H);
puts

Hp = r.ideal("",[hp]);
puts "Hp = " + str(Hp);
puts

Hpp = r.ideal("",[hpp]);
puts "Hpp = " + str(Hpp);
puts

startLog();

G = F.GB();
puts "G = " + str(G);
puts

Q = G.sat(H);
puts "Q = " + str(Q);
puts

Qp = Q.sat(Hp);
puts "Qp = " + str(Qp);
puts

Qpp = Q.sat(Hpp);
puts "Qpp = " + str(Qpp);
puts

D = Qpp.radicalDecomp();
puts "D = " + str(D);
puts

#Di = Qpp.decomposition();
#puts "Di = " + str(Di);
#puts

#startLog();

terminate();
#sys.exit(); 

