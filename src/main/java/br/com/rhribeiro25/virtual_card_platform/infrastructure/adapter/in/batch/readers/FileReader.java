package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.rhribeiro25.virtual_card_platform.shared.contants.SpringBatchConstants.*;
import static br.com.rhribeiro25.virtual_card_platform.shared.utils.SpringBatchUtils.getClassName;

@Slf4j
@Configuration("fileReaderConfig")
@AllArgsConstructor
public class FileReader {

    @Bean
    @StepScope
    public FlatFileItemReader<CsvFileRow> fileReader() {
        log.info("Starting reader: {}", FILE_READER);
        return new FlatFileItemReaderBuilder<CsvFileRow>()
                .name(getClassName(this.getClass()))
                .resource(RESOURCE)
                .linesToSkip(Integer.parseInt(LINES_TO_SKIP))
                .delimited()
                .delimiter(DELIMITER)
                .quoteCharacter(QUOTE_CHARACTER.charAt(0))
                .names(COLUMNS.toArray(new String[0]))
                .fieldSetMapper(FIELD_SET_MAPPER)
                .build();
    }
}

