#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# example for linear algebra
#
#

p = 11
N = 6;
r = Mat(QQ(),N,N);
#r = Mat(GF(p),N,N);
#puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
#puts;
#puts r.gens().map{ |g| str(g) + ", " };
#puts;

v = Vec(QQ(),N);
#v = Vec(GF(p),N);
#puts "v = " + str(v);
puts "v.factory() = " + str(v.factory());
puts;
#puts v.gens().map{ |g| str(g) + ", " };
#puts;

#puts "one:  " + str(r.one);
#puts "zero: " + str(r.zero);
#puts;
#puts "zero: " + str(v.zero);
#puts;

a = r.random(11);
puts "a:  " + str(a);

b = v.random(7);
puts "b:  " + str(b);

c = a * b;
puts "c:  " + str(c);

x = a.solve(c);
puts "x:  " + str(x);

puts "x == b: " + str(x == b);
puts;


L, U, P = a.decompLU();
puts "P:  " + str(P);
puts;
puts "L:  " + str(L);
puts;
puts "U:  " + str(U);
puts;

if P.elem.size() != 0 
  x = a.solveLU(P, c);
  puts "x:  " + str(x);

  puts "x == b: " + str(x == b);
  puts;


  d = a.determinant(P)
  puts "det(A):  " + str(d) + " ~= " + str(d.elem.getDecimal());
  #puts "det(A):  " + str(d) + " ~= " + str(d.elem.getSymmetricVal());
  puts;

end

a = r.random(11);
#puts "a:  " + str(a);

b = a.rowEchelon();
puts "b:  " + str(b);

puts "rank(b):  " + str(b.rank());
#puts "b:  " + str(b);
puts;


terminate();
#sys.exit();
