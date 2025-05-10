package ru.panyukovnn.videoretellingbot.model.event;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processing_events")
public class ProcessingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Тип события
     */
    @Enumerated(EnumType.STRING)
    private ProcessingEventType type;
    /**
     * Идентификатор сущности
     * Может быть как материал, так и пересказ, в зависимости от текущего этапа
     */
    private UUID baseId;
    /**
     * Статус обработки
     */
    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProcessingEvent processingEvent = (ProcessingEvent) o;
        return Objects.equals(id, processingEvent.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
