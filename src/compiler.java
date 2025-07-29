import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

class compiler {
  String data;
  int index;
  ArrayList<token> tokens;

  public static void main(String args[]) throws IOException {
    compiler inst = new compiler();
    //System.out.println(inst.readFile("test.tl"));
    // System.out.println(inst.tokenize(inst.readFile("test.tl")));
    // exprStmt a = new exprStmt(inst.tokenize(inst.readFile("test.tl")), new
    // HashMap<>()); System.out.println(a.parse(new HashMap<>()));
    Program a = new Program(inst.tokenize(inst.readFile("test.tl")));
    //System.out.println(a.parse(a.symboltable));
    inst.write(a.parse(a.symboltable));
    // a.build();
    // System.out.println(a.parse(a.symboltable));
  }

  // FILE
  // WRITER..................................................................................................
  void write(String content) throws IOException {
    FileWriter fw = new FileWriter("output.asm", false);
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
  abstract String parse(HashMap<String, Integer> symboltable);
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
  String parse(HashMap<String, Integer> symboltable) {
    return value;
  }
}

class StringNode extends ASTNode {
  String value;

  StringNode(String value) {
    this.value = value;
  }

  @Override
  String parse(HashMap<String, Integer> symboltable) {
    return "\"" + value + "\"";
  }
}

class IdentifierNode extends ASTNode {
  String value;

  IdentifierNode(String value) {
    this.value = value;
  }

  @Override
  String parse(HashMap<String, Integer> symboltable) {
    return "[rbp+" + 8 * (symboltable.size() - symboltable.get(value)) +
        "]\n"; // should be implemented for string values
    // return value;
  }
}

abstract class stmt {
  int index = 0;
  HashMap<String, Integer> symboltable;
  public ArrayList<token> tokens;

  abstract String parse();

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

  public ArrayList<token> nextSemiColon() {
    ArrayList<token> output = new ArrayList<>();
    while (index < tokens.size() &&
        tokens.get(index).type() != tokenType._semi_colon) {
      output.add(tokens.get(index));
      consume();
    }
    // hardExpect(tokenType._semi_colon);
    return output;
  }

  public void consume() {
    index++;
  }

  abstract void build();
}

class exprStmt extends stmt {
  ASTNode value;
  ASTNode operator;
  HashMap<String, Integer> symboltable;

  exprStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    this.tokens = tokens;
    this.symboltable = symboltable;
    // System.out.println(tokens);
    //System.out.println("Symboltable of expr : " + symboltable);
    build();
  }

  @Override
  String parse() { // alaways use pop after parsing Expression
    int op = 0;
    String output = "";
    //System.out.println(tokens);
    //System.out.println("INDEX: "+index);
    index = 0;
    // System.out.println("OUTPUT :::" + tokens);
    while (index < tokens.size()) {
      if (expect(tokenType._int)) {
        op++;
        output += "push " + tokens.get(index).val() + "\n";
      } else if (expect(tokenType._ident)) {
        if (!symboltable.containsKey(tokens.get(index).val())) {
          System.out.println("invalid Indentifier");
          System.exit(0);
        }
        output += "mov rax, [rbp" +(symboltable.get(tokens.get(index).val()))+"]\npush rax;getting "+tokens.get(index).val()+"\n";
        op++;
        // output += "push rax\n";
      } else {
        switch (tokens.get(index).type()) {
          case tokenType._add:
            output += "pop rax\npop rbx\nadd rax, rbx\npush rax\n";
            op--;
            break;
          case tokenType._mul:
            output += "pop rax\npop rbx\nimul rax, rbx\npush rax\n";
            op--;
            break;
          case tokenType._sub:
            output += "pop rax\npop rbx\nsub rbx, rax\npush rbx\n";
            op--;
            break;
          case tokenType._div:
            output += "pop rcx\npop rax\nxor rdx, rdx\ndiv rcx\npush rax\n";
            op--;
            break;
          case tokenType._mod:
            output += "pop rcx\npop rax\nxor rdx, rdx\ndiv rcx\npush rdx\n";
            op--;
            break;
          default:
            System.out.println("expr Error");
            System.exit(0);
        }
      }
      consume();
    }
    // System.out.println(output);
    if (op != 1) {
      System.out.println(output);
      System.out.println("Expression error");
    }
    return output + "; expression ends here\n";
  }

  void build() {
    // System.out.println("hello world");
    ArrayList<token> output = new ArrayList<>();
    Stack<token> stack = new Stack<>();
    int depth = 0;
    while (index < tokens.size()) {
      if (expect(tokenType._int)) {
        output.add(tokens.get(index));
      } else if (expect(tokenType._ident)) {
        if (!symboltable.containsKey(tokens.get(index).val())) {
          System.out.println(tokens.get(index).val());
          // System.out.println(symboltable);
          System.exit(0);
        }
        output.add(tokens.get(index));
      } else {
        switch (tokens.get(index).type()) {
          case tokenType._open_bracket:
            stack.push(tokens.get(index));
            break;
          case tokenType._close_bracket:
            while (!stack.isEmpty() &&
                stack.peek().type() != tokenType._open_bracket) {
              output.add(stack.pop());
            }
            if (stack.isEmpty()) {
              //System.out.println("NAH BRUH WRONG BRACKET");
              System.exit(0);
            }
            stack.pop();
            break;
          case tokenType._div:
          case tokenType._mul:
          case tokenType._mod:
            stack.push(tokens.get(index));
            break;
          case tokenType._add:
            while (!stack.isEmpty() && (stack.peek().type() == tokenType._mod ||
                stack.peek().type() == tokenType._div ||
                stack.peek().type() == tokenType._mul ||
                stack.peek().type() == tokenType._add)) {
              output.add(stack.pop());
            }
            stack.push(tokens.get(index));
            break;
          case tokenType._sub:
            while (!stack.isEmpty() && (stack.peek().type() == tokenType._mod ||
                stack.peek().type() == tokenType._div ||
                stack.peek().type() == tokenType._mul ||
                stack.peek().type() == tokenType._add ||
                stack.peek().type() == tokenType._sub)) {
              output.add(stack.pop());
            }
            stack.push(tokens.get(index));
            break;
        }
      }
      consume();
    }
    while (!stack.isEmpty()) {
      output.add(stack.pop());
    }
    // System.out.println("WHAT THE HELL :::: "+output);
    // tokens.clear();
    // tokens.addAll(output);
    index = 0;
    tokens = output;
  }
}

