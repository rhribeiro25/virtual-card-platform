package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionTypeStrategyFactory<R, P> {

    private final List<ActionTypeStrategy<R, P>> strategies;

    public ActionTypeStrategy<R, P> getStrategy(ActionType type, Class<R> clazz) {
        return strategies.stream()
                .filter(s -> s.isSupportedActionType(type))
                .filter(s -> s.isSupportedModelType().equals(clazz))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Type unsupported: " + type)
                );
    }
}