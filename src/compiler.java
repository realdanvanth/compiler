import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

class compiler {
  String data;
  ArrayList<token> tokens;

  public static void main(String args[]) throws IOException {
    compiler inst = new compiler();
    inst.readFile("test.tl");
    inst.tokenize();
    Program a = new Program(inst.tokens,true);
    // booleanStmt a = new booleanStmt(inst.tokens, new HashMap<>());
    // inst.write(a.parse());
    // System.out.println(a.tokens);
    inst.write(a.parse(a.symboltable));
    // System.out.println(a.parse(a.symboltable));
  }

  void write(String data) throws IOException {
    FileWriter fw = new FileWriter("output.asm", false);
    fw.write(data);
    fw.close();
  }

  public void readFile(String path) throws IOException {
    String data = "";
    FileReader fr = new FileReader(path);
    char buffer[] = new char[1024];
    int n = fr.read(buffer);
    while (n != -1) {
      data += new String(buffer, 0, n);
      n = fr.read(buffer);
    }
    fr.close();
    this.data = data;
    if (data == null) {
      System.out.println("Nothing Present in File");
      System.exit(0);
    }
  }

  public void tokenize() {
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
          case "if":
            tokens.add(new token(tokenType._if, ""));
            break;
          case "elif":
            tokens.add(new token(tokenType._else_if, ""));
            break;
          case "else":
            tokens.add(new token(tokenType._else, ""));
            break;
          case "bool":
            tokens.add(new token(tokenType._type_boolean, ""));
            break;
          case "true":
            tokens.add(new token(tokenType._boolean, "1"));
            break;
          case "false":
            tokens.add(new token(tokenType._boolean, "0"));
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
      } else if (data.charAt(i) == '{') {
        tokens.add(new token(tokenType._open_curly, ""));
      } else if (data.charAt(i) == '}') {
        tokens.add(new token(tokenType._close_curly, ""));
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
      } else if (data.charAt(i) == '&') {
        tokens.add(new token(tokenType._and, ""));
      } else if (data.charAt(i) == ':') {
        tokens.add(new token(tokenType._equal_to, ""));
      } else if (data.charAt(i) == '|') {
        tokens.add(new token(tokenType._or, ""));
      } else if (data.charAt(i) == '!') {
        tokens.add(new token(tokenType._not, ""));
      } else if (data.charAt(i) == '>') {
        tokens.add(new token(tokenType._greater_than, ""));
      } else if (data.charAt(i) == '<') {
        tokens.add(new token(tokenType._less_than, ""));
      } else {
        System.out.println("Lexing Error");
      }
    }
    this.tokens = tokens;
  }
}

// ...............................................................................................................
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
  _close_bracket,
  _if,
  _and,
  _or,
  _not,
  _type_boolean,
  _boolean,
  _equal_to,
  _greater_than,
  _less_than,
  _open_curly,
  _close_curly,
  _else,
  _else_if
}

// ...............................................................................................................
abstract class Stmt {
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

  public ArrayList<token> nextOccurance(tokenType t) {
    ArrayList<token> output = new ArrayList<>();
    int cdepth = 0;// gotta change this  for use the depth and shit
    int sdepth = 0;
    while (index < tokens.size() &&tokens.get(index).type() != t&&cdepth == 0&&sdepth==0) {
       if (expect(tokenType._open_bracket)) {
        sdepth++;
      } else if (expect(tokenType._close_bracket)) {
        sdepth--;
      } else if (expect(tokenType._open_curly)) {
        cdepth++;
      } else if (expect(tokenType._close_curly)) {
        cdepth--;
      }
      output.add(tokens.get(index));
      consume();
    }
    return output;
  }

