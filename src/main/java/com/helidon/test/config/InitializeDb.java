package com.helidon.test.config;

import com.google.gson.Gson;
import com.helidon.test.dto.EmployeeLogin;
import com.helidon.test.service.ServiceHandler;
import io.helidon.dbclient.DbClient;

import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class InitializeDb {

    public static void init(DbClient dbClient){
        try {
            dbClient.execute(
                    exec -> exec
                    .namedDml("create-role")
                    .flatMapSingle(result -> exec.namedDml("create-employee")))
                    .await();

            dbClient.execute(
                    exec -> exec.namedDml("create-task")
            ).await();

            dbClient.execute(
                    exec -> exec.namedDml("create-role-master")
            ).await();

            dbClient.execute(
                    exec -> exec.namedDml("create-employee-master")
            ).await();
        } catch (Exception ex1) {
            System.out.printf("Could not create tables: %s", ex1.getMessage());
        }
    }

    public static EmployeeLogin findByUsername(DbClient dbClient, String username){
        List<EmployeeLogin> logins = new ArrayList<>();

        try {
            dbClient.execute(exec -> exec
                    .createNamedGet("select-employee-by-username")
                    .addParam("username", username)
                    .execute())
                    .thenAccept(maybeRow -> maybeRow
                            .ifPresentOrElse(
                                    row -> {
                                        logins.add(new Gson().fromJson(row.as(JsonObject.class).toString(), EmployeeLogin.class));
                                    },
                                    () -> logins.add(new EmployeeLogin())))
                    .exceptionally(throwable -> ServiceHandler.sendError(throwable)).await();
        } catch (NumberFormatException ex) {
        }

        return logins.get(0);
    }

    private InitializeDb() {
        throw new UnsupportedOperationException("Instances of InitializeDb utility class are not allowed");
    }
}
