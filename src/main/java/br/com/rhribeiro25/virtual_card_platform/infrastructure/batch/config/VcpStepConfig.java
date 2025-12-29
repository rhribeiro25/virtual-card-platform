package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto.ExternalCardCsvRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class VcpStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ItemReader<ExternalCardCsvRow> read;
    private final ItemProcessor<ExternalCardCsvRow, Card> process;
    private final ItemWriter<Card> write;
    private final TaskExecutor task;

    @Bean
    public Step importVcpStep() {
        return new StepBuilder("importVcpStep", jobRepository)
                .<ExternalCardCsvRow, Card>chunk(5000, transactionManager)
                .reader(read)
                .processor(process)
                .writer(write)
                .taskExecutor(task)
                .build();
    }

}
