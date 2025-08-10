int i = 1;
int n = 2147483647;
int f = 0;
int rem = 0;
while(i<n){
  rem = n%i;
  if(rem:0){
    f = f+1;
  };
  i = i+1;
};
if(f:1){
  exit(1);
};
exit(0);
