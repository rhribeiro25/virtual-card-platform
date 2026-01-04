package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

@Slf4j
@Configuration
public class VcpMongoReader {

    @Bean
    @StepScope
    public MongoPagingItemReader<BatchAuditImport> mongoRepositoryReader(
            MongoTemplate mongoTemplate
    ) {
        log.info("Starting: {}", SpringBatchReader.MONGO_DB_READER);
        MongoPagingItemReader<BatchAuditImport> reader = new MongoPagingItemReader<>();
        reader.setName(SpringBatchReader.MONGO_DB_READER);
        reader.setTemplate(mongoTemplate);
        reader.setTargetType(BatchAuditImport.class);
        reader.setPageSize(1000);
        reader.setSort(Map.of("_id", Sort.Direction.ASC));
        reader.setQuery("{}");
        return reader;
    }


}
