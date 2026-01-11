package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchStep;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class VcpProviderStep {

    @Bean
    public Step providerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<BatchAuditImport> jbcReader,

            @Qualifier(SpringBatchProcessor.PROVIDER)
            ItemProcessor<BatchAuditImport, BatchAuditImport> processor,
            @Qualifier(SpringBatchWriter.CARD)
            ItemWriter<BatchAuditImport> writer
    ) {
        return new StepBuilder(SpringBatchStep.PROVIDER, jobRepository).
                <BatchAuditImport, BatchAuditImport>chunk(1000, transactionManager)
                .reader(jbcReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
