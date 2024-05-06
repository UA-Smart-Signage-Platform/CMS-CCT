package deti.uas.uasmartsignage.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String type;

    @ManyToOne
    @JoinColumn(name = "widget_id")
    private Widget widget;
    
    @ElementCollection
    @CollectionTable(name = "Content_Options", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "option")
    private List<String> options;

}
