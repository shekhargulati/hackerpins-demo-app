package org.hackerpins.business.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class GooseExtractorClient {

    private static final String CLIENT_URL = "http://gooseextractor-t20.rhcloud.com/api/v1/extract";
    @Inject
    private Logger logger;

    // TODO : Can we use JAX-RS Client Async API here?
    public Map<String, String> fetchImageAndDescription(String url) {
        Client client = ClientBuilder.newClient();
        try {
            String response = client.target(CLIENT_URL).queryParam("url", url).request().get(String.class);
            JsonReader jsonReader = Json.createReader(new StringReader(response));
            JsonObject jsonObject = jsonReader.readObject();
            String bannerImage = jsonObject.getString("image");
            String description = jsonObject.getString("text");
            String title = jsonObject.getString("title");
            Map<String, String> fetchedData = new HashMap<>();
            fetchedData.put("picUrl", bannerImage);
            fetchedData.put("description", description);
            fetchedData.put("title", title);
            return fetchedData;
        } catch (Exception e) {
            logger.severe("Exception encountered while getting response from GooseExtractor ..." + e.getMessage());
            return Collections.EMPTY_MAP;
        }


    }
}