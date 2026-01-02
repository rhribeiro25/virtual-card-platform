package br.com.rhribeiro25.virtual_card_platform.infrastructure.adapter.in.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.application.dto.CsvRow;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class VcpFileReader {

    @Bean
    @StepScope
    public FlatFileItemReader<CsvRow> fileReader() {
        return new FlatFileItemReaderBuilder<CsvRow>()
                .name("fileReader")
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
                    setTargetType(CsvRow.class);
                }})
                .build();
    }
}

