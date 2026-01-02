package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImportStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class VcpJdbcReader {

    @Bean
    @StepScope
    public JdbcPagingItemReader<AuditImport> jdbcReader(
            DataSource dataSource,
            @Value("#{stepExecution.stepName}") String step
    ) {
        String clause = "";
        if (step.equals(AuditImportStep.CARD.getName())) {
            clause = "is_processed_card = :is_processed";
        }
        if (step.equals(AuditImportStep.PROVIDER.getName())) {
            clause = "is_processed_provider = :is_processed";
        }
        if (step.equals(AuditImportStep.CARD_PROVIDER.getName())) {
            clause = "is_processed_card_provider = :is_processed";
        }
        if (step.equals(AuditImportStep.TRANSACTION.getName())) {
            clause = "is_processed_transaction = :is_processed";
        }

        return new JdbcPagingItemReaderBuilder<AuditImport>()
                .name("jdbcReader")
                .dataSource(dataSource)
                .pageSize(5000)
                .selectClause("""
                            SELECT
                                id,
                                card_ref,
                                provider_code,
                                tx_request_ref,
                                raw_payload,
                                is_processed_card,
                                is_processed_provider,
                                is_processed_card_provider,
                                is_processed_transaction,
                                created_at
                        """)
                .fromClause("FROM audit_import")
                .whereClause(clause)
                .parameterValues(Map.of("is_processed", "false"))
                .sortKeys(Map.of(
                        "created_at", Order.ASCENDING,
                        "id", Order.ASCENDING
                ))
                .rowMapper(new BeanPropertyRowMapper<>(AuditImport.class))
                .build();
    }

}

