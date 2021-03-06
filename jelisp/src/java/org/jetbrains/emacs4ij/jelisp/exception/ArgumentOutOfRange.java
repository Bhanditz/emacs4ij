package org.jetbrains.emacs4ij.jelisp.exception;

import org.jetbrains.emacs4ij.jelisp.JelispBundle;

@Error("args-out-of-range")
public class ArgumentOutOfRange extends LispException {
  private static String fromArray(Object... arguments) {
    if (arguments.length < 1)
      throw new InternalException(JelispBundle.message("no.args", "args-out-of-range"));
    StringBuilder builder = new StringBuilder(arguments[0].toString());
    for (int i = 1, argumentsLength = arguments.length; i < argumentsLength; i++) {
      builder.append(' ').append(arguments[i].toString());
    }
    return builder.toString();
  }

  public ArgumentOutOfRange(Object... arguments) {
    super("'(args-out-of-range " + fromArray(arguments) + ')');
  }
}