class PrintStmt extends stmt {
  ASTNode expression;

  PrintStmt(ArrayList<token> tokens) {
    this.tokens = tokens;
    build();
  }

  @Override
  String parse() {
    return "println!("
        + ");\n";
  }

  void build() {
    // System.out.println(tokens);
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
  exprStmt expr;
  HashMap<String, Integer> symboltable;
  String output = "";
  AssignStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    // System.out.println(tokens);
    this.tokens = tokens;
    this.symboltable = symboltable;
    // System.out.println(tokens);
    build();
  }

  @Override
  String parse() {
    if (tokens.get(0).type() == tokenType._type_int)
      return expr.parse()+ "pop rax\nmov [rbp"+(symboltable.get(var.value))+"], rax\n";
    else
      return "String logic idk";
  }

  void build() {
    if (expect(tokenType._type_int)) {
      consume();
      if (!symboltable.containsKey(tokens.get(index).val()))
        var = new IdentifierNode(tokens.get(index).val());
      else {
        System.out.println("Indentifier already in use");
        System.exit(0);
      }
      consume();
      hardExpect(tokenType._equal);
      expr = new exprStmt(nextSemiColon(), symboltable);
      output = expr.parse(); 
      symboltable.put(var.value, (symboltable.size()+1)*-8);
      System.out.println("updated the table " + symboltable);
    } else if (expect(
        tokenType._type_string)) { // symbol table to be implemented
      consume();
      var = new IdentifierNode(tokens.get(index).val());
      if (expect(tokenType._string)) {
        StringNode val = new StringNode(tokens.get(index).val());
      } else {
        System.out.println("invalid assignment");
        System.exit(0);
      }
    }
    // consume();
    hardExpect(tokenType._semi_colon);
  }
}

class ExitStmt extends stmt {
  exprStmt expr;
  HashMap<String, Integer> symboltable = new HashMap<>();

  ExitStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    this.tokens = tokens;
    this.symboltable = symboltable;
    build();
  }

  @Override
  String parse() {
    return expr.parse() + "pop rdi\nmov rax, 60\nsyscall\n";
  }

  void build() {
    // System.out.println("hello"+tokens);
    hardExpect(tokenType._exit);
    expr = new exprStmt(nextSemiColon(), symboltable);
    hardExpect(tokenType._semi_colon);
  }
}

class Program extends ASTNode {
  List<token> tokens;
  List<stmt> statements;
  HashMap<String, Integer> symboltable = new HashMap<>();
  int rsp;
  Program(List<token> tokens) {
    this.tokens = tokens;
    this.statements = new ArrayList<>();
    build();
  }

  @Override
  String parse(HashMap<String, Integer> symboltable) {
    String output = "global _start\n_start:\npush rbp\nmov rbp, rsp\nsub rsp,"+rsp+"\n";
    for (int i = 0; i < statements.size(); i++) {
      output += statements.get(i).parse();
    }
    return output;
  }

  void build() {
    int depth = 0;
    int index = 0;
    rsp = 0;
    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i).type() == tokenType._open_bracket) {
        depth++;
      }
      if (tokens.get(i).type() == tokenType._close_bracket) {
        depth--;
      }
      if (depth == 0 && tokens.get(i).type() == tokenType._semi_colon) {
        // statements.add(new stmt(tokens.subList(index, i)));
        // System.out.println(tokens.subList(index, i + 1));
        switch (tokens.get(index).type()) {
          case tokenType._print:
            statements.add(
                new PrintStmt(new ArrayList<>(tokens.subList(index, i + 1))));
            break;
          case tokenType._exit:
            //System.out.println("over here" + symboltable);
            ExitStmt exitstmt = new ExitStmt(new ArrayList<>(tokens.subList(index, i + 1)),
                new HashMap<>(symboltable));
            statements.add(exitstmt);
            symboltable.putAll(exitstmt.symboltable);
            break;
          case tokenType._type_int:
            AssignStmt assignstmt =new AssignStmt(new ArrayList<>(tokens.subList(index, i + 1)),
                    new HashMap<>(symboltable));
            statements.add(assignstmt);
            rsp+=8;
            symboltable.putAll(assignstmt.symboltable);
            break;
          default:
            System.out.println("Syntax error");
            System.exit(0);
            break;
        }
        index = i + 1;
      }
      // System.out.println(symboltable);
    }
    //System.out.println(symboltable);
  }
}
