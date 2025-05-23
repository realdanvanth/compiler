import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
class compiler {
  String data = ""; 
  ArrayList<token> tokens;
  public static void main(String args[])throws IOException
  {
    compiler inst = new compiler();
    inst.readFile("test.tl");
    inst.tokenize();
    inst.write(inst.tokenToAsm());
    
  }
  public void terminate(int e)
  {
    switch(e)
    {
      case 1:
        System.out.println("unknown token");
       break;
      default:
       System.out.println("unknown error");
       break;
    }
  }
  //FILE READER
  public void readFile(String path)throws IOException
  {
    FileReader fr = new FileReader(path);
    char buffer[] = new char[1024];
    int n  = fr.read(buffer) ;
    while(n!=-1){
      data+=new String(buffer,0,n);
      n = fr.read(buffer);
    }
    fr.close();
    System.out.print(data);
  }
  //STRUCT FOR TOKENS
  record token(tokenType type, String val){}
  enum tokenType{
    _return,
    _number,
    _semi_colon,
  }
  //TOKENIZER
  public void tokenize()
  {
    tokens = new ArrayList<token>();
    for(int i=0;i<data.length();i++){
      if(Character.isLetter(data.charAt(i))){
        String buffer = "";
        while (Character.isLetter(data.charAt(i))||Character.isDigit(data.charAt(i))){
          buffer+=data.charAt(i);
          i++;
        }
        i--;
        switch(buffer){
          case "return": 
            tokens.add(new token(tokenType._return,""));
            break;
          default:
            terminate(1);
        }
      }
      else if(Character.isDigit(data.charAt(i)))
      {
        String buffer = "";
        while(Character.isDigit(data.charAt(i)))
        {
          buffer+=data.charAt(i);
          i++;
        }
        i--;
        tokens.add(new token(tokenType._number,buffer));
      }
      else if(data.charAt(i)==';'){
        tokens.add(new token(tokenType._semi_colon,""));
      }
      else if(data.charAt(i)==' '){
        continue;
      }
    }
    System.out.println(tokens);
  }

  //PARSER
  public String tokenToAsm()
  {
    String output = "global _start\n_start:\n";
    for(int i = 0; i<tokens.size();i++){
      token curr = tokens.get(i);
      if(curr.type==tokenType._return){ 
        if(i+2<tokens.size()&&tokens.get(i+1).type==tokenType._number&&tokens.get(i+2).type==tokenType._semi_colon)
        {
          output+="   mov rax, 60\n";
          output+="   mov rdi, "+tokens.get(i+1).val+"\n";
          output+="   syscall";
        }
      }
    }
    System.out.println(output);
    return output;
  }

  //FILE WRITER
  void write(String content)throws IOException{
    FileWriter fw = new FileWriter("output.asm");
    fw.write(content);
    fw.close();
  }
}
