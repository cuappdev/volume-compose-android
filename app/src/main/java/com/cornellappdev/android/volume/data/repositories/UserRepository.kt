package com.cornellappdev.android.volume.data.repositories

import com.cornellappdev.android.volume.CreateUserMutation
import com.cornellappdev.android.volume.FollowOrganizationMutation
import com.cornellappdev.android.volume.FollowPublicationMutation
import com.cornellappdev.android.volume.GetUserQuery
import com.cornellappdev.android.volume.UnfollowOrganizationMutation
import com.cornellappdev.android.volume.UnfollowPublicationMutation
import com.cornellappdev.android.volume.data.NetworkApi
import com.cornellappdev.android.volume.data.models.Article
import com.cornellappdev.android.volume.data.models.User
import com.cornellappdev.android.volume.data.models.WeeklyDebrief
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User Repository. Encapsulate the logic required to access data sources, particularly
 * those depending on the User model and mutations/queries on Users.
 *
 * @see Article
 */
private const val TAG = "UserRepository"

@Singleton
class UserRepository @Inject constructor(private val networkApi: NetworkApi) {

    suspend fun createUser(
        followPublications: List<String>,
        deviceToken: String,
    ): User =
        networkApi.createUser(followPublications, deviceToken).dataAssertNoErrors.mapDataToUser()

    suspend fun followPublications(slugs: List<String>, uuid: String) =
        slugs.map {
            followPublication(it, uuid)
        }

    suspend fun followPublication(slug: String, uuid: String): User =
        networkApi.followPublication(slug, uuid).dataAssertNoErrors.mapDataToUser()

    suspend fun followOrganization(slug: String, uuid: String): User =
        networkApi.followOrganization(slug, uuid).dataAssertNoErrors.mapDataToUser()

    suspend fun unfollowPublication(
        slug: String,
        uuid: String,
    ): User =
        networkApi.unfollowPublication(slug, uuid).dataAssertNoErrors.mapDataToUser()

    suspend fun unfollowOrganization(
        slug: String,
        uuid: String,
    ): User =
        networkApi.unfollowOrganization(slug, uuid).dataAssertNoErrors.mapDataToUser()

    // Only getUser returns a User with WeeklyDebrief, can be updated in queries.graphql
    suspend fun getUser(uuid: String): User =
        networkApi.getUser(uuid).dataAssertNoErrors.mapDataToUser()

    suspend fun readArticle(articleId: String, uuid: String) {
        networkApi.readArticle(articleId, uuid)
    }

    suspend fun bookmarkArticle(articleId: String, uuid: String) {
        networkApi.bookmarkArticle(articleId, uuid)
    }

    suspend fun bookmarkMagazine(magazineId: String, uuid: String) {
        networkApi.bookmarkMagazine(magazineId, uuid)
    }

    suspend fun bookmarkFlyer(flyerId: String, uuid: String) {
        networkApi.bookmarkFlyer(flyerId, uuid)
    }

    private fun GetUserQuery.Data.mapDataToUser(): User {
        return this.getUser.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationSlugs = userData.followedPublications.map {
                    it.slug
                },
                weeklyDebrief = userData.weeklyDebrief?.let { weeklyDebrief ->
                    WeeklyDebrief(
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
                },
                followedOrganizationSlugs = userData.followedOrganizations.map { it.slug }
            )
        }
    }

    private fun CreateUserMutation.Data.mapDataToUser(): User {
        return this.createUser.let { userData ->
            User(
                uuid = userData.uuid,
                followedPublicationSlugs = userData.followedPublications.map {
                    it.slug
                },
                followedOrganizationSlugs = userData.followedOrganizations.map { it.slug }
            )
        }
    }

    private fun FollowPublicationMutation.Data.mapDataToUser(): User {
        return this.followPublication.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationSlugs = userData.followedPublications.map {
                    it.slug
                },
                followedOrganizationSlugs = userData.followedOrganizations.map { it.slug }
            )
        }
    }

    private fun FollowOrganizationMutation.Data.mapDataToUser(): User {
        return this.followOrganization.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationSlugs = userData.followedPublications.map {
                    it.slug
                },
                followedOrganizationSlugs = userData.followedOrganizations.map { it.slug }
            )
        }
    }

    private fun UnfollowPublicationMutation.Data.mapDataToUser(): User {
        return this.unfollowPublication.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationSlugs = userData.followedPublications.map {
                    it.slug
                },
                followedOrganizationSlugs = userData.followedOrganizations.map { it.slug }
            )
        }
    }

    private fun UnfollowOrganizationMutation.Data.mapDataToUser(): User {
        return this.unfollowOrganization.let { userData ->
            User(
                uuid = userData!!.uuid,
                followedPublicationSlugs = userData.followedPublications.map {
                    it.slug
                },
                followedOrganizationSlugs = userData.followedOrganizations.map { it.slug }
            )
        }
    }
}
