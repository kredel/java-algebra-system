#
# read output of jas runnings and prepare input for gnuplot
#

from sys import argv
from sys import exit
from time import strftime
import re;
import os
from os import system

dpat = re.compile(r'd = (\d+), time = (\d+) milliseconds, (\d+) start-up');
ppat = re.compile(r'time = (\d+) milliseconds');
fpat = re.compile(r'_p(\d+)\.');
rpat = re.compile(r'pairlist #put = (\d+) #rem = (\d+)');
res = {};
pairs = None;

for fname in argv[1:]:
    dn = -1;
    print "file =", fname
    f = open(fname,"r");
    for line in f:
        ma = dpat.search(line);
        if ma:
            print "1: ", line,
            dn = ma.group(1);
            tm = ma.group(2);
            su = ma.group(3);
            res[ int(dn) ] = { 't': tm, 's': su };
        else:
            ma = ppat.search(line);
            if ma:
                print "2: ", line,
                tm = ma.group(1);
                fm = fpat.search(fname);
                if fm:
                    print fname
                    dn = fm.group(1);
                    res[ int(dn) ] = { 't': tm, 's': "0" };
        if int(dn) >= 0 and pairs != None:
            res[ int(dn) ]['p'] = pairs[0];
            res[ int(dn) ]['r'] = pairs[1];
            pairs = None;
        pm = rpat.search(line);
        if pm:
            print "3: ", line,
            np = pm.group(1);
            nr = pm.group(2);
            if pairs == None:
                pairs = ( np, nr );
                #print "put = " + np + ", rem = " + nr;
            


print "\nres = ", res;

ks = res.keys();
print "ks = ", ks;

ks.sort();
print "ks = ", ks;

#for d in ks:
#    print " " + str(d) + " & " + res[d]['t'] + " & " + res[d]['s'] 

s1 = ks[0];
st = int( res[ s1 ]['t'] );

print "s1 = ", s1;
print "st = ", st;

speed = {};

for k in ks:
    speed[ k ] = st / float(res[ k ]['t']); 

print "speed = ", speed;

bname = "kat6_";
tag = argv[1];
i = tag.find("/");
if i >= 0:
    bname = tag[:i];
print "bname = " + bname;

summary =   "\n% run = " + tag + "\n";
summary += r"\begin{tabular}{|r|r|r|r|r|r|}" + "\n";
summary += r"\hline" + "\n";
summary += r"\#" + " threads / nodes & time & startup & speedup & put & rem \n";
summary += r"\\ \hline" + "\n";

for d in ks:
    if res[d]['s'] != "0":
        summary += " " + str(d) + " & " + res[d]['t'] + " & " + res[d]['s'] + " & " + str(speed[d])[:4];
    else: 
        summary += " " + str(d) + " & " + res[d]['t'] + " & " + " & " + str(speed[d])[:4];
    if res[d]['p'] != "":
        summary += " & " + res[d]['p'] + " & " + res[d]['r'] + "\n" 
    summary += r"\\ \hline" + "\n";

summary += r"\end{tabular}" + "\n";
print summary;

tname = bname+".tex";
tt=open(tname,"w");
tt.write(summary);
tt.close();


oname = bname+".po";
o=open(oname,"w");
ploting = "\n#threads time speedup\n";
for k in ks:
    ploting += str(k) + " " + str(res[k]['t']) + " " + str(speed[k]) + "\n";

ploting += "\n";

print ploting;
o.write(ploting);
o.close();

#exit();

#---------------------------------------
pscript = """
set grid 
set term %s
set output "%s.ps"
set title "GBs of Katsuras example on a grid cluster" 
set time
set xlabel "#nodes" 

set multiplot

set size 0.75,0.5
set origin 0,0.5
set ylabel "milliseconds" 
# smooth acsplines
plot "%s.po" title '%s computing time' with linespoints, \
     "%s.po" using 1:( %s/$1 ) title '%s ideal' with linespoints 

set size 0.75,0.5
set origin 0,0
set ylabel "speedup" 
# smooth bezier
plot  "%s.po" using 1:3 title '%s speedup' with linespoints, \
      "%s.po" using 1:1 title '%s ideal' with linespoints 

unset multiplot
pause mouse
"""
#---------------------------------------

exam = bname;
psname = bname + ".ps";
pname = "plotin.gp"
p=open(pname,"w");
print p;

mkpdf = 0; 

if mkpdf:
    pscript = pscript % ("postscript",bname,bname,exam,bname,str(res[ks[0]]['t']),exam,bname,exam,bname,exam);
else:
    pscript = pscript %       ("x11",bname,bname,exam,bname,str(res[ks[0]]['t']),exam,bname,exam,bname,exam);

p.write(pscript);
p.close();

os.system("gnuplot plotin.gp");

if mkpdf:
    cmd = "ps2pdf " + psname;
    print "cmd: " + cmd;
    os.system(cmd);
