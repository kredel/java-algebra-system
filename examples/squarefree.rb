#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: squarefree: characteristic 0

r = PolyRing.new(QQ(),"x, y, z",PolyRing.lex)
puts "Ring: " + str(r);
puts;

#automatic: one,x,y,z = r.gens();

a = r.random(k=2,l=3);
b = r.random(k=2,l=3);
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

f = a**2 * b**3 * c;

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
puts "f = " + str(f);
puts;

t = System.currentTimeMillis();
#F = r.squarefreeFactors(f);
F = f.squarefreeFactors();
t = System.currentTimeMillis() - t;
puts "factors:";
puts F.map { |k,v| k.to_s + ( v == 1 ? "" : "**" + v.to_s ) }.join(", ")
puts
puts "f == prod(F): " + (f == F.map { |k,v| k**v }.reduce(:*)).to_s
puts
puts "factor time = " + str(t) + " milliseconds";

startLog();
terminate();
