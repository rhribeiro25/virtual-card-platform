package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getConfigurationName;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileIngestionJob {
    private final JobRepository jobRepository;
    private final Flow auditFlowConfig;
    private final Flow cardFlowConfig;
//    private final Flow transactionAndCardProviderParallelFlowConfig;

    @Bean
    public Job fileIngestionJobConfig() {
        log.warn("Starting: {}", getClassName(this.getClass()));
        return new JobBuilder(getClassName(this.getClass()), jobRepository)
                .start(auditFlowConfig)
                .next(cardFlowConfig)
//                .next(transactionAndCardProviderParallelFlow)
                .end()
                .build();
    }
}
