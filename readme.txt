This is a pre-alpha release of jMARS a corewars simulator written in java.

The two programs included are jMARS the corewars MARS and AsTest a utility 
which attempts to assemble and then print out a listing of a warrior. 

NOTE: the directory in which the zip file was unzipped must be in your
CLASSPATH in order to run jMARS.

To run AsTest use a command line of "java corewars.jmars.AsTest warriorfile"

jMARS is invoked with "java corewars.jmars.jMARS [options] warriorlist". Where options are 
one or more of: -r, -s, -c, -p, -l or -S. These options work the same as 
the pMARS options of the same name.

The assembler is very primitive right now and can only handle what are called 
load files in the 94 draft. Basically what this means is that it can only 
handle comments and instructions, no macros, no EQU's, not even an expression. 
The easiest way to get a load file is to run a warrior through pMARS, direct 
the listing to a file and then manually change the first line to a comment and 
replace the START label. I've included a batch file to do the first two steps, 
it is invoked with "ldrfl warriorfile outputfile". Here is an example of 
converting a warrior. 

Our Original File, rave.red:

;redcode-94
;name Rave
;author Stefan Strack
;strategy Carpet-bombing scanner based on Agony and Medusa's
;strategy (written in ICWS'94)
;strategy Submitted: @date@
;assert CORESIZE==8000

CDIST   equ 12
IVAL    equ 42
FIRST   equ scan+OFFSET+IVAL
OFFSET  equ (2*IVAL)
DJNOFF  equ -431
BOMBLEN equ CDIST+2

        org comp

scan    sub.f  incr,comp
comp    cmp.i  FIRST,FIRST-CDIST        ;larger number is A
        slt.a  #incr-comp+BOMBLEN,comp  ;compare to A-number
        djn.f  scan,<FIRST+DJNOFF       ;decrement A- and B-number
        mov.ab #BOMBLEN,count
split   mov.i  bomb,>comp               ;post-increment
count   djn.b  split,#0
        sub.ab #BOMBLEN,comp
        jmn.b  scan,scan
bomb    spl.a  0,0
        mov.i  incr,<count
incr    dat.f  <0-IVAL,<0-IVAL
	end

Then run a command line of "ldrfl rave.red raveld.red". raveld.red then looks 
like this,

Program "Rave" (length 12) by "Stefan Strack"

       ORG      START
       SUB.F  $    11, $     1     
START  CMP.I  $   125, $   113     
       SLT.A  #    24, $    -1     
       DJN.F  $    -3, <  -308     
       MOV.AB #    14, $     2     
       MOV.I  $     4, >    -4     
       DJN.B  $    -1, #     0     
       SUB.AB #    14, $    -6     
       JMN.B  $    -8, $    -8     
       SPL.A  $     0, $     0     
       MOV.I  $     1, <    -4     
       DAT.F  <   -42, <   -42     

We then need to make the first line a comment and remove the START label. If 
we really want to get fancy we could change the first line to name and author 
comments and add an END statement. The resulting file would look like this,

;name Rave
;author Stefan Strack

       ORG      1
       SUB.F  $    11, $     1     
       CMP.I  $   125, $   113     
       SLT.A  #    24, $    -1     
       DJN.F  $    -3, <  -308     
       MOV.AB #    14, $     2     
       MOV.I  $     4, >    -4     
       DJN.B  $    -1, #     0     
       SUB.AB #    14, $    -6     
       JMN.B  $    -8, $    -8     
       SPL.A  $     0, $     0     
       MOV.I  $     1, <    -4     
       DAT.F  <   -42, <   -42     
       END

We could then run it as "java corewars.jmars.jMARS raveld.red".

run.bat is supplied as a convient way to run jmars on a dos/windows os and is
executed as "run warrior1 warrior2 ...".

Here is a list of known bugs:
When started the core display is sometimes not updated and remains blank
Occasionally a warrior cannot be loaded into core resulting in a "ERROR: could
not load warrior " 

After each round jMARS will print the number of seconds the round took to
System.out. Also I have included a few warriors already changed to a file that
jMARS can handle, these can be found in the war directory.

Brian Haskin
