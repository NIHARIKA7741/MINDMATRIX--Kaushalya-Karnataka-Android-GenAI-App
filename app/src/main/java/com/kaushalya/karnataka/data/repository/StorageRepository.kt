package com.kaushalya.karnataka.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

/**
 * StorageRepository — handles Firebase Storage uploads for profile images
 * and work gallery images.
 *
 * Storage paths:
 *   profile_images/{uid}/profile.jpg
 *   work_gallery/{uid}/{filename}
 */
class StorageRepository {

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ── Profile Image Upload ──────────────────────────────────────────────────

    /**
     * Uploads a profile image from a local URI.
     * @param uid     Worker / user UID to scope the storage path.
     * @param uri     Local content URI of the selected image.
     * @return Download URL string.
     * @throws Exception if upload fails.
     */
    suspend fun uploadProfileImage(uid: String, uri: Uri): String {
        val ref = storage.reference.child("profile_images/$uid/profile.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    // ── Work Gallery Upload ───────────────────────────────────────────────────

    /**
     * Uploads a single gallery image.
     * @param uid      Worker UID.
     * @param uri      Local content URI.
     * @param fileName Unique filename (e.g. UUID or timestamp).
     * @return Download URL string.
     */
    suspend fun uploadGalleryImage(uid: String, uri: Uri, fileName: String): String {
        val ref = storage.reference.child("work_gallery/$uid/$fileName")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    // ── Multiple gallery images ───────────────────────────────────────────────

    /**
     * Uploads multiple gallery images sequentially and returns their download URLs.
     * @param uid  Worker UID.
     * @param uris List of local content URIs.
     * @return List of download URL strings (same order as input URIs).
     */
    suspend fun uploadGalleryImages(uid: String, uris: List<Uri>): List<String> {
        return uris.mapIndexed { index, uri ->
            val fileName = "gallery_${System.currentTimeMillis()}_$index.jpg"
            uploadGalleryImage(uid, uri, fileName)
        }
    }

    // ── Delete image ──────────────────────────────────────────────────────────

    /**
     * Deletes an image from Storage by its download URL.
     * Silently ignores errors (image may have already been deleted).
     */
    suspend fun deleteImageByUrl(url: String) {
        runCatching {
            storage.getReferenceFromUrl(url).delete().await()
        }
    }
}
