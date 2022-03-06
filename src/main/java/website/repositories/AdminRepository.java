package website.repositories;

import org.springframework.data.repository.CrudRepository;
import website.entity.Admin;

import java.util.List;

public interface AdminRepository extends CrudRepository<Admin, Integer> {
    List<Admin> findByName(String name);
}
