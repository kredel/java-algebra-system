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
