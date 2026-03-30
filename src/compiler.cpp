#include <algorithm>
#include <bits/stdc++.h>
#include <fstream>
#include <iostream>
#include <istream>
#include <vector>
using namespace std;
string readfile(string f) {
  string out = "";
  ifstream file(f);
  string buf;
  while (getline(file, buf)) {
    out += buf;
  }
  file.close();
  return out;
}
enum tokenType {
  _type_int,
  _number,
  _sysexit,
  _ident,
  _semi_colon,
  _print,
  _equal,
  _add,
  _sub,
  _mul,
  _div,
  _mod,
  _open_braces,
  _close_braces,
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
  _else_if,
  _while,
};
struct Symbol {
  string text;
  tokenType type;
};
struct Token {
  tokenType type;
  string text;
};
class Expr {
public:
  Expr *left;
  Expr *right;
  bool isBraces;
  Token op;
  Expr(Token op) {
    this->op = op;
    left = nullptr;
    right = nullptr;
    isBraces = false;
  }
  int result() {
    if (op.type == _number) {
      return std::stoi(op.text);
    } else {
      if (op.type == _add) {
        return left->result() + right->result();
      } else if (op.type == _sub) {
        return left->result() - right->result();
      } else if (op.type == _mul) {
        return left->result() * right->result();
      } else if (op.type == _div) {
        return left->result() / right->result();
      } else if (op.type == _mod) {
        return left->result() % right->result();
      }
    }
    return 0;
  }
  void print() {
    cout << op.text << "\n";
    if (left != nullptr) {
      cout << " ";
      left->print();
      cout << "\n";
    }
    if (right != nullptr) {
      cout << " ";
      right->print();
      cout << "\n";
    }
  }
};
class Expression { // our goal is to build a tree and put it in root
public:
  std::vector<Token> tokens;
  int index;
  Expr *root;
  Expr *rightnode;
  Expression(std::vector<Token> tokens) {
    this->index = 0;
    this->tokens = tokens;
  }
  bool parseExpr() {
    if (tokens[0].type != _ident && tokens[0].type != _number &&
        tokens[0].type != _open_braces) {
      return false;
    }
    if (tokens[0].type != _open_braces) {
      root = new Expr(tokens[0]);
    } else {
      findnext(_close_braces);
      Expression *r = new Expression(
          std::vector<Token>(tokens.begin() + 1, tokens.begin() + index));
      // r->root->isBraces = true;
      r->parseExpr();
      root = r->root;
      root->isBraces = true;
      // root->print();
    }
    index++;
    while (index < tokens.size()) {
      if (index % 2 == 1 &&
          isOp(tokens[index])) { // for now we dont have brackets so this is the
                                 // case
        if (precedence(root) <= precedence(tokens[index])) {
          Expr *t = new Expr(
              tokens[index]); // this is cause +, stays above * and _ident
                              //  and * stays above ident , and the = there
                              //  ensures left better
          t->left = root;
          root = t;

        } else {
          // this is if a higher operator * comes after a
          // lower one + so we rewrite the tree
          Expr *t = new Expr(tokens[index]);
          t->left = root->right;
          root->right = t;
        }

      } else if (tokens[index].type == _ident ||
                 tokens[index].type == _number) {
        Expr *right = root->right; // we always find the rightmost element and
                                   // append the var or number
        if (right != nullptr) {
          while (right->right != nullptr) {
            right = right->right;
          }
          right->right = new Expr(tokens[index]);
        } else
          root->right = new Expr(tokens[index]);
      } else if (tokens[index].type == _open_braces) { // todo
        int l = index + 1;
        findnext(_close_braces);
        Expr *right = root->right;
        if (right != nullptr) {
          while (right->right != nullptr) {
            right = right->right;
          }
          Expression *r = new Expression(
              std::vector<Token>(tokens.begin() + l, tokens.begin() + index));
          r->parseExpr();
          r->root->isBraces =
              true; // so subexpressions have the same priority
                    // as an ident or a number , they must be lower in the tree

          right->right = r->root;
        } else {
          Expression *r = new Expression(
              std::vector<Token>(tokens.begin() + l, tokens.begin() + index));
          r->parseExpr();
          r->root->isBraces = true;
          root->right = r->root;
        }
      }
      index++;
    }
    return true;
  }
  void print() {
    for (int i = 0; i < tokens.size(); i++) {
      cout << tokens[i].text;
    }
  }
  bool isOp(Token token) {
    if (token.type == _add || token.type == _sub || token.type == _mul ||
        token.type == _div || token.type == _mod)
      return true;
    return false;
  }
  int findnext(tokenType t) {
    int depth = 0;
    while (index < tokens.size()) {
      if (tokens[index].type == _open_braces)
        depth++;
      if (tokens[index].type == _close_braces)
        depth--;
      if (depth == 0 && tokens[index].type == t) {
        break;
      }
      index++;
    }
    if (index >= tokens.size()) {
      cout << "token not found " << t;
      exit(0);
    } else {
      // cout << tokens[index].text;
      return index;
    }
  }
  int precedence(Token token) {
    if (token.type == _ident || token.type == _number) {
      return 0;
    }
    if (token.type == _add || token.type == _sub) {
      return 2;
    } else {
      return 1;
    }
  }
  int precedence(Expr *expr) {
    if (expr->isBraces || expr->op.type == _ident || expr->op.type == _number) {
      return 0;
    }
    if (expr->op.type == _add || expr->op.type == _sub) {
      return 2;
    } else {
      return 1;
    }
  }
};
std::vector<Token> tokens;
void lexer(string code) { // takes string and returns an array of tokens
  int i = 0;
  while (i < code.size()) {
    if (isalpha(code[i])) {
      string buffer = "";
      buffer += code[i++];
      while (i < code.size() &&
             (isdigit(code[i]) || isalpha(code[i]))) { // var_num --> accepted
        buffer += code[i++];
      }
      if (i < code.size()) { // we read an extra character so headback
        i--;
      }
      if (buffer == "exit") {
        tokens.push_back({_sysexit, ""});

      } else if (buffer == "int") {
        tokens.push_back({_type_int, ""});
      } else {
        tokens.push_back({_ident, buffer});
      }
    } else if (isdigit(code[i])) { // integers
      string buffer = "";
      buffer += code[i++];
      while (i < code.size() && isdigit(code[i])) {
        buffer += code[i++];
      }
      if (i < code.size()) { // we read an extra character so headback
        i--;
      }
      tokens.push_back({_number, buffer});
    } else if (code[i] == ';') {
      tokens.push_back({_semi_colon, ""});
    } else if (code[i] == '(') {
      tokens.push_back({_open_braces, "("});
    } else if (code[i] == ')') {
      tokens.push_back({_close_braces, ")"});
    } else if (code[i] == '+') {
      tokens.push_back({_add, "+"});
    } else if (code[i] == '-') {
      tokens.push_back({_sub, "-"});
    } else if (code[i] == '*') {
      tokens.push_back({_mul, "*"});
    } else if (code[i] == '/') {
      tokens.push_back({_div, "/"});
    } else if (code[i] == '%') {
      tokens.push_back({_mod, "%"});
    } else if (code[i] == '=') {
      tokens.push_back({_equal, "="});
    }
    i++;
  }
}
class Statement {
protected:
  std::vector<Token> tokens;
  int index;
  std::unordered_map<string, Symbol> &symboltable;

public:
  Statement(std::vector<Token> t, unordered_map<string, Symbol> &s)
      : symboltable(s) {
    tokens = t;
    index = 0;
  }
  bool expect(tokenType t) {
    if (index < tokens.size() && tokens[index].type == t) {
      index++;
      return true;
    }
    return false;
  }
  void hardexpect(tokenType t) {
    if (index < tokens.size()) {
      if (tokens[index].type == t) {
        index++;
        return;
      }
    }
    cout << t << " expeceted\n";
    exit(0);
  }
  int findnext(tokenType t) {
    int depth = 0;
    while (index < tokens.size()) {
      if (tokens[index].type == _open_braces)
        depth++;
      if (tokens[index].type == _close_braces && depth > 0)
        depth--;
      if (depth == 0 && tokens[index].type == t) {
        break;
      } else if (tokens[index].type == t) {
        cout << "found but depth not 0\n";
      }
      index++;
    }
    if (index >= tokens.size()) {
      return -999;
    } else {
      return index;
    }
  }
  virtual void build() = 0;
};
class Declaration : public Statement {
public:
  Declaration(std::vector<Token> t, unordered_map<string, Symbol> &symboltable)
      : Statement(t, symboltable) {
    build();
  }
  void build() {
    if (expect(_type_int)) {
      int l = index;
      hardexpect(_ident);
      hardexpect(_equal);
      if (symboltable.contains(tokens[l].text)) {
        cout << "variable exists\n";
        exit(0);
      } else {
        cout << "inserted";
        symboltable[tokens[l].text] = Symbol{tokens[l].text, _type_int};
      }

    } else if (expect(_type_boolean)) {
      cout << "to do\n";
    }
  }
};
class Program {
public:
  std::vector<Statement *> Statements;
  std::vector<Token> tokens;
  int index;
  unordered_map<string, Symbol> symboltable;
  Program(std::vector<Token> t,
          std::unordered_map<string, Symbol> symboltable) {
    this->symboltable = symboltable;
    this->tokens = t;
    index = 0;
    build();
  }
  int findnext(tokenType t) {
    int depth = 0;
    while (index < tokens.size()) {
      if (tokens[index].type == _open_braces)
        depth++;
      if (tokens[index].type == _close_braces && depth > 0)
        depth--;
      if (depth == 0 && tokens[index].type == t) {
        break;
      } else if (tokens[index].type == t) {
        cout << "found but depth not 0\n";
      }
      index++;
    }
    if (index >= tokens.size()) {
      return -999;
    } else {
      return index;
    }
  }
  void build() {
    int left = index;
    while (findnext(_semi_colon) != -999) {
      std::vector<Token> slice =
          std::vector<Token>(tokens.begin() + left, tokens.begin() + index);
      if (tokens[left].type == _type_int ||
          tokens[left].type ==
              _boolean) { // non scoping statements first as symboltables change
        Statements.push_back(new Declaration(slice, symboltable));
        cout << "hello \n";
      }
      index++;
      left = index;
    }
  }
};
int main() {
  lexer(readfile("test.dl"));
  unordered_map<string, Symbol> symboltable;
  Program *p = new Program(tokens, symboltable);
}
