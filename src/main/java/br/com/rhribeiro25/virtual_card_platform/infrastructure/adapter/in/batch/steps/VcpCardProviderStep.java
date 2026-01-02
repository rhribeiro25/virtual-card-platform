package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CardProvider;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class VcpCardProviderStep {

    @Bean
    public Step cardProviderStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            TaskExecutor task,

            ItemReader<AuditImport> jbcReader,

            @Qualifier("vcpCardProviderProcessor")
            ItemProcessor<AuditImport, CardProvider> processor,

            @Qualifier("vcpCardProviderWriter")
            ItemWriter<CardProvider> writer
    ) {
        return new StepBuilder("cardProviderStep", jobRepository).
                <AuditImport, CardProvider>chunk(5000, transactionManager)
                .reader(jbcReader)
                .processor(processor)
                .writer(writer)
//                .taskExecutor(task)
                .build();
    }

}
