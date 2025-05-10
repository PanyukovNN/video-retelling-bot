package ru.panyukovnn.videoretellingbot.model.retelling;

import jakarta.persistence.*;
import lombok.*;
import ru.panyukovnn.videoretellingbot.model.AuditableEntity;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "retellings")
public class Retelling extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор контента
     */
    private UUID contentId;
    /**
     * Промт
     */
    private String prompt;
    /**
     * Модель
     */
    private String aiModel;
    /**
     * Пересказ
     */
    private String retelling;
    /**
     * Тег
     */
    private String tag;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Retelling content = (Retelling) o;
        return Objects.equals(id, content.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
