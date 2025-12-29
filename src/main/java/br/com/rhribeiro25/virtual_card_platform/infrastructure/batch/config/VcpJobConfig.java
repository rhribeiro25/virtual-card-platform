package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VcpJobConfig {
    private final JobRepository jobRepository;
    private final Step importVcpStep;

    @Bean
    public Job importVcpJob() {
        return new JobBuilder("importVcpJob", jobRepository)
                .start(importVcpStep)
                .build();
    }
}
