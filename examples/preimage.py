#
# jython examples for jas.
# $Id$
#

from jas import Ring

from edu.jas import application

rs = """
# polynomial ring:
Rat(x1,x2,x3,y1,y2) G|3|
""";

ps = """
(
 ( y1 + y2 - 1 ),
 ( x1 - y1^2 - y1 - y2 ),
 ( x2 - y1 - y2^2 ),
 ( x3 - y1 y2 )
)
""";

r = Ring( rs );
print "Ring: " + str(r);

i = r.ideal( ps );
print "Ideal: " + str(i);

g = i.GB();
print "seq GB:", g;



rsi = """
# polynomial ring:
Rat(x1,x2,x3) G
""";

ri = Ring( rsi );
print "Ring: " + str(ri);


y = application.Ideal(g.pset).intersect(ri.ring);
len = y.list.size();
print "seq intersect y: ", y;


rs = """
# polynomial ring:
Rat(y1,y2,x1,x2,x3) G|2|
""";

ps = """
(
 ( y1 + y2 - 1 ),
 ( x1 - y1^2 - y1 - y2 ),
 ( x2 - y1 - y2^2 ),
 ( x3 - y1 y2 )
)
""";

r = Ring( rs );
print "Ring: " + str(r);

i = r.ideal( ps );
print "Ideal: " + str(i);

g = i.GB();
print "seq GB:", g;

rsb = """
# polynomial ring:
Rat(y1,y2) G
""";

rb = Ring( rsb );
print "Ring: " + str(rb);
print

y = application.Ideal(g.pset).intersect(rb.ring);
len = y.list.size();
print "seq intersect y: ", y;
print
