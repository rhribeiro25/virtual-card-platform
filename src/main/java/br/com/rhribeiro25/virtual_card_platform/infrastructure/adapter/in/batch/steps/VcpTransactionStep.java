package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Transaction;
import br.com.rhribeiro25.virtual_card_platform.application.dto.AuditImport;
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
public class VcpTransactionStep {

    @Bean
    public Step transactionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,

            ItemReader<AuditImport> jbcReader,

            @Qualifier("vcpTransactionProcessor")
            ItemProcessor<AuditImport, Transaction> processor,

            @Qualifier("vcpTransactionWriter")
            ItemWriter<Transaction> writer
    ) {
        return new StepBuilder("transactionStep", jobRepository).
                <AuditImport, Transaction>chunk(5000, transactionManager)
                .reader(jbcReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