  public int isValidtoUse(token ident, tokenType t,
      HashMap<String, Integer> symboltable) {
    if (!expect(tokenType._ident)) {
      System.out.println("Identifier expected ");
      System.exit(0);
    }
    // System.exit(0);
    // System.out.println(symboltable);
    if (!(symboltable.containsKey("1" + ident.val()) ||
        symboltable.containsKey("2" + ident.val()) ||
        symboltable.containsKey("3" + ident.val()))) {
      System.out.println("Invalid usage of identifier ");
      System.exit(0);
    }
    String check = "";
    switch (t) {
      case tokenType._int:
        check = "1";
        break;
      case tokenType._boolean:
        check = "2";
        break;
      case tokenType._string:
        check = "3";
        break;
    }
    if (symboltable.containsKey(check + ident.val())) {
      return symboltable.get(check + ident.val());
    }
    // System.out.println("use the correct type " + check + ident);
    // System.exit(0);
    return 0;
  }

  public void addident(token ident, tokenType t,
      HashMap<String, Integer> symboltable) {
    if (symboltable.containsKey("1" + ident.val()) ||
        symboltable.containsKey("2" + ident.val()) ||
        symboltable.containsKey("3" + ident.val())) {
      System.out.println("identifier in use");
    }
    String check = "";
    switch (t) {
      case tokenType._int:
        check = "1";
        break;
      case tokenType._boolean:
        check = "2";
        break;
      case tokenType._string:
        check = "3";
        break;
    }
    symboltable.put(check + ident.val(), (symboltable.size() + 1) * -8);
  }

  public void consume() {
    index++;
  }

  abstract void build();
}

// ...............................................................................................................
class exprStmt extends Stmt {
  exprStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    this.tokens = tokens;
    this.symboltable = symboltable;
    build();
  }

  @Override
  String parse() { // alaways use pop after parsing Expression
    int op = 0;
    String output = "";
    index = 0;
    while (index < tokens.size()) {
      if (expect(tokenType._int)) {
        op++;
        output += "push " + tokens.get(index).val() + "\n";
      } else if (expect(tokenType._ident)) {
        int addr = isValidtoUse(tokens.get(index), tokenType._int, symboltable);
        if (addr == 0) {
          System.out.println("use correct type");
          System.exit(0);
        }
        output += "mov rax, [rbp " + addr + "]\npush rax\n";
        op++;
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
            System.out.println("Invalid operand In the expression");
            System.exit(0);
        }
      }
      consume();
    }
    if (op != 1) {
      System.out.println(output);
      System.out.println("Expression error");
      System.exit(0);
    }
    return output + "\n";
  }

  void build() { // another method to check if its a valid expression to be
                 // added
    ArrayList<token> output = new ArrayList<>();
    Stack<token> stack = new Stack<>();
    while (index < tokens.size()) {
      if (expect(tokenType._int)) {
        output.add(tokens.get(index));
      } else if (expect(tokenType._ident)) {
        if (!symboltable.containsKey("1" + tokens.get(index).val())) {
          System.out.println(tokens.get(index).val() + "does not exist ");
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
              System.out.println("Invalid Usage of brackets");
              System.exit(0);
            }
            stack.pop();
            break;
          case tokenType._div:
          case tokenType._mul:
          case tokenType._mod:
            // stack.push(tokens.get(index));
            while (!stack.isEmpty() && (stack.peek().type() == tokenType._mod ||
                stack.peek().type() == tokenType._div ||
                stack.peek().type() == tokenType._mul)) {
              output.add(stack.pop());
            }
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
    tokens = output;
  }
}

// ...............................................................................................................
class exitStmt extends Stmt {
  Stmt expr;
  HashMap<String, Integer> symboltable = new HashMap<>();

  exitStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
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
    expr = new booleanStmt(nextOccurance(tokenType._semi_colon), symboltable);
    System.out.println("OVER HERE");
    //hardExpect(tokenType._semi_colon);
  }
}

// ...............................................................................................................
class booleanStmt extends Stmt {
  HashMap<String, Integer> symboltable;

  booleanStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    this.tokens = tokens;
    this.symboltable = symboltable;
    build();
  }

  @Override
  String parse() {
    int op = 0;
    String output = "";
    index = 0;
    while (index < tokens.size()) {
      if (expect(tokenType._boolean) || expect(tokenType._int)) {
        op++;
        output += "push " + tokens.get(index).val() + "\n";
      } else if (expect(tokenType._ident)) {
        int addr = isValidtoUse(tokens.get(index), tokenType._int, symboltable);
        int addr1 = isValidtoUse(tokens.get(index), tokenType._boolean, symboltable);
        if (addr == addr1) {
          System.out.println("Use correct type");
          System.exit(0);
        }
        output += "mov rax, [rbp " + (addr + addr1) + "]\npush rax\n";
        op++;
      } else if (expect(tokenType._not)) {
        output += "pop rax\ntest rax, rax\nsete al\nmovzx rax,al\npush rax\n";
      } else {
        output += "pop rax\npop rbx\n";
        switch (tokens.get(index).type()) {
          case tokenType._and:
            output += "and rax, rbx\nsetne al\nmovzx rax, al\n";
            break;
          case tokenType._or:
            output += "or rax, rbx\nsetne al\nmovzx rax, al\n";
            break;
          case tokenType._equal_to:
            output += "cmp rax, rbx\nsete al\nmovzx rax, al\n";
            break;
          case tokenType._greater_than:
            output += "cmp rbx, rax\nsetg al\nmovzx rax,al\n";
            break;
          case tokenType._less_than:
            output += "cmp rbx,rax\nsetl al\nmovzx rax, al\n";
            break;
          default:
             System.out.println("Invalid token inside boolean");
            System.out.println(tokens.get(index));
            System.exit(0);
            break;
        }
        op--;
        output += "push rax\n";
      }
      consume();
    }
    if (op != 1) {
      System.out.println("Invalid boolean ");
      System.exit(0);
    }
    return output;
  }

  void build() {
    ArrayList<token> output = new ArrayList<>();
    Stack<token> stack = new Stack<>();
    while (index < tokens.size()) {
      // System.out.println("Stack "+ stack + " output "+output);
      if (expect(tokenType._boolean) || expect(tokenType._int)) {
        output.add(tokens.get(index));
      } else if (expect(tokenType._ident)) {
        if (!symboltable.containsKey("2" + tokens.get(index).val()) &&
            !symboltable.containsKey("1" + tokens.get(index).val())) {
          System.out.println("2" + tokens.get(index).val() + "does not exist");
          System.out.println(symboltable);
          System.exit(0);
        }
        output.add(tokens.get(index));
      } else if (expect(tokenType._not)) {
        stack.add(tokens.get(index));
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
              System.out.println("Invalid Usage of brackets");
              System.exit(0);
            }
            System.out.println(stack.pop()+"popped out");
            break;
          case tokenType._and:
          case tokenType._or:
          case tokenType._equal_to:
          case tokenType._less_than:
          case tokenType._greater_than:
            while (!stack.isEmpty() &&
                stack.peek().type() != tokenType._open_bracket)
              output.add(stack.pop());
            stack.add(tokens.get(index));
            break;
          default:
            break;
        }
      }
      consume();
    }
    if (!stack.isEmpty())
      output.add(stack.pop());
    index = 0;
    // System.out.println(output);
    tokens = output;
  }
}

// ...............................................................................................................
class assignStmt extends Stmt {
  Stmt expr;
  HashMap<String, Integer> symboltable;
  String var;

  assignStmt(ArrayList<token> tokens, HashMap<String, Integer> symboltable) {
    // System.out.println(tokens);
    this.tokens = tokens;
    this.symboltable = symboltable;
    this.var = "";
    build();
  }

  @Override
  String parse() {
    return expr.parse() + "pop rax\nmov [rbp" + (symboltable.get(var)) +
        "], rax\n";
  }

