#
# read output of jas GB runs and prepare input for gnuplot
# $Id$
#

from sys import argv
from sys import exit
from time import strftime
import re;
import os
from os import system

hpat = re.compile(r'(m|d) = (\d+), ppn = (\d+), time = (\d+) milliseconds, (\d+) start-up');
dpat = re.compile(r'(m|d) = (\d+), time = (\d+) milliseconds, (\d+) start-up');
#hpat = re.compile(r'd = (\d+), ppn = (\d+), time = (\d+) milliseconds, (\d+) start-up');
#dpat = re.compile(r'd = (\d+), time = (\d+) milliseconds, (\d+) start-up');
ppat = re.compile(r'time = (\d+) milliseconds');
fpat = re.compile(r'p = (\d+), time = (\d+) milliseconds');
spat = re.compile(r'seq,');
####fpat = re.compile(r'_p(\d+)\.');
####spat = re.compile(r'_s\.');
#r0pat = re.compile(r'Pairlist\(#put=(\d+), #rem=(\d+), size=(\d+)\)');
rpat = re.compile(r'Pairlist\(#put=(\d+), #rem=(\d+)\)');
####r1pat = re.compile(r'pairlist #put = (\d+) #rem = (\d+)');
res = {};
pairs = None;
hybrid = False;
dist = False;
para = False;

for fname in argv[1:]:
    dn = -1;
    print "file =", fname
    f = open(fname,"r");
    for line in f:
        pm = rpat.search(line); # pairlist results
        if pm:
            print "pair: ", line,
            np = pm.group(1);
            nr = pm.group(2);
            if pairs == None:
                pairs = ( np, nr );
                #print "put = " + np + ", rem = " + nr;
            continue;
##         pm = r0pat.search(line); # pairlist results
##         if pm:
##             print "pair0: ", line,
##             np = pm.group(1);
##             nr = pm.group(2);
##             if pairs != None:
##                 pairs = ( np, nr );
##                 #print "put = " + np + ", rem = " + nr;
##             continue;
        ma = hpat.search(line); # distributed hybrid results
        if ma:
            print "h: ", line,
            d_h = ma.group(1)
            print "d_h: ", d_h,
            dn = int( ma.group(2) );
            pp = int( ma.group(3) );
            tm = long( ma.group(4) );
            su = long( ma.group(5) );
            hybrid = True;
            if not dn in res:
                res[ dn ] = {};
            if pp in res[ dn ]:
                if 't' in res[ dn ][ pp ]:
                    tx = res[ dn ][ pp ][ 't' ];
                    if tx < tm: # minimum or maximum
                        tm = tx;
                        su = res[ dn ][ pp ][ 's' ]
                        if 'p' in res[ dn ][ pp ] or 'r' in res[ dn ][ pp ]:
                            pairs = ( res[ dn ][ pp ]['p'], res[ dn ][ pp ]['r'] );
            res[ dn ][ pp ] = { 't': tm, 's': su };
            if pairs != None:
                res[ dn ][ pp ]['p'] = pairs[0];
                res[ dn ][ pp ]['r'] = pairs[1];
                pairs = None;
            continue;
        ma = dpat.search(line); # distributed results
        if ma:
            print "d: ", line,
            d_h = ma.group(1)
            print "d_h: ", d_h,
            dn = int( ma.group(2) );
            pp = 1;
            tm = long( ma.group(3) );
            su = long( ma.group(4) );
            dist = True;
            if not dn in res:
                res[ dn ] = {};
            if pp in res[ dn ]:
                if 't' in res[ dn ][ pp ]:
                    tx = res[ dn ][ pp ][ 't' ];
                    if tx < tm: # minimum or maximum
                        tm = tx;
                        su = res[ dn ][ pp ][ 's' ]
                        if 'p' in res[ dn ][ pp ] or 'r' in res[ dn ][ pp ]:
                            pairs = ( res[ dn ][ pp ]['p'], res[ dn ][ pp ]['r'] );
            res[ dn ][ pp ] = { 't': tm, 's': su };
            if pairs != None:
                res[ dn ][ pp ]['p'] = pairs[0];
                res[ dn ][ pp ]['r'] = pairs[1];
                pairs = None;
            continue;
        ma = ppat.search(line); # parallel and sequential results
        if ma:
            print "p: ", line,
            tm = long( ma.group(1) );
            fm = fpat.search(line);
            if fm:
                #print fname
                dn = 1;
                pp = int( fm.group(1) );
                para = True;
            else:
                sm = spat.search(line);
                if sm:
                    dn = 1;
                    pp = 0;
                else:
                    print "invalid: ", line,
                    dn = -1;
                    pp = -1;
            if not dn in res:
                res[ dn ] = {};
            if pp in res[ dn ]:
                if 't' in res[ dn ][ pp ]:
                    tx = res[ dn ][ pp ][ 't' ];
                    if tx < tm: # minimum or maximum
                        tm = tx;
                        if 'p' in res[ dn ][ pp ] or 'r' in res[ dn ][ pp ]:
                            pairs = ( res[ dn ][ pp ]['p'], res[ dn ][ pp ]['r'] );
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
kpp = [];
for d in ks:
    kdd = res[d].keys();
    for dd in kdd:
        if dd not in kpp:
            kpp.append(dd);
