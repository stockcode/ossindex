package bmob;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by vicky on 2015/1/13.
 */
public class PushMessage {

    public void send() {
        Unirest.setDefaultHeader("X-Bmob-Application-Id",
                "19fee4b5da44fc283e4c58e9f860ea96");
        Unirest.setDefaultHeader("X-Bmob-REST-API-Key",
                "2c8b047dd9e5f8b9d18cb908bd200b48");
        Unirest.setDefaultHeader("Content-Type", "application/json");

        HttpResponse<String> result = null;
        try {
            result = Unirest
                    .post("https://api.bmob.cn/1/functions/pay").asString();

            String resp = result.getBody();

            System.err.println(resp);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
