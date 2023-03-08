package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/*@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")*/
public class Comment {

    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotEmpty
    private String text;
    @ManyToOne()
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;
    @ManyToOne()
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;            // автор комментария;
    private LocalDateTime created;  // дата создания комментария.*/
}
