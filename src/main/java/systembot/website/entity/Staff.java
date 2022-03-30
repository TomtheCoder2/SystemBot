package systembot.website.entity;


import lombok.Getter;
import lombok.Setter;
import org.javacord.api.entity.user.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "admin")
public class Staff {
    @Setter @Getter private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) int id;
    @Setter @Getter private String name;
    @Setter @Getter private String avatarUrl;
    @Setter @Getter private Long userId;
    @Setter @Getter private String description;
    @Setter @Getter private String rank;

    public Staff(String name, String url, Long id, String desc) {
        this.name = name;
        avatarUrl = url;
        userId = id;
        description = desc;
    }

    public Staff(User user, String desc, String rank) {
        name = user.getDiscriminatedName();
        avatarUrl = user.getAvatar().getUrl().toString();
        userId = user.getId();
        description = desc;
        this.rank = rank;
    }

    public Staff() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return id == staff.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}
