package deti.uas.uasmartsignage.repositoryTests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import deti.uas.uasmartsignage.Models.Template;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import deti.uas.uasmartsignage.Models.Widget;
import deti.uas.uasmartsignage.Repositories.WidgetRepository;
import deti.uas.uasmartsignage.Models.Content;
import deti.uas.uasmartsignage.Models.TemplateWidget;
import deti.uas.uasmartsignage.Models.Template;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WidgetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WidgetRepository repository;

    @Test
    void whenFindAll_thenReturnAllWidgets(){
        Content content = new Content();
        content.setName("content");
        content.setType("type");
        content.setDescription("description");
        entityManager.persistAndFlush(content);

        Template template = new Template();
        template.setName("template");
        entityManager.persistAndFlush(template);

        /*TemplateWidget templatewidget = new TemplateWidget();
        templatewidget.setTop(1);
        templatewidget.setLeftPosition(1);
        templatewidget.setWidth(1);
        templatewidget.setHeight(1);
        templatewidget.setTemplate(template);
        entityManager.persistAndFlush(templatewidget);*/

        Widget widget1 = new Widget();
        widget1.setName("widget1");
        widget1.setPath("path");
        widget1.setContents(List.of(content));
        //widget1.setTemplateWidgets(List.of(templatewidget));
        entityManager.persistAndFlush(widget1);

        Widget widget2 = new Widget();
        widget2.setName("widget2");
        widget2.setPath("path");
        widget2.setContents(List.of(content));
        entityManager.persistAndFlush(widget2);

        List<Widget> found = repository.findAll();

        assertThat(found).hasSize(2).extracting(Widget::getName).contains("widget1", "widget2");
    }

    @Test
    void whenFindById_thenReturnWidget() {
        Content content = new Content();
        content.setName("content");
        content.setType("type");
        content.setDescription("description");
        entityManager.persistAndFlush(content);

        Template template = new Template();
        template.setName("template");
        entityManager.persistAndFlush(template);

        /*TemplateWidget templatewidget = new TemplateWidget();
        templatewidget.setTop(1);
        templatewidget.setLeftPosition(1);
        templatewidget.setWidth(1);
        templatewidget.setHeight(1);
        templatewidget.setTemplate(template);
        entityManager.persistAndFlush(templatewidget);*/

        Widget widget = new Widget();
        widget.setName("widget");
        widget.setPath("path");
        widget.setContents(List.of(content));
        //widget.setTemplateWidgets(List.of(templatewidget));
        entityManager.persistAndFlush(widget);

        Widget found = repository.findById(widget.getId()).get();

        assertThat(found).isEqualTo(widget);
    }

    @Test
    void whenFindByInvalidId_thenReturnEmpty() {
        Widget found = repository.findById(10L);
        assertThat(found).isNull();
    }

    @Test
    void whenDeleteWidget_thenReturnEmpty() {
        Content content = new Content();
        content.setName("content");
        content.setType("type");
        content.setDescription("description");
        entityManager.persistAndFlush(content);

        Template template = new Template();
        template.setName("template");
        entityManager.persistAndFlush(template);

        /*TemplateWidget templatewidget = new TemplateWidget();
        templatewidget.setTop(1);
        templatewidget.setLeftPosition(1);
        templatewidget.setWidth(1);
        templatewidget.setHeight(1);
        templatewidget.setTemplate(template);
        entityManager.persistAndFlush(templatewidget);*/

        Widget widget = new Widget();
        widget.setName("widget");
        widget.setPath("path");
        widget.setContents(List.of(content));
        //widget.setTemplateWidgets(List.of(templatewidget));
        entityManager.persistAndFlush(widget);

        repository.delete(widget);
        Widget found = repository.findById(widget.getId()).orElse(null);

        assertThat(found).isNull();
    }
}
