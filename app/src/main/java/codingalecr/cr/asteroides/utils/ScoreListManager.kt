package codingalecr.cr.asteroides.utils

class ScoreListManager(list: MutableList<String>) : ScoreStorage {
    private var scores = list

    override fun storeScore(points: Int, name: String, date: Long) {
        scores.add(0, "$points - $name")
    }

    override fun scoresList(quantity: Int): MutableList<String> {
        return scores
    }

}