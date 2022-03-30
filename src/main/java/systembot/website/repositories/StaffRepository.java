package systembot.website.repositories;

import org.springframework.data.repository.CrudRepository;
import systembot.website.entity.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends CrudRepository<Staff, Integer> {
    List<Staff> findByName(String name);
    Optional<Staff> findByUserId(long id);
}
