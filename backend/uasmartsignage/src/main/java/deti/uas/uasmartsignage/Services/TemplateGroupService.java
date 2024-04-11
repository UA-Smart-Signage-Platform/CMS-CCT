package deti.uas.uasmartsignage.Services;

import deti.uas.uasmartsignage.Models.Content;
import deti.uas.uasmartsignage.Models.Monitor;
import deti.uas.uasmartsignage.Models.MonitorsGroup;
import deti.uas.uasmartsignage.Models.Template;
import deti.uas.uasmartsignage.Models.TemplateGroup;
import deti.uas.uasmartsignage.Models.TemplateWidget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import deti.uas.uasmartsignage.Repositories.ContentRepository;
import deti.uas.uasmartsignage.Repositories.TemplateGroupRepository;

@Service
public class TemplateGroupService {

    @Autowired
    private TemplateGroupRepository templateGroupRepository;

    @Autowired
    private ContentService contentService;

    private final Logger logger = LoggerFactory.getLogger(TemplateGroupRepository.class);

    public TemplateGroup getGroupById(Long id) {
        return templateGroupRepository.findById(id).orElse(null);
    }

    public TemplateGroup saveGroup(TemplateGroup templateGroup) {
        return templateGroupRepository.save(templateGroup);
    }

    public void deleteGroup(Long id) {
        templateGroupRepository.deleteById(id);
    }

    public Iterable<TemplateGroup> getAllGroups() {
        return templateGroupRepository.findAll();
    }

    public TemplateGroup updateTemplateGroup(Long id, TemplateGroup templateGroup) {
        TemplateGroup templateGroupById = templateGroupRepository.findById(id).orElse(null);
        if (templateGroupById == null) {
            return null;
        }
        templateGroupById.setTemplate(templateGroup.getTemplate());
        templateGroupById.setTemplate(templateGroup.getTemplate());
        templateGroupById.setContent(templateGroup.getContent());
        return templateGroupRepository.save(templateGroupById);
    }

    /**
     * Takes a template and the contents to place in the widgets and returns the full html
     * 
     * @param template The Template containing the list of widgets
     * @param contents A Map that contains the ids of Contents and the respective variables to fill in the widgets
     * @param monitorWidth The screen width to be considered when calculating widget sizes
     * @param monitorHeight The screen height to be considered when calculating widget sizes
     * @return The created HTML in a String, or {@code null} if creation fails.
     */
    public String generateHTML(Template template, Map<Integer, String> contents, int monitorWidth, int monitorHeight) {

        List<TemplateWidget> widgets = template.getTemplateWidgets();
        String filePath = "static/base.html";

        try {
            
            // get a base html to add the widgets to
            File baseFile = ResourceUtils.getFile("classpath:" + filePath);
            Document doc = Jsoup.parse(baseFile, "UTF-8");

            for (TemplateWidget widget : widgets) {
                // fill in the variables inside the widgets
                // using the values inside "contents"
                String widgetHTML = loadWidget(widget, contents, monitorWidth, monitorHeight);
                
                if(widgetHTML == null){
                    continue;
                }   

                // add the html of the widget to the main body
                List<Element> widgetElements = Jsoup.parseBodyFragment(widgetHTML).body().children();
                for(Element child : widgetElements)
                    doc.body().appendChild(child);
            }

            return doc.html();

        } catch (FileNotFoundException e) {
            logger.error(String.format("Could not find file 'resources/%s'", filePath));
            return null;
        } catch (IOException e) {
            logger.error(String.format("Jsoup could not parse the file 'resources/%s'", filePath));
            return null;
        }

    }

    /**
     * Takes a widget and fills in the [[spaces]] with the Contents
     * 
     * @param widget The Widget that we want to complete
     * @param contents A Map that contains the ids of Contents and the respective variables to fill in the widgets
     * @param monitorWidth The screen width to be considered when calculating widget sizes
     * @param monitorHeight The screen height to be considered when calculating widget sizes
     * @return The created HTML in a String, or {@code null} if creation fails.
     */
    private String loadWidget(TemplateWidget widget, Map<Integer, String> contents, int monitorWidth, int monitorHeight) {

        try {
            File widgetFile = ResourceUtils.getFile("classpath:" + widget.getWidget().getPath());
            String widgetHTML = new String(Files.readAllBytes(Paths.get(widgetFile.toURI())));

            widgetHTML = widgetHTML
                    .replace("[[top]]", String.valueOf(monitorHeight * widget.getTop() / 100))
                    .replace("[[left]]", String.valueOf(monitorWidth * widget.getLeftPosition() / 100))
                    .replace("[[width]]", String.valueOf(monitorWidth * widget.getWidth() / 100))
                    .replace("[[height]]", String.valueOf(monitorHeight * widget.getHeight() / 100));

            // go through each of the contents
            // and fill in the variables
            for(int contentID : contents.keySet()){
                String value = contents.get(contentID);
                Content content = contentService.getContentById((long)contentID);
                widgetHTML = widgetHTML.replace("[[" + content.getName() + "]]", value);
            }

            return widgetHTML;

        } catch (FileNotFoundException e) {
            logger.error(String.format("Could not find file 'resources/%s'", widget.getWidget().getPath()));
            return null;
        } catch (IOException e) {
            logger.error(String.format("Could not read file 'resources/%s'", widget.getWidget().getPath()));
            return null;
        }

    }

}
