package deti.uas.uasmartsignage.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import deti.uas.uasmartsignage.Models.*;
import deti.uas.uasmartsignage.Services.*;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;

import deti.uas.uasmartsignage.Repositories.TemplateGroupRepository;
import deti.uas.uasmartsignage.Services.LogsService;
import deti.uas.uasmartsignage.Services.TemplateGroupService;
import deti.uas.uasmartsignage.Services.TemplateService;
import deti.uas.uasmartsignage.Services.MonitorGroupService;
import deti.uas.uasmartsignage.Models.Template;
import deti.uas.uasmartsignage.Models.MonitorsGroup;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TemplateGroupServiceTest {


    @Mock
    private TemplateGroupRepository repository;

    @Mock
    private LogsService logsService;

    @Mock
    private TemplateService templateService;

    @Mock
    private MonitorGroupService groupService;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private TemplateWidgetService templateWidgetService;

    @InjectMocks
    private TemplateGroupService service;




    @Test
    void testGetTemplateGroupByIdReturnsTemplateGroup(){
        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setPending(false);
        monitor.setWidth(1);
        monitor.setHeight(1);

        Monitor monitor1 = new Monitor();
        monitor1.setName("monitor1");
        monitor1.setPending(false);
        monitor1.setWidth(12);
        monitor1.setHeight(12);

        Schedule schedule = new Schedule();
        schedule.setPriority(1);

        MonitorsGroup group = new MonitorsGroup();
        group.setName("group1");
        group.setMonitors(List.of(monitor,monitor1));

        Widget widget = new Widget();
        widget.setName("widget1");

        TemplateWidget templateWidget = new TemplateWidget();
        templateWidget.setWidget(widget);

        TemplateWidget templateWidget1 = new TemplateWidget();
        templateWidget1.setWidget(widget);

        Template template = new Template();
        template.setName("template1");
        template.setTemplateWidgets(List.of(templateWidget,templateWidget1));


        TemplateGroup templateGroup = new TemplateGroup();
        templateGroup.setGroup(group);
        templateGroup.setTemplate(template);
        templateGroup.setSchedule(schedule);
        when(repository.findById(1L)).thenReturn(Optional.of(templateGroup));

        TemplateGroup retrievedTemplateGroup = service.getGroupById(1L);

        assertThat(retrievedTemplateGroup.getGroup()).isEqualTo(group);
    }

    @Test
    void testGetTemplateGroupByGroupID(){
        Schedule schedule = new Schedule();
        schedule.setPriority(1);

        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setPending(false);
        monitor.setWidth(1);
        monitor.setHeight(1);

        Monitor monitor1 = new Monitor();
        monitor1.setName("monitor1");
        monitor1.setPending(false);
        monitor1.setWidth(12);
        monitor1.setHeight(12);

        MonitorsGroup group = new MonitorsGroup();
        group.setName("group1");
        group.setMonitors(List.of(monitor,monitor1));

        Widget widget = new Widget();
        widget.setName("widget1");

        TemplateWidget templateWidget = new TemplateWidget();
        templateWidget.setWidget(widget);

        TemplateWidget templateWidget1 = new TemplateWidget();
        templateWidget1.setWidget(widget);

        Template template = new Template();
        template.setName("template1");
        template.setTemplateWidgets(List.of(templateWidget,templateWidget1));


        TemplateGroup templateGroup = new TemplateGroup();
        templateGroup.setGroup(group);
        templateGroup.setTemplate(template);
        templateGroup.setSchedule(schedule);
        when(repository.findByGroupId(group.getId())).thenReturn(templateGroup);

        TemplateGroup get_template = service.getTemplateGroupByGroupID(group.getId());

        assertThat(get_template.getGroup()).isEqualTo(group);
    }


    @Test
    void testSaveTemplateGroup() throws MqttException, JsonProcessingException {

        //MockitoAnnotations.openMocks(this);

        // Mock MqttConfig.getInstance() to return the mock mqttClient
        //when(mqttConfig.getInstance()).thenReturn(mqttClient);

        // Mock any necessary behavior of mqttClient
        //when(mqttClient.isConnected()).thenReturn(true);


        //when(templateMessage.getMethod()).thenReturn("TEMPLATE");
        //doNothing().when(mqttClient).publish(anyString(), any(MqttMessage.class));

        Schedule schedule1 = new Schedule();
        schedule1.setFrequency(7);
        schedule1.setEndDate(LocalDate.parse("2024-04-21"));
        schedule1.setStartDate(LocalDate.parse("2024-04-21"));
        schedule1.setStartTime(LocalTime.parse("08:30"));
        schedule1.setEndTime(LocalTime.parse("18:30"));
        schedule1.setPriority(1);


        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setPending(false);
        monitor.setWidth(1);
        monitor.setHeight(1);

        Monitor monitor1 = new Monitor();
        monitor1.setName("monitor1");
        monitor1.setPending(false);
        monitor1.setWidth(12);
        monitor1.setHeight(12);

        MonitorsGroup group = new MonitorsGroup();
        group.setName("group1");
        group.setMonitors(List.of(monitor,monitor1));

        Content content = new Content();
        content.setName("Content1");
        content.setType("text");

        Widget widget = new Widget();
        widget.setName("widget1");
        widget.setId(1L);
        widget.setContents(List.of(content));
        widget.setPath("path");

        TemplateWidget templateWidget = new TemplateWidget();
        templateWidget.setId(100L);
        templateWidget.setWidget(widget);
        templateWidget.setZIndex(1);
        templateWidget.setTop(1);
        templateWidget.setLeftPosition(1);
        templateWidget.setWidth(1);
        templateWidget.setHeight(1);


        TemplateWidget templateWidget1 = new TemplateWidget();
        templateWidget1.setId(200L);
        templateWidget1.setWidget(widget);
        templateWidget1.setZIndex(2);
        templateWidget1.setTop(2);
        templateWidget1.setLeftPosition(2);
        templateWidget1.setWidth(2);
        templateWidget1.setHeight(2);

        Template template = new Template();
        template.setName("template1");
        template.setTemplateWidgets(List.of(templateWidget,templateWidget1));

        TemplateGroup templateGroup = new TemplateGroup();
        templateGroup.setGroup(group);
        templateGroup.setTemplate(template);
        templateGroup.setSchedule(schedule1);
        templateGroup.setContent(Map.of(1,"Content1"));

        group.setTemplateGroups(List.of(templateGroup));

        when(templateService.getTemplateById(templateGroup.getTemplate().getId())).thenReturn(template);
        when(groupService.getGroupById(templateGroup.getGroup().getId())).thenReturn(group);
        when(repository.save(templateGroup)).thenReturn(templateGroup);
        when(scheduleService.saveSchedule(schedule1)).thenReturn(schedule1);
        when(templateWidgetService.getTemplateWidgetById(1L)).thenReturn(templateWidget);

        TemplateGroup saved_template = service.saveGroup(templateGroup);

        assertThat(saved_template).isEqualTo(templateGroup);
    }

    @Test
    void testDeleteTemplateGroup(){
        Schedule schedule = new Schedule();
        schedule.setPriority(1);

        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setPending(false);
        monitor.setWidth(1);
        monitor.setHeight(1);

        Monitor monitor1 = new Monitor();
        monitor1.setName("monitor1");
        monitor1.setPending(false);
        monitor1.setWidth(12);
        monitor1.setHeight(12);

        MonitorsGroup group = new MonitorsGroup();
        group.setName("group1");
        group.setMonitors(List.of(monitor,monitor1));

        Widget widget = new Widget();
        widget.setName("widget1");

        TemplateWidget templateWidget = new TemplateWidget();
        templateWidget.setWidget(widget);

        TemplateWidget templateWidget1 = new TemplateWidget();
        templateWidget1.setWidget(widget);

        Template template = new Template();
        template.setName("template1");
        template.setTemplateWidgets(List.of(templateWidget,templateWidget1));


        TemplateGroup templateGroup = new TemplateGroup();
        templateGroup.setGroup(group);
        templateGroup.setTemplate(template);
        templateGroup.setSchedule(schedule);

        service.deleteGroup(1L);
        assertFalse(repository.existsById(1L));

        //verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateTemplateGroup(){
        Schedule schedule = new Schedule();
        schedule.setPriority(1);

        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setPending(false);
        monitor.setWidth(1);
        monitor.setHeight(1);

        Monitor monitor1 = new Monitor();
        monitor1.setName("monitor1");
        monitor1.setPending(false);
        monitor1.setWidth(12);
        monitor1.setHeight(12);

        MonitorsGroup group = new MonitorsGroup();
        group.setName("group1");
        group.setMonitors(List.of(monitor,monitor1));

        Widget widget = new Widget();
        widget.setName("widget1");

        TemplateWidget templateWidget = new TemplateWidget();
        templateWidget.setWidget(widget);

        TemplateWidget templateWidget1 = new TemplateWidget();
        templateWidget1.setWidget(widget);

        Template template = new Template();
        template.setName("template1");
        template.setTemplateWidgets(List.of(templateWidget,templateWidget1));


        TemplateGroup templateGroup = new TemplateGroup();
        templateGroup.setGroup(group);
        templateGroup.setTemplate(template);
        templateGroup.setContent(Map.of(1,"Content1"));
        templateGroup.setSchedule(schedule);

        when(repository.findById(1L)).thenReturn(Optional.of(templateGroup));
        when(repository.save(templateGroup)).thenReturn(templateGroup);

        TemplateGroup updated_template = service.updateTemplateGroup(1L, templateGroup);

        assertThat(updated_template).isEqualTo(templateGroup);
    }
}
