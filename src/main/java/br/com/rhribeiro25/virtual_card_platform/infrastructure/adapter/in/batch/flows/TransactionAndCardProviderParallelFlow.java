//package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.flows;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.job.builder.FlowBuilder;
//import org.springframework.batch.core.job.flow.Flow;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.TaskExecutor;
//
//import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;
//import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getConfigurationName;
//
//@Configuration
//@RequiredArgsConstructor
//public class TransactionAndCardProviderParallelFlow {
//
//    @Bean
//    public Flow transactionAndCardProviderParallelFlowConfig(TaskExecutor parallelTaskConfig, Flow transactionFlowConfig, Flow cardProviderFlowConfig) {
//        return new FlowBuilder<Flow>(getClassName(this.getClass()))
//                .split(parallelTaskConfig)
//                .add(transactionFlowConfig, cardProviderFlowConfig)
//                .build();
//    }
//}
//
