package org.jetbrains.emacs4ij.jelisp.exception;

import org.jetbrains.emacs4ij.jelisp.JelispBundle;

/**
 * Created with IntelliJ IDEA.
 * User: kate
 * Date: 4/25/12
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoBufferException extends LispException {
    public NoBufferException (String bufferName) {
        super(JelispBundle.message("no.buffer", bufferName));
    }
}
