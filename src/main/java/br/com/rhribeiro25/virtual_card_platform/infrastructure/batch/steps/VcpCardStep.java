package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.steps;

import br.com.rhribeiro25.virtual_card_platform.domain.model.Card;
import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
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
public class VcpCardStep {

    @Bean
    public Step cardStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,

            ItemReader<VirtualCardsCsvRow> itemReader,

            @Qualifier("vcpCardProcessor")
            ItemProcessor<VirtualCardsCsvRow, Card> processor,

            @Qualifier("vcpCardWriter")
            ItemWriter<Card> writer
    ) {
        return new StepBuilder("cardStep", jobRepository).
                <VirtualCardsCsvRow, Card>chunk(5000, transactionManager)
                .reader(itemReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
