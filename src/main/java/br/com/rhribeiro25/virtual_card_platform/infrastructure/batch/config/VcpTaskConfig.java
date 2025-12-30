//package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.core.task.TaskExecutor;
//
//@Configuration
//@RequiredArgsConstructor
//public class VcpTaskConfig {
//    private final JobRepository jobRepository;
//    private final Step importVcpStep;
//
//    @Bean
//    public TaskExecutor task() {
//        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
//        asyncTaskExecutor.setConcurrencyLimit(10);
//        return asyncTaskExecutor;
//    }
//}
