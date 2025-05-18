package ru.panyukovnn.videoretellingbot.model.event;

import jakarta.persistence.*;
import lombok.*;
import ru.panyukovnn.videoretellingbot.model.ConveyorType;
import ru.panyukovnn.videoretellingbot.model.ConveyorTag;

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
     * Идентификатор материала
     */
    private UUID contentId;
    /**
     * Идентификатор пересказа
     * Указывается на этапе пересказа
     */
    private UUID retellingId;
    /**
     * Тип конвейера
     */
    @Enumerated(EnumType.STRING)
    private ConveyorType conveyorType;
    /**
     * Тег, по которому конкретизируется конвейер
     */
    @Enumerated(EnumType.STRING)
    private ConveyorTag conveyorTag;

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
