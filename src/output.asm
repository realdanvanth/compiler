global _start
_start:
push rbp
mov rbp, rsp
sub rsp,24
push 50
push 7
pop rcx
pop rax
xor rdx, rdx
div rcx
push rdx
; expression ends here
pop rax
mov [rbp-8], rax
mov rax, [rbp-8]
push rax;getting a
push 20
pop rax
pop rbx
imul rax, rbx
push rax
; expression ends here
pop rax
mov [rbp-16], rax
mov rax, [rbp-16]
push rax;getting b
push 6
pop rcx
pop rax
xor rdx, rdx
div rcx
push rdx
; expression ends here
pop rax
mov [rbp-24], rax
mov rax, [rbp-24]
push rax;getting c
; expression ends here
pop rdi
mov rax, 60
syscall
