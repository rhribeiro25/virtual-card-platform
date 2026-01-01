package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
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
                                processed_step,
                                is_processed,
                                created_at
                        """)
                .fromClause("FROM audit_import")
                .whereClause("processed_step = :step and is_processed = false")
                .parameterValues(Map.of("step", step))
                .sortKeys(Map.of(
                        "created_at", Order.ASCENDING,
                        "id", Order.ASCENDING
                ))
                .rowMapper(new BeanPropertyRowMapper<>(AuditImport.class))
                .build();
    }

}

