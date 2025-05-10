package ru.panyukovnn.videoretellingbot.model.content;

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
@Table(name = "content_rate")
public class ContentRate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Идентификатор контента
     */
    private UUID contentId;
    /**
     * Оценка
     */
    private Integer rate;
    /**
     * Промт
     */
    private String prompt;
    /**
     * Обоснование
     */
    private String grounding;
    /**
     * Тег оценки
     */
    private String tag;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ContentRate content = (ContentRate) o;
        return Objects.equals(id, content.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
