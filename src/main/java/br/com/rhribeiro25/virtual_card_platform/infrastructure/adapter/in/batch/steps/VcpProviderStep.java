package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Provider;
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
public class VcpProviderStep {

    @Bean
    public Step providerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,

            ItemReader<AuditImport> jbcReader,

            @Qualifier("vcpProviderProcessor")
            ItemProcessor<AuditImport, Provider> processor,

            @Qualifier("vcpProviderWriter")
            ItemWriter<Provider> writer
    ) {
        return new StepBuilder("providerStep", jobRepository).
                <AuditImport, Provider>chunk(5000, transactionManager)
                .reader(jbcReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
