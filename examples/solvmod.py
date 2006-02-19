#
# jython examples for jas.
# $Id$
#

from jas import SolvableModule
from jas import SolvableSubModule

# Armbruster module example

r = SolvableModule( "Rat(u,v,l) L" );
print "Module: " + str(r);
print;


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

f = SolvableSubModule( r, ps );
print "SubModule: " + str(f);
print;

rg = f.leftGB();
print "seq Output:", rg;
print;

