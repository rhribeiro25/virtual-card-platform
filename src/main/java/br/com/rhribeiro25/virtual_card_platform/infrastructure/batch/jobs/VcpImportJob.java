package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class VcpImportJob {
    private final JobRepository jobRepository;
    private final Step auditStep;
    private final Step cardStep;
    private final Step providerStep;
    private final Step cardProviderStep;
    private final Step transactionStep;

    @Bean
    @Transactional
    public Job importVcpJob() {
        return new JobBuilder("importVcpJob", jobRepository)
                .start(auditStep)
                .next(cardStep)
                .next(providerStep)
                .next(cardProviderStep)
                .next(transactionStep)
                .build();
    }
}
