import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

class compiler {
  String data = "";
  HashSet<String> _varInt = new HashSet<>();
  HashSet<String> _varString = new HashSet<>();
  int parseIndex = 0;
  int temp = 0;
  public static void main(String args[]) throws IOException {
    compiler inst = new compiler();
    inst.readFile("test.tl");
    inst.write(inst.parse(inst.tokenize()));
    //System.out.println(inst.isNumExp(inst.tokenize()));
  }

  public void terminate(int e, String error) {
    switch (e) {
      case 1:
        System.out.println("Unknown Token : " + error);
        break;
      case 2:
        System.out.println("Missing SemiColon");
        break;
      case 3:
        System.out.println("Invalid Numeric Notation");
        break;
      case 4:
        System.out.println("Invalid Usage of Brackets");
        break;
      case 5:
        System.out.println("Invalid arguments for Exit");
        break;
      case 6:
        System.out.println("Invalid integer declaration");
        break;
      case 7:
        System.out.println("Invalid print statement");
        break;
      case 8:
        System.out.println("Invalid String declaration");
        break;
      case 9:
        System.out.println("No variable named : " + error);
        break;
      case 10:
        System.out.println("Invalid assignment");
        break;
      default:
        System.out.println("unknown error");
        break;
    }
    System.exit(0);
  }

  // FILE READER
  public void readFile(String path) throws IOException {
    FileReader fr = new FileReader(path);
    char buffer[] = new char[1024];
    int n = fr.read(buffer);
    while (n != -1) {
      data += new String(buffer, 0, n);
      n = fr.read(buffer);
    }
    fr.close();
    System.out.print(data);
  }

  // STRUCT FOR TOKENS
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

