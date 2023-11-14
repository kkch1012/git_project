package ad.kr.hansung.carrotmarketproject

data class ChatItem(
    val senderId: String,
    val message: String,
) {
    constructor(): this("", "")
}