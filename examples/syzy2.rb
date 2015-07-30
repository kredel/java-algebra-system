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

java_import "edu.jas.poly.ModuleList";
java_import "edu.jas.gbufd.SyzygySeq";
java_import "edu.jas.gb.GroebnerBaseSeq";

s = SyzygySeq.new(r.ring.coFac).zeroRelations( rg.list );
sl = ModuleList.new(rg.pset.ring,s);

puts "syzygy:" + str(sl);
puts;

z = SyzygySeq.new(r.ring.coFac).isZeroRelation( s, rg.list );

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
   #mg = ModGroebnerBaseSeq.new(r.ring.coFac).GB(sl);
   mg = GroebnerBaseSeq.new().GB(sl);
   puts "Mod GB: " + str(mg);
   puts;

   zg = SyzygySeq.new(r.ring.coFac).zeroRelations(mg);
   puts "syzygies of Mod GB: " + str(zg);
   puts;

   #if ModGroebnerBaseSeq.new(r.ring.coFac).isGB( mg )
   if GroebnerBaseSeq.new().isGB( mg )
       puts "is GB";
   else
       puts "is not GB";
   end

   if SyzygySeq.new(r.ring.coFac).isZeroRelation(zg,mg)
       puts "is Syzygy";
   else
       puts "is not Syzygy";
   end

   if not zg
       break;
   end
end

terminate();
