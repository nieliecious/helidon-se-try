package com.helidon.test.service;

import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import java.util.logging.Logger;
import static com.helidon.test.Main.dbClient;

public class PostTaskService {
    private static final Logger LOGGER = Logger.getLogger(PostTaskService.class.getName());

    public static void post(ServerRequest request, ServerResponse response){

        String task = request.path().param("task");

        dbClient.execute(exec -> exec
                .createNamedInsert("insert-new-task")
                .addParam("task", task)
                .execute())
                .thenAccept( count -> response.send("Inserted: " + count + " values\n"))
                .exceptionally(throwable -> ServiceHandler.sendError(throwable, response, LOGGER));
    }
}
