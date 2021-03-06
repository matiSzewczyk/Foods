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

class AwaitingProductsViewModel : ViewModel() {

    private var user: User? = null
    private var partitionValue : String? = null
    private var realm: Realm? = null

    var itemCount: Int = 0


    var productList: RealmResults<AwaitingProduct>? = null

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

    fun productList(): RealmResults<AwaitingProduct> {
        return realm!!.where(AwaitingProduct::class.java)
            .findAll()
            .sort("time", Sort.ASCENDING)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewEntry(name: String, grammage: String, isUrgent: Boolean, currentUser: User?) {
        val currentDateTime = LocalDateTime.now()
        val product = AwaitingProduct()

        product.name = name
        product.time = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        product.timestamp = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        product.grammage = grammage
        product.isUrgent = isUrgent
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
                update.isUrgent = !update.isUrgent
                it.copyToRealmOrUpdate(update)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addToCompleted(id: String) {
        realm!!.executeTransaction { it ->
            val completed = productList!!.find {
                id == it.id
            }

            if (completed != null) {
                val test = CompletedProduct()
                val currentDateTime = LocalDateTime.now()
                test.name = completed.name
                test.time = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                test.timestamp = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm")).toString()
                test.grammage = completed.grammage
                it.copyToRealmOrUpdate(test)
            }
        }
        deleteFromRealm(id)
    }

    fun getListCount() {
        itemCount = productList!!.size
    }

    fun isNewEntry(): Boolean {
        return if (productList!!.isNotEmpty())
            itemCount < productList!!.size
        else
            false
    }

    fun notifyObjectName(): String {
        return productList!!.sort("time", Sort.DESCENDING)[0]!!.name
    }

    fun notifyObjectGrammage() : String {
        return productList!!.sort("time", Sort.DESCENDING)[0]!!.grammage
    }

    fun isSameUser(): Boolean {
        return productList!!.sort("time", Sort.DESCENDING)[0]!!.userId == user!!.toString()
    }

    fun alreadyExists(name: String): Boolean {
        return productList!!.where().equalTo("name", name).findFirst() != null
    }
}