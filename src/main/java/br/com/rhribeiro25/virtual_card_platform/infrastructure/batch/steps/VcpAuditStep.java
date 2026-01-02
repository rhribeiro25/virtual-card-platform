package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditImport;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.CsvRow;
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
public class VcpAuditStep {

    @Bean
    public Step auditStep(

            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,

            ItemReader<CsvRow> fileReader,

            @Qualifier("vcpAuditProcessor")
            ItemProcessor<CsvRow, AuditImport> processor,

            @Qualifier("vcpAuditWriter")
            ItemWriter<AuditImport> writer

    ) {
        return new StepBuilder("auditStep", jobRepository)
                .<CsvRow, AuditImport>chunk(5000, transactionManager)
                .reader(fileReader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}

