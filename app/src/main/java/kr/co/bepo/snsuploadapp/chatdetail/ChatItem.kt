package kr.co.bepo.snsuploadapp.chatdetail

data class ChatItem(
    val senderId: String,
    val message: String
) {
    constructor() : this("", "")
}