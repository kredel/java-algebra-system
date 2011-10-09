#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# example from CLO(UAG), 4.4

#R = PolyRing( ZM(32003), "x,y,z" );
R = PolyRing.new( QQ(), "x,y,z" );
puts "Ring: " + str(R);
puts;

one,x,y,z = R.gens();

f1 = x**5 - x * y**6 - z**7;
f2 = x * y + y**3 + z**3;
f3 = x**2 + y**2 - z**2;

L = [f1,f2,f3];
#puts "L = ", str(L);

F = R.ideal( "", L );
puts "Ideal: " + str(F);
puts;

PR = R.powerseriesRing();
puts "Power series ring: " + str(PR);
puts;

Fp = PSIdeal.new(PR,L);
puts "Power series ideal: " + str(Fp);
puts;

startLog();

#9+5 # truncate at total degree 9
S = Fp.STD(); 
#puts "std: " + str(S);
puts;
for p in S.list do
    #puts "p = ", str(p.asPolynomial());
    puts "p = " + str(p);
end
puts;

#sys.exit();

#terminate();

