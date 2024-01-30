package com.spacescanner.service;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class SpaceXApiService {

    private static final String SPACEX_API_URL = "https://api.spacexdata.com/v4/launches";

    public String getSpaceXApiResponse() throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(SPACEX_API_URL);

        String response = httpClient.execute(request, httpResponse -> {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                return EntityUtils.toString(httpResponse.getEntity());
            } else {
                throw new RuntimeException("SpaceX API request failed with status code: " + statusCode);
            }
        });

        System.out.println(response);

        return response;
    }
}
