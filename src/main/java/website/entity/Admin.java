package website.entity;


import lombok.Getter;
import lombok.Setter;
import org.javacord.api.entity.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "admin")
public class Admin {
    @Setter @Getter private @Id @GeneratedValue int id;
    @Setter @Getter private String name;
    @Setter @Getter private String avatarUrl;
    @Setter @Getter private Long userId;
    @Setter @Getter private String description;

    public Admin(String name, String url, Long id, String desc) {
        this.name = name;
        avatarUrl = url;
        userId = id;
        description = desc;
    }

    public Admin(User user, String desc) {
        name = user.getDiscriminatedName();
        avatarUrl = user.getAvatar().getUrl().toString();
        userId = user.getId();
        description = desc;
    }

    public Admin() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", userId=" + userId +
                '}';
    }
}
