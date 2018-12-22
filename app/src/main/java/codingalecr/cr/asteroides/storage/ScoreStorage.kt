package codingalecr.cr.asteroides.storage

interface ScoreStorage {
    fun storeScore(points: Int, name: String, date: Long)
    fun scoresList(quantity: Int): List<String>
}