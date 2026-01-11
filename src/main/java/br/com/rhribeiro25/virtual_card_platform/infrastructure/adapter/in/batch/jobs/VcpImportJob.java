package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.jobs;

import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VcpImportJob {
    private final JobRepository jobRepository;
    private final Flow auditFlow;
    private final Flow cardFlow;
    private final Flow transactionAndCardProviderParallelFlow;

    @Bean
    public Job importVcpJob() {
        log.warn("Starting: {}", SpringBatchJob.CSV_IMPORT);
        return new JobBuilder(SpringBatchJob.CSV_IMPORT, jobRepository)
                .start(auditFlow)
                .next(cardFlow)
//                .next(transactionAndCardProviderParallelFlow)
                .end()
                .build();
    }
}
