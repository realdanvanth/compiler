class test{
  public static void main(String args[]){
    int i = 1;
    int n = 214748;
    int f = 0;
    int rem = 0;
    while(i<n){
      rem = n%i;
      if(rem == 0){
        f = f+1;
      }
      i = i+1;
    }
    if(f==1){
      System.exit(1);
    }
    System.exit(0);
  }
}
