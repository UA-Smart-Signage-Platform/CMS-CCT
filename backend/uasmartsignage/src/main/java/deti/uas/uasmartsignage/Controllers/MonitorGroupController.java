package deti.uas.uasmartsignage.Controllers;

import java.util.List;

import deti.uas.uasmartsignage.Models.MonitorsGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import deti.uas.uasmartsignage.Services.MonitorGroupService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/groups")
public class MonitorGroupController {

    private MonitorGroupService monitorGroupService;

    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        List<MonitorsGroup> monitorsGroups = (List<MonitorsGroup>) monitorGroupService.getAllGroups();
        return new ResponseEntity<>(monitorsGroups, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(Long id) {
        MonitorsGroup monitorsGroup = monitorGroupService.getGroupById(id);
        if (monitorsGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(monitorsGroup, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getGroupByName(String name) {
        MonitorsGroup monitorsGroup = monitorGroupService.getGroupByName(name);
        if (monitorsGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(monitorsGroup, HttpStatus.OK);
    }

    @GetMapping("/{id}/screens")
    public ResponseEntity<?> getScreensByGroup(Long id) {
        MonitorsGroup monitorsGroup = monitorGroupService.getGroupById(id);
        if (monitorsGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(monitorsGroup.getMonitors(), HttpStatus.OK);
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<?> getTemplateByGroup(Long id) {
        MonitorsGroup monitorsGroup = monitorGroupService.getGroupById(id);
        if (monitorsGroup == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(monitorsGroup.getTemplateGroup(), HttpStatus.OK);
    }
    
}
