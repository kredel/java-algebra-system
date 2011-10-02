#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# examples for power series

#
# rational number examples
#

psr = SeriesRing.new("Q(y)");
puts "psr: " + str(psr);
puts;

one = psr.one();
puts "one: " + str(one);
#puts;

zero = psr.zero();
puts "zero: " + str(zero);
puts;

r1 = psr.random(4);
puts "r1: " + str(r1);
#puts;
puts "r1: " + str(r1);
#puts;
puts "r1-r1: " + str(r1-r1);
puts;

r2 = psr.random(4);
puts "r2: " + str(r2);
#puts;
puts "r2: " + str(r2);
#puts;
puts "r2-r2: " + str(r2-r2);
puts;

#sys.exit();

r3 = r1 + r2;
puts "r3: " + str(r3);
puts;

r4 = r1 * r2 + one;
puts "r4: " + str(r4);
puts;

e = psr.exp();
puts "e: " + str(e);
puts;

r5 = r1 * r2 + e;
puts "r5: " + str(r5);
puts;

#puts "psr.gens: ", [ str(i) for i in psr.gens() ];
one,y = psr.gens();
puts "y: " + str(y);
puts;

r6 = one - y;
puts "r6: " + str(r6);
puts;

r7 = one / r6;
puts "r7: " + str(r7);
puts;


s = psr.sin();
puts "s: " + str(s);
puts;

r8 = psr.gcd(y,s);
puts "r8: " + str(r8);
puts;



s1 = s.evaluate( QQ(0) );
puts "s1: " + str(s1);
puts;

c = psr.cos();
puts "c: " + str(c);
puts;

c1 = c.evaluate( QQ(0) );
puts "c1: " + str(c1);
puts;

s2c2 = s*s+c*c; # sin^2 + cos^2 = 1
puts "s2c2: " + str(s2c2);
puts;


#
# floating point examples
#

dr = SeriesRing.new("",nil,nil,DD());
puts "dr: " + str(dr);
puts;

de = dr.exp();
puts "de: " + str(de);
puts;

d0 = de.evaluate( DD(0) );
puts "d0: " + str(d0);
#puts;

d1 = de.evaluate( DD(0.5) );
puts "d1: " + str(d1);
#puts;

d01 = de.evaluate( DD(0.000000000000000001) );
puts "d01: " + str(d01);
puts;


#def f(a)
#    return a*a;
#end
# use lambdas:

f = lambda { |a| a*a };

ps = psr.create(f);
puts "ps: " + str(ps);
puts;

g = lambda { |a| a+a };

ps1 = psr.create(g);
puts "ps1: " +str(ps1);
puts;

ps2 = ps * ps1;
puts "ps2: " + str(ps2);
puts;

#exit();

h = lambda { |a| psr.ring.coFac.fromInteger( 3*a ) };

ps3 = psr.create(nil,h);
puts "ps3: " + str(ps3);
puts;

ps4 = ps3 * ps1;
puts "ps4: " + str(ps4);
puts;

# does not work, since get() is not known
#def k(a)
#    if a > 0
#        return get(a-1).multiply( psr.ring.coFac.fromInteger( 2*a ) );
#    else
#        return psr.ring.coFac.fromInteger( 2*a );
#    end
#end
#no#ps5 = psr.create(jfunc=k);
#no#puts "ps5: ", ps5;
#no#puts;


class Mcoeff < Coefficients
    def initialize(cofac)
        super();
        @coFac = cofac;
    end
    def generate(i)
        if i == 0
            return @coFac.getZERO();
        else
            if i == 1
                return @coFac.getONE();
            else
                c = get( i-2 ).negate();
                return c.divide( @coFac.fromInteger(i) ).divide( @coFac.fromInteger(i-1) );
            end
        end
    end
end

ps6 = psr.create(nil,nil, Mcoeff.new(psr.ring.coFac) );
puts "ps6: " + str(ps6);
puts;

ps7 = ps6 - s;
puts "ps7: " + str(ps7);
puts;

#exit();

class Cosmap 
    include UnivPowerSeriesMap # for interfaces use Module notations
    def initialize(cofac)
        @coFac = cofac;
    end
    def map(ps)
        return ps.negate().integrate( @coFac.getZERO() ).integrate( @coFac.getONE() );
    end
end

ps8 = psr.fixPoint( Cosmap.new( psr.ring.coFac ) );
puts "ps8: " + str(ps8);
puts;

ps9 = ps8 - c;
puts "ps9: " + str(ps9);
puts;

#exit();

# conversion from polynomials

pr = Ring.new("Q(y) L");
puts "pr: " + str(pr);
puts;

one,yp = pr.gens();

p1 = one;
p2 = one - yp;

ps1 = psr.fromPoly(p1);
ps2 = psr.fromPoly(p2);

# rational function as power series:
ps3 = ps1 / ps2;

puts "p1:  " + str(p1);
puts "p2:  " + str(p2);
puts "ps1: " + str(ps1);
puts "ps2: " + str(ps2);
puts "ps3: " + str(ps3);
puts;


p1 = one * 2 + yp**3 - yp**5;
p2 = one - yp**2 + yp**4;

ps1 = psr.fromPoly(p1);
ps2 = psr.fromPoly(p2);

# rational function as power series:
ps3 = ps1 / ps2;

ps4 = ps3.integrate( QQ(1) );
ps5 = ps3.differentiate();

puts "p1:  " + str(p1);
puts "p2:  " + str(p2);
puts "ps1: " + str(ps1);
puts "ps2: " + str(ps2);
puts "ps3: " + str(ps3);
puts "ps4: " + str(ps4);
puts "ps5: " + str(ps5);
puts;


#exit();
