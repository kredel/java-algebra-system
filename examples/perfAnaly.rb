#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# performance analysis of multi-core clusters

startLog();


# first ring with variables q,p,r,v,x,b,l1

ring = PolyRing.new(QQ(),"q,p,r,v,x,b,l1",PolyRing.lex);
puts "ring  = " + str(ring);

qring = RF(ring);
puts "qring = " + str(qring.factory());
one,q,p,r,v,x,b,l1 = qring.gens(); # required for RF
puts "one = " + str(one);

def sa(z) 
  return z/(1+z)
end

def so(z) # does not work
  #return z-1;
  puts "z-1: " +str(z-1);
  if z-1 then return 1 else return z end
end

#x = a/as;
puts "x = " + str(x);
puts "q = " + str(q);
puts "x/q = " + str(x/q);

s = sa(x/q)
puts "s = " + str(s);

# q\, p\, l_c \cdot 
# \frac{s(\frac{x}{q})}{1 + \frac{q\, p}{\beta(p)} \cdot \frac{s(\frac{x}{q})}{v\, r\, \frac{x}{q}}}
lcl = q * p * l1 * ( s / ( s + (q*(p/b)) * ( 1 / (v*r*(x/q))) ));
puts "lcl = " + str(lcl);

etacl = lcl / (q*p*l1);
puts "etacl = " + str(etacl);

Scl = lcl / (l1);
puts "Scl = " + str(Scl);
puts


# second ring with variables q,p,wp,lp,n,ln

ringq = PolyRing.new(QQ(),"q,p,wp,lp,n,ln",PolyRing.lex);
puts "ringq = " + str(ringq);

qringq = RF(ringq);
puts "qringq = " + str(qringq.factory());
one,q,p,wp,lp,n,ln = qringq.gens(); # required for RF
puts "one = " + str(one);
puts

w = 8
c = 2*w
b = 1
#b = p

if false
  puts "scalar-prod:" 
  hashb  = 2*n*w
  hashop = 2*n - 1
  hashx  = p*w
end
if true
  puts "mat-mult:" 
  hashb  = 2*n**2*w
  hashop = 2*n**3 - n**2
  hashx  = 2*n**2*wp*w
end
if false
  puts "linpack:" 
  hashb  = 2*n**2*w
  hashop = 2/3*n**3
  hashx  = 1*(1 + 1/12 * lp)*n**2*w
end
if false
  puts "1-dim FFT:" 
  hashb  = n*c
  hashop = n*ln
  hashx  = (n/p)*lp*c 
end
if false
  puts "2-dim FFT:" 
  hashb  = n**2*c
  hashop = 2*n**2*ln
  hashx  = (n**2/p)*lp*c 
end
puts "w   = " + str(w);
puts "b   = " + str(b);
puts "#b  = " + str(hashb);
puts "#op = " + str(hashop);
puts "#x  = " + str(hashx);
puts

a = hashop / hashb
r = hashb / hashx
puts "a  = " + str(a);
puts "r  = " + str(r);
puts

if true
  puts "bwGRiD:" 
  l1  = 8
  lm  = l1*q
  bm  = 3
  bcl = 3/2
end
if false
  puts "bw*Cluster:" 
  l1  = 18
  lm  = l1*q
  bm  = 6
  bcl = 3
end
puts "l1  = " + str(l1);
puts "lm  = " + str(lm);
puts "bm  = " + str(bm);
puts "bcl = " + str(bcl);
puts

as = lm / bcl
v  = bcl / bm
puts "as = " + str(as);
puts "v  = " + str(v);
puts

x = a / as
puts "x = " + str(x);

rvxq = r * v * x / q;
puts "rvxq = " + str(rvxq);
puts

etacl  =          r * v * x**2 * b / ( r * v * x**2 * b + q**2 * p * x + q**3 * p )
puts "etacl = " + str(etacl);
puts
speedcl  = q * p* r * v * x**2 * b / ( r * v * x**2 * b + q**2 * p * x + q**3 * p )
puts "speedcl = " + str(speedcl);
puts


#puts
terminate();

