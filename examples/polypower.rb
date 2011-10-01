#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# sparse polynomial powers

#r = Ring( "Mod 1152921504606846883 (x,y,z) G" );
#r = Ring( "Rat(x,y,z) G" );
#r = Ring( "C(x,y,z) G" );
r = PolyRing.new( ZZ(), "(x,y,z)", PolyRing.lex );

puts "Ring: " + str(r);
puts;

one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
puts;

p1 = ( 1 + x**2147483647 + y**2147483647 + z**2147483647 );
p2 = ( 1 + x + y + z );
p3 = ( 10000000001 + 10000000001 * x + 10000000001 * y + 10000000001 * z );

puts "p1  = " + str(p1);
puts "p2  = " + str(p2);
puts "p3  = " + str(p3);
puts;

# unused:
ps = """
( 
 ( 1 + x^2147483647 + y^2147483647 + z^2147483647 )
 ( 1 + x + y + z )
 ( 10000000001 + 10000000001 x + 10000000001 y + 10000000001 z )
) 
""";

#f = Ideal.new( r, ps );
f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

plist = f.pset.list;
puts "plist: " + str(plist);
puts;
p = plist[0];
#p = plist[2];


p = p2;
#p = p1;
puts "p: " + str(p);
puts;

q = p;
for i in 1..19
    q = q * p;
end

puts "q: " + str(q.elem.length());
puts;

q1 = q + one;
#puts "q1:", q1;
puts "q1: " + str(q1.elem.length());
puts;

t = System.currentTimeMillis();
q2 = q * q1;
t = System.currentTimeMillis() - t;

puts "q2: " + str(q2.elem.length());
puts "time " + str(t) + " milliseconds";
puts;


#puts "Integer.MAX_VALUE = " +str(Integer::MAX_VALUE);
