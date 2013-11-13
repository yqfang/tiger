package lexer;

import java.io.BufferedInputStream;
import java.util.LinkedList;

import lexer.Token.Kind;
import util.Todo;
import util.SynbollMap;

public class Lexer {
	LinkedList<Integer> queuebuff = new LinkedList<Integer>();
	String fname; // the input file name to be compiled
	BufferedInputStream fstream; // input stream for the above file
	static Integer LineNum = 1;
	SynbollMap map = new SynbollMap();

	public Lexer(String fname, BufferedInputStream fstream) {
		this.fname = fname;
		this.fstream = fstream;
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		int c;
		// String lexeme = new String("ID");
		if (queuebuff.isEmpty())
			c = this.fstream.read();
		else
			c = queuebuff.removeFirst();
		// skip all kinds of "blanks"
		while (' ' == c || 9 == c || 13 == c || 10 == c) {
			if (c == 13) {
				LineNum++;
			}
			c = this.fstream.read();
		}
		if (-1 == c)
		// The value for "lineNum" is now "null",
		// you should modify this to an appropriate
		// line number for the "EOF" token.
		{
			System.out.println("\n:----------At Line " + LineNum + ": EOF!----------:\n");// 打印输出
			return new Token(Kind.TOKEN_EOF, LineNum);
		}
		// ///////////////////////////////////////////ID开头不能以_打头
		if ('_' == c) {
			System.out.println(LineNum + " Line '_' Errer,Throw Errer！");// 打印输出
			return null;
		}
		// ////////////////////////////////////////////DFA有限状态机
		if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
			queuebuff.addLast(c);
			c = this.fstream.read();
			while (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0'
					&& c <= '9' || c == '_') {
				queuebuff.addLast(c);
				c = this.fstream.read();
			}
			String lex = new String();
			while (!(queuebuff.isEmpty())) {
				int temp = queuebuff.removeFirst();
				lex = lex + (char) temp;
			}
			queuebuff.addLast(c);
			if (map.GetLexeme(lex) == null) {
				return new Token(Kind.TOKEN_ID, LineNum, lex);
			} else {
				return new Token(map.GetLexeme(lex), LineNum, lex);
			}
		} else if (c >= '0' && c <= '9') {
			queuebuff.addLast(c);
			c = this.fstream.read();
			while (c >= '0' && c <= '9') {
				queuebuff.addLast(c);
				c = this.fstream.read();
			}
			String lex = new String();
			while (!(queuebuff.isEmpty())) {
				int temp = queuebuff.removeFirst();
				lex = lex + (char) temp;
			}
			if (lex.length() > 1 && lex.charAt(lex.length() - 1) == 0) {
				new Todo();
				return null;
			} else {
				queuebuff.addLast(c);
				return new Token(Kind.TOKEN_NUM, LineNum, lex);
			}
		} else {
			// ///////////////////////////////////////////
			switch (c) {
			case '+':
				return new Token(Kind.TOKEN_ADD, LineNum, "+");
			case '*':
				return new Token(Kind.TOKEN_TIMES, LineNum, "*");
			case '=':
				return new Token(Kind.TOKEN_ASSIGN, LineNum, "=");
			case ',':
				return new Token(Kind.TOKEN_COMMER, LineNum, ",");
			case '.':
				return new Token(Kind.TOKEN_DOT, LineNum, ".");
			case '{':
				return new Token(Kind.TOKEN_LBRACE, LineNum, "{");
			case '[':
				return new Token(Kind.TOKEN_LBRACK, LineNum, "[");
			case '(':
				return new Token(Kind.TOKEN_LPAREN, LineNum, "(");
			case '<':
				return new Token(Kind.TOKEN_LT, LineNum, "<");
			case '!':
				return new Token(Kind.TOKEN_NOT, LineNum, "!");
			case '}':
				return new Token(Kind.TOKEN_RBRACE, LineNum, "}");
			case ']':
				return new Token(Kind.TOKEN_RBRACK, LineNum, "]");
			case ')':
				return new Token(Kind.TOKEN_RPAREN, LineNum, ")");
			case ';':
				return new Token(Kind.TOKEN_SEMI, LineNum, ";");
			case '-':
				return new Token(Kind.TOKEN_SUB, LineNum, "-");
			case '/': {
				c = this.fstream.read();
				if (c == '/') {
					c = this.fstream.read();
					System.out.println("At" + " Line " + LineNum
							+ ": '//' Annotation！");// 打印输出
					while (10 != c) {// 知道读到回车
						c = this.fstream.read();
					}
					LineNum++;
					return new Token(Kind.TOKEN_NOTE, LineNum - 1, "//");
				} else {
					queuebuff.addLast(c);
					new Todo();
					return null;
				}
			}
			case '&': {
				c = this.fstream.read();
				if (c == '&')
					return new Token(Kind.TOKEN_AND, LineNum, "&&");
				else {
					queuebuff.addLast(c);
					new Todo();
					return null;
				}
			}
			default:
				// Lab 1, exercise 2: supply missing code to
				// lex other kinds of tokens.
				// Hint: think carefully about the basic
				// data structure and algorithms. The code
				// is not that much and may be less than 50 lines. If you
				// find you are writing a lot of code, you
				// are on the wrong way.
				new Todo();
				return null;
			}
		}
	}

	public Token nextToken() {
		Token t = null;

		try {
			t = this.nextTokenInternal();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if (control.Control.lex)
			System.out.println(t.toString());// 打印输出
		return t;
	}
}
