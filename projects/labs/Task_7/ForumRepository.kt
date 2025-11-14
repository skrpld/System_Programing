import android.util.Log
import com.example.hyperlocal_forum.data.models.firestore.Topic
import com.example.hyperlocal_forum.di.GeoUtils
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * Несколько примеров функций (содержащих обработку исключений)
 * для работы с БД Cloud Firestore в моём курсовом проекте
 */

class ForumRepository(
    private val forumDao: ForumDao
) {
    private val db = FirebaseFirestore.getInstance()

    private val topicsCollection = db.collection("topics")

    /**
     * Суть исключения данного метода это выводить
     * логи в консоль для удобства отладки и
     * разработки приложения
     *
     * Updates the content of a specific topic in Firestore.
     * @param topicId The ID of the topic to update.
     * @param newContent The new content string for the topic.
     */
    suspend fun updateTopicContent(topicId: String, newContent: String) {
        try {
            val topicData = mapOf(
                "content" to newContent
            )
            topicsCollection.document(topicId).update(topicData).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating topic content", e)
            throw e
        }
    }

    /**
     * Суть исключения данного метода в том,что если
     * вдруг у пользователя не будет доступа к интернету
     * или же серверу, то вместо стандартной загрузки элементов
     * экрана с сервера, отобразятся локальные данные,
     * которые ранее были загружены на устройство
     *
     * Retrieves all topics from Firestore, ordered by timestamp.
     * If there's a network error, it falls back to the local database cache.
     * @return A Flow emitting a list of all Topic objects.
     */
    fun getAllTopics(): Flow<List<Topic>> = flow {
        try {
            val snapshot = topicsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val topics = snapshot.documents.mapNotNull { doc ->
                val geoPoint = doc.getGeoPoint("location")
                val timestamp = doc.getTimestamp("timestamp")
                if (geoPoint != null && timestamp != null) {
                    Topic(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        location = GeoCoordinates(geoPoint.latitude, geoPoint.longitude),
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        timestamp = timestamp
                    )
                } else {
                    null
                }
            }
            emit(topics)
        } catch (e: Exception) {
            val localTopics = forumDao.getAllTopics().first().map { localTopic ->
                Topic(
                    id = localTopic.id,
                    userId = localTopic.userId,
                    location = GeoCoordinates(localTopic.latitude, localTopic.longitude),
                    title = localTopic.title,
                    content = localTopic.content,
                    timestamp = Timestamp(localTopic.timestamp / 1000, 0)
                )
            }
            emit(localTopics)
        }
    }

    /**
     * Суть исключения в этом методе это отображение ошибки
     * которую вернёт сервер при некорректном обновлении БД
     *
     * Updates a user's data in Firestore and the local database.
     * @param user The User object with updated information.
     * @return True if the update was successful, false otherwise.
     */
    suspend fun updateUser(user: User): Boolean {
        return try {
            val userData = hashMapOf(
                "username" to user.username,
                "email" to user.email,
                "timestamp" to user.timestamp
            )

            usersCollection.document(user.id).update(userData as Map<String, Any>).await()

            val localUser = LocalUser(
                id = user.id,
                username = user.username,
                passwordHash = "",
                email = user.email,
                timestamp = user.timestamp.seconds * 1000
            )
            forumDao.updateUser(localUser)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
}