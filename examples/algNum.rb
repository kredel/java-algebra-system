#
# jruby examples for jas.
# $Id: $
#

require "examples/jas"

# multiple algebraic field extensions

startLog();

puts "------- multiple algebraic field extensions QQ(alpha)(beta)(gamma)(delta) ---------";
r = PolyRing.new(QQ(),"alpha",PolyRing.lex);
puts "r     = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
ap = a**5 - a + 1;
puts "ap    = " + str(ap);
x =r.factors(ap); 
puts "x     = " + str( x.map{ |p,e| str(p)+"**"+str(e)+"," });
qs2 = AN(ap,x.size==1); # and e==1
puts "qs2   = " + str(qs2.factory());
one,alpha = qs2.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);

b = alpha**2 - 1;
puts "b     = " + str(b);
c = 1 / b;
puts "c     = " + str(c);
puts

ar = PolyRing.new(qs2,"beta",PolyRing.lex);
puts "ar    = " + str(ar);
e,a,b = ar.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
puts "b     = " + str(b);

bp = b**2 - a;
puts "bp    = " + str(bp);
x =ar.factors(bp); 
puts "x     = " + str( x.map{ |p,e| str(p)+"**"+str(e)+"," });
qs3 = AN(bp,x.size==1); # and e==1
puts "qs3   = " + str(qs3.factory());
one,alpha,beta = qs3.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);
puts "beta  = " + str(beta);

c = -alpha + beta**3;
puts "c     = " + str(c);
d = 1 / c;
puts "d     = " + str(d);
e = c * d;
puts "e     = " + str(e);
puts

br = PolyRing.new(qs3,"gamma",PolyRing.lex);
puts "br    = " + str(br);
e,a,b,c = br.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
puts "b     = " + str(b);
puts "c     = " + str(c);

cp = c**2 - b*c - a;
puts "cp    = " + str(cp);
x =br.factors(cp); 
puts "x     = " + str( x.map{ |p,e| str(p)+"**"+str(e)+"," });
qs4 = AN(cp,x.size==1); # and e==1
puts "qs4   = " + str(qs4.factory());
one,alpha,beta,gamma = qs4.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);
puts "beta  = " + str(beta);
puts "gamma = " + str(gamma);

d = -alpha*gamma + beta;
puts "d     = " + str(d);
e = 1 / d;
puts "e     = " + str(e);
f = e * d;
puts "f     = " + str(f);
puts

cr = PolyRing.new(qs4,"delta",PolyRing.lex);
puts "cr    = " + str(cr);
e,a,b,c,d = cr.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
puts "b     = " + str(b);
puts "c     = " + str(c);
puts "d     = " + str(d);

dp = d**2 + c*d - b + a; # irreducible
#dp = (d**2 + b) * ( c**2 - a ); # = b * c * ( d**2 + b )
#dp = (d + b * c) * ( d + c - a**2 );
puts "dp    = " + str(dp);
x =cr.factors(dp); 
puts "x     = " + str( x.map{ |p,e| str(p)+"**"+str(e)+", " });
qs5 = AN(dp,x.size==1); # and e==1
puts "qs5   = " + str(qs5.factory());
one,alpha,beta,gamma,delta = qs5.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);
puts "beta  = " + str(beta);
puts "gamma = " + str(gamma);
puts "delta = " + str(delta);

#e = -alpha*gamma + beta*delta; # ok
#e = alpha**3 - gamma; # 1/e should fail but does not because it is a unit
e = delta**2 + beta; # 1/e fails
e = delta + beta*gamma; # 1/e should fail
puts "e     = " + str(e);
f = 1 / e;
puts "f     = " + str(f);
g = e * f;
puts "g     = " + str(g);
puts

f = ( ( ( ( 3/82 * alpha**4 - 12/41 * alpha**3 - 13/82 * alpha**2 - 19/82 * alpha - 15/82 ) * beta + ( 3/82 * alpha**4 - 12/41 * alpha**3 - 13/82 * alpha**2 - 19/82 * alpha - 15/82 ) ) * gamma - ( ( 21/82 * alpha**4 - 45/82 * alpha**3 - 25/41 * alpha**2 - 51/82 * alpha - 32/41 ) * beta - ( 45/82 * alpha**4 + 25/41 * alpha**3 + 51/82 * alpha**2 + 43/82 * alpha + 21/82 ) ) ) * delta - ( ( 39/82 * alpha**4 + 8/41 * alpha**3 - 5/82 * alpha**2 - 1/82 * alpha - 31/82 ) * beta * gamma + ( 11/82 * alpha**4 - 3/41 * alpha**3 + 7/82 * alpha**2 - 15/82 * alpha + 27/82 ) ) );
puts "f     = " + str(f);
e = 1 / f;
puts "e     = " + str(e);
g = e * f;
puts "g     = " + str(g);
puts


puts

terminate();

__END__
s
to_skip = <<TOSKIP

TOSKIP
