package testcontainers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import utils.HttpUtils;
import java.io.IOException;

public class UsersManagementApiTest {
    GenericContainer usersContainer;
    GenericContainer postgresContainer;
    Network network = Network.newNetwork();

    @Before
    public void startContainer() throws InterruptedException {
        postgresContainer = new GenericContainer("postgres")
                .withNetwork(network)
                .withNetworkAliases("postgres")
                .withExposedPorts(5432)
                .withEnv("POSTGRES_PASSWORD", "1234");
        postgresContainer.start();

        Thread.sleep(10000); //wait for postgres to be ready for connections

        usersContainer = new GenericContainer("theswdeveloper/user-mngmnt-api")
                .withNetwork(network)
                .withNetworkAliases("usersApi")
                .withExposedPorts(8080)
                .withEnv("postgres_ip", "postgres");
        usersContainer.start();
    }

    @After
    public void stopEnv() {
        usersContainer.stop();
        postgresContainer.stop();
    }

    @Test
    public void addNewUser() throws IOException {
        String userJson = "{\"firstName\":\"jon\",\"lastName\":\"dao\"}";

        String addUserPath = "http://localhost:" + usersContainer.getMappedPort(8080) + "/api/v1/addUser";
        CloseableHttpResponse response = HttpUtils.httpPostWithBody(addUserPath, userJson);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());

        String getUsersPath = "http://localhost:" + usersContainer.getMappedPort(8080) + "/api/v1/getAllUsers";
        CloseableHttpResponse getUsersResponse = HttpUtils.httpGetRequest(getUsersPath);
        String users = HttpUtils.httpResponseToString(getUsersResponse);
        System.out.println("got response:\n" + users);

        Gson gson = new Gson();
        JsonArray usersArray = gson.fromJson(users, JsonArray.class);
        String actualFirstName = usersArray.get(0).getAsJsonObject().get("firstName").getAsString();
        String actualLastName = usersArray.get(0).getAsJsonObject().get("lastName").getAsString();
        int actualId = usersArray.get(0).getAsJsonObject().get("id").getAsInt();

        Assert.assertEquals("firstName not as expected!", "jon", actualFirstName);
        Assert.assertEquals("lastName not as expected!", "dao", actualLastName);
        Assert.assertEquals("id not as expected!", 1, actualId);
    }

}
