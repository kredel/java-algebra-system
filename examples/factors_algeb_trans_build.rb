#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# polynomial examples: factorization over Q(sqrt(2))(x)(sqrt(x))[y]

Yr = EF.new(QQ()).extend("w2","w2^2 - 2").extend("x").extend("wx","wx^2 - x").polynomial("y").build();
puts "Yr    = " + str(Yr);
puts

one,w2,x,wx,y = Yr.gens();
puts "one   = " + str(one);
puts "w2    = " + str(w2);
puts "x     = " + str(x);
puts "wx    = " + str(wx);
puts "y     = " + str(y);
puts;

f = ( y**2 - x ) * ( y**2 - 2 );
#f = ( y**2 - x )**2 * ( y**2 - 2 )**3;
#f = ( y**4 - x * 2 );
#f = ( y**7 - x * 2 );
#f = ( y**2 - 2 );
#f = ( y**2 - x );
#f = ( w2 * y**2 - 1 );
#f = ( y**2 - 1/x );
#f = ( y**2 - (1,2) );
#f = ( y**2 - 1/x ) * ( y**2 - (1,2) );

puts "f = ", f;
puts;

#exit();

startLog();

t = System.currentTimeMillis();
G = Yr.factors(f);
t = System.currentTimeMillis() - t;
#puts "G = ", G;
#puts "factor time =", t, "milliseconds";

#exit();

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
#puts "g    = " +  str(g);

if f == g then
    puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): true" ;
else
    puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): " + str(f==g);
end
puts;

#startLog();
terminate();
