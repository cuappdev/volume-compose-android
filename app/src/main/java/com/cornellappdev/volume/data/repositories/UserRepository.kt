package com.cornellappdev.volume.data.repositories

import com.cornellappdev.volume.CreateUserMutation
import com.cornellappdev.volume.FollowPublicationMutation
import com.cornellappdev.volume.GetUserQuery
import com.cornellappdev.volume.UnfollowPublicationMutation
import com.cornellappdev.volume.data.NetworkingApi
import com.cornellappdev.volume.data.models.Article
import com.cornellappdev.volume.data.models.User
import com.cornellappdev.volume.data.models.WeeklyDebrief

/**
 * User Repository. Encapsulate the logic required to access data sources, particularly
 * those depending on the User model and mutations/queries on Users.
 *
 * @see Article
 */
object UserRepository {

    suspend fun createUser(
        followPublications: List<String>,
        deviceToken: String
    ): User =
        NetworkingApi.createUser(followPublications, deviceToken).dataAssertNoErrors.mapDataToUser()

    suspend fun followPublications(pubIDs: List<String>, uuid: String) =
        pubIDs.map {
            followPublication(it, uuid)
        }

    suspend fun followPublication(pubID: String, uuid: String): User =
        NetworkingApi.followPublication(pubID, uuid).dataAssertNoErrors.mapDataToUser()

    suspend fun unfollowPublication(
        pubID: String,
        uuid: String
    ): User =
        NetworkingApi.unfollowPublication(pubID, uuid).dataAssertNoErrors.mapDataToUser()

    // Only getUser returns a User with WeeklyDebrief, can be updated in queries.graphql
    suspend fun getUser(uuid: String): User =
        NetworkingApi.getUser(uuid).dataAssertNoErrors.mapDataToUser()

    private fun GetUserQuery.Data.mapDataToUser(): User {
        return this.getUser.let { userData ->
            val weeklyDebrief = userData!!.weeklyDebrief!!
            User(
                uuid = userData.uuid,
                followedPublicationIDs = userData.followedPublications.map {
                    it.id
                },
                weeklyDebrief = WeeklyDebrief(
                    uuid = weeklyDebrief.uuid,
                    readArticlesIDs = weeklyDebrief.readArticles.map {
                        it.id
                    },
                    randomArticlesIDs = weeklyDebrief.readArticles.map {
                        it.id
                    },
                    creationDate = weeklyDebrief.creationDate.toString(),
                    expirationDate = weeklyDebrief.expirationDate.toString(),
                    numShoutouts = weeklyDebrief.numShoutouts.toInt(),
                    numBookmarkedArticles = weeklyDebrief.numBookmarkedArticles.toInt(),
                    numReadArticles = weeklyDebrief.numReadArticles.toInt()
                )
            )

        }
    }

    private fun CreateUserMutation.Data.mapDataToUser(): User {
        return this.createUser.let { userData ->
            User(
                uuid = userData.uuid,
                followedPublicationIDs = userData.followedPublications.map {
                    it.id
                }
            )
        }
    }

    private fun FollowPublicationMutation.Data.mapDataToUser(): User {
        return this.followPublication.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationIDs = userData.followedPublications.map {
                    it.id
                }
            )
        }
    }

    private fun UnfollowPublicationMutation.Data.mapDataToUser(): User {
        return this.unfollowPublication.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationIDs = userData.followedPublications.map {
                    it.id
                }
            )
        }
    }
}
