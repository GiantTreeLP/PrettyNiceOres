package gtlp.relocate.org.apache.commons.math3.exception;

import gtlp.relocate.org.apache.commons.math3.exception.util.ExceptionContext;
import gtlp.relocate.org.apache.commons.math3.exception.util.Localizable;

public final class MathArithmeticException extends ArithmeticException {
    private final ExceptionContext context = new ExceptionContext();

    public MathArithmeticException(Localizable pattern, Object... args) {
        this.context.addMessage(pattern, args);
    }

    public final String getMessage() {
        return this.context.getMessage();
    }

    public final String getLocalizedMessage() {
        return this.context.getLocalizedMessage();
    }
}
