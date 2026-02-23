package br.com.rhribeiro25.virtual_card_platform.shared.contants;

import br.com.rhribeiro25.virtual_card_platform.domain.model.CsvFileRow;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;

public class SpringBatchConstants {
    public static final Integer SPRING_BATCH_SIZE = 200;
    public static final Integer PAGE_SIZE = 200;
    public static final String SORT_ATTRIBUTE = "actionFileDate";
    public static final String QUERY_MONGO_AUDIT = "queryMongoAudit";

    public static final ClassPathResource RESOURCE = new ClassPathResource("input/virtual_cards_import.csv");
    public static final String LINES_TO_SKIP = "1";
    public static final String DELIMITER = ",";
    public static final String QUOTE_CHARACTER = "\"";
    public static final List<String> COLUMNS = Arrays.asList(
            "actionType", "transactionDate", "cardRef",
            "state", "brandCode", "holderNameRaw",
            "balanceTxt", "currencyCode", "internationalFlag",
            "expiryTxt", "cvvTxt", "pinTxt",
            "maxDailyTxTxt", "maxTxAmountTxt", "issuingCountryCode",
            "notesRaw", "providerCode", "providerFeePctTxt",
            "providerDailyLimitTxt", "providerPriorityTxt", "providerState",
            "providerCountry", "txKind", "txAmountTxt", "txRequestRef"
    );
    public static final BeanWrapperFieldSetMapper<CsvFileRow> FIELD_SET_MAPPER = new BeanWrapperFieldSetMapper<>() {{
        setTargetType(CsvFileRow.class);
    }};

}
