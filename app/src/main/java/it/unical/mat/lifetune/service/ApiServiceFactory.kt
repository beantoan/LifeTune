package it.unical.mat.lifetune.service


import com.facebook.stetho.okhttp3.StethoInterceptor
import it.unical.mat.lifetune.EnvConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by beantoan on 5/14/16.
 */
object ApiServiceFactory {
    private var retrofit: Retrofit? = null

    fun getRetrofit(): Retrofit {
        if (ApiServiceFactory.retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY


            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addNetworkInterceptor(StethoInterceptor())
                    .build()

            ApiServiceFactory.retrofit = Retrofit.Builder()
                    .baseUrl(EnvConstants.BASE_API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }

        return ApiServiceFactory.retrofit!!
    }

    fun <T> create(service: Class<T>): T {
        return ApiServiceFactory.getRetrofit().create(service)
    }
}
