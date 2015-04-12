package ossindex;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ossindex.model.PhotoGallery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by vicky on 2014/12/4.
 */
public class RandomHitPhotoGallery {
	Gson gson = new Gson();

	List<String> uploaders =  Arrays.asList("丝客", "我爱袜", "迷恋", "老烟枪", "长棍", "夏末", "依依", "小痘痘", "雪沫", "小女子");

	public static void main(String[] args) throws UnirestException {


		RandomHitPhotoGallery randomHitPhotoGallery = new RandomHitPhotoGallery();

		randomHitPhotoGallery.start();
	}

	public RandomHitPhotoGallery() {
		Unirest.setDefaultHeader("X-Bmob-Application-Id",
				"19fee4b5da44fc283e4c58e9f860ea96");
		Unirest.setDefaultHeader("X-Bmob-REST-API-Key",
				"2c8b047dd9e5f8b9d18cb908bd200b48");
		Unirest.setDefaultHeader("Content-Type", "application/json");
	}

	public void start()
            throws UnirestException, JSONException {

		HttpResponse<JsonNode> result = Unirest
				.get("https://api.bmob.cn/1/classes/PhotoGallery")
				.queryString("limit", 1000).asJson();

		JSONArray results = result.getBody().getObject().getJSONArray("results");

		Random rd = new Random();

		for(int i = 0; i < results.length(); i++) {
			Hit hit = new Hit();
			hit.setHit(rd.nextInt(10000));
			hit.setUploader(uploaders.get(rd.nextInt(uploaders.size())));

			JSONObject obj = results.getJSONObject(i);
			String objectId = obj.getString("objectId");
			HttpResponse<JsonNode> jsonResponse = Unirest
				.put("https://api.bmob.cn/1/classes/PhotoGallery/" + objectId).body(gson.toJson(hit))
				.asJson();

			String updatedAt = jsonResponse.getBody().getObject().getString("updatedAt");

			System.err.println(objectId + ":" + updatedAt);
		}



//
//			photoGallery.setObjectId(jsonResponse.getBody().getObject().getString("objectId"));
	}
}

class Hit {
	int hit;
	String uploader;

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
}