package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.AuditStepListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.GenericChunkListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors.AuditProcessor;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers.AuditWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.*;

@Configuration
@RequiredArgsConstructor
public class AuditStep {

    @Bean
    public Step auditStepConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,

            ItemReader<CsvFileRow> fileReader,
            AuditProcessor processor,
            AuditWriter writer,

            AuditStepListener stepListener,
            GenericChunkListener chunkListener

    ) {
        return new StepBuilder(AUDIT_STEP, jobRepository)
                .<CsvFileRow, BatchAuditImport>chunk(SPRING_BATCH_SIZE, transactionManager)
                .reader(fileReader)
                .processor(processor)
                .writer(writer)

                .listener(stepListener)
                .listener(chunkListener)

                .faultTolerant()
                .retry(RETRAY_CLASS)
                .retryLimit(RETRY_LIMIT)
                .skip(SKIP_CLASS)
                .skipLimit(SKIP_LIMIT)

                .build();
    }
}

