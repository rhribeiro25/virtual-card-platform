package br.com.rhribeiro25.virtual_card_platform.shared.Exception;

public final class BatchErrorException {

    private BatchErrorException() {}

    public static String formatThrowable(Throwable t) {
        if (t == null) return "null";

        Throwable root = rootCause(t);

        StackTraceElement ste = firstAppFrame(root.getStackTrace(),
                "br.com.rhribeiro25.virtual_card_platform");

        String where = (ste == null)
                ? "unknown"
                : ste.getClassName() + "." + ste.getMethodName() +
                "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";

        String msg = root.getMessage();
        String type = root.getClass().getSimpleName();

        return "%s: %s | at %s".formatted(type, msg, where);
    }

    public static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) cur = cur.getCause();
        return cur;
    }

    private static StackTraceElement firstAppFrame(StackTraceElement[] stack, String basePackage) {
        if (stack == null) return null;
        for (StackTraceElement e : stack) {
            if (e.getClassName() != null && e.getClassName().startsWith(basePackage)) return e;
        }

        return stack.length > 0 ? stack[0] : null;
    }
}
