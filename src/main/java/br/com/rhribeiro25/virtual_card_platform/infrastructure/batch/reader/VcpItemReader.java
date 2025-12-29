package br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.reader;

import br.com.rhribeiro25.virtual_card_platform.infrastructure.batch.dto.ExternalCardCsvRow;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@RequiredArgsConstructor
public class VcpItemReader {

    @Bean
    public FlatFileItemReader<ExternalCardCsvRow> read() {
        FlatFileItemReader<ExternalCardCsvRow> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/input/external_cards_25000.csv"));
        itemReader.setLinesToSkip(1);

        DefaultLineMapper<ExternalCardCsvRow> mapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames(
                "CARD_REF", "STATE", "BRAND_CODE", "HOLDER_NAME_RAW",
                "BALANCE_TXT", "CURRENCY_CODE", "ACTIVE_FLAG", "INTERNATIONAL_FLAG",
                "EXPIRY_TXT", "CVV_TXT", "PIN_TXT",
                "MAX_DAILY_TX_TXT", "MAX_TX_AMOUNT_TXT",
                "ISSUING_COUNTRY_CODE", "NOTES_RAW",
                "PROVIDER_CODE", "PROVIDER_FEE_PCT_TXT", "PROVIDER_DAILY_LIMIT_TXT", "PROVIDER_PRIORITY_TXT", "PROVIDER_ENABLED_FLAG",
                "TX_KIND", "TX_AMOUNT_TXT", "TX_REQUEST_REF"
        );

        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldSet -> new ExternalCardCsvRow(
                fieldSet.readString("CARD_REF"),
                fieldSet.readString("STATE"),
                fieldSet.readString("BRAND_CODE"),
                fieldSet.readString("HOLDER_NAME_RAW"),
                fieldSet.readString("BALANCE_TXT"),
                fieldSet.readString("CURRENCY_CODE"),
                fieldSet.readString("ACTIVE_FLAG"),
                fieldSet.readString("INTERNATIONAL_FLAG"),
                fieldSet.readString("EXPIRY_TXT"),
                fieldSet.readString("CVV_TXT"),
                fieldSet.readString("PIN_TXT"),
                fieldSet.readString("MAX_DAILY_TX_TXT"),
                fieldSet.readString("MAX_TX_AMOUNT_TXT"),
                fieldSet.readString("ISSUING_COUNTRY_CODE"),
                fieldSet.readString("NOTES_RAW"),
                fieldSet.readString("PROVIDER_CODE"),
                fieldSet.readString("PROVIDER_FEE_PCT_TXT"),
                fieldSet.readString("PROVIDER_DAILY_LIMIT_TXT"),
                fieldSet.readString("PROVIDER_PRIORITY_TXT"),
                fieldSet.readString("PROVIDER_ENABLED_FLAG"),
                fieldSet.readString("TX_KIND"),
                fieldSet.readString("TX_AMOUNT_TXT"),
                fieldSet.readString("TX_REQUEST_REF")
        ));

        itemReader.setLineMapper(mapper);
        return itemReader;
    }

}
