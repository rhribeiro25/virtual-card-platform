package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.writers;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditVcpRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class VcpAuditWriter implements ItemWriter<AuditVcpRow> {

    private final DataSource dataSource;

    private JdbcBatchItemWriter<AuditVcpRow> jdbcWriter;

    @Override
    public void write(Chunk<? extends AuditVcpRow> chunk) throws Exception {
        // Persist all audit rows in batch
        saveAll().write(chunk);
    }

    private JdbcBatchItemWriter<AuditVcpRow> saveAll() {
        if (jdbcWriter == null) {
            jdbcWriter = new JdbcBatchItemWriterBuilder<AuditVcpRow>()
                    .dataSource(dataSource)
                    .sql("""
                        INSERT INTO stg_virtual_cards
                        (id, card_ref, provider_code, tx_request_ref, raw_payload, processed)
                        VALUES (:id, :cardRef, :providerCode, :txRequestRef, :rawPayload, :processed)
                    """)
                    .beanMapped()
                    .build();
            jdbcWriter.afterPropertiesSet();
        }
        return jdbcWriter;
    }


}
