package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.flows;

import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchFlow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
public class VcpCardAndProviderParallelFlow {

    @Bean
    public Flow cardAndProviderParallelFlow(
            TaskExecutor parallelTask,
            Flow cardFlow,
            Flow providerFlow
    ) {
        return new FlowBuilder<Flow>(SpringBatchFlow.CARD_AND_PROVIDER)
                .split(parallelTask)
                .add(cardFlow, providerFlow)
                .build();
    }
}

