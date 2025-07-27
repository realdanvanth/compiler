import java.beans.Expression;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
class transpiler {
  String data;
  int index;
  ArrayList<token> tokens;

  public static void main(String args[]) throws IOException {
    transpiler inst = new transpiler();
    System.out.println(inst.readFile("test.tl"));
    // System.out.println(inst.tokenize(inst.readFile("test.tl")));
    exprStmt a = new exprStmt(inst.tokenize(inst.readFile("test.tl")));
    System.out.println(a.parse(new HashMap<>()));
    // a.build();
    //System.out.println(a.parse(a.symboltable));
  }

  // FILE
  // WRITER..................................................................................................
  void write(String content) throws IOException {
    FileWriter fw = new FileWriter("output.rs");
    fw.write(content);
    fw.close();
  }

  // FILE
  // READER..................................................................................................
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

  // LEXER.......................................................................................................
  public ArrayList<token> tokenize(String data) {
    ArrayList<token> tokens = new ArrayList<token>();
    for (int i = 0; i < data.length(); i++) {
      if (Character.isLetter(data.charAt(i))) { // variables
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
      } else if (Character.isDigit(data.charAt(i))) {
        String buffer = "";
        while (Character.isDigit(data.charAt(i))) {
          buffer += data.charAt(i);
          i++;
        }
        i--;
        tokens.add(new token(tokenType._int, buffer));
      } else if (data.charAt(i) == '"') {
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
    // System.out.println(tokens);
    return tokens;
  }
  // ............................................................................................................
  // .............................................................................................................
}

record token(tokenType type, String val) {
}

enum tokenType {
  _type_int,
  _type_string,
  _int,
  _string,
  _exit,
  _ident,
  _semi_colon,
  _NumExp,
  _print,
  _equal,
  _add,
  _sub,
  _mul,
  _div,
  _mod,
  _open_bracket,
  _close_bracket
}

abstract class ASTNode {
  abstract String parse(HashMap<String,Integer> symboltable);
  /*
   * public static void main(String args[]){
   * ASTNode s = new StringNode("hello world");
   * stmt s1 = new ExitStmt(s);
   * System.out.println(s1.parse(HashMap<String,Integer> symboltable));
   * }
   */
}

class IntNode extends ASTNode {
  String value;

  IntNode(String value) {
    this.value = value;
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    return value;
  }
}

class StringNode extends ASTNode {
  String value;

  StringNode(String value) {
    this.value = value;
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    return "\"" + value + "\"";
  }
}

class IdentifierNode extends ASTNode {
  String value;
  IdentifierNode(String value) {
    this.value = value;
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    return "[rsp+"+8*(symboltable.size()-symboltable.get(value))+"]";//should be implemented for string values 
    //return value;
  }
}

class BinOpNode extends ASTNode {
  enum Operator {
    add, sub, mul, div, mod
  }

  Operator op;
  ASTNode left;
  ASTNode right;

  BinOpNode(ASTNode left, Operator op, ASTNode right) {
    this.left = left;
    this.right = right;
    this.op = op;
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    String OpStr = "";
    switch (op) {
      case Operator.add:
        OpStr = "+";
        break;
      case Operator.sub:
        OpStr = "-";
        break;
      case Operator.mul:
        OpStr = "*";
        break;
      case Operator.div:
        OpStr = "/";
        break;
      case Operator.mod:
        OpStr = "%";
        break;
      default:
        System.exit(0);
    }
    return left.parse(symboltable) + OpStr + right.parse(symboltable);
  }
}

abstract class stmt {
  int index = 0;
  public ArrayList<token> tokens;

  abstract String parse(HashMap<String,Integer> symboltable);

  public void hardExpect(tokenType t) {
    if (tokens == null || index >= tokens.size() ||
        tokens.get(index).type() != t) {
      System.out.println("expected : " + t);
      System.exit(0);
    }
    index++;
  }

  public boolean expect(tokenType t) {
    if (index >= tokens.size() || tokens.get(index).type() != t) {
      return false;
    }
    return true;
  }

  public void consume() {
    index++;
  }

  abstract void build();
}

class exprStmt extends stmt {
  ASTNode value;
  ASTNode operator;

  exprStmt(ArrayList<token> tokens) {
    this.tokens = tokens;
    build();
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    int op = 0;
    String output = ""; 
    while(index<tokens.size()){
      if(expect(tokenType._int))
      {
        op++;
        output+="push "+tokens.get(index).val()+"\n";
      }
      else if(expect(tokenType._ident)){
        op++;
        if(!symboltable.containsKey(tokens.get(index).val())){
          System.out.println("invalid Indentifier");
          System.exit(0);
        }
        output+="mov rax, "+8*(symboltable.size()-symboltable.get(tokens.get(index).val()))+"]";
        output+="push rax";
      }
      else{
        switch(tokens.get(index).type()){
          case tokenType._add:
            output+="pop rax\npop rbx\nadd rax, rbx\npush rax\n";
            op--;
            break;
          case tokenType._mul:
            output+="pop rax\npop rbx\nimul rax, rbx\npush rax\n";
            op--;
            break;
          case tokenType._sub:
            output+="pop rax\npop rbx\nsub rbx, rax\npush rbx\n";
            op--;
            break;
          case tokenType._div:
            output+="pop rcx\npop rax\nxor rdx, rdx\ndiv rcx\npush rax\n";
            op--;
            break;
          case tokenType._mod:
            output="pop rcx\npop rax\nxor rdx, rdx\ndiv rc\npush rcx";
            op--;
            break;
          default:
            System.out.println("expr Error");
            System.exit(0);
        }
      }
      consume(); 
    }
    if(op!=1){
      System.out.println("Expression error");
    }
    return output;
  }

  void build() {
    //System.out.println("hello world");
  }
}
class PrintStmt extends stmt {
  ASTNode expression;

  PrintStmt(ArrayList<token> tokens) {
    this.tokens = tokens;
    build();
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    return "println!(" + expression.parse(symboltable) + ");\n";
  }

  void build() {
    System.out.println(tokens);
    hardExpect(tokenType._print);
    hardExpect(tokenType._open_bracket);
    if (expect(tokenType._ident)) {
      expression = new IdentifierNode(tokens.get(index).val());
    } else if (expect(tokenType._int)) {
      expression = new IntNode(tokens.get(index).val());
    } else if (expect(tokenType._string)) {
      expression = new StringNode(tokens.get(index).val());
    } else {
      System.out.println("invalid print stmt");
      System.exit(0);
    }
    consume();
    hardExpect(tokenType._close_bracket);
    hardExpect(tokenType._semi_colon);
  }
}

class AssignStmt extends stmt {
  IdentifierNode var;
  ASTNode value;
  HashMap<String, Integer> symboltable;

  AssignStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    // System.out.println(tokens);
    this.tokens = tokens;
    this.symboltable = symboltable;
    System.out.println(tokens);
    build();
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    if (tokens.get(0).type() == tokenType._type_int)
      return "push "+value.parse(symboltable)+"\n";
    else
      return "String logic idk";
  }

  void build() {
    if (expect(tokenType._type_int)) {
      consume();
      if (!symboltable.containsKey(tokens.get(index).val()))
        var = new IdentifierNode(tokens.get(index).val());
      else
        System.out.println("Indentifier already in use");
      consume();
      hardExpect(tokenType._equal);
      if (expect(tokenType._int)) {
        value = new IntNode(tokens.get(index).val());
        symboltable.put(var.value,symboltable.size()+1);
      } else {
        System.out.println("invalid assignment");
        System.exit(0);
      }
    } else if (expect(tokenType._type_string)) { // symbol table to be implemented
      consume();
      var = new IdentifierNode(tokens.get(index).val());
      if (expect(tokenType._string)) {
        value = new StringNode(tokens.get(index).val());
      } else {
        System.out.println("invalid assignment");
        System.exit(0);
      }
    }
    consume();
    hardExpect(tokenType._semi_colon);
  }
}

class ExitStmt extends stmt {
  ASTNode expression;

