package com.vie.mit.initdata

import com.google.cloud.Timestamp

fun main() {
    FirebaseInitializer.init()
    UserProfileFieldsMigration.migrateUserProfileFields(
        phoneNumber = "0355636999",
        birthOfDay = Timestamp.now(),
    )
//    FirestoreSeeder.seedUsers()
    // ConversationSeeder.seed()
//    FriendShipSeeder.seed()
//    FriendRequestSeeder.seed()
//    for (i in 1..12){
//        FriendRequestSeeder.createMockUsersAndRequests("9XMtQAMCW1YZkN3gKVnCgYbBh7n2", count = 1)
//    }

//    UserUidMigration.migrateUserUids()
}