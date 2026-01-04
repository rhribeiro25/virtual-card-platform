package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchFlow;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchJob;
import br.com.rhribeiro25.virtual_card_platform.domain.model.contants.SpringBatchReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class VcpFileReader {

    @Bean
    @StepScope
    public FlatFileItemReader<CsvFileRow> csvFileReader() {
        log.info("Starting: {}", SpringBatchReader.CSV_FILE_READER);
        return new FlatFileItemReaderBuilder<CsvFileRow>()
                .name(SpringBatchReader.CSV_FILE_READER)
                .resource(new ClassPathResource("input/virtual_cards_25k.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .quoteCharacter('"')
                .names(
                        "cardRef", "state", "brandCode", "holderNameRaw",
                        "balanceTxt", "currencyCode", "internationalFlag",
                        "expiryTxt", "cvvTxt", "pinTxt",
                        "maxDailyTxTxt", "maxTxAmountTxt",
                        "issuingCountryCode", "notesRaw",
                        "providerCode", "providerFeePctTxt",
                        "providerDailyLimitTxt", "providerPriorityTxt",
                        "providerState", "providerCountry",
                        "txKind", "txAmountTxt", "txRequestRef"
                )
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(CsvFileRow.class);
                }})
                .build();
    }
}

