/*
 * Copyright (c) 2018. An change in this code must be done under my supervision and any misuse my lead to legal actions
 */

package in.restroin.partnerrestroin.platforms;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import in.restroin.partnerrestroin.models.ProfileModel;
import in.restroin.partnerrestroin.models.RestaurantProfileModel;

public class RestaurantDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement Restaurantprofile = json.getAsJsonObject().get("restaurant_profile");
        return new Gson().fromJson(Restaurantprofile, RestaurantProfileModel.class);
    }
}
