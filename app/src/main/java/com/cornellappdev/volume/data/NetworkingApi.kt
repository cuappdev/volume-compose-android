package com.cornellappdev.volume.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.cornellappdev.volume.*
import javax.inject.Inject
import javax.inject.Singleton

private const val DEVICE_TYPE: String = "ANDROID"

@Singleton
class NetworkApi @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun fetchAllArticles(limit: Double? = null): ApolloResponse<AllArticlesQuery.Data> =
        apolloClient.query(AllArticlesQuery(Optional.presentIfNotNull(limit))).execute()

    suspend fun fetchTrendingArticles(limit: Double? = null): ApolloResponse<TrendingArticlesQuery.Data> =
        apolloClient.query(TrendingArticlesQuery(Optional.presentIfNotNull(limit))).execute()

    suspend fun fetchAllPublications(): ApolloResponse<AllPublicationsQuery.Data> =
        apolloClient.query(AllPublicationsQuery()).execute()

    suspend fun fetchArticleByPublicationID(pubID: String): ApolloResponse<ArticlesByPublicationIDQuery.Data> =
        apolloClient.query(ArticlesByPublicationIDQuery(pubID)).execute()

    suspend fun fetchArticlesByPublicationIDs(pubIDs: List<String>): ApolloResponse<ArticlesByPublicationIDsQuery.Data> =
        apolloClient.query(ArticlesByPublicationIDsQuery(pubIDs)).execute()

    suspend fun fetchPublicationsByIDs(pubIDs: List<String>): ApolloResponse<PublicationsByIDsQuery.Data> =
        apolloClient.query(PublicationsByIDsQuery(pubIDs)).execute()

    suspend fun fetchPublicationByID(pubID: String): ApolloResponse<PublicationByIDQuery.Data> =
        apolloClient.query(PublicationByIDQuery(pubID)).execute()

    suspend fun fetchArticlesByIDs(ids: List<String>): ApolloResponse<ArticlesByIDsQuery.Data> =
        apolloClient.query(ArticlesByIDsQuery(ids)).execute()

    suspend fun fetchArticleByID(id: String): ApolloResponse<ArticleByIDQuery.Data> =
        apolloClient.query(ArticleByIDQuery(id)).execute()

    suspend fun incrementShoutout(
        id: String,
        uuid: String
    ): ApolloResponse<IncrementShoutoutMutation.Data> =
        apolloClient.mutation(IncrementShoutoutMutation(id, uuid)).execute()

    suspend fun createUser(
        followedPublications: List<String>,
        deviceToken: String
    ): ApolloResponse<CreateUserMutation.Data> = apolloClient.mutation(
        CreateUserMutation(
            DEVICE_TYPE,
            followedPublications,
            deviceToken
        )
    ).execute()

    suspend fun getUser(uuid: String): ApolloResponse<GetUserQuery.Data> =
        apolloClient.query(GetUserQuery(uuid)).execute()

    suspend fun followPublication(
        pubID: String,
        uuid: String
    ): ApolloResponse<FollowPublicationMutation.Data> =
        apolloClient.mutation(FollowPublicationMutation(pubID, uuid)).execute()

    suspend fun unfollowPublication(
        pubID: String,
        uuid: String
    ): ApolloResponse<UnfollowPublicationMutation.Data> =
        apolloClient.mutation(UnfollowPublicationMutation(pubID, uuid)).execute()

    suspend fun readArticle(
        articleId: String,
        uuid: String
    ): ApolloResponse<ReadArticleMutation.Data> =
        apolloClient.mutation(ReadArticleMutation(articleId, uuid)).execute()

    suspend fun bookmarkArticle(
        uuid: String
    ): ApolloResponse<BookmarkArticleMutation.Data> =
        apolloClient.mutation(BookmarkArticleMutation(uuid)).execute()

}
