package com.cornellappdev.volume.data.repositories

import com.cornellappdev.volume.CreateUserMutation
import com.cornellappdev.volume.FollowPublicationMutation
import com.cornellappdev.volume.UnfollowPublicationMutation
import com.cornellappdev.volume.data.NetworkingApi
import com.cornellappdev.volume.data.models.User

class UserRepository(private val webService: NetworkingApi) {
    suspend fun createUser(
        followPublications: List<String>,
        deviceToken: String
    ): User =
        webService.createUser(followPublications, deviceToken).dataAssertNoErrors.mapDataToUser()

    suspend fun followPublications(pubIDs: List<String>, uuid: String) =
        pubIDs.map {
            followPublication(it, uuid)
        }

    suspend fun followPublication(pubID: String, uuid: String): User =
        webService.followPublication(pubID, uuid).dataAssertNoErrors.mapDataToUser()

    suspend fun unfollowPublication(
        pubID: String,
        uuid: String
    ): User =
        webService.unfollowPublication(pubID, uuid).dataAssertNoErrors.mapDataToUser()

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
                uuid = userData.uuid,
                followedPublicationIDs = userData.followedPublications.map {
                    it.id
                }
            )
        }
    }

    private fun UnfollowPublicationMutation.Data.mapDataToUser(): User {
        return this.unfollowPublication.let { userData ->
            User(
                uuid = userData.uuid,
                followedPublicationIDs = userData.followedPublications.map {
                    it.id
                }
            )
        }
    }
}
