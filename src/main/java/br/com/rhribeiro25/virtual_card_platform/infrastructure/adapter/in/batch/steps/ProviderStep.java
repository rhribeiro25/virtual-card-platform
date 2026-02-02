//package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.steps;
//
//import br.com.rhribeiro25.virtual_card_platform.domain.model.BatchAuditImport;
//import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors.CardProcessor;
//import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.processors.ProviderProcessor;
//import br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.writers.CardWriter;
//import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchProcessor;
//import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchStep;
//import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchWriter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//@RequiredArgsConstructor
//public class ProviderStep {
//
//    @Bean
//    public Step providerStepConfig(
//            JobRepository jobRepository,
//            PlatformTransactionManager transactionManager,
//            ItemReader<BatchAuditImport> mongoReader,
//            ProviderProcessor processor,
//            ProviderWriter writer
//    ) {
//        return new StepBuilder(this.getClass().getAnnotation(Configuration.class).value(), jobRepository).
//                <BatchAuditImport, BatchAuditImport>chunk(SPRING_BATCH_SIZE, transactionManager)
//                .reader(mongoReader)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }
//
//}
