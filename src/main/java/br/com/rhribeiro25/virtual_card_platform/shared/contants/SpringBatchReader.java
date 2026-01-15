package br.com.rhribeiro25.virtual_card_platform.shared.contants;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SpringBatchReader {
    public final String MONGO_DB_READER = "mongoDbReader";
    public final Integer PAGE_SIZE = 1000;
    public final String SORT_ATTRIBUTE = "actionFileDate";
    public final String QUERY = "{}";

    public final String CSV_FILE_READER = "csvFileReader";
    public final ClassPathResource RESOURCE = new ClassPathResource("input/virtual_cards_10.csv");
    public final String LINES_TO_SKIP = "1";
    public final String DELIMITER = ",";
    public final String QUOTE_CHARACTER = "\"";
    public final List<String> COLUMNS = Arrays.asList(
            "actionType", "createdDate", "cardRef",
            "state", "brandCode", "holderNameRaw",
            "balanceTxt", "currencyCode", "internationalFlag",
            "expiryTxt", "cvvTxt", "pinTxt",
            "maxDailyTxTxt", "maxTxAmountTxt", "issuingCountryCode",
            "notesRaw", "providerCode", "providerFeePctTxt",
            "providerDailyLimitTxt", "providerPriorityTxt", "providerState",
            "providerCountry", "txKind", "txAmountTxt", "txRequestRef"
    );
    public final BeanWrapperFieldSetMapper<CsvFileRow> FIELD_SET_MAPPER = new BeanWrapperFieldSetMapper<>() {{
        setTargetType(CsvFileRow.class);
    }};
}
