global _start
_start:
push rbp
mov rbp, rsp
sub rsp,32
push 1
pop rax
mov [rbp-8], rax
push 214748
pop rax
mov [rbp-16], rax
push 0
pop rax
mov [rbp-24], rax
push 0
pop rax
mov [rbp-32], rax
L10:
mov rax, [rbp -8]
push rax
mov rax, [rbp -16]
push rax
pop rax
pop rbx
cmp rbx,rax
setl al
movzx rax, al
push rax
pop rax
cmp rax,0
je exitfor2
L100:
mov rax, [rbp-16]
push rax
mov rax, [rbp-8]
push rax
pop rcx
pop rax
xor rdx, rdx
div rcx
push rdx
pop rax
mov [rbp-32], rax
mov rax, [rbp -32]
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
je L10100
jmp exitif102
L10100:
mov rax, [rbp-24]
push rax
push 1
pop rax
pop rbx
add rax, rbx
push rax
pop rax
mov [rbp-24], rax
jmp exitif102
exitif102:
mov rax, [rbp-8]
push rax
push 1
pop rax
pop rbx
add rax, rbx
push rax
pop rax
mov [rbp-8], rax
jmp L10
exitfor2:
mov rax, [rbp -24]
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
je L200
jmp exitif3
L200:
push 1
pop rdi
mov rax, 60
syscall
jmp exitif3
exitif3:
push 0
pop rdi
mov rax, 60
syscall
add rsp, 32