  ExitStmt(ArrayList<token> tokens) {
    this.tokens = tokens;
    build();
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    return "mov rax, 60\nmov rdi, " + expression.parse(symboltable) + "\nsyscall\n";
  }

  void build() {
    // System.out.println("hello"+tokens);
    hardExpect(tokenType._exit);
    hardExpect(tokenType._open_bracket);
    if (expect(tokenType._ident)) {
      // System.out.println("hey i am here");
      expression = new IdentifierNode(tokens.get(index).val());
    } else if (expect(tokenType._int)) {
      expression = new IntNode(tokens.get(index).val());
    } else {
      System.out.println("invalid exit stmt");
      System.exit(0);
    }
    consume();
    hardExpect(tokenType._close_bracket);
    hardExpect(tokenType._semi_colon);
  }
}

class Program extends ASTNode {
  List<token> tokens;
  List<stmt> statements;
  HashMap<String, Integer> symboltable = new HashMap<>();

  Program(List<token> tokens) {
    this.tokens = tokens;
    this.statements = new ArrayList<>();
    build();
  }

  @Override
  String parse(HashMap<String,Integer> symboltable) {
    String output = "";
    for (int i = 0; i < statements.size(); i++) {
      output += statements.get(i).parse(symboltable);
    }
    return output;
  }

  void build() {
    int depth = 0;
    int index = 0;
    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i).type() == tokenType._open_bracket) {
        depth++;
      }
      if (tokens.get(i).type() == tokenType._close_bracket) {
        depth--;
      }
      if (depth == 0 && tokens.get(i).type() == tokenType._semi_colon) {
        // statements.add(new stmt(tokens.subList(index, i)));
        //System.out.println(tokens.subList(index, i + 1));
        switch (tokens.get(index).type()) {
          case tokenType._print:
            statements.add(
                new PrintStmt(new ArrayList<>(tokens.subList(index, i + 1))));
            break;
          case tokenType._exit:
            statements.add(
                new ExitStmt(new ArrayList<>(tokens.subList(index, i + 1))));
            break;
          case tokenType._type_int:
            statements.add(new AssignStmt(
                new ArrayList<>(tokens.subList(index, i + 1)), symboltable));
            break;
        }
        index = i + 1;
      }
    }
    System.out.println(symboltable);
  }
}
