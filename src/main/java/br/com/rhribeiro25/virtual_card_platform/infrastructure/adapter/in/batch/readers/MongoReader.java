package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.MongoPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Slf4j
@Configuration("mongoReaderConfig")
@AllArgsConstructor
public class MongoReader {

    @Bean
    @StepScope
    public MongoPagingItemReader<BatchAuditImport> mongoReader(
            MongoTemplate mongoTemplate,
            @Value("#{stepExecutionContext['" + QUERY_MONGO_AUDIT + "']}") String query
    ) {
        log.info("Starting reader: {}", getClassName(this.getClass()));
        MongoPagingItemReader<BatchAuditImport> reader = new MongoPagingItemReader<>();
        reader.setName(getClassName(this.getClass()));
        reader.setTemplate(mongoTemplate);
        reader.setTargetType(BatchAuditImport.class);
        reader.setQuery(query);

        reader.setSort(Map.of(SORT_ATTRIBUTE, Sort.Direction.ASC));
        reader.setPageSize(PAGE_SIZE);
        return reader;
    }

}
