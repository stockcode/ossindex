package ossindex;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

import org.json.JSONArray;

import org.json.JSONException;
import ossindex.model.PhotoGallery;

/**
 * Created by vicky on 2014/12/4.
 */
public class RestPhotoGallery {

	Gson gson = new Gson();

	public RestPhotoGallery() {
		Unirest.setDefaultHeader("X-Bmob-Application-Id",
				"19fee4b5da44fc283e4c58e9f860ea96");
		Unirest.setDefaultHeader("X-Bmob-REST-API-Key",
				"2c8b047dd9e5f8b9d18cb908bd200b48");
		Unirest.setDefaultHeader("Content-Type", "application/json");
	}

	public String SavePhotoGallery(PhotoGallery photoGallery)
            throws UnirestException, JSONException {

		PhotoGallery query = new PhotoGallery();
		
		query.setKey(photoGallery.getKey());
		
		HttpResponse<JsonNode> result = Unirest
				.get("https://api.bmob.cn/1/classes/PhotoGallery")
				.queryString("where", gson.toJson(query)).asJson();

		JSONArray results = result.getBody().getObject().getJSONArray("results");
		
		if (!results.isNull(0)) {
			return results.getJSONObject(0).get("objectId").toString();
		}
		else {

		
		HttpResponse<JsonNode> jsonResponse = Unirest
				.post("https://api.bmob.cn/1/classes/PhotoGallery").body(gson.toJson(photoGallery))
				.asJson();

		return jsonResponse.getBody().getObject().get("objectId").toString();
		}
	}
}
