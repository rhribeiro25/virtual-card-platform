package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import com.mongodb.DuplicateKeyException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.BindException;
import java.time.format.DateTimeParseException;

@Component
public class CustomSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {

        Throwable root = rootCause(t);

        // ====== SKIP: Data / Conversion / Validation Errors ======
        if (root instanceof FlatFileParseException) return true;
        if (root instanceof IllegalArgumentException) return true;

        if (root instanceof BindException) return true;
        if (root instanceof ConversionFailedException) return true;
        if (root instanceof DateTimeParseException) return true;

        if (root instanceof ConstraintViolationException) return true;

        // Duplicated / constraints (Mongo + Postgres)
        if (root instanceof DuplicateKeyException) return true;
        if (root instanceof DataIntegrityViolationException) return true;

        // ====== Not SKIP: IO / infra ======
        if (root instanceof IOException) return false;
        if (root instanceof UncheckedIOException) return false;

        if (root instanceof DataAccessResourceFailureException) return false;
        if (root instanceof TransientDataAccessResourceException) return false;

        if (root instanceof UncategorizedMongoDbException) return false;

        // default: Not Skip
        return false;
    }

    private Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) cur = cur.getCause();
        return cur;
    }
}
