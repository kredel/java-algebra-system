#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"

# example polynomial with multivariate powerseries coefficients

startLog();

#s = MPS(QQ(),"x,y");
s = MultiSeriesRing.new("",8,nil,QQ(),"x,y");
puts "s: " + s.to_s;

##one,x,y = s.gens();
##puts "1: " + one.to_s;
##puts "x: " + x.to_s;
##puts "y: " + y.to_s;

r = PolyRing.new(s.ring,"z");
puts "r: " + r.to_s;
puts;
puts "z: " + z.to_s;

#one,x,y,z = r.gens();
puts "x: " + x.to_s;
puts "y: " + y.to_s;

p = y + z + (x+1) * z**2;
#p = z + z**2;
puts "p: " + p.to_s;

q = p.monic();
puts "q: " + q.to_s;

o = p / q;
puts "o: " + o.to_s;

o2 = o*q;
puts "o*q: " + o2.to_s;
puts "p == o*q: " + (p == o2).to_s;
puts;

o = p*q;
puts "p*q: " + o.to_s;
puts;

#terminate();
