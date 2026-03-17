#include <bits/stdc++.h>
#include <fstream>
#include <iostream>
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
struct Token {
  tokenType type;
  string text;
};
class Expr {
public:
  Expr *left;
  Expr *right;
  Token op;
  Expr(Token op) {
    this->op = op;
    left = nullptr;
    right = nullptr;
  }
  void parse() {
    if (left != nullptr) {
      left->parse();
    }
    cout << " " << op.text;
    if (right != nullptr) {
      right->parse();
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
    if (tokens[0].type != _ident && tokens[0].type != _number) {
      return false;
    }
    root = new Expr(tokens[0]);
    index++;
    while (index < tokens.size()) {
      if (index % 2 == 1 &&
          isOp(tokens[index])) { // for now we dont have brackets so this is the
                                 // case
        if (precedence(root->op) <= precedence(tokens[index])) {
          Expr *t = new Expr(
              tokens[index]); // this is cause +, stays above * and _ident
                              //  and * stays above ident , and the = there
                              //  ensures left better
          t->left = root;
          root = t;

        } else {
          cout << "came here\n"; // this is if a higher operator * comes after a
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
        } else {
          root->right = new Expr(tokens[index]);
        }
        // rightnode = rightnode->right;
      } else if (tokens[index].type == _open_braces) { // todo
        int l = ++index;
        while (index < tokens.size() && tokens[index].type != _close_braces) {
          index++;
        }
        if (index >= tokens.size()) {
          cout << "bracket error check again\n";
          exit(0);
        }
        // cout << "hlllo";
        // cout << tokens[index].text;
        Expr *right = root->right;
        if (right != nullptr) {
          while (right->right != nullptr) {
            right = right->right;
          }
          Expression *r = new Expression(
              std::vector<Token>(tokens.begin() + l, tokens.begin() + index));
          r->parseExpr();
          root->right = r->root;

        } else {
          Expression *r = new Expression(
              std::vector<Token>(tokens.begin() + l, tokens.begin() + index));
          r->parseExpr();
          root->right = r->root;
        }
      }
      index++;
    }
    // root->parse();
    return true;
  }
  bool isOp(Token token) {
    if (token.type == _add || token.type == _sub || token.type == _mul ||
        token.type == _div || token.type == _mod)
      return true;
    return false;
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
        // cout << "exit\n";
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
      // cout << buffer << "\n";
    } else if (code[i] == ';') {
      tokens.push_back({_semi_colon, ""});
      cout << ";\n";
    } else if (code[i] == '(') {
      tokens.push_back({_open_braces, "("});
      // cout << "(\n";
    } else if (code[i] == ')') {
      tokens.push_back({_close_braces, ")"});
      // cout << ")\n";
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
    }
    i++;
  }
}
int main() {
  lexer(readfile("test.dl"));
  Expression *hello = new Expression(tokens);
  hello->parseExpr();
  hello->root->parse();
}
