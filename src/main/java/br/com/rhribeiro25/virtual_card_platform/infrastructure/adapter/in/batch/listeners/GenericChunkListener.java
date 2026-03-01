package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GenericChunkListener implements ChunkListener {

    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution stepExecution =
                context.getStepContext().getStepExecution();

        Long readCount = stepExecution.getReadCount();
        Long writeCount = stepExecution.getWriteCount();

        log.info("Chunk finished | Step: {} | Total Read: {} | Total Write: {}",
                stepExecution.getStepName(),
                readCount,
                writeCount
        );
    }
}
