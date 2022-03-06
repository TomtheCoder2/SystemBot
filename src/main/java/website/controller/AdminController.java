package website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import website.entity.Admin;
import website.repositories.AdminRepository;

import java.util.List;

import static systembot.SystemBot.api;

@RestController
@RequestMapping("/admins/")
public class AdminController {

    @Autowired AdminRepository adminRepository;

    @GetMapping
    public Iterable findAll() {
        return adminRepository.findAll();
    }

    @GetMapping("/name/{name}")
    public List findByTitle(@PathVariable String name) {
        return adminRepository.findByName(name);
    }

    @GetMapping("/{id}")
    public Admin findById(@PathVariable Integer id) {
        return adminRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT));
    }

    @GetMapping("/newAdmin")
    public Admin createAdmin(@RequestParam(value = "id") Long id, @RequestParam(value = "desc") String desc) {
        Admin newAdmin;
        try {
            newAdmin = new Admin(api.getUserById(id).get(), desc);
            adminRepository.save(newAdmin);
        } catch (Exception e) {
            System.out.println("There was an error while creating new Admin (" + id + "):");
            e.printStackTrace();
            return null;
        }
        return newAdmin;
    }

    @GetMapping("/updateAdmin")
    public Admin updateAdmin(@RequestBody Admin admin) {
        try {
            adminRepository.save(admin);
        } catch (Exception e) {
            System.out.println("There was an error while creating new Admin (" + admin.getName() + "):");
            e.printStackTrace();
            return null;
        }
        return admin;
    }
}