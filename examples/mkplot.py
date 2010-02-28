#
# read output of jas runnings and prepare input for gnuplot
#

from sys import argv
from sys import exit
from time import strftime
import re;
import os
from os import system

hpat = re.compile(r'd = (\d+), ppn = (\d+), time = (\d+) milliseconds, (\d+) start-up');
dpat = re.compile(r'd = (\d+), time = (\d+) milliseconds, (\d+) start-up');
ppat = re.compile(r'time = (\d+) milliseconds');
fpat = re.compile(r'_p(\d+)\.');
spat = re.compile(r'_s\.');
rpat = re.compile(r'OrderedPairlist\(size=0, #put=(\d+), #rem=(\d+)\)');
r1pat = re.compile(r'pairlist #put = (\d+) #rem = (\d+)');
res = {};
pairs = None;
xy = False;

for fname in argv[1:]:
    dn = -1;
    print "file =", fname
    f = open(fname,"r");
    for line in f:
        pm = rpat.search(line); # pairlist results
        if pm:
            print "3: ", line,
            np = pm.group(1);
            nr = pm.group(2);
            if pairs == None:
                pairs = ( np, nr );
                #print "put = " + np + ", rem = " + nr;
            continue;
        pm = r1pat.search(line); # pairlist results
        if pm:
            print "3: ", line,
            np = pm.group(1);
            nr = pm.group(2);
            if pairs == None:
                pairs = ( np, nr );
                #print "put = " + np + ", rem = " + nr;
            continue;
        ma = hpat.search(line); # distributed hybrid results
        if ma:
            print "d: ", line,
            dn = int( ma.group(1) );
            pp = int( ma.group(2) );
            tm = ma.group(3);
            su = ma.group(4);
            xy = True;
            if not dn in res:
                res[ dn ] = {};
            if pp in res[ dn ]:
                if 't' in res[ dn ][ pp ]:
                    tx = res[ dn ][ pp ][ 't' ];
                    if tx < tm: # minimum or maximum
                        tm = tx;
                        su = res[ dn ][ pp ][ 's' ]
            res[ dn ][ pp ] = { 't': tm, 's': su };
            if pairs != None:
                res[ dn ][ pp ]['p'] = pairs[0];
                res[ dn ][ pp ]['r'] = pairs[1];
                pairs = None;
            continue;
        ma = dpat.search(line); # distributed results
        if ma:
            print "d: ", line,
            dn = int( ma.group(1) );
            pp = 1;
            tm = ma.group(2);
            su = ma.group(3);
            if not dn in res:
                res[ dn ] = {};
            if pp in res[ dn ]:
                if 't' in res[ dn ][ pp ]:
                    tx = res[ dn ][ pp ][ 't' ];
                    if tx < tm: # minimum or maximum
                        tm = tx;
                        su = res[ dn ][ pp ][ 's' ]
            res[ dn ][ pp ] = { 't': tm, 's': su };
            if pairs != None:
                res[ dn ][ pp ]['p'] = pairs[0];
                res[ dn ][ pp ]['r'] = pairs[1];
                pairs = None;
            continue;
        ma = ppat.search(line); # parallel and sequential results
        if ma:
            print "p: ", line,
            tm = ma.group(1);
            fm = fpat.search(fname);
            if fm:
                print fname
                dn = 1;
                pp = int( fm.group(1) );
            else:
                if spat.search(fname):
                    dn = 0;
                    pp = 0;
                else:
                    dn = -1;
                    pp = -1;
            if not dn in res:
                res[ dn ] = {};
            if pp in res[ dn ]:
                if 't' in res[ dn ][ pp ]:
                    tx = res[ dn ][ pp ][ 't' ];
                    if tx < tm: # minimum or maximum
                        tm = tx;
            res[ dn ][ pp ] = { 't': tm, 's': '0' };
            if pairs != None:
                res[ dn ][ pp ]['p'] = pairs[0];
                res[ dn ][ pp ]['r'] = pairs[1];
                pairs = None;
            
print "\nres = ", res;

ks = res.keys();
#print "ks = ", ks;
ks.sort();
print "ks = ", ks;

for d in ks:
    for p in res[d]:
        print " " + str(d) + "," + str(p) + " & " + res[d][p]['t'] + " & " + res[d][p]['s'] 

s1 = ks[0];
if s1 == 0:
    p1 = res[1].keys()[0];
    st = int( res[ 1 ][ p1 ]['t'] );
else:
    p1 = res[s1].keys()[0];
    st = int( res[ s1 ][ p1 ]['t'] );
print "s1 = ", s1;
print "p1 = ", p1;
print "st = ", st;

speed = {};

for k in ks:
    speed[k] = {};
    for p in res[k]:
        speed[k][p] = st / float(res[k][p]['t']); 
print "speed = ", speed;

