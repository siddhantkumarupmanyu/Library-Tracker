package sku.app.lib_tracker.api

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import sku.app.lib_tracker.vo.Library
import sku.app.lib_tracker.vo.Package
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

// lack of better name
class CustomConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type == Library::class.java) {
            return LibraryConverter()
        } else if ((type is ParameterizedType) && (getRawType(type) == List::class.java)) {
            if (getParameterUpperBound(0, type) == Package::class.java) {
                return PackageConverter()
            }
        }
        return super.responseBodyConverter(type, annotations, retrofit)
    }

    class PackageConverter : Converter<ResponseBody, List<Package>> {
        override fun convert(value: ResponseBody): List<Package> {
            return PackageParser.parsePackages(value.string())
        }

    }

    class LibraryConverter : Converter<ResponseBody, Library> {
        override fun convert(value: ResponseBody): Library {
            return LibraryParser.parseLibrary(value.string())
        }

    }

}