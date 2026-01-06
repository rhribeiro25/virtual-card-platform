package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.flows;

import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchFlow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VcpTransactionFlow {

    @Bean
    public Flow transactionFlow(
            Step transactionStep
    ) {
        return new FlowBuilder<Flow>(SpringBatchFlow.TRANSACTION)
                .start(transactionStep)
                .build();
    }

}