bname = "kat_";
tag = argv[1];
bname = tag;
i = tag.find("/");
if i >= 0:
    bname = tag[:i];
i = bname.find(".out");
if i >= 0:
    bname = tag[:i];
print "bname = " + bname;


# LaTex output:

summary =   "\n% run = " + tag + "\n";
summary += r"\begin{tabular}{|r|r|r|r|r|r|r|}" + "\n";
summary += r"\hline" + "\n";
summary += r"\#" + "nodes & ppn & time & startup & speedup & put & rem \n";
summary += r"\\ \hline" + "\n";

for d in ks:
    for p in res[d]:
        if res[d][p]['s'] != "0":
            summary += " " + str(d) + " & " + str(p) + " & " + res[d][p]['t'] + " & " + res[d][p]['s'] + " & " + str(speed[d][p])[:4];
        else:
            summary += " " + str(d) + " & " + str(p) + " & " + res[d][p]['t'] + " & " + " & " + str(speed[d][p])[:4];
        if 'p' in res[d][p] and 'r' in res[d][p]:
            summary += " & " + res[d][p]['p'] + " & " + res[d][p]['r'] + "\n"
        else:
            summary += " & " + " & " + "\n" 
        summary += r"\\ \hline" + "\n";

summary += r"\end{tabular}" + "\n";
print summary;

tname = bname+".tex";
tt=open(tname,"w");
tt.write(summary);
tt.close();


# output for gnuplot:

oname = bname+".po";
o=open(oname,"w");
ploting = "\n#nodes #ppn time speedup ideal\n";
for k in ks:
    for p in res[k]:
        ideals = k*p;
        if ideals == 0:
            ideals = 1;
        ploting += str(k) + " " + str(p) + " " + str(res[k][p]['t']) + " " + str(speed[k][p]) + " " + str(ideals) + "\n";

ploting += "\n";

print ploting;
o.write(ploting);
o.close();

#exit();

#---------------------------------------
pscript_2d = """
set grid 
set term %s
set output "%s.ps"
set title "GBs of Katsuras example on a grid cluster" 
set time
set xlabel "number of nodes" 
set ylabel "threads per node" 

set xrange [0:7] 
set yrange [0:7]

#reverse

#set clip two

set xtics 1.0
set ytics 1.0

set data style lines
#set contour base
#set surface

#set pm3d
set dgrid3d 
set hidden3d

set multiplot

set size 0.95,0.95
#set size 0.95,0.5
#set origin 0,0.5
set origin 0,0

##set zlabel "milliseconds" 
set zlabel "seconds" 
splot "%s.po" using 1:2:($3/1000) title '%s computing time', "%s.po" using 1:2:( (%s/1000)/($1*$2) ) every ::2 title '%s ideal'

#set size 0.95,0.45
#set origin 0,0
#set zlabel "speedup" 
#splot "%s.po" using 1:2:4 title '%s speedup', "%s.po" using 1:2:(($1*$2)) title '%s ideal'

## every ::2 start from the second line
## smooth acsplines
##with linespoints
##with linespoints 
## smooth bezier

unset multiplot
pause mouse

set terminal postscript
replot

"""
#---------------------------------------

#---------------------------------------
pscript = """
set grid 
set term %s
set output "%s.ps"
set title "GBs of Katsuras example on a grid cluster" 
set time
set xlabel "number of nodes/threads" 

#set xrange [0:8] 

set xtics 1.0

set data style lines

set multiplot

set size 0.95,0.5
set origin 0,0.5
#set origin 0,0

##set ylabel "milliseconds" 
set ylabel "seconds" 
# smooth acsplines
plot "%s.po" using 1:($3/1000) title '%s computing time', "%s.po" using 1:( (%s/1000)/($1) ) title '%s ideal'

#set size 0.95,0.45
set origin 0,0
set ylabel "speedup" 
# smooth bezier
plot "%s.po" using 1:4 title '%s speedup', "%s.po" using 1:(($1)/10) title '%s ideal'

#with linespoints 
#with linespoints

unset multiplot
pause mouse

set terminal postscript
replot

"""
#---------------------------------------

exam = bname;
psname = bname + ".ps";
pname = "plotin.gp"
p=open(pname,"w");
print p;

sqt = st;
print "seq time = ", sqt;

if xy:
    pscript = pscript_2d % ("x11",bname,bname,exam,bname,str(sqt),exam,bname,exam,bname,exam);
else:
    pscript = pscript % ("x11",bname,bname,exam,bname,str(sqt),exam,bname,exam,bname,exam);

p.write(pscript);
p.close();

cmd = "gnuplot " + pname;
print "cmd: " + cmd;
os.system(cmd);

# convert to pdf, better use pstopdf
#cmd = "ps2pdf " + psname;
cmd = "epstopdf " + psname;
print "cmd: " + cmd;
os.system(cmd);
