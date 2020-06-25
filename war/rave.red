;name Rave

       ORG      1
       SUB.F  $    11, $     1     
       CMP.I  $   125, $   119     
       SLT.A  #    18, $    -1     
       DJN.F  $    -3, <  -273     
       MOV.AB #     8, $     2     
       MOV.I  $     4, >    -4     
       DJN.B  $    -1, #     0     
       SUB.AB #     8, $    -6     
       JMN.B  $    -8, $    -8     
       SPL.A  #     0, $     0     
       MOV.I  $     1, <    -4     
       DAT.F  <   -42, <   -42     