  // TOKENIZER
  public ArrayList<token> tokenize() {
    ArrayList<token> tokens = new ArrayList<token>();
    for (int i = 0; i < data.length(); i++) {
      if (Character.isLetter(data.charAt(i))) {
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
            tokens.add(new token(tokenType._ident, ""));
        }
      } else if (Character.isDigit(data.charAt(i))) // tokenize number
      {
        String buffer = "";
        while (Character.isDigit(data.charAt(i))) {
          buffer += data.charAt(i);
          i++;
        }
        i--;
        tokens.add(new token(tokenType._int, buffer));
      } else if (data.charAt(i) == '"') // tokenize Strings
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
        terminate(1, "" + data.charAt(i));
      }
    }
    // System.out.println(tokens);
    return tokens;
  }

  // PARSER
  public String parse(ArrayList<token> tokens) {
    String output = "fn main(){\n";
    for (parseIndex = 0; parseIndex < tokens.size(); parseIndex++) {
      if (tokens.get(parseIndex).type == tokenType._exit) {
        output += parseExit(nextSemiColon(tokens, parseIndex + 1));
      } else if (tokens.get(parseIndex).type == tokenType._type_int) {
        output += parseIntDeclaration(nextSemiColon(tokens, parseIndex + 1));
      } else if (tokens.get(parseIndex).type == tokenType._print) {
        output += parsePrint(nextSemiColon(tokens, parseIndex + 1));
      } else if (tokens.get(parseIndex).type == tokenType._type_string) {
        output += parseStringDeclaration(nextSemiColon(tokens, parseIndex + 1));
      } else if (tokens.get(parseIndex).type == tokenType._ident) {
        output += parseModIdent(nextSemiColon(tokens, parseIndex + 1));
      }
    }
    output += "}";
    System.out.println(output);
    return output;
  }

  // NEXT BRACKET
  public ArrayList<token> nextBracket(ArrayList<token> tokens,
      int n) // pass i (i and the rbracket is removed)
  {
    temp=0;
    System.out.println("Next Brackets tokens"+tokens);
    ArrayList<token> out = new ArrayList<token>();
    int depth = 0; 
    for (int i = n; i < tokens.size(); i++) {
      if (tokens.get(i).type == tokenType._open_bracket) {
          depth++;
      } else if (depth!=0&&tokens.get(i).type == tokenType._close_bracket) {
        depth--;
      } else if (tokens.get(i).type == tokenType._close_bracket) {
        temp = i+1;
        return out;
      }
      out.add(tokens.get(i));
    }
    if (depth == 0) {
      if (out.get(0).type == tokenType._open_bracket &&
          out.get(out.size() - 1).type == tokenType._close_bracket) {
        temp = tokens.size();
        return new ArrayList<token>(out.subList(1, out.size() - 1));
      }
      return out;
    }
    terminate(4, "");
    return null;
  }

  // NEXT SEMI COLON
  public ArrayList<token> nextSemiColon(ArrayList<token> tokens,
      int n) // pass i+1 (semicolon is removed)
  {
    //System.out.println(tokens);
    ArrayList<token> out = new ArrayList<token>();
    for (int i = n; i < tokens.size(); i++) {
      if (tokens.get(i).type == tokenType._semi_colon) {
        System.out.println(out);
        parseIndex = i + 1;
        return out;
      }
      out.add(tokens.get(i));
    }
    terminate(2, "");
    return null;
  }

  // PARSE NUMEXP
  public Boolean isNumExp(ArrayList<token> tokens) {
    System.out.println("is numexp?: "+tokens);
    if(tokens.size()==0){
      return false;
    }
    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i).type == tokenType._open_bracket) {
        System.out.println("hello there , this is a braket within a braket" +
                           nextBracket(tokens, i));
        if (isNumExp(nextBracket(tokens, i)) == false) {
          terminate(3, "");
        } else {
          while (i < tokens.size() &&
                 tokens.get(i).type != tokenType._close_bracket) {
            i++;
          }
          i++;
        }
      }
      else if (tokens.get(i).type == tokenType._mod ||
                 tokens.get(i).type == tokenType._add ||
                 tokens.get(i).type == tokenType._div ||
                 tokens.get(i).type == tokenType._sub ||
                 tokens.get(i).type == tokenType._mul) {
          if(i-1<0||i+1>=tokens.size()){
            terminate(3,"");
          }
          if(!((tokens.get(i-1).type==tokenType._close_bracket||tokens.get(i-1).type==tokenType._int)
              &&(tokens.get(i+1).type==tokenType._open_bracket||tokens.get(i+1).type==tokenType._int)))
          {
            System.out.println("hellll");
            terminate(3,"");
          }
        }
       else if (tokens.get(i).type != tokenType._int) {
         System.out.println("hello");
        terminate(3, "");
        return false;
      }
    }
    return true;
  }

  public String parseNumExp() {
    return "";
  }

  // PARSE EXIT
  public String parseExit(ArrayList<token> tokens) { // (0)
    System.out.println("peeled one: "+nextBracket(tokens, 0));
    String output = "";
    /*
     * if (isNumExp(nextBracket(tokens, 0))) {
     * System.out.println(tokens);
     * output += "process::exit(" + tokens.get(1).val + ")\n";
     * return output;
     * } else {
     * terminate(5, "");
     * }
     */
    tokenType grammar[] = { tokenType._open_bracket, tokenType._NumExp,
        tokenType._close_bracket };
    System.out.println("TOKENS HERE"+tokens);
    if(isSame(constructGrammar(grammar),tokens)){
      return "process::exit("+tokens.get(1).val+");\n";
    }
    return "Error";
  }

  public String parseIntDeclaration(ArrayList<token> tokens) {
    System.out.println(tokens);
    tokenType grammar[] = { tokenType._ident, tokenType._equal, tokenType._int };
    if (isSame(constructGrammar(grammar), tokens)) {
      System.out.println("i am hereee");
      return "let mut " + tokens.get(0).val + " = " + tokens.get(2).val + ";\n";
    }
    terminate(6, "");
    return "Error";
  }

  public String parseStringDeclaration(ArrayList<token> tokens) {
    System.out.println(tokens);
    String output = "let mut ";
    if (tokens.size() != 3) {
      terminate(8, "");
    }
    if (tokens.get(0).type == tokenType._ident &&
        tokens.get(1).type == tokenType._equal &&
        tokens.get(2).type == tokenType._string) {
      output += tokens.get(0).val + "= \"" + tokens.get(2).val + "\";\n";
      _varString.add(tokens.get(0).val);
      return output;
    }
    terminate(8, "");
    return "Error";
  }

  public String parsePrint(ArrayList<token> tokens) {
    System.out.println(tokens);
    if (tokens.size() != 3) {
      terminate(7, "");
    }
    if (tokens.get(0).type == tokenType._open_bracket &&
        tokens.get(1).type == tokenType._string &&
        tokens.get(2).type == tokenType._close_bracket) {
      return "println!(\"" + tokens.get(1).val + "\");\n";
    }
    terminate(7, "");
    return "Error";
  }

  public String parseModIdent(ArrayList<token> tokens) {
    // a = b; a is int
    /*
     * if (tokens.size() == 4) {//should be implemented in int declaration and
     * string declaration if (isVar(tokens.get(1).val) == 1 &&
     * tokens.get(0).type
     * == tokenType._type_int) { // int a = b;
     * }
     *
     * // a = b; a is string
     * if (isVar(tokens.get(1).val) == 1 &&
     * tokens.get(0).type == tokenType._type_string) { // String b= a;
     * }
     * }
     */

    if (tokens.size() == 3) { // a=b;
      if (isVar(tokens.get(0).val) == 1 && isVar(tokens.get(2).val) == 1 &&
          tokens.get(1).type == tokenType._equal) {
        return tokens.get(0).val + " = " + tokens.get(2).val;
      }
      if (isVar(tokens.get(0).val) == 2 && isVar(tokens.get(2).val) == 2 &&
          tokens.get(1).type == tokenType._equal) {
        return tokens.get(0).val + " = " + tokens.get(2).val;
      }
    }
    terminate(10, "");
    return "Error";
  }

  public int isVar(String s) {
    if (_varInt.contains(s)) {
      return 1; // int also include scope
    } else if (_varString.contains(s)) {
      return 2; // string
    } else {
      terminate(9, s);
      return 0;
    }
  }

  public boolean isSame(ArrayList<token> grammar, ArrayList<token> tokens) {
    /*if (tokens.size() != grammar.size()) {
      //System.out.println("i am here");
      return false;
    } */
    //if {
    System.out.println("WHAR THE FUCK IS HAPPENING");
    System.out.println(grammar);
    System.out.println(tokens);
    temp = 0;
      for (int i = 0; i < tokens.size(); i++) {
        if (grammar.get(i).type == tokenType._NumExp) {
          System.out.println("i am here though");
          if (!isNumExp(nextBracket(tokens, i))) {
            System.out.println("fucker i am here");
            terminate(3, "");
          }
        }
        if (tokens.get(i+temp).type != grammar.get(i).type) {
          System.out.println(tokens.get(i));
          System.out.println("returned false");
          return false;
        }
      }
      return true;
    //}
  }

  public ArrayList<token> constructGrammar(tokenType type[]) {
    ArrayList<token> grammar = new ArrayList<>();
    for (int i = 0; i < type.length; i++) {
      grammar.add(new token(type[i], ""));
    }
    return grammar;
  }

  // FILE WRITER
  void write(String content) throws IOException {
    FileWriter fw = new FileWriter("output.asm");
    fw.write(content);
    fw.close();
  }
}
