int number = 101;
int n = number;
int rev = 0;
while(!(number:0)){
  rev = rev*10+number%10;
  number = number/10;
};
if(rev:n){
  exit(1);
};
exit(0);




int sum = 0;
int number = 28;
int i = 1;
while (i < number) {
    int rem = number % i;
    if (rem : 0) {
        sum = sum + i;
    };
    i = i + 1;
};
if (sum : number) {
    exit(1);  
};
exit(0);      




int sof = 0;
int num = 145;
int number = num;
while(!(num:0)){
  int i = num%10;
  int f = 1;
  while(i>1){
    f = f*i;
    i = i-1;
  };
  sof = sof + f;
  num = num / 10;
};
if (sof : number) {
    exit(1);  
};
exit(sof);  



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


int n = 101;
int o = 0;
int i = 0;
while(!(n:0)){
  int j = 0;
  int s = 1;
  while(j<i){
    s = s * 2;
    j = j + 1;
  };
  int d = n%10;
  if(!(d:0))
  {
    o = o + s;
  };
  n = n/10;
  i = i+1;
};
exit(o);

