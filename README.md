# compiler 
minimal x86_64 compiler that converts my code to assembly
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

# features implemented and yet to be 
- [x] 1. lexer
- [x] 2. parser
- [x] 3. ast
- [x] 4. code gen
- [x] 5. stack management
- [x] 7. arithmetic expressions
- [x] 8. boolean expressions
- [x] 9. while loops
- [ ] 10. variable scopes
- [ ] 11. functions
- [ ] 12. peephole optimization
- [ ] 13. arrays and strings
- [ ] 14. input and output 
# what motivated me to do this?
https://youtu.be/NYClgSGzWnI?si=0JVQt2eyNDXL4UK_
