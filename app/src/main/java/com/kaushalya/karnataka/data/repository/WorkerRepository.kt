package com.kaushalya.karnataka.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.kaushalya.karnataka.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * WorkerRepository — manages worker profiles, bookings, and reviews in Firestore.
 *
 * Collections:
 *   workers/               → worker profile documents (keyed by uid)
 *   workers/{wid}/reviews/ → subcollection of reviews
 *   bookings/              → all booking documents
 *   users/{uid}/notifications/ → notification placeholders
 */
class WorkerRepository {

    private val db = FirebaseFirestore.getInstance()

    // ── Worker Profile ────────────────────────────────────────────────────────

    /** Save (create or overwrite) a worker profile document. */
    suspend fun saveWorkerProfile(worker: FirestoreWorker) {
        db.collection("workers").document(worker.uid).set(worker.toMap()).await()
    }

    /** Fetch a single worker profile by UID. Returns null if not found. */
    suspend fun getWorkerProfile(uid: String): FirestoreWorker? {
        val snap = db.collection("workers").document(uid).get().await()
        return snap.toFirestoreWorkerOrNull()
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toFirestoreWorkerOrNull(): FirestoreWorker? {
        if (!exists()) return null
        val w = runCatching { toObject(FirestoreWorker::class.java) }.getOrNull() ?: return null
        val withUid = if (w.uid.isBlank() && id.isNotBlank()) w.copy(uid = id) else w
        return withUid.copy(category = parseWorkerCategory(withUid.category).name)
    }

    /** Fetch all workers (for listing screens). No server-side orderBy on rating so docs without that field are included. */
    suspend fun getAllWorkers(): List<FirestoreWorker> {
        val snap = db.collection("workers").get().await()
        return snap.documents.mapNotNull { it.toFirestoreWorkerOrNull() }
            .sortedByDescending { it.rating }
    }

    /** Fetch workers by category string (WorkerCategory.name). */
    suspend fun getWorkersByCategory(category: String): List<FirestoreWorker> {
        val snap = db.collection("workers")
            .whereEqualTo("category", category)
            .get().await()
        return snap.documents.mapNotNull { it.toFirestoreWorkerOrNull() }
            .sortedByDescending { it.rating }
    }

    /** Fetch featured workers (rating >= 4.5). */
    suspend fun getFeaturedWorkers(): List<FirestoreWorker> {
        return getAllWorkers().filter { it.rating >= 4.5f }.take(10)
    }

    /** Realtime: all workers in `workers` collection (sorted client-side by rating). */
    fun observeAllWorkers(): Flow<List<FirestoreWorker>> = callbackFlow {
        val reg: ListenerRegistration = db.collection("workers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Keep flow alive; transient permission/network errors recover on next snapshot.
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { it.toFirestoreWorkerOrNull() }
                    .sortedByDescending { it.rating }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /** Realtime: workers filtered by category name (WorkerCategory.name). */
    fun observeWorkersByCategory(category: String): Flow<List<FirestoreWorker>> = callbackFlow {
        val reg = db.collection("workers")
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { it.toFirestoreWorkerOrNull() }
                    .sortedByDescending { it.rating }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /** Realtime: single worker document at `workers/{uid}`. */
    fun observeWorker(uid: String): Flow<FirestoreWorker?> = callbackFlow {
        val reg = db.collection("workers").document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                trySend(snap?.toFirestoreWorkerOrNull())
            }
        awaitClose { reg.remove() }
    }

    /** Realtime: bookings where workerId matches (sorted client-side by createdAt). */
    fun observeBookingsForWorker(workerId: String): Flow<List<FirestoreBooking>> = callbackFlow {
        val reg = db.collection("bookings")
            .whereEqualTo("workerId", workerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { it.toObject(FirestoreBooking::class.java) }
                    .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /** Realtime: bookings where customerId matches (sorted client-side by createdAt). */
    fun observeBookingsForCustomer(customerId: String): Flow<List<FirestoreBooking>> = callbackFlow {
        val reg = db.collection("bookings")
            .whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.mapNotNull { it.toObject(FirestoreBooking::class.java) }
                    .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /** Update availability flag for a worker. */
    suspend fun updateAvailability(uid: String, isAvailable: Boolean) {
        db.collection("workers").document(uid)
            .update("isAvailable", isAvailable, "updatedAt", Timestamp.now()).await()
    }

    /** Update worker profile fields (partial update). */
    suspend fun updateWorkerProfile(uid: String, updates: Map<String, Any?>) {
        val mutableUpdates = updates.toMutableMap()
        mutableUpdates["updatedAt"] = Timestamp.now()
        val ref = db.collection("workers").document(uid)
        runCatching { ref.update(mutableUpdates).await() }
            .recoverCatching { ref.set(mutableUpdates, SetOptions.merge()).await() }
            .getOrThrow()
    }

    /** Update profile image URL for worker document. */
    suspend fun updateWorkerProfileImage(uid: String, url: String) {
        val ref = db.collection("workers").document(uid)
        val payload = mapOf("profileImageUrl" to url, "updatedAt" to Timestamp.now(), "uid" to uid)
        runCatching { ref.update("profileImageUrl", url, "updatedAt", Timestamp.now()).await() }
            .recoverCatching { ref.set(payload, SetOptions.merge()).await() }
            .getOrThrow()
    }

    /** Add gallery image URL to worker's workGalleryUrls array. */
    suspend fun addGalleryImageUrl(uid: String, url: String) {
        val snap = db.collection("workers").document(uid).get().await()
        val worker = snap.toObject(FirestoreWorker::class.java) ?: return
        val newList = worker.workGalleryUrls + url
        db.collection("workers").document(uid)
            .update("workGalleryUrls", newList, "updatedAt", Timestamp.now()).await()
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    /** Fetch all reviews for a worker (subcollection). */
    suspend fun getReviewsForWorker(workerId: String): List<FirestoreReview> {
        val snap = db.collection("workers").document(workerId)
            .collection("reviews")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        return snap.documents.mapNotNull { it.toObject(FirestoreReview::class.java) }
    }

    /** Add a review for a worker and update aggregate rating. */
    suspend fun addReview(workerId: String, review: FirestoreReview) {
        // 1. Write the review document
        db.collection("workers").document(workerId)
            .collection("reviews").document(review.id).set(review.toMap()).await()

        // 2. Recalculate aggregate rating
        val allReviews = getReviewsForWorker(workerId)
        if (allReviews.isNotEmpty()) {
            val avgRating = allReviews.sumOf { it.rating.toDouble() } / allReviews.size
            db.collection("workers").document(workerId).update(
                "rating", avgRating.toFloat(),
                "reviewCount", allReviews.size,
                "updatedAt", Timestamp.now()
            ).await()
        }
    }

    // ── Bookings ─────────────────────────────────────────────────────────────

    /** Create a new booking document. */
    suspend fun createBooking(booking: FirestoreBooking): String {
        val docRef = db.collection("bookings").document()
        val withId = booking.copy(bookingId = docRef.id, createdAt = Timestamp.now(), updatedAt = Timestamp.now())
        docRef.set(withId.toMap()).await()
        return docRef.id
    }

    /** Fetch all bookings for a customer (by customerId). */
    suspend fun getBookingsForCustomer(customerId: String): List<FirestoreBooking> {
        val snap = db.collection("bookings")
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        return snap.documents.mapNotNull { it.toObject(FirestoreBooking::class.java) }
    }

    /** Fetch all bookings for a worker (by workerId). */
    suspend fun getBookingsForWorker(workerId: String): List<FirestoreBooking> {
        val snap = db.collection("bookings")
            .whereEqualTo("workerId", workerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        return snap.documents.mapNotNull { it.toObject(FirestoreBooking::class.java) }
    }

    /** Update booking status. */
    suspend fun updateBookingStatus(bookingId: String, status: String) {
        db.collection("bookings").document(bookingId)
            .update("status", status, "updatedAt", Timestamp.now()).await()
    }

    // ── Notifications (Placeholder) ───────────────────────────────────────────

    /** Write a notification placeholder to Firestore (no push yet). */
    suspend fun addNotification(uid: String, notification: FirestoreNotification) {
        val docRef = db.collection("users").document(uid)
            .collection("notifications").document()
        val withId = notification.copy(id = docRef.id, createdAt = Timestamp.now())
        docRef.set(withId.toMap()).await()
    }

    /** Fetch notifications for a user. */
    suspend fun getNotifications(uid: String): List<FirestoreNotification> {
        val snap = db.collection("users").document(uid)
            .collection("notifications")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get().await()
        return snap.documents.mapNotNull { it.toObject(FirestoreNotification::class.java) }
    }

    /** Mark notification as read. */
    suspend fun markNotificationRead(uid: String, notificationId: String) {
        db.collection("users").document(uid)
            .collection("notifications").document(notificationId)
            .update("isRead", true).await()
    }

    /** Mark all notifications as read. */
    suspend fun markAllNotificationsRead(uid: String) {
        val snap = db.collection("users").document(uid)
            .collection("notifications")
            .whereEqualTo("isRead", false)
            .get().await()
        val batch = db.batch()
        snap.documents.forEach { doc ->
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()
    }
}
