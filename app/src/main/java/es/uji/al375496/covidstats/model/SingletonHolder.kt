package es.uji.al375496.covidstats.model

open class SingletonHolder<out T,in A>(private val constructor: (A) -> T)
{
    @Volatile
    private var     instance: T? =null
    fun getInstance(arg: A): T =
        instance ?: synchronized(this){
            instance ?:constructor(arg).also { instance = it}
        }
}