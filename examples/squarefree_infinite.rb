#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: squarefree: characteristic p, infinite

r = PolyRing.new( RF(PolyRing.new(ZM(5,field=true),"u, v",PolyRing.lex)), "x y z",PolyRing.lex);
puts "r = " + str(r);
one,u,v,x,y,z = r.gens();
puts "one   = " + str(one);
puts "u     = " + str(u);
puts "v     = " + str(v);
puts "x     = " + str(x);
puts "y     = " + str(y);
puts "z     = " + str(z);

puts "Ring: " + str(r);
puts;

a = r.random(k=1,l=3);
b = r.random(k=1,l=3);
c = r.random(k=1,l=3);

if a.isZERO()
    a = x;
end
if b.isZERO()
    b = y;
end
if c.isZERO()
    c = z;
end

#f = a**2 * b**3 * c;
f = b**5 * c;

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
puts "f = " + str(f);
puts;

t = System.currentTimeMillis();
F = r.squarefreeFactors(f);
t = System.currentTimeMillis() - t;
puts "factors:";

for g in F.keys()
    i = F[g];
    puts "g = #{g}**#{i}";
end
puts
puts "factor time = " + str(t) + " milliseconds";

startLog();
terminate();
