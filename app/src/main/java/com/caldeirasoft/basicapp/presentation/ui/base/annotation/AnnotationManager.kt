package com.caldeirasoft.basicapp.presentation.ui.base.annotation

import android.util.NoSuchPropertyException

internal object AnnotationManager {
    private val LAYOUTS_MAP: MutableMap<String, FragmentLayout> = HashMap()
    private val MENU_MAP: MutableMap<String, FragmentMenu> = HashMap()

    fun getLayoutOrThrow(fragment: Any): FragmentLayout {
        return getMetaFromCacheOrRaw(fragment, LAYOUTS_MAP, true)!!
    }

    fun getMenuOrNull(fragment: Any): FragmentMenu? {
        return getMetaFromCacheOrRaw(fragment, MENU_MAP, false)
    }


    private inline fun <reified A : Annotation> getMetaFromCacheOrRaw(fragment: Any,
                                                                      map: MutableMap<String, A>,
                                                                      throwIfNotExists: Boolean): A? {

        val cachedName = fragment.javaClass.name

        if (map.containsKey(cachedName)) {
            return map[cachedName]!!
        }

        val clazz = A::class.java

        val annotation = fragment.javaClass.getAnnotation(clazz)

        if (annotation != null) {
            map.put(cachedName, annotation)
            return annotation
        }

        if (throwIfNotExists.not()) {
            return null
        }

        throw NoSuchPropertyException("Fragment ${fragment.javaClass.simpleName} must contain ${clazz.name} annotation")
    }
}