  void build() {
    if (expect(tokenType._type_int)) {
      consume();
      // hardExpect(tokenType._ident);
      addident(tokens.get(index), tokenType._int, symboltable);
      var = "1" + tokens.get(index).val();
      consume();
      hardExpect(tokenType._equal);
      expr = new exprStmt(nextOccurance(tokenType._semi_colon), symboltable);
    } else if (expect(tokenType._type_boolean)) {
      consume();
      addident(tokens.get(index), tokenType._boolean, symboltable);
      var = "2" + tokens.get(index).val();
      consume();
      hardExpect(tokenType._equal);
      expr = new booleanStmt(nextOccurance(tokenType._semi_colon), symboltable);
      // System.out.println(expr.parse());
    } else if (expect(
        tokenType._type_string)) { // symbol table to be implemented
      consume();
      var = "3" + tokens.get(index).val(); // to be filled out in the future
    }
    // consume();
    hardExpect(tokenType._semi_colon);
  }
}
//...............................................................................................................
class ifStmt extends Stmt{
  int rsp;
  ArrayList<ifStmt> ladder;
  exprStmt expr;
  Program pr;
  ifStmt(ArrayList<token> tokens,HashMap<String,Integer> symboltable, ArrayList<ifStmt> ladder){
    this.tokens = tokens;
    this.symboltable = symboltable;
    this.ladder = ladder;
    build();
  }
  @Override
  String parse(){
    return expr.parse()+"\n"+expr.parse();//do a for loop and shi
  }
  void build(){
    if(expect(tokenType._if)||expect(tokenType._else_if)||expect(tokenType._else))
    consume();
    exprStmt expr = new exprStmt(nextOccurance(tokenType._close_bracket), symboltable);
    hardExpect(tokenType._close_bracket);
    hardExpect(tokenType._open_curly);
    Program pr = new Program(nextOccurance(tokenType._close_curly), false);
    hardExpect(tokenType._close_curly);
    if(expect(tokenType._else)||expect(tokenType._else_if)){
      ladder.add(new ifStmt(new ArrayList<>(tokens.subList(index,tokens.size()-1)), symboltable, ladder));
    }
  }
}
// ..............................................................................................................
class Program {
  List<token> tokens;
  List<Stmt> statements;
  HashMap<String, Integer> symboltable = new HashMap<>();
  boolean isGlobal;
  int rsp;

  Program(List<token> tokens,boolean isGlobal) {
    this.tokens = tokens;
    this.statements = new ArrayList<>();
    this.isGlobal = isGlobal;
    build();
  }

  String parse(HashMap<String, Integer> symboltable) {
    String output = "";
    if(isGlobal){
      output = "global _start\n_start:\n";
    }
    output = "push rbp\nmov rbp, rsp\nsub rsp," + rsp + "\n";
    for (int i = 0; i < statements.size(); i++) {
      output += statements.get(i).parse();
    }
    return output;
  }

  void build() {
    int sdepth = 0;
    int cdepth = 0;
    int index = 0;
    rsp = 0;
    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i).type() == tokenType._open_bracket) {
        sdepth++;
      } else if (tokens.get(i).type() == tokenType._close_bracket) {
        sdepth--;
      } else if (tokens.get(i).type() == tokenType._open_curly) {
        cdepth++;
      } else if (tokens.get(i).type() == tokenType._close_curly) {
        cdepth--;
      }
      if (sdepth == 0 && cdepth == 0 &&
          tokens.get(i).type() == tokenType._semi_colon) {
        // statements.add(new stmt(tokens.subList(index, i)));
        // System.out.println(tokens.subList(index, i + 1));
        switch (tokens.get(index).type()) {
          /*
           * case tokenType._print:
           * statements.add(
           * new PrintStmt(new ArrayList<>(tokens.subList(index, i + 1))));
           * break;
           */
          case tokenType._exit:
            // System.out.println("over here" + symboltable);
            exitStmt exitstmt = new exitStmt(new ArrayList<>(tokens.subList(index, i + 1)),
                new HashMap<>(symboltable));
            statements.add(exitstmt);
            symboltable.putAll(exitstmt.symboltable);
            break;
          case tokenType._type_int:
          case tokenType._type_boolean:
            // System.out.println("HELLO THEE");
            assignStmt assignstmt = new assignStmt(new ArrayList<>(tokens.subList(index, i + 1)),
                new HashMap<>(symboltable));
            statements.add(assignstmt);
            rsp += 8;
            symboltable.putAll(assignstmt.symboltable);
            break;
          case tokenType._if:

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
    // System.out.println(symboltable);
  }
}
