#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# ? example

r = Ring.new( "Rat(x,y,z) L" );
puts "Ring: " + str(r);
puts;

ps = """
( 
 ( z^3 - y ),
 ( y z - x ),
 ( y^3 - x^2 z ),
 ( x z^2 - y^2 )
) 
""";

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

rg = f.GB();
puts "seq Output:" + str(rg);
puts;

#startLog();

include_class "edu.jas.poly.ModuleList";
include_class "edu.jas.gbmod.SyzygyAbstract";
include_class "edu.jas.gbmod.ModGroebnerBaseAbstract";

s = SyzygyAbstract.new().zeroRelations( rg.list );
sl = ModuleList.new(rg.pset.ring,s);

puts "syzygy:" + str(sl);
puts;

z = SyzygyAbstract.new().isZeroRelation( s, rg.list );

print "is Syzygy ? "
if z
    puts "true"
else
    puts "false"
end
puts;

zg = sl;

for i in 1..(r.ring.nvar) 
   puts "\n #{i}. resolution";

   sl = zg;
   mg = ModGroebnerBaseAbstract.new().GB(sl);
   puts "Mod GB: " + str(mg);
   puts;

   zg = SyzygyAbstract.new().zeroRelations(mg);
   puts "syzygies of Mod GB: " + str(zg);
   puts;

   if ModGroebnerBaseAbstract.new().isGB( mg )
       puts "is GB";
   else
       puts "is not GB";
   end

   if SyzygyAbstract.new().isZeroRelation(zg,mg)
       puts "is Syzygy";
   else
       puts "is not Syzygy";
   end

   if not zg
       break;
   end
end

terminate();
