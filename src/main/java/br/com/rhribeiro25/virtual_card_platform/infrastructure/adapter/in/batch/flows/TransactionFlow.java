//package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.flows;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.FlowBuilder;
//import org.springframework.batch.core.job.flow.Flow;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;
//import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getConfigurationName;
//
//@Configuration
//@RequiredArgsConstructor
//public class TransactionFlow {
//
//    @Bean
//    public Flow transactionFlowConfig(Step transactionStepConfig) {
//        return new FlowBuilder<Flow>(getClassName(this.getClass()))
//                .start(transactionStepConfig)
//                .build();
//    }
//
//}
