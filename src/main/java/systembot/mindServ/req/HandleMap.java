package systembot.mindServ.req;

import arc.util.Log;
import arc.util.serialization.Base64Coder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import systembot.mindServ.ContentHandler;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static systembot.mindServ.MindServ.*;

public class HandleMap implements HttpHandler {
    @Override
    public void handle(HttpExchange ex) throws IOException {
        Log.info("Receiving data for map preview...");
        Log.info(ex.getRequestMethod());
        if (!ex.getRequestMethod().equals("POST")) {
            emptyResponse(ex, 400);
            return;
        }

        InputStream data = ex.getRequestBody();
        ContentHandler.Map map = contentHandler.readMap(data);

        Log.info(map.author);
        Log.info(map.description);

        File imgFile = new File(assets + randomString(10) + ".png");
        List<String> param = ex.getRequestHeaders().get("terrain");
        ImageIO.write(
                param != null && Boolean.parseBoolean(param.get(0)) ? map.terrain : map.image, "png", imgFile
        );

        Headers headers = ex.getResponseHeaders();
        headers.add("Content-Type", "application/json");
        if (map.author != null) {
            headers.add("author", Base64Coder.encodeString(map.author));
            headers.add("desc", Base64Coder.encodeString(map.description));
            headers.add("size", Base64Coder.encodeString(map.image.getWidth() + " x " + map.image.getHeight()));
            headers.add("name", Base64Coder.encodeString(map.name));
        }

        fileResponse(ex, imgFile);
    }
}
