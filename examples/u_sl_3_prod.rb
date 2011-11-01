#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# U(sl_3) example

rs = """
# solvable polynomials, U(sl_3):
Rat(Xa,Xb,Xc,Ya,Yb,Yc,Ha,Hb) G
RelationTable
(
 ( Xb ), ( Xa ), ( Xa Xb - Xc ),
 ( Ya ), ( Xa ), ( Xa Ya - Ha ),
 ( Yc ), ( Xa ), ( Xa Yc + Yb ),
 ( Ha ), ( Xa ), ( Xa Ha + 2 Xa ),
 ( Hb ), ( Xa ), ( Xa Hb - Xa),

 ( Yb ), ( Xb ), ( Xb Yb - Hb ),
 ( Yc ), ( Xb ), ( Xb Yc - Ya ),
 ( Ha ), ( Xb ), ( Xb Ha - Xb ),
 ( Hb ), ( Xb ), ( Xb Hb + 2 Xb ),

 ( Ya ), ( Xc ), ( Xc Ya + Xb ),
 ( Yb ), ( Xc ), ( Xc Yb - Xa ),
 ( Yc ), ( Xc ), ( Xc Yc - Ha - Hb ),
 ( Ha ), ( Xc ), ( Xc Ha + Xc ),
 ( Hb ), ( Xc ), ( Xc Hb + Xc ),

 ( Yb ), ( Ya ), ( Ya Yb + Yc ),
 ( Ha ), ( Ya ), ( Ya Ha - 2 Ya ),
 ( Hb ), ( Ya ), ( Ya Hb + Ya ),

 ( Ha ), ( Yb ), ( Yb Ha + Yb ),
 ( Hb ), ( Yb ), ( Yb Hb - 2 Yb ),

 ( Ha ), ( Yc ), ( Yc Ha - Yc ),
 ( Hb ), ( Yc ), ( Yc Hb - Yc )
 
)
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;


ps = """
(
 ( Xb + Yb )
)
""";
# ( Xa + Xb + Xc + Ya + Yb + Yc + Ha + Hb )

f = SolvableIdeal.new( r, ps );
puts "SolvIdeal: " + str(f);
puts;

#startLog();

fl = f.list;
puts "fl: " + str(fl);

p = fl[0];
puts "p: " + str(p);
puts;

#from java.lang import System
p2 = p;
n = 15;
t = System.currentTimeMillis();
for i in 1..n
    p2 = p2.multiply(p);
    t1 = System.currentTimeMillis() -t;
    puts "one product in #{t1} ms";
end

puts "p^#{n}.length: " + str(p2.length());
puts;

p2 = p;
t = System.currentTimeMillis();
for i in 1..n
    p2 = p2.multiply(p);
    t1 = System.currentTimeMillis() -t;
    puts "one product in #{t1} ms";
end

puts "p^#{n}.length: " + str(p2.length());
puts;


ps = """
(
 ( Xa ),
 ( Xb ),
 ( Xc ),
 ( Ya ),
 ( Yb ),
 ( Yc ),
 ( Ha ),
 ( Hb )
)
""";

f = SolvableIdeal.new( r, ps );
#puts "SolvableIdeal: " + str(f);
#puts;

fl = f.list;
Yb = fl[4];
p1 = Yb;
Xb = fl[1];
p2 = Xb;

#n = 10;
t = System.currentTimeMillis();
for i in 1..n
    p1 = p1.multiply(Yb);
    p2 = p2.multiply(Xb);
    p  = p1.multiply(p2);
    t1 = System.currentTimeMillis() -t;
    puts "products in #{t1} ms";
end

puts "Xb^#{n} * Yb^#{n} .length(): " + str(p.length);
puts;
pp = p;

p1 = Yb;
p2 = Xb;
t = System.currentTimeMillis();
for i in 1..n
    p1 = p1.multiply(Yb);
    p2 = p2.multiply(Xb);
    p  = p1.multiply(p2);
    t1 = System.currentTimeMillis() -t;
    puts "products in #{t1} ms";
end

puts "Xb^#{n} * Yb^#{n} .length(): " + str(p.length());
puts;

puts "pp == p: " + str(pp == p);
puts;

#puts "SolvIdeal: " + str(f);
#puts;
