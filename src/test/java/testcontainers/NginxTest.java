package testcontainers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public class NginxTest {
    GenericContainer nginxContainer;
    int nginxIntPort = 80;
    @Before
    public void startContainer() {
        nginxContainer = new GenericContainer("nginx")
                .withNetworkAliases("nginx_server")
                .withExposedPorts(nginxIntPort);
        nginxContainer.start();
    }

    @After
    public void stopContainer() {
        nginxContainer.stop();
    }

    /**
     * send get request to nginx address and verify proper response.
     * @throws IOException
     */
    @Test
    public void verifyWebServerIsRunning() throws IOException {
        int ngnixExtPort = nginxContainer.getMappedPort(80);
        CloseableHttpResponse response = httpGetRequest("http://localhost:" + ngnixExtPort);
        String responseString = EntityUtils.toString(response.getEntity());
        String expectedTitle = "<title>Welcome to nginx!</title>";
        System.out.println(responseString);
        Assert.assertTrue(responseString.contains(expectedTitle));
    }

    private CloseableHttpResponse httpGetRequest(String url) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        return client.execute(request);
    }

}
