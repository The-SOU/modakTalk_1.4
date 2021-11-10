package com.modak.modaktestone.navigation.util


import com.squareup.okhttp.ResponseBody
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

object ApiClient {
    const val TOUR_BASE_URL = "http://api.visitkorea.or.kr/openapi/service/"
    const val FCM_URL = "https://fcm.googleapis.com/"
}

interface FcmInterface {
    @POST("fcm/send")
    fun sendNotification(
        @Body notification: NotificationBody
    ): Single<ResponseBody>
}

data class NotificationBody(val to : String?, val data: NotificationData){
    data class NotificationData(val title: String?, val body: String?)
}

//val apiModule: Module = module {
//    single<ApiInterface>(named("tour")) { get<Retrofit>(named("tour")).create(ApiInterface::class.java) }
//    single<FcmInterface>(named("fcm")) { get<Retrofit>(named("fcm")).create(FcmInterface::class.java) }
//
//    single<Retrofit>(named("fcm")) {
//        Retrofit.Builder()
//            .baseUrl(ApiClient.FCM_URL)
//            .client(get(named("fcm")))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(get<GsonConverterFactory>())
//            .build()
//    }
//
//    single<Retrofit>(named("tour")) {
//        Retrofit.Builder()
//            .baseUrl(ApiClient.TOUR_BASE_URL)
//            .client(get(named("tour")))
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(get<GsonConverterFactory>())
//            .build()
//    }
//
//
//    single<GsonConverterFactory> { GsonConverterFactory.create() }
//
//    single<OkHttpClient>(named("tour")) {
//        OkHttpClient.Builder()
//            .run {
//                addInterceptor(get<Interceptor>(named("tour")))
//                build()
//            }
//    }
//
//    single<OkHttpClient>(named("fcm")) {
//        OkHttpClient.Builder()
//            .run {
//                addInterceptor(get<Interceptor>(named("fcm")))
//                build()
//            }
//    }
//
//    single<Interceptor>(named("fcm")) {
//        Interceptor { chain ->
//            with(chain) {
//                val newRequest = request().newBuilder()
//                    .addHeader("Authorization", "key=$FCM_KEY")
//                    .addHeader("Content-Type", "application/json")
//                    .build()
//                proceed(newRequest)
//            }
//        }
//    }
//
//
//
//    single<Interceptor>(named("tour")) {
//        Interceptor { chain ->
//            with(chain) {
//                val newRequest = request().newBuilder()
//                    .build()
//                proceed(newRequest)
//            }
//        }
//    }
//}


