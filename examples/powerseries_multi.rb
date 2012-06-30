#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# examples for power series

#
# rational number examples
#

psr = MultiSeriesRing.new("Q(x,y,z)",truncate=4);
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

e = psr.exp(1);
puts "e: " + str(e);
puts;

r5 = r1 * r2 + e;
puts "r5: " + str(r5);
puts;

#puts "psr.gens: ", [ str(i) for i in psr.gens() ];
one,x,y,z = psr.gens();
puts "x: " + str(x);
puts "y: " + str(y);
puts "z: " + str(z);
puts;

r6 = one - y;
puts "r6: " + str(r6);
puts;

r7 = one / r6;
puts "r7: " + str(r7);
puts;


s = psr.sin(1);
puts "s: " + str(s);
puts;

r8 = psr.gcd(y,s);
puts "r8: " + str(r8);
puts;


## s1 = s.evaluate( QQ(0) );
## puts "s1: " + str(s1);
## puts;

c = psr.cos(1);
puts "c: " + str(c);
puts;

## c1 = c.evaluate( QQ(0) );
## puts "c1: " + str(c1);
## puts;

s2c2 = s*s + c*c; # sin^2 + cos^2 = 1
puts "s2c2: " + str(s2c2);
puts "s2c2 == 1: " + str(s2c2.isONE());
puts;


# conversion from polynomials

pr = Ring.new("Q(x,y,z) L");
puts "pr: " + str(pr);
puts;

one,xp,yp,zp = pr.gens();

p1 = one;
p2 = one - yp;

ps1 = psr.fromPoly(p1);
ps2 = psr.fromPoly(p2);

# rational function as power series:
ps3 = ps1 / ps2;

puts "p1: "  + str(p1);
puts "p2: "  + str(p2);
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

ps4 = ps3.integrate( QQ(1), 1 );
ps5 = ps3.differentiate(1);

puts "p1: "  + str(p1);
puts "p2: "  + str(p2);
puts "ps1: " + str(ps1);
puts "ps2: " + str(ps2);
puts "ps3: " + str(ps3);
puts "ps4: " + str(ps4);
puts "ps5: " + str(ps5);
puts;


#exit();


#
# floating point examples
#

#dr = MultiSeriesRing.new(cofac=DD(),names=psr.ring.polyRing().getVars(),truncate=psr.ring.truncate());
dr = MultiSeriesRing.new("",psr.ring.truncate(),nil,DD(),psr.ring.polyRing().getVars());
puts "dr: " + str(dr);
puts;

de = dr.exp(1);
puts "de: " + str(de);
puts;


# use lambdas:
# type(a) == ExpVector
f = lambda { |a| a.getVal(0) * a.getVal(1) * a.getVal(2) };

ps = psr.create(f);
puts "ps: " + str(ps);
puts;

# type(a) == ExpVector
g = lambda { |a| a.totalDeg() };

ps1 = psr.create(g);
puts "ps1: " +str(ps1);
puts;


ps2 = ps * ps1;
puts "ps2: " + str(ps2);
puts;


h = lambda { |a| psr.ring.coFac.fromInteger( 3 * a.totalDeg() ) };

ps3 = psr.create(nil,h);
puts "ps3: " + str(ps3);
puts;

ps4 = ps3 * ps1;
puts "ps4: " + str(ps4);
puts;

#exit();

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


class Mycoeff < MultiVarCoefficients
    def initialize(ring,ifunc=nil,jfunc=nil)
        super(ring);
        @coFac = ring.coFac;
        @ifunc = ifunc;
        @jfunc = jfunc;
    end
    def generate(i) # type(i) == ExpVector
        if i.isZERO()
            return @coFac.getONE();
        else
            dep = i.dependencyOnVariables();
            if dep.length == 0 
               return @coFac.getZERO();
            end
            i1 = i.subst( dep[0], i.getVal(dep[0])-1 ); # i1 < i
            c = get( i1 ).negate();
            c = c.divide( @coFac.fromInteger(i.maxDeg()) ).sum( @coFac.fromInteger(i.totalDeg()-1) );
            #puts "c: " + str(c) + " i: " + str(i);
            return c;
        end
    end
end


ps6 = psr.create(nil,nil, Mycoeff.new(psr.ring) );
puts "ps6: " + str(ps6);
puts;

ps7 = ps6*2 + s - ps6 - ps6;
puts "ps7: " + str(ps7);
puts "ps7 == s: " + str(ps7 == s);
puts;

#exit();

class Cosmap 
    include MultiVarPowerSeriesMap # for interfaces use Module notations
    def initialize(cofac)
        @coFac = cofac;
    end
    def map(ps)
        return ps.negate().integrate( @coFac.getZERO(), 1 ).integrate( @coFac.getONE(), 1 );
    end
end

ps8 = psr.fixPoint( Cosmap.new( psr.ring.coFac ) );
puts "ps8: " + str(ps8);
puts;

ps9 = ps8 - c;
puts "ps9: " + str(ps9);
puts "ps9 == 0: " + str(ps9.isZERO());
puts;


#exit();
