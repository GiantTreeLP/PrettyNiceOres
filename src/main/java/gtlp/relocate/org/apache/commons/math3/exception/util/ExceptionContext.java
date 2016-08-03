package gtlp.relocate.org.apache.commons.math3.exception.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public final class ExceptionContext implements Serializable {
    private List<Localizable> msgPatterns = new ArrayList<>();
    private List<Object[]> msgArguments = new ArrayList<>();

    public ExceptionContext() {
        new HashMap();
    }

    public final void addMessage(Localizable pattern, Object... arguments) {
        this.msgPatterns.add(pattern);
        this.msgArguments.add(arguments);
    }

    public final String getMessage() {
        return this.getMessage(Locale.US);
    }

    public final String getLocalizedMessage() {
        return this.getMessage(Locale.getDefault());
    }

    private String getMessage(Locale locale) {
        String var3 = ": ";
        ExceptionContext var10 = this;
        StringBuilder var4 = new StringBuilder();
        int var5 = 0;
        int var6 = this.msgPatterns.size();

        for (int var7 = 0; var7 < var6; ++var7) {
            Localizable var8 = var10.msgPatterns.get(var7);
            Object[] var9 = var10.msgArguments.get(var7);
            MessageFormat var11 = new MessageFormat(var8.getLocalizedString(locale), locale);
            var4.append(var11.format(var9));
            ++var5;
            if (var5 < var6) {
                var4.append(var3);
            }
        }

        return var4.toString();
    }
}