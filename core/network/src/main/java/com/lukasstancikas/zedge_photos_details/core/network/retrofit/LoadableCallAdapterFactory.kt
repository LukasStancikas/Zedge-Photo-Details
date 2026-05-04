package com.lukasstancikas.zedge_photos_details.core.network.retrofit

import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LoadableCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        // When a suspend function is used, Retrofit wraps the return type in a Call.
        // e.g., suspend fun foo(): Loadable<T> becomes a search for CallAdapter<T, Call<Loadable<T>>>
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(callType) != Loadable::class.java) {
            return null
        }

        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
        return LoadableCallAdapter<Any>(resultType)
    }
}

private class LoadableCallAdapter<R>(
    private val responseType: Type,
) : CallAdapter<R, Call<Loadable<R>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Call<Loadable<R>> = LoadableCall(call, responseType)
}

private class LoadableCall<R>(
    private val retrofitCall: Call<R>,
    private val responseType: Type,
) : Call<Loadable<R>> {
    override fun enqueue(callback: Callback<Loadable<R>>) {
        retrofitCall.enqueue(object : Callback<R> {
            override fun onResponse(call: Call<R>, response: Response<R>) {
                val result = response.toLoadable()
                callback.onResponse(this@LoadableCall, Response.success(result))
            }

            override fun onFailure(call: Call<R>, t: Throwable) {
                // wrap with success to avoid retrofit throwing exceptions at call site
                callback.onResponse(this@LoadableCall, Response.success(Loadable.Error(t)))
            }
        })
    }

    override fun isExecuted(): Boolean = retrofitCall.isExecuted

    override fun execute(): Response<Loadable<R>> = try {
        val response = retrofitCall.execute()
        val result = response.toLoadable()
        Response.success(result)
    } catch (e: Exception) {
        // wrap with success to avoid retrofit throwing exceptions at call site
        Response.success(Loadable.Error(e))
    }

    override fun cancel() = retrofitCall.cancel()
    override fun isCanceled(): Boolean = retrofitCall.isCanceled
    override fun clone(): Call<Loadable<R>> = LoadableCall(retrofitCall.clone(), responseType)
    override fun request(): Request = retrofitCall.request()
    override fun timeout(): Timeout = retrofitCall.timeout()

    @Suppress("UNCHECKED_CAST")
    private fun Response<R>.toLoadable(): Loadable<R> {
        val body = body()
        val code = code()
        val result = when {
            this.isSuccessful && body != null -> Loadable.Success(body)
            this.isSuccessful && body == null && responseType == Unit::class.java
            -> Loadable.Success(Unit as R)

            this.isSuccessful && body == null && responseType != Unit::class.java
            -> Loadable.Error(Exception("Response body is null, return type should be declared Loadable<Unit>"))

            else -> Loadable.Error(Exception("Network error: $code"))
        }
        return result
    }
}
