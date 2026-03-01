package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.FILE_INGESTION_JOB;

@Slf4j
@Configuration("fileIngestionJobConfig")
@RequiredArgsConstructor
public class FileIngestionJob {
    private final JobRepository jobRepository;
    private final Flow auditFlowConfig;
    private final Flow cardFlowConfig;
    private final Flow transactionFlowConfig;
    private final Flow providerFlowConfig;
    private final Flow cardProviderFlowConfig;

    @Bean
    public Job fileIngestionJob() {
        log.warn("Starting: {}", FILE_INGESTION_JOB);
        return new JobBuilder(FILE_INGESTION_JOB, jobRepository)
                .start(auditFlowConfig)
                .next(cardFlowConfig)
                .next(providerFlowConfig)
                .next(cardProviderFlowConfig)
                .next(transactionFlowConfig)
                .end()
                .build();
    }
}
