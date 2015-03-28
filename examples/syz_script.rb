#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# trinks 7 example

r = PolyRing.new(QQ(), "B,S,T,Z,P,W", PolyRing.lex );
puts "Ring: " + str(r);
puts;
one,B,S,T,Z,P,W = r.gens(); # capital letter variables not automaticaly included

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 *B * S + 3 * B**2;
f7 = 10000 * B**2 + 6600 * B + 2673;

f = r.ideal( "", [f1,f2,f3,f4,f5,f6,f7] );
puts "Ideal: " + str(f);
puts;

#exit()

rg = f.GB();
puts "GB: " + str(rg);
puts;

#startLog();

s = rg.syzygy();
puts "syzygy: " + str(s);
puts;

t = rg.isSyzygy(s);
puts "is syzygy: " + str(t);
puts "is syzygy: " + str(s.isSyzygy(rg));
puts;

exit()

java_import "edu.jas.poly.ModuleList";
java_import "edu.jas.gbmod.SyzygySeq";
java_import "edu.jas.gbmod.ModGroebnerBaseSeq";

s = SyzygySeq.new(r.ring.coFac).zeroRelations( rg.list );
sl = ModuleList.new(rg.pset.ring,s);

puts "syzygy:" + str(sl);
puts;

exit()

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
   mg = ModGroebnerBaseSeq.new(r.ring.coFac).GB(sl);
   puts "Mod GB: " + str(mg);
   puts;

   zg = SyzygySeq.new(r.ring.coFac).zeroRelations(mg);
   puts "syzygies of Mod GB: " + str(zg);
   puts;

   if ModGroebnerBaseSeq.new(r.ring.coFac).isGB( mg )
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
