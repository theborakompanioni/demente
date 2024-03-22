package org.tbk.nostr.demented.domain.event;

public final class EventEntityEvents {
    private EventEntityEvents() {
        throw new UnsupportedOperationException();
    }

    public record CreatedEvent(EventEntity.EventEntityId eventId) {
    }

    public record MarkDeletedEvent(EventEntity.EventEntityId eventId) {
    }
}
