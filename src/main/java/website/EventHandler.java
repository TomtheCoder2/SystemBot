package website;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import website.entity.Admin;

import static website.WebSocketConfiguration.MESSAGE_PREFIX;


@Component
@RepositoryEventHandler(Admin.class)
public class EventHandler {

    private final SimpMessagingTemplate websocket;

    private final EntityLinks entityLinks;

    @Autowired
    public EventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
        this.websocket = websocket;
        this.entityLinks = entityLinks;
    }

    @HandleAfterCreate
    public void newPlayer(Admin data) {
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/newAdmin", getPath(data));
    }

    @HandleAfterDelete
    public void deletePlayer(Admin data) {
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/deleteAdmin", getPath(data));
    }

    @HandleAfterSave
    public void updatePlayer(Admin data) {
        this.websocket.convertAndSend(
                MESSAGE_PREFIX + "/updateAdmin", getPath(data));
    }

    /**
     * Take an {@link Admin} and get the URI using Spring Data REST's {@link EntityLinks}.
     *
     * @param data Admin
     */
    private String getPath(Admin data) {
        return this.entityLinks.linkForItemResource(data.getClass(),
                data.getId()).toUri().getPath();
    }
}
