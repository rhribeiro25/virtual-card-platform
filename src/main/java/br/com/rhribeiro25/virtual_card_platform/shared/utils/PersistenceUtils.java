package br.com.rhribeiro25.virtual_card_platform.shared.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PersistenceUtils {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
