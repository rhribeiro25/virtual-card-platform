package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
public class VcpFileReader {

    private final SpringBatchReader springBatchReader;

    @Bean
    @StepScope
    public FlatFileItemReader<CsvFileRow> csvFileReader() {
        log.info("Starting: {}", springBatchReader.CSV_FILE_READER);
        return new FlatFileItemReaderBuilder<CsvFileRow>()
                .name(springBatchReader.CSV_FILE_READER)
                .resource(springBatchReader.RESOURCE)
                .linesToSkip(Integer.parseInt(springBatchReader.LINES_TO_SKIP))
                .delimited()
                .delimiter(springBatchReader.DELIMITER)
                .quoteCharacter(springBatchReader.QUOTE_CHARACTER.charAt(0))
                .names(String.valueOf(springBatchReader.COLUMNS))
                .fieldSetMapper(springBatchReader.FIELD_SET_MAPPER)
                .build();
    }
}

