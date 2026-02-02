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
//public class ProviderFlow {
//
//    @Bean
//    public Flow providerFlowConfig(Step providerStepConfig) {
//        return new FlowBuilder<Flow>(getClassName(this.getClass()))
//                .start(providerStepConfig)
//                .build();
//    }
//
//}
