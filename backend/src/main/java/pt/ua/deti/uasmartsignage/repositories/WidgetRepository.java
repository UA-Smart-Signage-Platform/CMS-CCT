package pt.ua.deti.uasmartsignage.repositories;

import pt.ua.deti.uasmartsignage.models.Widget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetRepository extends MongoRepository<Widget, String> {
    Widget findByName(String name);
}
