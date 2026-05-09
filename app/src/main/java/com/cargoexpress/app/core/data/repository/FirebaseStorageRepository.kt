package com.cargoexpress.app.core.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageRepository {

    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val fileName = "logos/${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child(fileName)

            imageRef.putFile(uri).await()
            val downloadUrl = imageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}