package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.flows;

import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchFlow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
public class VcpTransactionAndCardProviderParallelFlow {

    @Bean
    public Flow transactionAndCardProviderParallelFlow(
            TaskExecutor task,
            Flow transactionFlow,
            Flow cardProviderFlow
    ) {
        return new FlowBuilder<Flow>(SpringBatchFlow.CARD_PROVIDER_AND_TRANSACTION)
                .split(task)
                .add(transactionFlow, cardProviderFlow)
                .build();
    }
}

