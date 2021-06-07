#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# example for rational and real algebraic numbers
#
#

# continued fractions:

r = PolyRing.new(QQ(),"alpha",PolyRing.lex);
puts "r = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
sqrt2 = a**2 - 2;
puts "sqrt2 = " + str(sqrt2);
Qs2r = RealN(sqrt2,[1,2],a-1);
#Qs2r = RealN(sqrt2,[-2,-1],a+1);
puts "Qs2r  = " + str(Qs2r.factory()) + " :: " + str(Qs2r.elem);
one,alpha = Qs2r.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);


cf = Qs2r.contFrac(20);
puts "cf    = " + str(cf);
nb = Qs2r.contFracApprox(cf);
puts "nb    = " + str(nb) + " ~= " + str(nb.elem.getDecimal());

cf = nb.contFrac(0);
puts "cf    = " + str(cf);
nb = nb.contFracApprox(cf);
puts "nb    = " + str(nb) + " ~= " + str(nb.elem.getDecimal());

nb = nb.contFracApprox(nil);
puts "nb    = " + str(nb.signum == 0);

terminate();
#sys.exit();
