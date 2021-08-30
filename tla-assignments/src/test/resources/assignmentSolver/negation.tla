---------------------- MODULE negation ----------------------
VARIABLE a, b

Next == ~(a' = 1 /\ b' = 2) /\ (a' = 2 /\ b' = 2)
       
Init == a = 0
        
Spec == [][Next]_<< a >>     
==============================================================
