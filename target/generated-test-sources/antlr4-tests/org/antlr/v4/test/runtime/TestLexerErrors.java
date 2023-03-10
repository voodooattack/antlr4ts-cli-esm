package org.antlr.v4.test.runtime;

import org.antlr.v4.test.runtime.typescript.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestLexerErrors extends BaseTest {

	@Test
	public void testDFAToATNThatFailsBackToDFA() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(39);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'ab' ;\n");
		grammarBuilder.append("B : 'abc' ;");
		String grammar = grammarBuilder.toString();

		this.input ="ababx";
		this.expectedOutput = 
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:3='ab',<1>,1:2]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";
		this.expectedErrors = "line 1:4 token recognition error at: 'x'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testDFAToATNThatMatchesThenFailsInATN() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(52);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'ab' ;\n");
		grammarBuilder.append("B : 'abc' ;\n");
		grammarBuilder.append("C : 'abcd' ;");
		String grammar = grammarBuilder.toString();

		this.input ="ababcx";
		this.expectedOutput = 
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:4='abc',<2>,1:2]\n" +
			"[@2,6:5='<EOF>',<-1>,1:6]\n";
		this.expectedErrors = "line 1:5 token recognition error at: 'x'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testEnforcedGreedyNestedBrances_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(77);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ACTION : '{' (ACTION | ~[{}])* '}';\n");
		grammarBuilder.append("WS : [ \\r\\n\\t]+ -> skip;");
		String grammar = grammarBuilder.toString();

		this.input ="{ { } }";
		this.expectedOutput = 
			"[@0,0:6='{ { } }',<1>,1:0]\n" +
			"[@1,7:6='<EOF>',<-1>,1:7]\n";
		this.expectedErrors = "";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testEnforcedGreedyNestedBrances_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(77);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ACTION : '{' (ACTION | ~[{}])* '}';\n");
		grammarBuilder.append("WS : [ \\r\\n\\t]+ -> skip;");
		String grammar = grammarBuilder.toString();

		this.input ="{ { }";
		this.expectedOutput = "[@0,5:4='<EOF>',<-1>,1:5]\n";
		this.expectedErrors = "line 1:0 token recognition error at: '{ { }'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testErrorInMiddle() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(28);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'abc' ;");
		String grammar = grammarBuilder.toString();

		this.input ="abx";
		this.expectedOutput = "[@0,3:2='<EOF>',<-1>,1:3]\n";
		this.expectedErrors = "line 1:0 token recognition error at: 'abx'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testInvalidCharAtStart() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(30);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'a' 'b' ;");
		String grammar = grammarBuilder.toString();

		this.input ="x";
		this.expectedOutput = "[@0,1:0='<EOF>',<-1>,1:1]\n";
		this.expectedErrors = "line 1:0 token recognition error at: 'x'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testInvalidCharAtStartAfterDFACache() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(30);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'a' 'b' ;");
		String grammar = grammarBuilder.toString();

		this.input ="abx";
		this.expectedOutput = 
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		this.expectedErrors = "line 1:2 token recognition error at: 'x'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testInvalidCharInToken() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(30);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'a' 'b' ;");
		String grammar = grammarBuilder.toString();

		this.input ="ax";
		this.expectedOutput = "[@0,2:1='<EOF>',<-1>,1:2]\n";
		this.expectedErrors = "line 1:0 token recognition error at: 'ax'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testInvalidCharInTokenAfterDFACache() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(30);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("A : 'a' 'b' ;");
		String grammar = grammarBuilder.toString();

		this.input ="abax";
		this.expectedOutput = 
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,4:3='<EOF>',<-1>,1:4]\n";
		this.expectedErrors = "line 1:2 token recognition error at: 'ax'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testLexerExecDFA() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(98);
		grammarBuilder.append("grammar L;\n");
		grammarBuilder.append("start : ID ':' expr;\n");
		grammarBuilder.append("expr : primary expr? {} | expr '->' ID;\n");
		grammarBuilder.append("primary : ID;\n");
		grammarBuilder.append("ID : [a-z]+;");
		String grammar = grammarBuilder.toString();

		this.input ="x : x";
		this.expectedOutput = 
			"[@0,0:0='x',<3>,1:0]\n" +
			"[@1,2:2=':',<1>,1:2]\n" +
			"[@2,4:4='x',<3>,1:4]\n" +
			"[@3,5:4='<EOF>',<-1>,1:5]\n";
		this.expectedErrors = 
			"line 1:1 token recognition error at: ' '\n" +
			"line 1:3 token recognition error at: ' '\n";
		generateLexerTest("L.g4", grammar, "LLexer", false);

	}

	@Test
	public void testStringsEmbeddedInActions_1() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(110);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ACTION2 : '[' (STRING | ~'\"')*? ']';\n");
		grammarBuilder.append("STRING : '\"' ('\\\\\"' | .)*? '\"';\n");
		grammarBuilder.append("WS : [ \\t\\r\\n]+ -> skip;");
		String grammar = grammarBuilder.toString();

		this.input ="[\"foo\"]";
		this.expectedOutput = 
			"[@0,0:6='[\"foo\"]',<1>,1:0]\n" +
			"[@1,7:6='<EOF>',<-1>,1:7]\n";
		this.expectedErrors = "";
		generateLexerTest("L.g4", grammar, "L", false);

	}

	@Test
	public void testStringsEmbeddedInActions_2() throws Exception {
		mkdir(tmpdir);

		StringBuilder grammarBuilder = new StringBuilder(110);
		grammarBuilder.append("lexer grammar L;\n");
		grammarBuilder.append("ACTION2 : '[' (STRING | ~'\"')*? ']';\n");
		grammarBuilder.append("STRING : '\"' ('\\\\\"' | .)*? '\"';\n");
		grammarBuilder.append("WS : [ \\t\\r\\n]+ -> skip;");
		String grammar = grammarBuilder.toString();

		this.input ="[\"foo]";
		this.expectedOutput = "[@0,6:5='<EOF>',<-1>,1:6]\n";
		this.expectedErrors = "line 1:0 token recognition error at: '[\"foo]'\n";
		generateLexerTest("L.g4", grammar, "L", false);

	}


}
