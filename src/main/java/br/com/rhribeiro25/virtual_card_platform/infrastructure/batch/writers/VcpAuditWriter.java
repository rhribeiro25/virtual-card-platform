package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpAuditWriter implements ItemWriter<AuditImport> {

    private final DataSource dataSource;

    private JdbcBatchItemWriter<AuditImport> jdbcWriter;

    @Override
    public void write(Chunk<? extends AuditImport> chunk) throws Exception {
        // Persist all audit rows in batch
        saveAll().write(chunk);
    }

    private JdbcBatchItemWriter<AuditImport> saveAll() {
        if (jdbcWriter == null) {
            jdbcWriter = new JdbcBatchItemWriterBuilder<AuditImport>()
                    .dataSource(dataSource)
                    .sql("""
                        INSERT INTO audit_import
                                (id, card_ref, provider_code, tx_request_ref, raw_payload)
                                VALUES (:id, :cardRef, :providerCode, :txRequestRef, :rawPayload)
                    """)
                    .beanMapped()
                    .build();
            jdbcWriter.afterPropertiesSet();
        }
        return jdbcWriter;
    }


}
