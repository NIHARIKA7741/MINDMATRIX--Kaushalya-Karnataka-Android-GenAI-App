package com.kaushalya.karnataka.data.model

import com.google.firebase.Timestamp

/** Map Firestore `category` (enum name, display label, or mixed case) to [WorkerCategory]. */
fun parseWorkerCategory(raw: String): WorkerCategory {
    val t = raw.trim()
    if (t.isBlank()) return WorkerCategory.ELECTRICIAN
    runCatching { return WorkerCategory.valueOf(t.uppercase()) }.getOrNull()
    WorkerCategory.entries.forEach { e ->
        if (e.name.equals(t, ignoreCase = true)) return e
        if (e.displayName.equals(t, ignoreCase = true)) return e
    }
    return WorkerCategory.ELECTRICIAN
}

// ─── Firestore User Document ─────────────────────────────────────────────────
// Stored in: users/{uid}

data class FirestoreUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "customer",          // "customer" | "worker"
    val hasWorkerProfile: Boolean = false,
    val profileImageUrl: String = "",
    val address: String = "",
    val createdAt: Timestamp? = null
) {
    /** Convert to map for Firestore writes */
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "role" to role,
        "hasWorkerProfile" to hasWorkerProfile,
        "profileImageUrl" to profileImageUrl,
        "address" to address,
        "createdAt" to createdAt
    )
}

// ─── Firestore Worker Document ────────────────────────────────────────────────
// Stored in: workers/{uid}

data class FirestoreWorker(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val category: String = "ELECTRICIAN",
    val skills: List<String> = emptyList(),
    val experience: Int = 0,
    val pricePerHour: Int = 0,
    val location: String = "",
    val bio: String = "",
    val isAvailable: Boolean = true,
    val profileImageUrl: String = "",
    val workGalleryUrls: List<String> = emptyList(),
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val completedJobs: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "category" to category,
        "skills" to skills,
        "experience" to experience,
        "pricePerHour" to pricePerHour,
        "location" to location,
        "bio" to bio,
        "isAvailable" to isAvailable,
        "profileImageUrl" to profileImageUrl,
        "workGalleryUrls" to workGalleryUrls,
        "rating" to rating,
        "reviewCount" to reviewCount,
        "completedJobs" to completedJobs,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    /** Convert to local Worker model for UI */
    fun toWorker(reviews: List<Review> = emptyList()): Worker {
        val cat = parseWorkerCategory(category)

        return Worker(
            id = uid,
            name = name,
            category = cat,
            rating = rating,
            reviewCount = reviewCount,
            experience = experience,
            pricePerHour = pricePerHour,
            location = location,
            phone = phone,
            isAvailable = isAvailable,
            profileImageUrl = profileImageUrl,
            bio = bio,
            skills = skills,
            completedJobs = completedJobs,
            reviews = reviews,
            workGalleryUrls = workGalleryUrls
        )
    }
}

// ─── Firestore Booking Document ───────────────────────────────────────────────
// Stored in: bookings/{bookingId}

data class FirestoreBooking(
    val bookingId: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val workerCategory: String = "ELECTRICIAN",
    val customerId: String = "",
    val customerName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "PENDING",         // BookingStatus.name
    val address: String = "",
    val totalAmount: Int = 0,
    val notes: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "bookingId" to bookingId,
        "workerId" to workerId,
        "workerName" to workerName,
        "workerCategory" to workerCategory,
        "customerId" to customerId,
        "customerName" to customerName,
        "date" to date,
        "time" to time,
        "status" to status,
        "address" to address,
        "totalAmount" to totalAmount,
        "notes" to notes,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )

    fun toBooking(): Booking {
        val cat = runCatching { WorkerCategory.valueOf(workerCategory) }
            .getOrDefault(WorkerCategory.ELECTRICIAN)
        val bookingStatus = runCatching { BookingStatus.valueOf(status) }
            .getOrDefault(BookingStatus.PENDING)
        return Booking(
            id = bookingId,
            workerId = workerId,
            workerName = workerName,
            workerCategory = cat,
            customerName = customerName,
            date = date,
            time = time,
            status = bookingStatus,
            address = address,
            totalAmount = totalAmount,
            notes = notes
        )
    }
}

// ─── Firestore Review Document ────────────────────────────────────────────────
// Stored in: workers/{workerId}/reviews/{reviewId}

data class FirestoreReview(
    val id: String = "",
    val reviewerId: String = "",
    val reviewerName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val avatarUrl: String = "",
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "reviewerId" to reviewerId,
        "reviewerName" to reviewerName,
        "rating" to rating,
        "comment" to comment,
        "avatarUrl" to avatarUrl,
        "createdAt" to createdAt
    )

    fun toReview(): Review = Review(
        id = id,
        reviewerName = reviewerName,
        rating = rating,
        comment = comment,
        date = createdAt?.toDate()?.toString() ?: "",
        avatarUrl = avatarUrl
    )
}

// ─── Notification (local-only placeholder) ───────────────────────────────────
// NOTE: Push notifications are a future feature. These are stored locally in
// Firestore under users/{uid}/notifications/{notifId} as a placeholder.

data class FirestoreNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "ALERT",             // NotificationType.name
    val isRead: Boolean = false,
    val createdAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "message" to message,
        "type" to type,
        "isRead" to isRead,
        "createdAt" to createdAt
    )

    fun toNotification(): Notification = Notification(
        id = id,
        title = title,
        message = message,
        time = createdAt?.toDate()?.toString() ?: "recently",
        type = runCatching { NotificationType.valueOf(type) }.getOrDefault(NotificationType.ALERT),
        isRead = isRead
    )
}
