package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VcpCardFlow {

    @Bean
    public Flow cardFlow(
            Step cardStep
    ) {
        return new FlowBuilder<Flow>("cardFlow")
                .start(cardStep)
                .build();
    }

}
