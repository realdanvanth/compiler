javac $1.java && java $1 
nasm -felf64 output.asm
ld -o output output.o
echo "OUTPUT: "
./output
echo $?
