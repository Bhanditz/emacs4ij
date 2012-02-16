package org.jetbrains.emacs4ij.jelisp;

import junit.framework.Assert;
import org.jetbrains.emacs4ij.jelisp.elisp.*;
import org.jetbrains.emacs4ij.jelisp.exception.LispException;
import org.jetbrains.emacs4ij.jelisp.exception.MissingClosingBracketException;
import org.jetbrains.emacs4ij.jelisp.exception.MissingClosingDoubleQuoteException;
import org.jetbrains.emacs4ij.jelisp.exception.UnknownCodeBlockException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Ekaterina.Polishchuk
 * Date: 7/12/11
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForwardParserTest {
    private ForwardParser p;

    @Before
    public void setUp() throws Exception {
        p = new ForwardParser();
    }

    @Test
    public void testEmptyList () throws LispException {
        LObject lispObject = p.parseLine("()");
        Assert.assertEquals(LispList.list(), lispObject);
    }
    @Test
    public void testEmptyString() throws LispException {
        LObject lispObject = p.parseLine("\"\"");
        Assert.assertEquals(new LispString(""), lispObject);
    }
    @Test
    public void testString() throws LispException {
        LObject lispObject = p.parseLine("\"hello\"");
        Assert.assertEquals(new LispString("hello"), lispObject);
    }

    @Test
    public void testNumber() throws LispException {
        LObject lispObject = p.parseLine("5");
        Assert.assertEquals(new LispInteger(5), lispObject);
    }
    @Test
    public void testList () throws LispException {
        LObject lispObject = p.parseLine("(5)");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispInteger(5))), lispObject);
    }
    @Test
    public void testComplexList () throws LispException {
        LObject lispObject = p.parseLine("(5 \"lala\")");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispInteger(5), new LispString("lala"))), lispObject);
    }

    @Test
    public void testStringWithSpaceList () throws LispException {
        LObject lispObject = p.parseLine("(5 \"la la\")");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispInteger(5), new LispString("la la"))), lispObject);
    }

    @Test(expected = MissingClosingBracketException.class)
    public void testMissingClosingBracket () throws LispException {
        p.parseLine("(5 \"la la\"");
    }

    @Test(expected = MissingClosingDoubleQuoteException.class)
    public void testMissingClosingDblQuote () throws LispException {
        p.parseLine("(5 \"la la");
    }

    @Test (expected = UnknownCodeBlockException.class)
    public void testQuotedList () throws LispException {
        LObject lispObject = p.parseLine("'    (5 \"la la\")");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispSymbol("quote"),  LispList.list(Arrays.<LObject>asList(new LispInteger(5), new LispString("la la"))))), lispObject);
    }

    @Test
    public void testStringWithDblQuote () throws LispException {
        LObject lispObject = p.parseLine("(5 \"la \\\" la\")");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispInteger(5), new LispString("la \" la"))), lispObject);
    }

    @Test
    public void testInnerList () throws LispException {
        LObject lispObject = p.parseLine("(5 (10))");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispInteger(5), LispList.list(Arrays.<LObject>asList(new LispInteger(10))))), lispObject);
    }

    @Test
    public void testListWithMultipleSpaces () throws LispException {
        LObject lispObject = p.parseLine("(   5    \"la   la\"   )");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispInteger(5), new LispString("la   la"))), lispObject);
    }

    @Test
    public void testFloatNegativeInfinity() throws LispException {
        LObject lispObject = p.parseLine("-1.0e+INF");
        Assert.assertEquals(LispFloat.ourNegativeInfinity, lispObject);
    }

    @Test
    public void testFloatPositiveInfinity() throws LispException {
        LObject lispObject = p.parseLine("1.0e+INF");
        Assert.assertEquals(LispFloat.ourPositiveInfinity, lispObject);
    }

    @Test
    public void testFloatNanNoSign() throws LispException {
        LObject lispObject = p.parseLine("0.0e+NaN");
        Assert.assertEquals(LispFloat.ourNaN, lispObject);
    }

    @Test
    public void testFloatNanWithSign() throws LispException {
        LObject lispObject = p.parseLine("-0.0e+NaN");
        Assert.assertEquals(LispFloat.ourNaN, lispObject);
    }

    /*@Test
    public void testFloatNanEquality() throws LispException {
        LObject lispObject = p.parseLine("-0.0e+NaN");
        Assert.assertEquals(LispFloat.ourNaN, lispObject);
    }*/

    @Test
    public void testFloat1() throws LispException {
        LObject lispObject = p.parseLine("1500.0");
        Assert.assertEquals(new LispFloat(1500), lispObject);
    }

    @Test
    public void testFloat2() throws LispException {
        LObject lispObject = p.parseLine("15e2");
        Assert.assertEquals(new LispFloat(1500), lispObject);
    }

    @Test
    public void testFloat3() throws LispException {
        LObject lispObject = p.parseLine("15.0e2");
        Assert.assertEquals(new LispFloat(1500), lispObject);
    }

    @Test
    public void testFloat4() throws LispException {
        LObject lispObject = p.parseLine("1.5e3");
        Assert.assertEquals(new LispFloat(1500), lispObject);
    }

    @Test
    public void testFloat5() throws LispException {
        LObject lispObject = p.parseLine(".15e4");
        Assert.assertEquals(new LispFloat(1500), lispObject);
    }

    @Test
    public void testEmptyLineWithComments() throws LispException {
        LObject lispObject = p.parseLine("; a comment");
        Assert.assertNull(lispObject);
    }

    @Test
    public void testIntegerWithComments() throws LispException {
        LObject lispObject = p.parseLine("5; a comment");
        Assert.assertEquals(new LispInteger(5), lispObject);
    }

    @Test
    public void testStringWithComments() throws LispException {
        LObject lispObject = p.parseLine("\"test;\"; a comment");
        Assert.assertEquals(new LispString("test;"), lispObject);
    }

    @Test
    public void testEmptyListWithComments() throws LispException {
        LObject lispObject = p.parseLine("(); a comment");
        Assert.assertEquals(LispList.list(), lispObject);
    }

    @Test (expected = MissingClosingBracketException.class)
    public void testUnclosedListWithComments() throws LispException {
        p.parseLine("(5 10 ;); a comment");
    }

    @Test
    public void testEmptyQuote() throws LispException {
        LObject lispObject = p.parseLine("'");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispSymbol("quote"),  LispSymbol.ourNil)), lispObject);
    }

    //todo
    /*@Test what expected
    public void testQuotedSpace() throws LispException {
        LObject lispObject = p.parseLine("' ");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispSymbol("quote"),  LispSymbol.ourNil)), lispObject);
    }*/

    @Test
    public void testQuotedInt() throws LispException {
        LObject lispObject = p.parseLine("'5");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispSymbol("quote"),  new LispInteger(5))), lispObject);
    }

    @Test
    public void testQuotedQuotedList() throws LispException {
        LObject lispObject = p.parseLine("'(quote 5)");
        Assert.assertEquals(LispList.list(new LispSymbol("quote"), LispList.list(new LispSymbol("quote"), new LispInteger(5))), lispObject);
    }

    @Test
    public void testListOfQuotedItems() {
        LObject lispObject = p.parseLine("('test)");
        Assert.assertEquals(LispList.list(LispList.list(new LispSymbol("quote"), new LispSymbol("test"))), lispObject);
    }

    @Test
    public void testParsePlus () throws LispException {
        LObject lispObject = p.parseLine("(+ 2 2)");
        Assert.assertEquals(LispList.list(Arrays.<LObject>asList(new LispSymbol("+"), new LispInteger(2), new LispInteger(2))), lispObject);
    }

    @Test
    public void testParseMultiLineListWithComments () {
        LObject lispObject = p.parseLine("(rose\nbutterfly;;something like comment\nelefant;;kit\n)");
        Assert.assertEquals(LispList.list(new LispSymbol("rose"), new LispSymbol("butterfly"), new LispSymbol("elefant")), lispObject);
    }

    @Test
    public void testParseMultiLineList () {
        LObject lispObject = p.parseLine("(rose\nbutterfly\nelefant)");
        Assert.assertEquals(LispList.list(new LispSymbol("rose"), new LispSymbol("butterfly"), new LispSymbol("elefant")), lispObject);
    }

    @Test
    public void testParseMultiLineString () {
        LObject lispObject = p.parseLine("\"multiline\nstring\"");
        Assert.assertEquals(new LispString("multiline\nstring"), lispObject);
    }

    @Test
    public void testParseSymbol() throws LispException {
        LObject lispObject = p.parseLine("test");
        Assert.assertEquals(new LispSymbol("test"), lispObject);
    }

    @Test
    public void testParseVector() {
        LObject lispObject = p.parseLine("[1 \"hello\" anna ['q]]");
        Assert.assertEquals(new LispVector(new LispInteger(1), new LispString("hello"), new LispSymbol("anna"), new LispVector(LispList.list(new LispSymbol("quote"), new LispSymbol("q")))), lispObject);
    }

    @Test
    public void testParseCharacter() {
        LObject c = p.parseLine("?a");
        Assert.assertEquals(new LispInteger(65), c);
        c = p.parseLine("?A");
        Assert.assertEquals(new LispInteger(65), c);
    }

    @Test
    public void testParseZ() {
        LObject c = p.parseLine("?\\C-\\z");
        Assert.assertEquals(new LispInteger(26), c);
        c = p.parseLine("?\\C-z");
        Assert.assertEquals(new LispInteger(26), c);
    }
    
    @Test
    public void testParseSpecialCharA() {
        // ?\a ⇒ 7                 ; control-g, C-g
        LObject c = p.parseLine("?\\a");
        Assert.assertEquals(new LispInteger(7), c);
        c = p.parseLine("?\\C-g");
        Assert.assertEquals(new LispInteger(7), c);
        c = p.parseLine("?\\^g");
        Assert.assertEquals(new LispInteger(7), c);
        c = p.parseLine("?\\A");
        Assert.assertEquals(new LispInteger(7), c);
        c = p.parseLine("?\\C-G");
        Assert.assertEquals(new LispInteger(7), c);
    }
    
    @Test
    public  void testParseA() {
        LObject c = p.parseLine("?\\C-\\a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108871), c);

        c = p.parseLine("?\\M-a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(134217825), c);
        c = p.parseLine("?\\M-\\M-a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(134217825), c);
        c = p.parseLine("?\\M-\\C-a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(134217729), c);
        c = p.parseLine("?\\C-\\M-a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(134217729), c);
        c = p.parseLine("?\\C-\\C-a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108865), c);

        c = p.parseLine("?\\C-a");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(1), c);
        c = p.parseLine("?\\C-A");
        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(1), c);
    }

    @Test
    public void testSpace() {
        LObject c = p.parseLine("?\\ ");
        Assert.assertEquals(new LispInteger(32), c);
    }

    @Test
    public void testParseDoubleCtrl() {
        LObject c = p.parseLine("?\\C-\\C-g");
//        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108871), c);
        c = p.parseLine("?\\^\\^g");
//        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108871), c);
        c = p.parseLine("?\\C-\\7");
//        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108871), c);
        c = p.parseLine("?\\C-\\a");
//        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108871), c);
        c = p.parseLine("?\\C-7");
//        System.out.println(((LispInteger)c).getData());
        Assert.assertEquals(new LispInteger(67108919), c);
    }

    @Test
    public void testParseNumbers() {
        for (int i = 0; i < 8; ++i) {
            LObject c = p.parseLine("?\\" + Integer.toString(i));
            Assert.assertEquals(new LispInteger(i), c);
        }
        LObject c = p.parseLine("?\\8");
        Assert.assertEquals(new LispInteger(56), c);
        c = p.parseLine("?\\9");
        Assert.assertEquals(new LispInteger(57), c);
        c = p.parseLine("?\\10");
        Assert.assertEquals(new LispInteger(8), c);
        c = p.parseLine("?\\11");
        Assert.assertEquals(new LispInteger(9), c);
        c = p.parseLine("?\\012");
        Assert.assertEquals(new LispInteger(10), c);
        c = p.parseLine("?\\101");
        Assert.assertEquals(new LispInteger(65), c);
    }

    @Test
    public void testParseHex() {
        LObject c = p.parseLine("?\\x41");
        Assert.assertEquals(new LispInteger(65), c);
        c = p.parseLine("?\\x1");
        Assert.assertEquals(new LispInteger(1), c);
        c = p.parseLine("?\\x8e0");
        Assert.assertEquals(new LispInteger(2272), c);
    }

    @Test
    public void testParseSpecialCharB() {
        // ?\b ⇒ 8                 ; backspace, <BS>, C-h
        LObject c = p.parseLine("?\\b");
        Assert.assertTrue(8 == ((LispInteger)c).getData());

        c = p.parseLine("?\\C-h");
        Assert.assertTrue(8 == ((LispInteger)c).getData());

        c = p.parseLine("?\\^h");
        Assert.assertTrue(8 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharT() {
        //?\t ⇒ 9                 ; tab, <TAB>, C-i
        LObject c = p.parseLine("?\\t");
        Assert.assertTrue(9 == ((LispInteger)c).getData());

        c = p.parseLine("?\\C-i");
        Assert.assertTrue(9 == ((LispInteger)c).getData());

        c = p.parseLine("?\\^i");
        Assert.assertTrue(9 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharN() {
        //?\n ⇒ 10                ; newline, C-j
        LObject c = p.parseLine("?\\n");
        Assert.assertTrue(10 == ((LispInteger)c).getData());
        c = p.parseLine("?\\C-j");
        Assert.assertTrue(10 == ((LispInteger)c).getData());
        c = p.parseLine("?\\^j");
        Assert.assertTrue(10 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharV() {
        //?\v ⇒ 11                ; vertical tab, C-k
        LObject c = p.parseLine("?\\v");
        Assert.assertTrue(11 == ((LispInteger)c).getData());
        c = p.parseLine("?\\C-k");
        Assert.assertTrue(11 == ((LispInteger)c).getData());
        c = p.parseLine("?\\^k");
        Assert.assertTrue(11 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharF() {
        //?\f ⇒ 12                ; formfeed character, C-l
        LObject c = p.parseLine("?\\f");
        Assert.assertTrue(12 == ((LispInteger)c).getData());
        c = p.parseLine("?\\C-l");
        Assert.assertTrue(12 == ((LispInteger)c).getData());
        c = p.parseLine("?\\^l");
        Assert.assertTrue(12 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharR() {
        //?\r ⇒ 13                ; carriage return, <RET>, C-m
        LObject c = p.parseLine("?\\r");
        Assert.assertTrue(13 == ((LispInteger)c).getData());
        c = p.parseLine("?\\C-m");
        Assert.assertTrue(13 == ((LispInteger)c).getData());
        c = p.parseLine("?\\^m");
        Assert.assertTrue(13 == ((LispInteger)c).getData());
    }

    @Ignore
    @Test
    public void testParseSpecialCharE() {
        //?\e ⇒ 27                ; escape character, <ESC>, C-[
        LObject c = p.parseLine("?\\e");
        Assert.assertTrue(c instanceof LispInteger);
        Assert.assertTrue(27 == ((LispInteger)c).getData());

        c = p.parseLine("?\\C-[");
        //todo: must throw (scan-error "Containing expression ends prematurely" 2150 2150)
        c = p.parseLine("?\\^[");
        //todo: must throw (scan-error "Containing expression ends prematurely" 2149 2149)
    }

    @Test
    public void testParseSpecialCharS() {
        //?\s ⇒ 32                ; space character, <SPC>
        LObject c = p.parseLine("?\\s");
        Assert.assertTrue(c instanceof LispInteger);
        Assert.assertTrue(32 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharSpace() {
        //?\s ⇒ 32                ; space character, <SPC>
        LObject c = p.parseLine("? ");
        Assert.assertTrue(c instanceof LispInteger);
        Assert.assertTrue(32 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharBackslash() {
        //?\\ ⇒ 92                ; backslash character, \
        LObject c = p.parseLine("?\\\\");
        Assert.assertTrue(c instanceof LispInteger);
        Assert.assertTrue(92 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseSpecialCharDel() {
        //?\d ⇒ 127               ; delete character, <DEL>
        LObject c = p.parseLine("?\\d");
        Assert.assertTrue(127 == ((LispInteger)c).getData());
    }

    @Test
    public void testParseBackQuoteSimple() {
        LObject c = p.parseLine("`()");
        Assert.assertEquals("(\\` nil)", c.toString());
    }

    @Test
    public void testParseBackQuoteComma() {
        LObject c = p.parseLine("`(1 ,2)");
        Assert.assertEquals("(\\` (1 (\\, 2)))", c.toString());
    }

    @Test
    public void testParseBackQuoteDog() {
        LObject c = p.parseLine("`(1 ,2 ,@3)");
        Assert.assertEquals("(\\` (1 (\\, 2) (\\,@ 3)))", c.toString());
    }

    @Test
    public void testParseBackQuoteNested() {
        LObject c = p.parseLine("`(1 ,2 ,@`3)");
        Assert.assertEquals("(\\` (1 (\\, 2) (\\,@ (\\` 3))))", c.toString());
    }

    @Test
    public void testParseDot() {
        try {
            p.parseLine("(. . 2)");
        } catch (Exception e) {
            Assert.assertEquals("'(invalid-read-syntax \".\")", e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void testParseDot1() {
        try {
            p.parseLine("(.\n. 2)");
        } catch (Exception e) {
            Assert.assertEquals("'(invalid-read-syntax \".\")", e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void testParseDot2() {
        LObject r = p.parseLine("(. 2)");
        Assert.assertEquals(new LispInteger(2), r);
    }

    @Test
    public void testParseDot3() {
        try {
            p.parseLine("(. 2 3)");
        } catch (Exception e) {
            Assert.assertEquals("'(invalid-read-syntax \". in wrong context\")", e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void testParseDot40() {
        LObject r = p.parseLine("(1 2 . 3)");
        Assert.assertEquals(LispList.testList(new LispInteger(1), new LispInteger(2), new LispInteger(3)), r);
    }

    @Test
    public void testParseDot4() {
        LObject r = p.parseLine("(1 . 2)");
        Assert.assertEquals(LispList.cons(new LispInteger(1), new LispInteger(2)), r);
    }

    @Test
    public void testParseDot5() {
        try {
            p.parseLine("(1 . 2 3)");
        } catch (Exception e) {
            Assert.assertEquals("'(invalid-read-syntax \". in wrong context\")", e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void testParseDot6() {
        LObject r = p.parseLine("(.. 2)");
        Assert.assertEquals(LispList.list(new LispSymbol("\\.\\."), new LispInteger(2)), r);
    }

    @Test
    public void testParseDot7() {
        LObject r = p.parseLine("(.)");
        Assert.assertEquals(LispList.list(new LispSymbol("\\.")), r);
        r = p.parseLine("( .)");
        Assert.assertEquals(LispList.list(new LispSymbol("\\.")), r);
    }

    @Test
    public void testParseDot8() {
        try {
            p.parseLine("(. )");
        } catch (Exception e) {
            Assert.assertEquals("'(invalid-read-syntax \")\")", e.getMessage());
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSymbol1() {
        LObject symbol = p.parseLine("\\\\a");
        Assert.assertEquals(new LispSymbol("\\\\a"), symbol);
    }

    @Test
    public void testSymbol2() {
        LObject symbol = p.parseLine("\\a");
        Assert.assertEquals(new LispSymbol("a"), symbol);
    }

    @Test
    public void testSymbol3() {
        LObject symbol = p.parseLine("\\.a");
        Assert.assertEquals(new LispSymbol("\\.a"), symbol);
    }

    @Test
    public void testSymbol4() {
        LObject symbol = p.parseLine("b\\\\a");
        Assert.assertEquals(new LispSymbol("b\\\\a"), symbol);
    }

    @Test
    public void testSymbol5() {
        LObject symbol = p.parseLine("b\\a");
        Assert.assertEquals(new LispSymbol("ba"), symbol);
    }

    @Test
    public void testSymbol6() {
        LObject symbol = p.parseLine("b\\.a");
        Assert.assertEquals(new LispSymbol("b\\.a"), symbol);
    }

    @Test
    public void testSymbol7() {
        LObject symbol = p.parseLine("b.a");
        Assert.assertEquals(new LispSymbol("b\\.a"), symbol);
    }

    @Test
    public void testSymbol8() {
        LObject symbol = p.parseLine(".a");
        Assert.assertEquals(new LispSymbol("\\.a"), symbol);
    }

    @Test
    public void testSymbol9() {
        LObject symbol = p.parseLine("b\\\\");
        Assert.assertEquals(new LispSymbol("b\\\\"), symbol);
    }

    @Test
    public void testSymbol10() {
        LObject symbol = p.parseLine("b\\");
        Assert.assertEquals(new LispSymbol("b"), symbol);
    }

    @Test
    public void testSymbol11() {
        LObject symbol = p.parseLine("b\\.");
        Assert.assertEquals(new LispSymbol("b\\."), symbol);
    }

    @Test
    public void testSymbol12() {
        LObject symbol = p.parseLine("b.");
        Assert.assertEquals(new LispSymbol("b\\."), symbol);
    }

    @Test
    public void testSymbol13() {
        LObject symbol = p.parseLine("b.,?a");
        Assert.assertEquals(new LispSymbol("b\\.\\,\\?a"), symbol);
    }

    @Test
    public void testParseConsWithCommentsOk() {
        LObject r = p.parseLine("(1 . ;;ololo\n;;hello\n2)");
        Assert.assertEquals(LispList.cons(new LispInteger(1), new LispInteger(2)), r);
    }

    @Test
    public void testParseConsWithCommentsFail() {
        try {
            p.parseLine("'(1 . ;;ololo\n)");
        } catch (Exception e) {
            Assert.assertEquals("'(invalid-read-syntax \")\")", e.getMessage());
            return;
        }
        Assert.fail();
    }
}


