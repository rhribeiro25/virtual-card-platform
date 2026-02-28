package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.GenericChunkListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.GenericSkipListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners.GenericStepListener;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors.CardProviderProcessor;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers.CardProviderWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Configuration("cardProviderStep")
@RequiredArgsConstructor
public class CardProviderStep {

    @Bean
    public Step cardProviderStepConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,

            ItemReader<BatchAuditImport> mongoReader,
            CardProviderProcessor processor,
            CardProviderWriter writer,

            GenericStepListener stepListener,
            GenericSkipListener skipListener,
            GenericChunkListener chunkListener
    ) {
        return new StepBuilder(getClassName(this.getClass()), jobRepository).
                <BatchAuditImport, BatchAuditImport>chunk(SPRING_BATCH_SIZE, transactionManager)

                .reader(mongoReader)
                .processor(processor)
                .writer(writer)

                .listener(skipListener)
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
