package com.example.foods

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
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
    var config: SyncConfiguration? = null

    var itemCount: Int = 0


    private var productList: RealmResults<AwaitingProduct>? = null

    fun loginAnon(foodsApp: App) {
        val credentials : Credentials = Credentials.anonymous()

        foodsApp.login(credentials)
    }

    fun createRealm(foodsApp: App) {
        user = foodsApp.currentUser()
        partitionValue = "partition"
        config = SyncConfiguration.Builder(user!!, partitionValue)
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

        realm = Realm.getInstance(config)

        productList = productList()
    }

    fun productList(): RealmResults<AwaitingProduct> {
        return realm!!.where(AwaitingProduct::class.java)
            .findAll()
            .sort("timestamp", Sort.ASCENDING)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewEntry(name: String, grammage: String, urgency: String, currentUser: User?) {
        val currentDateTime = LocalDateTime.now()
        val product = AwaitingProduct()

        product.name = name
        product.timestamp = currentDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)).toString()
        product.grammage = grammage
        product.urgent = urgency
        product.userId = currentUser.toString()
        addToRealm(product)
    }

    private fun addToRealm(entity: AwaitingProduct) {
        realm!!.executeTransactionAsync { bgRealm ->
            bgRealm.copyToRealmOrUpdate(entity)
        }
    }

    fun deleteFromRealm(entry: String) {
        if (!realm!!.isInTransaction) {
            realm!!.executeTransaction {
                it.where(AwaitingProduct::class.java)
                    .equalTo("id", entry)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
        itemCount--
    }

    fun toggleUrgency(id: String) {
        realm!!.executeTransactionAsync {
            val update = it.where(AwaitingProduct::class.java)
                .equalTo("id", id)
                .findFirst()
            if (update != null) {
                if (update.isUrgent) {
                    update.isUrgent = false
                    update.urgent = ""
                } else {
                    update.isUrgent = true
                    update.urgent = "pilne"
                }
                it.copyToRealmOrUpdate(update)
            }
        }
    }

    fun addToCompleted(id: String) {
        realm!!.executeTransaction {
            val completed = it.where(AwaitingProduct::class.java)
                .equalTo("id", id)
                .findFirst()

            if (completed != null) {
                val test = CompletedProduct()
                test.name = completed.name
                test.timestamp = completed.timestamp
                test.grammage = completed.grammage
                it.copyToRealmOrUpdate(test)
            }
        }
        deleteFromRealm(id)
    }

    fun getListCount() {
        realm!!.executeTransaction {
            itemCount = it.where(AwaitingProduct::class.java)
                .findAll()
                .count()
        }
    }

    fun isNewEntry(): Boolean {
        return itemCount < productList!!.size
    }


    fun notifyObjectName() : String {
        var name = ""
        if (!realm!!.isInTransaction) {
            realm!!.executeTransaction {
                val nameInList = it.where(AwaitingProduct::class.java)
                    .sort("timestamp", Sort.DESCENDING)
                    .findFirst()
                name = nameInList!!.name
            }
        }
        return name
    }

    fun notifyObjectGrammage() : String {
        var grammage = ""
        if (!realm!!.isInTransaction) {
            realm!!.executeTransaction {
                val grammageInList = it.where(AwaitingProduct::class.java)
                    .sort("timestamp", Sort.DESCENDING)
                    .findFirst()
                grammage = grammageInList!!.grammage
            }
        }
        return grammage
    }

    fun isSameUser(userId: String): Boolean {
        return productList!!.sort("timestamp", Sort.DESCENDING)[0]!!.userId == userId
    }
}