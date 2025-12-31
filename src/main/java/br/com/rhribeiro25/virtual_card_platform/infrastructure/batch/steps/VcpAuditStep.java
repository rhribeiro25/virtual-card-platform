package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.AuditVcpRow;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class VcpAuditStep {

    @Bean
    public Step auditStep(

            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            TaskExecutor task,

            ItemReader<VirtualCardsCsvRow> auditReader,

            @Qualifier("vcpAuditProcessor")
            ItemProcessor<VirtualCardsCsvRow, AuditVcpRow> processor,

            @Qualifier("vcpAuditWriter")
            ItemWriter<AuditVcpRow> writer

    ) {
        return new StepBuilder("auditStep", jobRepository)
                .<VirtualCardsCsvRow, AuditVcpRow>chunk(1000, transactionManager)
                .reader(auditReader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(task)
                .build();
    }
}

