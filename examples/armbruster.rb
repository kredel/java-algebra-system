#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Armbruster module example

r = CommutativeModule.new( "Rat(u,v,l) L" ); 
puts "Module: " + str(r);
puts;


ps = """
(
(     ( 1 ),         ( 2 ),       ( 0 ),       ( l^2 ) ),
(     ( 0 ),   ( l + 3 v ),       ( 0 ),         ( u ) ),
(     ( 1 ),         ( 0 ),       ( 0 ),       ( l^2 ) ),
( ( l + v ),         ( 0 ),       ( 0 ),         ( u ) ),
(   ( l^2 ),         ( 0 ),       ( 0 ),         ( v ) ),
(     ( u ),         ( 0 ),       ( 0 ), ( v l + v^2 ) ),
(     ( 1 ),         ( 0 ), ( l + 3 v ),         ( 0 ) ),
(   ( l^2 ),         ( 0 ),     ( 2 u ),         ( v ) ),
(     ( 0 ),         ( 1 ),   ( l + v ),         ( 0 ) ),
(     ( 0 ),       ( l^2 ),       ( u ),         ( 0 ) ),
(     ( 0 ),         ( v ),   ( u l^2 ),         ( 0 ) ),
(     ( 0 ), ( v l + v^2 ),     ( u^2 ),         ( 0 ) )
) 
""";

f = SubModule.new( r, ps );
puts "SubModule: " + str(f);
puts;

rg = f.GB();
puts "seq Output: " + str(rg);
puts;

puts "isGB: " + str(rg.isGB());

