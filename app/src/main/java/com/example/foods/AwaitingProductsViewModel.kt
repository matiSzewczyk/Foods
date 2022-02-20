package com.example.foods

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AwaitingProductsViewModel : ViewModel() {

    private var user: User? = null
    private var partitionValue : String? = null
    private var realm: Realm? = null


    var productList: RealmResults<AwaitingProduct>? = null

    fun loginAnon(foodsApp: App) {
        val credentials : Credentials = Credentials.anonymous()

        foodsApp.login(credentials)
    }

    fun test() {
        realm!!.executeTransactionAsync { bgRealm ->
            bgRealm.delete(AwaitingProduct::class.java)
//            val product = randomProduct()
//            bgRealm.copyToRealmOrUpdate(product)
        }
    }

    fun createRealm(foodsApp: App) {
        user = foodsApp.currentUser()
        partitionValue = "partition"
        val config = SyncConfiguration.Builder(user!!, partitionValue)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        realm = Realm.getInstance(config)

        productList = productList()
    }

    fun productList(): RealmResults<AwaitingProduct> {
        return realm!!.where(AwaitingProduct::class.java)
            .findAll()
            .sort("timestamp")
    }

    @SuppressLint("NewApi")
    fun randomProduct() : AwaitingProduct {
        val currentDateTime = LocalDateTime.now()
        val product = AwaitingProduct()

        product.name = "Orzech Laskowy"
        product.timestamp = currentDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)).toString()
        product.grammage = "500g"

        return product
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewEntry(name: String, grammage: String, urgency: String) {
        val currentDateTime = LocalDateTime.now()
        val product = AwaitingProduct()

        product.name = name
        product.timestamp = currentDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)).toString()
        product.grammage = grammage
        product.urgent = urgency
        addToRealm(product)
    }

    private fun addToRealm(entity: AwaitingProduct) {
        realm!!.executeTransactionAsync { bgRealm ->
//            bgRealm.delete(AwaitingProduct::class.java)
            bgRealm.copyToRealmOrUpdate(entity)
        }
    }
}