package gtlp.relocate.org.apache.commons.math3.exception;

import gtlp.relocate.org.apache.commons.math3.exception.util.ExceptionContext;
import gtlp.relocate.org.apache.commons.math3.exception.util.Localizable;

public class MathIllegalArgumentException extends IllegalArgumentException {
    private final ExceptionContext context = new ExceptionContext();

    public MathIllegalArgumentException(Localizable pattern, Object... args) {
        this.context.addMessage(pattern, args);
    }

    public String getMessage() {
        return this.context.getMessage();
    }

    public String getLocalizedMessage() {
        return this.context.getLocalizedMessage();
    }
}
