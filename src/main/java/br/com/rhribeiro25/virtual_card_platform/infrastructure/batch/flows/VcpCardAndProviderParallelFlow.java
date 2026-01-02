package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
public class VcpCardAndProviderParallelFlow {

    @Bean
    public Flow cardAndProviderParallelFlow(
            TaskExecutor task,
            Flow cardFlow,
            Flow providerFlow
    ) {
        return new FlowBuilder<Flow>("cardAndProviderParallelFlow")
                .split(task)
                .add(cardFlow, providerFlow)
                .build();
    }
}

