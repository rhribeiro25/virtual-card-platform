package br.com.rhribeiro25.virtual_card_platform.domain.service.batch.strategy;

import br.com.rhribeiro25.virtual_card_platform.domain.model.enums.ActionType;

public interface ActionTypeStrategy<R, P> {

    boolean isSupportedActionType(ActionType type);
    Class<R> isSupportedModelType();
    R execute(P input);
}
