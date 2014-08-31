#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: recursive factorization


cr = PolyRing.new( QQ(), "x", PolyRing.lex );
puts "Ring: " + str(cr);
puts;
r = PolyRing.new( cr, "y,z", PolyRing.lex );
puts "Ring: " + str(r);
puts

#one,x,y,z = r.gens();

#f = z * ( y + 1 )**2 * ( x**2 + x + 1 )**3;
#f = z * ( y + 1 ) * ( x**2 + x + 1 );
#f = ( y + 1 ) * ( x**2 + x + 1 );
#f = ( y + z**2 ) * ( x**2 + x + 1 );

#f = x**4 * y + x**3  + z + x   + z**2 + y * z**2;
## f = x**3 + ( ( y + 2 ) * z + 2 * y + 1 ) * x**2 \
##     + ( ( y + 2 ) * z**2 + ( y**2 + 2 * y + 1 ) * z + 2 * y**2 + y ) * x \
##     + ( y + 1 ) * z**3 + ( y + 1 ) * z**2 + ( y**3 + y**2 ) * z + y**3 + y**2;

f = ( x + y * z + y + z + 1 ) * ( x**2 + ( y + z ) * x + y**2 + z**2 );
#f = ( x + y * z + y + z + 1 ) * ( x**2 + ( y + z ) * x + y**2 + 1 );

#f = ( x + y ) * ( x - y);

puts "f = ", f;
puts;

startLog();

t = System.currentTimeMillis();
#G = r.squarefreeFactors(f);
G = r.factors(f);
t = System.currentTimeMillis() - t;
puts "G = ", str(G.map{ |h,i| str(h)+"**"+str(i)+" " });
#puts "G = ", G;
#puts "factor time =", t, "milliseconds";

puts;
puts "f    = " + str(f);
g = one;
for h, i in G do
    if i > 1 then
        puts "h**i = " + str(h) + "**" + str(i);
    else
        puts "h    = " + str(h);
    end
    h = h**i;
    g = g*h;
end
puts;
#puts "g = ", g;

if f == g then
    puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): true" ;
else
    puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): " + str(f==g);
end
puts;

#startLog();
terminate();
