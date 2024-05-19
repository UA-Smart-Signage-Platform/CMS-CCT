package deti.uas.uasmartsignage.serviceTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import deti.uas.uasmartsignage.Models.Monitor;
import deti.uas.uasmartsignage.Models.MonitorsGroup;
import deti.uas.uasmartsignage.Repositories.MonitorRepository;
import deti.uas.uasmartsignage.Services.LogsService;
import deti.uas.uasmartsignage.Services.MonitorService;
import deti.uas.uasmartsignage.Services.TemplateGroupService;
import deti.uas.uasmartsignage.Repositories.MonitorGroupRepository;

@ExtendWith(MockitoExtension.class)
class MonitorServiceTest {

    @Mock
    private MonitorRepository repository;

    @Mock
    private LogsService logsService;

    @InjectMocks
    private MonitorService service;

    @Mock
    private TemplateGroupService templateGroupService;

    @Mock
    private MonitorGroupRepository monitorsGroupRepository;

    @Test void
    getMonitorByIdTestReturnsMonitor(){
        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor.setPending(false);
        when(repository.findById(1L)).thenReturn(Optional.of(monitor));

        Monitor retu = service.getMonitorById(1L);

        assertThat(retu.getName()).isEqualTo("monitor");
    }

    @Test void
    whenServiceSaveThenRepositorySave(){
        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor.setPending(false);
        when(repository.save(monitor)).thenReturn(monitor);

        Monitor retu = service.saveMonitor(monitor);

        assertThat(retu.getName()).isEqualTo("monitor");
    }

    @Test void
    UpdateMonitorServiceTest(){
        MonitorsGroup group1 = new MonitorsGroup();
        group1.setId(1L);
        group1.setName("group1");

        MonitorsGroup group2 = new MonitorsGroup();
        group2.setId(2L);
        group2.setName("group2");

        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setGroup(group1);
        monitor.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor.setPending(false);

        Monitor monitorUpdated = new Monitor();
        monitorUpdated.setGroup(group2);
        monitorUpdated.setName("Name");
        monitorUpdated.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitorUpdated.setPending(false);

        when(repository.save(monitor)).thenReturn(monitor);
        when(repository.getReferenceById(1L)).thenReturn(monitor);

        Monitor retu = service.updateMonitor(1L, monitorUpdated);

        assertThat(retu.getName()).isEqualTo("Name");
        assertThat(retu.getGroup().getName()).isEqualTo("group2");

    }
    
    @Test void
    whenUpdatePendingInServiceThenPendingIsUpdated(){
        MonitorsGroup group2 = new MonitorsGroup();
        group2.setId(2L);
        group2.setName("group2");

        Monitor monitor = new Monitor();
        monitor.setId(1L);
        monitor.setName("monitor");
        monitor.setGroup(group2);
        monitor.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor.setPending(true);

        when(repository.save(monitor)).thenReturn(monitor);
        when(repository.getReferenceById(1L)).thenReturn(monitor);

        Monitor retu = service.updatePending(1L, false);

        assertFalse(retu.isPending());
    }

    @Test void
    WhenServiceGetAllMonitorsByPendingThenRepositoryGetAllMonitorsByPending(){
        MonitorsGroup group1 = new MonitorsGroup();
        group1.setId(1L);
        group1.setName("group1");

        MonitorsGroup group2 = new MonitorsGroup();
        group2.setId(2L);
        group2.setName("group2");

        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setGroup(group1);
        monitor.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor.setPending(false);

        Monitor monitor2 = new Monitor();
        monitor2.setGroup(group2);
        monitor2.setName("Name");
        monitor2.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor2.setPending(false);

        when(repository.findByPending(false)).thenReturn(Arrays.asList(monitor,monitor2));

        List<Monitor> monitors = service.getAllMonitorsByPending(false);

        assertThat(monitors).hasSize(2).extracting(Monitor::getName).contains("monitor","Name");
    }

    @Test void
    whenServiceGetMonitorsByGroupThenRepositoryGetMonitorsByGroupNotPending(){
        MonitorsGroup group1 = new MonitorsGroup();
        group1.setId(1L);
        group1.setName("group2");

        Monitor monitor = new Monitor();
        monitor.setName("monitor");
        monitor.setGroup(group1);
        monitor.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor.setPending(false);

        Monitor monitor2 = new Monitor();
        monitor2.setGroup(group1);
        monitor2.setName("Name");
        monitor2.setUuid("1c832f8c-1f6b-4722-a693-a3956b0cbbc9");
        monitor2.setPending(false);

        when(repository.findByPendingAndGroup_Id(false, 1L)).thenReturn(Arrays.asList(monitor,monitor2));

        List<Monitor> monitors = service.getMonitorsByGroup(1L);

        assertThat(monitors).hasSize(2).extracting(Monitor::getName).contains("monitor","Name");
    }

}
