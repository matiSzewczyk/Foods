package com.example.foods

import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.mongodb.App
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration

class CompletedProductsViewModel : ViewModel() {

    private var user: User? = null
    private var partitionValue : String? = null
    private var realm: Realm? = null


    private var productList: RealmResults<CompletedProduct>? = null

    fun loginAnon(foodsApp: App) {
        val credentials : Credentials = Credentials.anonymous()

        foodsApp.login(credentials)
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


    fun productList(): RealmResults<CompletedProduct> {
        return realm!!.where(CompletedProduct::class.java)
            .findAll()
            .sort("timestamp", Sort.ASCENDING)
    }

    fun deleteFromRealm(entry: String) {
        realm!!.executeTransactionAsync {
            it.where(CompletedProduct::class.java)
                .equalTo("id", entry)
                .findAll()
                .deleteAllFromRealm()
        }
    }
}