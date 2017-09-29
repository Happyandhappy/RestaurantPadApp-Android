package com.example.fleissig.restaurantserver;

import com.example.fleissig.mylibrary.FirebaseConstants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class MyRetrofitRequests {
    private static Retrofit retrofit = null;

    public static ResponseData sendNotification(String orderId, String notificationTitle,
                                                String notificationText, String message)
            throws IOException {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://fcm.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        Service service = retrofit.create(Service.class);
        final Call<ResponseData> sentimentCall = service.sendNotification(
                new RequestData("/topics/" + orderId,
                        new Notification(notificationTitle, notificationText),
                        new Data(message)));
        final Response<ResponseData> execute = sentimentCall.execute();
        final ResponseData body = execute.body();
        return body;
    }

    interface Service {

        @Headers({"Authorization:key=" + FirebaseConstants.SERVER_API_KEY,
                "Content-type: application/json"})
        @POST("fcm/send")
        Call<ResponseData> sendNotification(@Body RequestData requestData);
    }

    static class Notification {
        private String title;
        private String text;
        private String click_action = FirebaseConstants.CLICK_ACTION;

        public Notification(String title, String text) {
            this.title = title;
            this.text = text;
        }
    }

    static class Data {
        private String message;

        public Data(String message) {
            this.message = message;
        }

        public String getMessage() {

            return message;
        }
    }

    static class RequestData {
        private String to;
        private Notification notification;
        private Data data;

        public RequestData(String to, Notification notification, Data data) {
            this.to = to;
            this.notification = notification;
            this.data = data;
        }

        public String getTo() {

            return to;
        }

        public Notification getNotification() {
            return notification;
        }

        public Data getData() {
            return data;
        }
    }

    static class ResponseData {
        private long message_id;

        public long getMessage_id() {
            return message_id;
        }
    }
}
