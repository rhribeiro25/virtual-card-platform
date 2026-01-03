package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.out.persistence.mongo.BatchAuditMongoTemplate;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VcpMongoReader {

    @Bean
    @StepScope
    public MongoPagingItemReader<BatchAuditImport> mongoRepositoryReader(
            BatchAuditMongoTemplate batchAuditMongoTemplate,
            @Value("#{stepExecution.stepName}") String step
    ) {
        return batchAuditMongoTemplate.findAll();
    }


}
