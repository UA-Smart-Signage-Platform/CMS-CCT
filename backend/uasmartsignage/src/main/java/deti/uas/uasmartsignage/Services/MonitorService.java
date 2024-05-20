package deti.uas.uasmartsignage.Services;

import deti.uas.uasmartsignage.Models.MonitorsGroup;
import deti.uas.uasmartsignage.Models.Severity;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import deti.uas.uasmartsignage.Repositories.MonitorGroupRepository;
import deti.uas.uasmartsignage.Repositories.MonitorRepository;
import deti.uas.uasmartsignage.Models.Monitor;
import java.util.List;

@Service 
public class MonitorService {

    private final MonitorRepository monitorRepository;

    private final MonitorGroupRepository monitorGroupRepository;

    private final TemplateGroupService templateGroupService;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(MonitorService.class);

    private final String source = this.getClass().getSimpleName();

    private final LogsService logsService;

    private static final String ADDLOGERROR = "Failed to add log to InfluxDB";
    private static final String ADDLOGSUCCESS = "Added log to InfluxDB: {}";


    public MonitorService(MonitorRepository monitorRepository, MonitorGroupRepository monitorGroupRepository, LogsService logsService, TemplateGroupService templateGroupService){
        this.monitorRepository = monitorRepository;
        this.monitorGroupRepository = monitorGroupRepository;
        this.logsService = logsService;
        this.templateGroupService = templateGroupService;
    }

    /**
     * Retrieves and returns a Monitor by its ID.
     *
     * @param id The ID of the Monitor to retrieve.
     * @return The Monitor with the specified ID.
     */
    public Monitor getMonitorById(Long id) {
        String operation = "getMonitorById";
        String description = "Getting monitor by id " + id;

        Monitor monitor = monitorRepository.findById(id).orElse(null);

        if (monitor == null) {
            return null;
        }

        boolean online = logsService.keepAliveIn10min(monitor);
        monitor.setOnline(online);

        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }

        return monitor;
    }

    /**
     * Saves a Monitor to the database.
     *
     * @param monitor The Monitor to save.
     * @return The saved Monitor.
     */
    public Monitor saveMonitor(Monitor monitor) {
        String operation = "saveMonitor";
        String description = "Saving monitor " + monitor.getName();

        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }

        return monitorRepository.save(monitor);
    }

    /**
     * Deletes a Monitor from the database.
     *
     * @param id The ID of the Monitor to delete.
     */
    public void deleteMonitor(Long id) {
        String operation = "deleteMonitor";
        String description = "Deleting monitor by id " + id;
        
        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }

        monitorRepository.deleteById(id);
    }
    
    /**
     * Updates a Monitor in the database.
     *
     * @param id The ID of the Monitor to update.
     * @param monitor The Monitor to update.
     * @return The updated Monitor.
     */
    public Monitor updateMonitor(Long id, Monitor monitor) {
        String operation = "updateMonitor";
        String description = "Updating monitor by id " + id;

        Monitor monitorById = monitorRepository.getReferenceById(id);
        MonitorsGroup group = monitorById.getGroup();
        monitorById.setName(monitor.getName());
        monitorById.setGroup(monitor.getGroup());
        Monitor returnMonitor = monitorRepository.save(monitorById);

        if (group.isMadeForMonitor()) {
            if (group.getId() == monitorById.getGroup().getId()){
                group.setName(monitor.getName());
                monitorGroupRepository.save(group);
            }
            else{
                if (group.getTemplateGroups().isEmpty()){
                    monitorGroupRepository.deleteById(group.getId());
                } else {
                    for (int i = 0; i < group.getTemplateGroups().size(); i++) {
                        templateGroupService.deleteGroup(group.getTemplateGroups().get(i).getId());
                    }
                    monitorGroupRepository.deleteById(group.getId());
                }
            }
        }

        // Send all template groups etc to the monitor group
        MonitorsGroup monitorGroup = monitorGroupRepository.findById(monitor.getGroup().getId()).orElse(null);
        templateGroupService.sendAllSchedulesToMonitorGroup(monitorGroup, true);

        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }

        return returnMonitor;
    }

    /**
     * Retrieves and returns all Monitors.
     *
     * @return A list of all Monitors.
     */
    public Monitor updatePending(Long id,boolean pending){
        String operation = "updatePending";
        String description = "Updating monitor pending by id " + id;

        Monitor monitorById = monitorRepository.getReferenceById(id);
        monitorById.setPending(pending);

        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }

        return monitorRepository.save(monitorById);
    }
    
    /**
     * Retrieves and returns all Monitors.
     *
     * @return A list of all Monitors.
     */
    public List<Monitor> getAllMonitorsByPending(boolean pending) {
        String operation = "getAllMonitorsByPending";
        String description = "Getting all monitors by pending " + pending;

        List<Monitor> monitors = monitorRepository.findByPending(pending);

        for (Monitor monitor : monitors) {
            boolean online = logsService.keepAliveIn10min(monitor);
            monitor.setOnline(online);
        }

        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }
        return monitors;
    }

    /**
     * Retrieves and returns all Monitors.
     *
     * @return A list of all Monitors.
     */
    public List<Monitor> getMonitorsByGroup(long groupId) {
        String operation = "getMonitorsByGroup";
        String description = "Getting all monitors by group " + groupId;

        List<Monitor> monitors = monitorRepository.findByPendingAndGroup_Id(false,groupId);

        for (Monitor monitor : monitors) {
            boolean online = logsService.keepAliveIn10min(monitor);
            monitor.setOnline(online);
        }


        if (!logsService.addBackendLog(Severity.INFO, source, operation, description)) {
            logger.error(ADDLOGERROR);
        }
        else {
            logger.info(ADDLOGSUCCESS, description);
        }
        
        return monitors;
    }

        /**
     * Retrieves and returns all Monitors.
     *
     * @return A list of all Monitors.
     */
    public Monitor getMonitorByUUID(String uuid) {
        return monitorRepository.findByUuid(uuid);
    }

}
