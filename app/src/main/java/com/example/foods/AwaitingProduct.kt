package com.example.foods

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField
import java.util.*

@RealmClass
class AwaitingProduct : RealmObject() {
    @PrimaryKey @RealmField("_id")
    var id: String = UUID.randomUUID().toString()

    var name: String = ""

    var isUrgent: Boolean = false

    var urgent = "pilne"

    var timestamp: String = ""

}
