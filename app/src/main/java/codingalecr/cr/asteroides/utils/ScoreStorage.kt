package codingalecr.cr.asteroides.utils

interface ScoreStorage {
    fun storeScore(points: Int, name: String, date: Long)
    fun scoresList(quantity: Int): List<String>
}