package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.shared.Exception.ConflictException;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeParseException;

@Component
public class CustomRetryPolice implements RetryPolicy {

    private final RetryPolicy delegate;

    public CustomRetryPolice() {
        this.delegate = buildDelegate();
    }

    private RetryPolicy buildDelegate() {
        ExceptionClassifierRetryPolicy p = new ExceptionClassifierRetryPolicy();

        p.setExceptionClassifier(throwable -> {
            Throwable root = rootCause(throwable);

            // ====== NEVER RETRY: data/conversion/validation ======
            if (root instanceof FlatFileParseException) return new NeverRetryPolicy();
            if (root instanceof IllegalArgumentException) return new NeverRetryPolicy();
            if (root instanceof ConversionFailedException) return new NeverRetryPolicy();
            if (root instanceof DateTimeParseException) return new NeverRetryPolicy();
            if (root instanceof ConstraintViolationException) return new NeverRetryPolicy();

            // ====== RETRY: IO / infra/transient ======
            if (root instanceof IOException) return simple(3);
            if (root instanceof UncheckedIOException) return simple(3);

            // Spring DAO transient
            if (root instanceof TransientDataAccessResourceException) return simple(5);
            if (root instanceof DataAccessResourceFailureException) return simple(5);

            // Locks / deadlocks (Postgres)
            if (root instanceof DeadlockLoserDataAccessException) return simple(5);
            if (root instanceof CannotAcquireLockException) return simple(5);

            // Mongo transient-ish
            if (root instanceof UncategorizedMongoDbException) return simple(5);
            if (root instanceof MongoTimeoutException) return simple(5);
            if (root instanceof MongoSocketException) return simple(5);
            if (root instanceof MongoException) return simple(3);
            if (root instanceof ConflictException) return simple(5);

            // default: do not retry
            return new NeverRetryPolicy();
        });

        return p;
    }

    private RetryPolicy simple(int maxAttempts) {
        return new SimpleRetryPolicy(maxAttempts);
    }

    private Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) cur = cur.getCause();
        return cur;
    }

    // ====== RetryPolicy interface delegation ======

    @Override
    public boolean canRetry(RetryContext context) {
        return delegate.canRetry(context);
    }

    @Override
    public RetryContext open(RetryContext parent) {
        return delegate.open(parent);
    }

    @Override
    public void close(RetryContext context) {
        delegate.close(context);
    }

    @Override
    public void registerThrowable(RetryContext context, Throwable throwable) {
        delegate.registerThrowable(context, throwable);
    }
}
