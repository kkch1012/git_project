package ad.kr.hansung.carrotmarketproject

data class ArticleModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: Int, // Int로 변경
    val imageURL: String
) {
    constructor(): this("", "", 0, 0, "")
}


