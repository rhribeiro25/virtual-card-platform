package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.AuditStepListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors.AuditProcessor;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers.AuditWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getConfigurationName;

@Configuration
@RequiredArgsConstructor
public class AuditStep {

    @Bean
    public Step auditStepConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            TaskExecutor multiTaskConfig,
            ItemReader<CsvFileRow> fileReaderConfig,

            AuditProcessor processor,
            AuditWriter writer,
            AuditStepListener listener

    ) {
        return new StepBuilder(getClassName(this.getClass()), jobRepository)
                .<CsvFileRow, BatchAuditImport>chunk(SPRING_BATCH_SIZE, transactionManager)
                .reader(fileReaderConfig)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .taskExecutor(multiTaskConfig)
                .build();
    }
}

