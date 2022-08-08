package com.example.dorazy

class GroupModel {
    var groupID: String? = null
    var title: String? = null
    var leader: String? = null
    var userCount: Int? = null
    var unreadCount: Int? = null
    var users: Map<String, String> = HashMap()
}