package com.example.foods

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField
import java.util.*

@RealmClass
open class CompletedProduct : RealmObject() {
    @PrimaryKey
    @RealmField("_id")
    var id: String = UUID.randomUUID().toString()

    var name: String = ""

    var grammage: String = ""

    var time: String = ""

    var timestamp: String = ""

    var userId: String = ""
}