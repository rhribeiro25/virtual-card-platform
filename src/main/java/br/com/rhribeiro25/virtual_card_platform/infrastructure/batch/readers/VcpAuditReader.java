package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.readers;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dtos.VirtualCardsCsvRow;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class VcpAuditReader {

    @Bean
    @StepScope
    public FlatFileItemReader<VirtualCardsCsvRow> auditReader() {
        return new FlatFileItemReaderBuilder<VirtualCardsCsvRow>()
                .name("vcpAuditReader")
                .resource(new ClassPathResource("input/virtual_cards_100k.csv"))
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
                    setTargetType(VirtualCardsCsvRow.class);
                }})
                .build();
    }
}

