#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# chebyshev polynomial example
# T(0) = 1
# T(1) = x
# T(n) = 2 * x * T(n-1) - T(n-2)

r = Ring.new( "Z(x) L" );
puts "Ring: " + str(r);
puts;

# sage like: with generators for the polynomial ring
one,x = r.gens();

x2 = 2 * x;

N = 10;
T = [one,x];
for n in 2..N 
    t = x2 * T[n-1] - T[n-2];
    T[n] = t;
end

for n in 0..N 
  puts "T[#{n}] = #{T[n]}";
end

puts;

#sys.exit();
