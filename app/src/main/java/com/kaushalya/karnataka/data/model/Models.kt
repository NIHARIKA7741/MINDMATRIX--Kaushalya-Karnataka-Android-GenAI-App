package com.kaushalya.karnataka.data.model

data class Worker(
    val id: String,
    val name: String,
    val category: WorkerCategory,
    val rating: Float,
    val reviewCount: Int,
    val experience: Int, // in years
    val pricePerHour: Int, // in INR
    val location: String,
    val phone: String,
    val email: String = "",
    val isAvailable: Boolean,
    val profileImageUrl: String,
    val bio: String,
    val skills: List<String>,
    val completedJobs: Int,
    val reviews: List<Review>,
    val workGalleryUrls: List<String> = emptyList()
)

data class Review(
    val id: String,
    val reviewerName: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val avatarUrl: String
)

enum class WorkerCategory(
    val displayName: String,
    val iconName: String,
    val description: String,
    val color: Long
) {
    ELECTRICIAN("Electrician", "electrical_services", "Wiring, repairs & installations", 0xFF1565C0),
    PLUMBER("Plumber", "plumbing", "Pipes, leaks & drainage", 0xFF00695C),
    CARPENTER("Carpenter", "carpenter", "Furniture, doors & woodwork", 0xFF4E342E),
    PAINTER("Painter", "format_paint", "Interior & exterior painting", 0xFF6A1B9A),
    MECHANIC("Mechanic", "build", "Vehicle & machinery repairs", 0xFFE65100)
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val isWorker: Boolean,
    val profileImageUrl: String = "",
    val address: String = ""
)

data class Booking(
    val id: String,
    val workerId: String,
    val workerName: String,
    val workerCategory: WorkerCategory,
    val customerName: String,
    val date: String,
    val time: String,
    val status: BookingStatus,
    val address: String,
    val totalAmount: Int,
    val notes: String = ""
)

enum class BookingStatus(val displayName: String) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    CONFIRMED("Confirmed"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType {
    BOOKING_UPDATE,
    NEW_REQUEST,
    PAYMENT,
    ALERT,
    PROMOTION
}

data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: Long,
    val imageDescription: String
)

/** Generic home banners (no mock worker names). */
val defaultHomePromoBanners: List<PromoBanner> = listOf(
    PromoBanner(
        id = "pb1",
        title = "Trusted local professionals",
        subtitle = "Book verified electricians, plumbers, carpenters & more across Karnataka",
        backgroundColor = 0xFF1B7A3E,
        imageDescription = "Home services"
    ),
    PromoBanner(
        id = "pb2",
        title = "Fair, transparent pricing",
        subtitle = "Compare rates and book the right expert for your job",
        backgroundColor = 0xFF1A6B5A,
        imageDescription = "Pricing"
    ),
    PromoBanner(
        id = "pb3",
        title = "Grow your skills business",
        subtitle = "List your services and reach customers nearby",
        backgroundColor = 0xFF4E342E,
        imageDescription = "Workers"
    )
)

data class Earning(
    val month: String,
    val amount: Int,
    val jobsCompleted: Int
)
