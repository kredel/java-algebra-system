#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# module example

p = PolyRing.new(QQ(),"u,v,l", PolyRing.lex);
#r = CommutativeModule.new( "Rat(u,v,l) L", nil, 4 );
r = CommutativeModule.new( "", p, 4 );
puts "Module: " + str(r);
puts;

G = r.gens();
puts "gens() = " + str( G.map { |e| str(e) }.join(", ") );
puts

L = G.map { |e| e.elem.val }
puts "gens() = " + str( L.map { |e| str(e) }.join(", ") );
puts

M = r.submodul("", L );
puts "M = " + str(M);
puts

P = M.mset.getPolynomialList();
puts "P = " + str(P.toScript());
puts

puts "M.isGB(): " + str(M.isGB());
puts
