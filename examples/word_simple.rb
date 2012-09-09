#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"

# non-commutative polynomial examples: simple test

r = WordPolyRing.new(QQ(),"x,y");
puts "WordPolyRing: " + str(r);
puts;

one,x,y = r.gens();
puts "one = " + str(one);
puts "x = " + str(x);
puts "y = " + str(y);
puts;

f1 = x*y - 1/10;
f2 = y*x + x + y;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts;

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;

puts "c1 = " + str(c1);
puts "c2 = " + str(c2);
puts "s  = " + str(s);
puts;

ff = r.ideal( "", [f1,f2] );
puts "ff = " + str(ff);
puts;

#startLog();

gg = ff.GB();
puts "gg = " + str(gg);
puts "isGB(gg) = " + str(gg.isGB());
puts;


ff = r.ideal( "", [f1,f2,c1,c2,s] );
puts "ff = " + str(ff);
puts;

gg = ff.GB();
puts "gg = " + str(gg);
puts "isGB(gg) = " + str(gg.isGB());
puts;

p = r.random(3,6,4);
puts "p = " + str(p);
puts;

pp = p**5;
puts "pp = " + str(pp.to_s.length);
puts "p == pp: " + str(p == pp);
puts "pp == pp: " + str(pp == pp);
puts "pp-pp == 0: " + str(pp-pp == 0);
puts;

#exit(0);

ri = WordPolyRing.new(ZZ(),"x,y");
puts "WordPolyRing: " + str(ri);
puts;

one,x,y = ri.gens();
puts "one = " + str(one);
puts "x = " + str(x);
puts "y = " + str(y);
puts;

f1 = x*y - 10;
f2 = y*x + x + y;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts;

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;

puts "c1 = " + str(c1);
puts "c2 = " + str(c2);
puts "s  = " + str(s);
puts;

fi = ri.ideal( "", [f1,f2] );
puts "fi = " + str(fi);
puts;

#not implemented:
#Gi = Fi.GB();
#puts "Gi = " + str(Gi);
#puts "isGB(Gi) = " + str(Gi.isGB());
#puts;

#exit(0);

rp = WordPolyRing.new(GF(23),"x,y");
puts "WordPolyRing: " + str(rp);
puts;

one,x,y = rp.gens();
puts "one = " + str(one);
puts "x = " + str(x);
puts "y = " + str(y);
puts;

f1 = x*y - 10;
f2 = y*x + x + y;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts;

c1 = f1 * f2;
c2 = f2 * f1;
s = c1 - c2;
s1 = 22 * y*x*x*y + x*y*y*x + 22 * y*x*y + x*y*y + x*y*x + 22 * x*x*y;

puts "c1 = " + str(c1);
puts "c2 = " + str(c2);
puts "s  = " + str(s);
puts "s1 = " + str(s1);
puts "s == s1: " + str(s==s1);
puts;

fp = rp.ideal( "", [f1,f2] );
puts "fp = " + str(fp);
puts;

gp = fp.GB();
puts "gp = " + str(gp);
puts "isGB(gp) = " + str(gp.isGB());
puts;