kpp.sort();
print "kpp = ", kpp;


for d in ks:
    rd = res[d].keys();
    rd.sort();
    for p in rd:
        print " " + str(d) + "," + str(p) + " & " + str(res[d][p]['t']) + " & " + str(res[d][p]['s']) 

s1 = ks[0];
if s1 == 0:
    rxx = res.keys();
    rxr = rxx[1];
    rk = res[rxr].keys();
    rk.sort();
    p1 = rk[0];
    st = int( res[ rxr ][ p1 ]['t'] );
else:
    rk = res[s1].keys();
    rk.sort();
    p1 = rk[0];
    st = int( res[ s1 ][ p1 ]['t'] );
print "s1 = ", s1;
print "p1 = ", p1;
print "st = ", st;

speed = {};

for k in ks:
    speed[k] = {};
    rk = res[k].keys();
    rk.sort();
    for p in rk:
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
summary += r"\begin{tabular}{|r|r|r|r|r|r|}" + "\n";
summary += r"\hline" + "\n";
summary += " nodes & ppn & time & speedup & put & rem \n";
summary += r"\\ \hline" + "\n";

for d in ks:
    rd = res[d].keys();
    rd.sort();
    for p in rd:
        #if res[d][p]['s'] != "0":
        #    summary += " " + str(d) + " & " + str(p) + " & " + str(res[d][p]['t']) + " & " + str(res[d][p]['s']) + " & " + str(speed[d][p])[:4];
        #else:
        #    summary += " " + str(d) + " & " + str(p) + " & " + str(res[d][p]['t']) + " & " + " & " + str(speed[d][p])[:4];
        summary += " " + str(d) + " & " + str(p) + " & " + str(res[d][p]['t']) + " & " + str(speed[d][p])[:4];
        if 'p' in res[d][p] and 'r' in res[d][p]:
            summary += " & " + str(res[d][p]['p']) + " & " + str(res[d][p]['r']) + "\n"
        else:
            summary += " & " + " & " + "\n" 
        summary += r"\\ \hline" + "\n";

summary += r"\end{tabular}" + "\n";
print summary;

tname = bname+".tex";
tt=open(tname,"w");
tt.write(summary);
tt.close();


# output for 2-d gnuplot:

oname = bname+".po";
o=open(oname,"w");
ploting = "\n#nodes #ppn time speedup ideal\n";
for k in ks:
    rk = res[k].keys();
    rk.sort();
    for p in rk:
        ideals = k*p;
        if ideals == 0:
            ideals = 1;
        ploting += str(k) + " " + str(p) + " " + str(res[k][p]['t']) + " " + str(speed[k][p]) + " " + str(ideals) + "\n";
ploting += "\n";

print ploting;
o.write(ploting);
o.close();


# output for 1-d gnuplot, colums for nodes:

oname = bname+"_p.po";
o=open(oname,"w");
ploting = "\n#ppn time p, time d1, time d2, ... \n";
for p in kpp:
    ploting += str(p);
    for k in ks:
        #print "p = " + str(p) + ", k = " + str(k)
        if res[k]:
            if p in res[k]:
                #ploting += " " + str(float(res[k][p]['t'])/1000.0);
                ploting += " " + str(speed[k][p]);
            else:
                ploting += " -";
    ploting += "\n";
#ploting += "\n";

print ploting;
o.write(ploting);
o.close();


# output for 1-d gnuplot, colums for ppn:

oname = bname+"_d.po";
o=open(oname,"w");
ploting = "\n#nodes time ppn1, time ppn2, time ppn3, ... \n";
for k in ks:
    ploting += str(k);
    for p in kpp:
        #print "p = " + str(p) + ", k = " + str(k)
        if res[k]:
            if p in res[k]:
                #ploting += " " + str(float(res[k][p]['t'])/1000.0); 
                ploting += " " + str(speed[k][p]); 
            else:
                ploting += " -";
    ploting += "\n";
#ploting += "\n";

print ploting;
o.write(ploting);
o.close();

#exit();

#---------------------------------------
pscript_2d = """
set grid 
set terminal x11
set title "Groebner bases on a grid cluster, distributed hybrid version" 
set time
set xlabel "number of nodes" 
set ylabel "threads per node" 

set xrange [0:7] 
set yrange [0:7]

#reverse

#set clip two

set xtics 1.0
set ytics 1.0

set style data lines
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

set datafile missing "-"

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
set output "%s.ps"
replot

"""
#---------------------------------------

