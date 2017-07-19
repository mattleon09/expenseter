package com.mattleo.finance.utils.errors;

public class ExportError extends AppError {
    public ExportError(String detailMessage) {
        super(detailMessage);
    }

    public ExportError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
