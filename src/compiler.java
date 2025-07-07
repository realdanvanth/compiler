import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
class transpiler{
  String data;
  int index;
  ArrayList<token> tokens;
  public static void main(String args[]) throws IOException{
    transpiler inst= new transpiler();
    System.out.println(inst.readFile("test.tl"));
    //System.out.println(inst.tokenize(inst.readFile("test.tl")));
    ArrayList<token> tokens = inst.tokenize(inst.readFile("test.tl"));
    PrintStmt stmt = new PrintStmt(tokens);
    stmt.build();
    System.out.println(stmt.parse());
  }
  //FILE WRITER..................................................................................................
  void write(String content) throws IOException {
    FileWriter fw = new FileWriter("output.rs");
    fw.write(content);
    fw.close();
  }
  //FILE READER..................................................................................................
  public String readFile(String path) throws IOException {
    String data = "";
    FileReader fr = new FileReader(path);
    char buffer[] = new char[1024];
    int n = fr.read(buffer);
    while (n != -1) {
      data += new String(buffer, 0, n);
      n = fr.read(buffer);
    }
    fr.close();
    return data;
  }
  //LEXER....................................................................................................... 
  public ArrayList<token> tokenize(String data) {
    ArrayList<token> tokens = new ArrayList<token>();
    for (int i = 0; i < data.length(); i++) {
      if (Character.isLetter(data.charAt(i))) {// variables
        String buffer = "";
        while (Character.isLetter(data.charAt(i)) ||
            Character.isDigit(data.charAt(i))) {
          buffer += data.charAt(i);
          i++;
        }
        i--;
        switch (buffer) {
          case "exit":
            tokens.add(new token(tokenType._exit, ""));
            break;
          case "int":
            tokens.add(new token(tokenType._type_int, ""));
            break;
          case "string":
            tokens.add(new token(tokenType._type_string, ""));
            break;
          case "print":
            tokens.add(new token(tokenType._print, ""));
            break;
          default:
            tokens.add(new token(tokenType._ident, buffer));
        }
      } else if (Character.isDigit(data.charAt(i))){
        String buffer = "";
        while (Character.isDigit(data.charAt(i))) {
          buffer += data.charAt(i);
          i++;
        }
        i--;
        tokens.add(new token(tokenType._int, buffer));
      } else if (data.charAt(i) == '"')
      {
        String buffer = "";
        i++;
        while (i < data.length() && data.charAt(i) != '"') {
          buffer += data.charAt(i);
          i++;
        }
        tokens.add(new token(tokenType._string, buffer));
      } else if (data.charAt(i) == '(') {
        tokens.add(new token(tokenType._open_bracket, ""));
      } else if (data.charAt(i) == ')') {
        tokens.add(new token(tokenType._close_bracket, ""));
      } else if (data.charAt(i) == ';') {
        tokens.add(new token(tokenType._semi_colon, ""));
      } else if (data.charAt(i) == '=') {
        tokens.add(new token(tokenType._equal, ""));
      } else if (data.charAt(i) == ' ' || data.charAt(i) == '\n') {
        continue;
      } else if (data.charAt(i) == '+') {
        tokens.add(new token(tokenType._add, ""));
      } else if (data.charAt(i) == '-') {
        tokens.add(new token(tokenType._sub, ""));
      } else if (data.charAt(i) == '*') {
        tokens.add(new token(tokenType._mul, ""));
      } else if (data.charAt(i) == '/') {
        tokens.add(new token(tokenType._div, ""));
      } else if (data.charAt(i) == '%') {
        tokens.add(new token(tokenType._mod, ""));
      } else {
        System.out.println("Lexing Error");
      }
    }
    this.tokens = tokens;
    return tokens;
  }
  //............................................................................................................ 
  //.............................................................................................................
}
record token(tokenType type, String val) {
  }

enum tokenType {
    _type_int, _type_string, _int, _string, _exit, _ident,_semi_colon,
    _NumExp,_print,_equal,_add,_sub,_mul,_div,_mod,_open_bracket,
    _close_bracket
  }
abstract class ASTNode {
  abstract String parse(); 
  /*public static void main(String args[]){
  ASTNode s = new StringNode("hello world");
  stmt s1 = new ExitStmt(s);
  System.out.println(s1.parse());
  }*/
}
class IntNode extends ASTNode {
  String value;
  IntNode(String value) {
    this.value = value;
  }
  @Override
  String parse(){
    return value;
  }
}
class StringNode extends ASTNode{
  String value;
  StringNode(String value){
    this.value = value;
  }
  @Override
  String parse(){
    return "\""+value+"\"";
  } 
}
class IdentifierNode extends ASTNode{
  String value;
  IdentifierNode(String value){
    this.value = value;
  }
  @Override
  String parse(){
    return "\""+value+"\"";
  }
}
class BinOpNode extends ASTNode{
  enum Operator{
    add,sub,mul,div,mod
  }
  Operator op;
  ASTNode left;
  ASTNode right;
  BinOpNode(ASTNode left,Operator op,ASTNode right){
    this.left = left;
    this.right = right;
    this.op = op;
  }
  @Override
  String parse(){
    String OpStr="";
    switch(op){
      case Operator.add:
        OpStr="+";
      break;
      case Operator.sub:
        OpStr="-";
      break;
      case Operator.mul:
        OpStr="*";
      break;
      case Operator.div:
        OpStr="/";
      break;
      case Operator.mod:
        OpStr="%";
      break;
      default:
      System.exit(0);
    }
    return left.parse()+OpStr+right.parse();
  } 
}
abstract class stmt{
  int index=0; 
  public ArrayList<token> tokens;
  abstract String parse();
  public void hardExpect(tokenType t){
    if(index>=tokens.size()||tokens.get(index).type()!=t){
      System.out.println("expected : "+t);
      System.exit(0);
    }
    index++;
  }
  public boolean expect(tokenType t){
    if(index>=tokens.size()||tokens.get(index).type()!=t){
      return false;
    }
    return true;
  }
  public void consume(){
    index++;
  }
  abstract void build();
}
class PrintStmt extends stmt{
  ASTNode expression;
  PrintStmt(ArrayList<token> tokens){ 
    this.tokens = tokens;
  }
  @Override
  String parse(){
    return "println!("+expression.parse()+");\n";
  }
  void build(){
    System.out.println(tokens);
    hardExpect(tokenType._open_bracket);
    if(expect(tokenType._ident)){
      expression = new IdentifierNode(tokens.get(index).val());
    }
    if(expect(tokenType._int)){
      expression = new IntNode(tokens.get(index).val());
    }
    else if(expect(tokenType._string)){
      expression = new StringNode(tokens.get(index).val());
    }
    else{
      System.out.println("invalid print stmt");
    }
    consume();
    hardExpect(tokenType._close_bracket);
  }
}
/*
class AssignStmt extends stmt{
  IdentifierNode var;
  ASTNode value;
  AssignStmt(IdentifierNode var,ASTNode value){
    this.var = var;
    this.value = value;
  }
  @Override
  String parse(){
    return "let mut "+var.parse()+" = "+value.parse()+";\n";
  }
}
class ExitStmt extends stmt{
  ASTNode expression;
  ExitStmt(ASTNode expression){
    this.expression = expression;
  }
  @Override
  String parse(){
    return "std::process:exit("+expression.parse()+");\n";
  }
}
class Program extends ASTNode{
  List<stmt> statements;
  Program(List<stmt>statements){
    this.statements=statements;
  }
  @Override
  String parse(){
    String output="";
    for(int i = 0;i<statements.size();i++){
      output+=statements.get(i).parse();
    }
    return output;
  } 
}
*/

