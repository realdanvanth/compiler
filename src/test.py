i = 1
n = 214748
f = 0
rem = 0
while(i<n):
    rem = n%i
    if rem==0:
        f = f+1
    i = i+1
if f == 1:
    exit(1)
exit(0)
