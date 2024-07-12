package pt.ua.deti.uasmartsignage.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import pt.ua.deti.uasmartsignage.models.embedded.TemplateWidget;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "templates")
public class Template {

    @Id
    private String id;
    private String name;
    private List<TemplateWidget> widgets;
    
}

