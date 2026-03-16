use std::process;
fn main(){
    let mut i:i32 = 1;
    let n = 214748;
    let mut f:u16 = 0;
    let mut rem = 0;
    while i<n{
        rem = n%i;
        if rem == 0{
            f = f+1;
        }
        i = i+1;
    }
    if f == 1
    {
        process::exit(1);
    }
    process::exit(0);
}
