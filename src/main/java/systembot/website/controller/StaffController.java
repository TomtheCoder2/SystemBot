package systembot.website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import systembot.website.entity.Staff;
import systembot.website.repositories.StaffRepository;

import java.util.List;

import static systembot.SystemBot.api;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    public @Autowired StaffRepository staffRepository;

    @GetMapping
    public Iterable findAll() {
        return staffRepository.findAll();
    }

    @GetMapping("/name/{name}")
    public List findByTitle(@PathVariable String name) {
        return staffRepository.findByName(name);
    }

    @GetMapping("/{id}")
    public Staff findById(@PathVariable Integer id) {
        return staffRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT));
    }

    @GetMapping("/newStaff")
    public Staff createAdmin(@RequestParam(value = "id") Long id, @RequestParam(value = "desc") String desc, @RequestParam(value = "rank") String rank) {
        Staff newStaff;
        try {
            newStaff = new Staff(api.getUserById(id).get(), desc, rank);
            staffRepository.save(newStaff);
        } catch (Exception e) {
            System.out.println("There was an error while creating new Staff (" + id + "):");
            e.printStackTrace();
            return null;
        }
        return newStaff;
    }

    @GetMapping("/newStaffs")
    public Iterable createStaffs(@RequestBody Iterable<Staff> staffList) {
        for (Staff staff : staffList) {
            try {
                staffRepository.save(staff);
            } catch (Exception e) {
                System.out.println("There was an error while creating new Staff (" + staff.getId() + ", " + staff.getName() + "):");
                e.printStackTrace();
                return null;
            }
        }
        return staffList;
    }

    @GetMapping("/updateStaff")
    public Staff updateAdmin(@RequestBody Staff staff) {
        try {
            staffRepository.save(staff);
        } catch (Exception e) {
            System.out.println("There was an error while creating new Staff (" + staff.getName() + "):");
            e.printStackTrace();
            return null;
        }
        return staff;
    }
}