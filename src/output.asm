global _start
_start:
push rbp
mov rbp, rsp
sub rsp,16
push 71

pop rax
mov [rbp-8], rax
mov rax, [rbp -8]
push rax
push 3
pop rcx
pop rax
xor rdx, rdx
div rcx
push rdx

pop rax
mov [rbp-16], rax
mov rax, [rbp -16]
push rax
push 0
pop rax
pop rbx
cmp rax, rbx
sete al
movzx rax, al
push rax

pop rax
cmp rax,1
je L1
mov rax, [rbp -16]
push rax
push 1
pop rax
pop rbx
cmp rax, rbx
sete al
movzx rax, al
push rax

pop rax
cmp rax,1
je L2
jmp L3
L3:
push rbp
mov rbp, rsp
sub rsp,0
push 3
pop rdi
mov rax, 60
syscall
mov rsp, rbp
pop rbp
L2:
push rbp
mov rbp, rsp
sub rsp,0
push 2
pop rdi
mov rax, 60
syscall
mov rsp, rbp
pop rbp
jmp exitif1
L1:
push rbp
mov rbp, rsp
sub rsp,0
push 1
pop rdi
mov rax, 60
syscall
mov rsp, rbp
pop rbp
jmp exitif1
exitif1:
mov rsp, rbp
pop rbp
