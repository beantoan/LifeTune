package it.unical.mat.lifetune.api


import it.unical.mat.lifetune.EnvConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by beantoan on 5/14/16.
 */
object ApiServiceFactory {
    private var retrofit: Retrofit? = null
    private var retrofitXml: Retrofit? = null

    fun getRetrofit(baseUrl: String): Retrofit {
        if (ApiServiceFactory.retrofit == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY


            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

            ApiServiceFactory.retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }

        return ApiServiceFactory.retrofit!!
    }

    fun getRetrofitXml(baseUrl: String): Retrofit {
        if (ApiServiceFactory.retrofitXml == null) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY


            val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

            ApiServiceFactory.retrofitXml = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }

        return ApiServiceFactory.retrofitXml!!
    }

    fun getRetrofit(): Retrofit = getRetrofit(EnvConstants.BASE_API_URL)


    fun <T> create(service: Class<T>): T {
        return ApiServiceFactory.getRetrofit().create(service)
    }

    fun <T> createXml(service: Class<T>, baseUrl: String): T {
        return ApiServiceFactory.getRetrofitXml(baseUrl).create(service)
    }

    fun createCategoryApi(): CategoryApiInterface =
            ApiServiceFactory.create(CategoryApiInterface::class.java)

    fun createPlaylistApi(): PlaylistApiInterface =
            ApiServiceFactory.create(PlaylistApiInterface::class.java)

    fun createPlaylistXmlApi(): TrackListApiInterface =
            ApiServiceFactory.createXml(TrackListApiInterface::class.java, EnvConstants.NCT_BASE_URL)
}
