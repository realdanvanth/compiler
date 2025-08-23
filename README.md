# compiler 
minimal x86_64 compiler that converts cslop to assembly
it is written entirely from scratch without any dependencies 
except the java runtime environment and nasm assembler 
currently all the code is modular in assembly ,meaning its really 
unoptimized and it still manages to beat python at speed 
## installation 
```bash
npm install real-compiler 
```
# execution 
```bash
npx compiler yourfile.tl
```

## operators 
- ':' -> equal to
- '|' -> or
- '&' -> and

the rest are standard

## variable declaration statement
```c
int a = {NumExp} \\ all integers and booleans are 64 bit
boolean a = {BoolExp}
```
## if statements 
```c
if(cond a){
{Program}
}
elif(cond b){
{Program}
}
else
{
{Program}
};
```
## loop statements 
```c
while(cond a){
{Prog}
};
```
## exit statement
```c
exit({NumExp});
```
## program
a program is a list of statements 
```c
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
```

# features implemented and yet to be 
- [x] 1. lexer
- [x] 2. parser
- [x] 3. ast
- [x] 4. code gen
- [x] 5. stack management
- [x] 7. arithmetic expressions
- [x] 8. boolean expressions
- [x] 9. while loops
- [x] 10. variable scopes
- [ ] 11. error handling
- [ ] 12. functions
- [ ] 13. peephole optimization
- [ ] 14. arrays and strings
- [ ] 15. input and output 
# what motivated me to do this?
https://youtu.be/NYClgSGzWnI?si=0JVQt2eyNDXL4UK_
