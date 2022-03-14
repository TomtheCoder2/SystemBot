package website.repositories;

import org.springframework.data.repository.CrudRepository;
import website.entity.Staff;

import java.util.List;

public interface AdminRepository extends CrudRepository<Staff, Integer> {
    List<Staff> findByName(String name);
}
