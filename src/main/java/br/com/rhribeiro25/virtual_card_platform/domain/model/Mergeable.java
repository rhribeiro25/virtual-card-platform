package br.com.rhribeiro25.virtual_card_platform.domain.model;

public interface Mergeable<T> {
    void mergeFrom(T incoming);
}