#---------------------------------------
pscript_1d = """
set grid 
set terminal x11
set title "Groebner bases on a grid cluster, distributed version" 
set time
set xlabel "number of nodes" 

#set xrange [0:8] 

set xtics 1.0

set style data lines

set multiplot

set size 0.95,0.5
set origin 0,0.5
#set origin 0,0

##set ylabel "milliseconds" 
set ylabel "seconds" 
# smooth acsplines
plot "%s.po" using 1:($3/1000) title '%s computing time', "%s.po" using 1:( (%s/1000)/($5) ) title '%s ideal'

#set size 0.95,0.45
set origin 0,0
set ylabel "speedup" 
# smooth bezier
plot "%s.po" using 1:4 title '%s speedup', "%s.po" using 1:(($5)) title '%s ideal'

#with linespoints 
#with linespoints

unset multiplot
pause mouse

set terminal postscript
set output "%s.ps"
replot

"""
#---------------------------------------

#---------------------------------------
pscript_1p = """
set grid 
set terminal x11
set title "Groebner bases on a multi-core computer" 
set time
set xlabel "number of threads" 

#set xrange [0:8] 

set xtics 1.0

set style data lines

set multiplot

set size 0.95,0.5
set origin 0,0.5
#set origin 0,0

##set ylabel "milliseconds" 
set ylabel "seconds" 
# smooth acsplines
plot "%s.po" using 2:($3/1000) title '%s computing time', "%s.po" using 2:( (%s/1000)/($5) ) title '%s ideal'

#set size 0.95,0.45
set origin 0,0
set ylabel "speedup" 
# smooth bezier
plot "%s.po" using 2:4 title '%s speedup', "%s.po" using 2:(($5)) title '%s ideal'

#with linespoints 
#with linespoints

unset multiplot
pause mouse

set terminal postscript
set output "%s.ps"
replot

"""
#---------------------------------------


#---------------------------------------
pscript_d = """
set grid 
set terminal x11
set title "Groebner bases on a grid cluster, distributed hybrid version" 
set time
set xlabel "number of threads" 

#set xrange [0:8] 

set xtics 1.0

set style data lines

#set multiplot

#set size 0.95,0.5
#set origin 0,0.5
set origin 0,0

##set ylabel "milliseconds" 
#set ylabel "seconds" 
set ylabel "speedup" 
# smooth acsplines
plot "%s.po" using 1:2 title '%s, parallel, n = 1',\
     "%s.po" using 1:3 title 'distributed, n = 2',\
     "%s.po" using 1:4 title 'distributed, n = 3',\
     "%s.po" using 1:5 title 'distributed, n = 4',\
     "%s.po" using 1:6 title 'distributed, n = 5',\
     "%s.po" using 1:7 title 'distributed, n = 6',\
     "%s.po" using 1:8 title 'distributed, n = 7'

     #with linespoints

#unset multiplot
pause mouse

set terminal postscript
set output "%s.ps"
replot

"""
#---------------------------------------

#---------------------------------------
pscript_dp = """
set grid 
set terminal x11
set title "Groebner bases on a grid cluster, distributed hybrid version" 
set time
set xlabel "number of nodes" 

#set xrange [0:8] 

set xtics 1.0

set style data lines

#set multiplot

#set size 0.95,0.5
#set origin 0,0.5
set origin 0,0

##set ylabel "milliseconds" 
#set ylabel "seconds" 
set ylabel "speedup" 
# smooth acsplines
# 1:2 ignored
plot "%s.po" using 1:3 title '%s, ppn = 1',\
     "%s.po" using 1:4 title 'ppn = 2',\
     "%s.po" using 1:5 title 'ppn = 3',\
     "%s.po" using 1:6 title 'ppn = 4',\
     "%s.po" using 1:7 title 'ppn = 5',\
     "%s.po" using 1:8 title 'ppn = 6',\
     "%s.po" using 1:9 title 'ppn = 7',\
     "%s.po" using 1:10 title 'ppn = 8'

     #with linespoints

#unset multiplot
pause mouse

set terminal postscript
set output "%s.ps"
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
#print "args: %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s. " % ("x11",bname,exam,bname,str(sqt),exam,bname,exam,bname,exam,bname);

hyb2d = False; 
#hyb2d = True
t2n = True; # thread to nodes
#t2n = False

if hybrid:
    if hyb2d:
        pscript = pscript_2d % (bname,exam,bname,str(sqt),exam,bname,exam,bname,exam,bname);
    else:
        if t2n:
            oname = bname+"_p";
            pscript = pscript_d % (oname,exam,oname,oname,oname,oname,oname,oname,bname);
        else:
            oname = bname+"_d";
            pscript = pscript_dp % (oname,exam,oname,oname,oname,oname,oname,oname,oname,bname);
else:
    if dist:
        pscript = pscript_1d % (bname,exam,bname,bname,str(sqt),exam,bname,exam,bname,exam);
    else:
        if para:
            pscript = pscript_1p % (bname,exam,bname,bname,str(sqt),exam,bname,exam,bname,exam);

p.write(pscript);
p.close();

cmd = "gnuplot " + pname;
print "cmd: " + cmd;
os.system(cmd);

# convert to pdf, better use pstopdf, embed all fonts
#cmd = "ps2pdf " + psname;
cmd = "epstopdf --autorotate=All --embed " + psname;
print "cmd: " + cmd;
os.system(cmd);
