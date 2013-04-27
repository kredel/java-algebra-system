#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# ? example

rs = """
     Rat(a,b,e1,e2,e3) L
     RelationTable
     (
       ( e3 ), ( e1 ), ( e1 e3 - e1 ),
       ( e3 ), ( e2 ), ( e2 e3 - e2 )
     )
""";

r = SolvableRing.new( rs );
puts "SolvableRing: " + str(r);
puts;

ps = """
(
 ( e1 e3^3 + e2^10 - a ),
 ( e1^3 e2^2 + e3 ),
 ( e3^3 + e3^2 - b )
)
""";

f = r.ideal( ps );
puts "SolvIdeal: " + str(f);
puts;

include_class "edu.jas.gbmod.SolvableSyzygyAbstract";

#startLog();

R = SolvableSyzygyAbstract.new().resolution( f.pset );

for i in 0..R.size() 
    puts "\n #{i+1}. resolution";
    puts "\n" + str(R[i]);
end

#terminate();
