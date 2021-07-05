package io.omniedge

object ProviderManager {
    private val map = HashMap<String, Any>()

    fun addProvider(name: String, provider: Any) {
        map[name] = provider
    }

    fun <T> getProvider(name: String): T {
        return map[name] as T
    }
}