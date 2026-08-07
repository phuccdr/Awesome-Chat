package com.vie.mit.initdata

fun main() {
    println("Starting custom seeding...")
    FirebaseInitializer.init()
    
    val targetSenderId = "9XMtQAMCW1YZkN3gKVnCgYbBh7n2"
    FriendRequestSeeder.seedRequestsFromSender(targetSenderId)
    
    println("Custom seeding finished.")
}
