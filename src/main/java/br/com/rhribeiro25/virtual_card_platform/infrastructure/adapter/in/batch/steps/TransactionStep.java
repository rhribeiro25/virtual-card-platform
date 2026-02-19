package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.GenericStepListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors.TransactionProcessor;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers.TransactionWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.SPRING_BATCH_SIZE;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Configuration
@RequiredArgsConstructor
public class TransactionStep {

    @Bean
    public Step transactionStepConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<BatchAuditImport> mongoReader,
            TransactionProcessor processor,
            TransactionWriter writer,
            GenericStepListener listener
    ) {
        return new StepBuilder(getClassName(this.getClass()), jobRepository).
                <BatchAuditImport, BatchAuditImport>chunk(SPRING_BATCH_SIZE, transactionManager)
                .reader(mongoReader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .build();
    }

}
