package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VcpImportJob {
    private final JobRepository jobRepository;
    private final Flow auditFlow;
    private final Flow cardAndProviderParallelFlow;
    private final Flow transactionAndCardProviderParallelFlow;

    @Bean
    public Job importVcpJob() {

        return new JobBuilder("importVcpJob", jobRepository)
                .start(auditFlow)
                .next(cardAndProviderParallelFlow)
                .next(transactionAndCardProviderParallelFlow)
                .end()
                .build();
    }
}
