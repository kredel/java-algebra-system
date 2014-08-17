#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# example for rational and complex numbers
#
#

zn = ZZ(7);
puts "zn:   " + str(zn);
puts "zn^2: " + str(zn*zn);
puts;

x = 10000000000000000000000000000000000000000000000000;
puts "x:    " + str(x);
rn = QQ(2*x,4*x);
puts "rn:   " + str(rn);
puts "rn^2: " + str(rn*rn);
puts;

rn = QQ(6/4);
puts "rn:   " + str(rn);
puts "rn^2: " + str(rn*rn);
puts;


c = CC();
puts "c:   " + str(c);
c = c.one();
puts "c:   " + str(c);
c = CC(2,3);
puts "c:   " + str(c);
puts "c^5: " + str(c**5 + c.one());
puts;

c = CC( 2,rn );
puts "c:   " + str(c);
puts;


r = PolyRing.new(QQ(), "x,y", PolyRing.lex );
puts "Ring: " + str(r);
puts;

# sage like: with generators for the polynomial ring
one,x,y = r.gens();
zero = r.zero();

begin
    f = RF(r);
rescue
    f = None;
end
puts "f: " + str(f);

d = x**2 + 5 * x - 6;
puts "d: " + str(d);
f = RF(r,d);
puts "f: " + str(f);

n = d*d + y + 1;
f = RF(r,d,n);
puts "f: " + str(f);
puts;

# beware not to mix expressions
f = f**2 - f;
puts "f^2-f: " + str(f);
puts;

f = f/f;
puts "f/f: " + str(f);

f = RF(r,d,one);
puts "f: " + str(f);

f = RF(r,zero);
puts "f: " + str(f);

f = RF(r,d,y); 
puts "f: " + str(f);

puts "one:  " + str(f.one());
puts "zero: " + str(f.zero());
puts;

terminate();
#sys.exit();
