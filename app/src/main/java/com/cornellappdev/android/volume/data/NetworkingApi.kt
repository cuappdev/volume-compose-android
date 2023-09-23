package com.cornellappdev.android.volume.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.cornellappdev.android.volume.AllArticlesQuery
import com.cornellappdev.android.volume.AllMagazinesQuery
import com.cornellappdev.android.volume.AllPublicationSlugsQuery
import com.cornellappdev.android.volume.AllPublicationsQuery
import com.cornellappdev.android.volume.ArticleByIDQuery
import com.cornellappdev.android.volume.ArticlesByIDsQuery
import com.cornellappdev.android.volume.ArticlesByPublicationSlugQuery
import com.cornellappdev.android.volume.ArticlesByPublicationSlugsQuery
import com.cornellappdev.android.volume.BookmarkArticleMutation
import com.cornellappdev.android.volume.CreateUserMutation
import com.cornellappdev.android.volume.FeaturedMagazinesQuery
import com.cornellappdev.android.volume.FlyersAfterDateQuery
import com.cornellappdev.android.volume.FlyersBeforeDateQuery
import com.cornellappdev.android.volume.FlyersByCategorySlugQuery
import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.FollowPublicationMutation
import com.cornellappdev.android.volume.GetUserQuery
import com.cornellappdev.android.volume.IncrementMagazineShoutoutsMutation
import com.cornellappdev.android.volume.IncrementShoutoutsMutation
import com.cornellappdev.android.volume.IncrementTimesClickedMutation
import com.cornellappdev.android.volume.MagazineByIdQuery
import com.cornellappdev.android.volume.MagazinesByIDsQuery
import com.cornellappdev.android.volume.MagazinesByPublicationSlugQuery
import com.cornellappdev.android.volume.MagazinesBySemesterQuery
import com.cornellappdev.android.volume.OrganizationsByCategoryQuery
import com.cornellappdev.android.volume.PublicationBySlugQuery
import com.cornellappdev.android.volume.ReadArticleMutation
import com.cornellappdev.android.volume.SearchArticlesQuery
import com.cornellappdev.android.volume.SearchFlyersQuery
import com.cornellappdev.android.volume.SearchMagazinesQuery
import com.cornellappdev.android.volume.ShuffledArticlesByPublicationSlugsQuery
import com.cornellappdev.android.volume.TrendingArticlesQuery
import com.cornellappdev.android.volume.TrendingFlyersQuery
import com.cornellappdev.android.volume.UnfollowPublicationMutation
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

    suspend fun fetchAllPublicationSlugs(): ApolloResponse<AllPublicationSlugsQuery.Data> =
        apolloClient.query(AllPublicationSlugsQuery()).execute()

    suspend fun fetchArticleByPublicationSlug(slug: String): ApolloResponse<ArticlesByPublicationSlugQuery.Data> =
        apolloClient.query(ArticlesByPublicationSlugQuery(slug)).execute()

    suspend fun fetchArticlesByPublicationSlugs(slugs: List<String>): ApolloResponse<ArticlesByPublicationSlugsQuery.Data> =
        apolloClient.query(ArticlesByPublicationSlugsQuery(slugs)).execute()

    suspend fun fetchShuffledArticlesByPublicationSlugs(slugs: List<String>): ApolloResponse<ShuffledArticlesByPublicationSlugsQuery.Data> =
        apolloClient.query(ShuffledArticlesByPublicationSlugsQuery(slugs = slugs)).execute()

    suspend fun fetchPublicationBySlug(slug: String): ApolloResponse<PublicationBySlugQuery.Data> =
        apolloClient.query(PublicationBySlugQuery(slug)).execute()

    suspend fun fetchArticlesByIDs(ids: List<String>): ApolloResponse<ArticlesByIDsQuery.Data> =
        apolloClient.query(ArticlesByIDsQuery(ids)).execute()

    suspend fun fetchArticleByID(id: String): ApolloResponse<ArticleByIDQuery.Data> =
        apolloClient.query(ArticleByIDQuery(id)).execute()

    suspend fun fetchSearchedArticles(query: String): ApolloResponse<SearchArticlesQuery.Data> =
        apolloClient.query(SearchArticlesQuery(query = query)).execute()

    suspend fun fetchAllMagazines(limit: Double? = null): ApolloResponse<AllMagazinesQuery.Data> =
        apolloClient.query(AllMagazinesQuery(Optional.presentIfNotNull(limit))).execute()

    suspend fun fetchFeaturedMagazines(limit: Double? = null): ApolloResponse<FeaturedMagazinesQuery.Data> =
        apolloClient.query(FeaturedMagazinesQuery(Optional.presentIfNotNull(limit))).execute()

    suspend fun fetchMagazinesBySemester(
        limit: Double? = null,
        semester: String,
    ): ApolloResponse<MagazinesBySemesterQuery.Data> =
        apolloClient.query(
            MagazinesBySemesterQuery(
                limit = Optional.presentIfNotNull(limit),
                semester = semester
            )
        ).execute()

    suspend fun fetchMagazinesByPublication(
        limit: Double? = null,
        slug: String,
    ): ApolloResponse<MagazinesByPublicationSlugQuery.Data> =
        apolloClient.query(
            MagazinesByPublicationSlugQuery(
                limit = Optional.presentIfNotNull(limit),
                slug = slug
            )
        ).execute()

    suspend fun fetchMagazineById(id: String): ApolloResponse<MagazineByIdQuery.Data> =
        apolloClient.query(
            MagazineByIdQuery(
                id = id
            )
        ).execute()

    suspend fun fetchMagazinesByIds(ids: List<String>): ApolloResponse<MagazinesByIDsQuery.Data> =
        apolloClient.query(
            MagazinesByIDsQuery(
                ids = ids
            )
        ).execute()

    suspend fun fetchSearchedMagazines(query: String): ApolloResponse<SearchMagazinesQuery.Data> =
        apolloClient.query(SearchMagazinesQuery(query = query)).execute()

    suspend fun fetchFlyersByIds(ids: List<String>): ApolloResponse<FlyersByIDsQuery.Data> =
        apolloClient.query(
            FlyersByIDsQuery(
                ids = ids
            )
        ).execute()

    suspend fun fetchTrendingFlyers(): ApolloResponse<TrendingFlyersQuery.Data> =
        apolloClient.query(TrendingFlyersQuery()).execute()

    suspend fun fetchFlyersAfterDate(date: String): ApolloResponse<FlyersAfterDateQuery.Data> =
        apolloClient.query(FlyersAfterDateQuery(since = date)).execute()

    suspend fun fetchFlyersBeforeDate(date: String): ApolloResponse<FlyersBeforeDateQuery.Data> =
        apolloClient.query(FlyersBeforeDateQuery(before = date)).execute()

    suspend fun fetchSearchedFlyers(query: String): ApolloResponse<SearchFlyersQuery.Data> =
        apolloClient.query(SearchFlyersQuery(query = query)).execute()

    suspend fun fetchFlyersByCategorySlug(slug: String): ApolloResponse<FlyersByCategorySlugQuery.Data> =
        apolloClient.query(FlyersByCategorySlugQuery(categorySlug = slug))
            .execute()

    suspend fun fetchOrganizationsByCategory(category: String): ApolloResponse<OrganizationsByCategoryQuery.Data> =
        apolloClient.query(OrganizationsByCategoryQuery(categorySlug = category)).execute()

    suspend fun incrementShoutout(
        id: String,
        uuid: String,
    ): ApolloResponse<IncrementShoutoutsMutation.Data> =
        apolloClient.mutation(IncrementShoutoutsMutation(id, uuid)).execute()

    suspend fun incrementMagazineShoutout(
        id: String,
        uuid: String,
    ): ApolloResponse<IncrementMagazineShoutoutsMutation.Data> =
        apolloClient.mutation(IncrementMagazineShoutoutsMutation(id, uuid)).execute()

    suspend fun incrementTimesClicked(
        id: String,
    ): ApolloResponse<IncrementTimesClickedMutation.Data> =
        apolloClient.mutation(IncrementTimesClickedMutation(id)).execute()

    suspend fun createUser(
        followedPublications: List<String>,
        deviceToken: String,
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
        slug: String,
        uuid: String,
    ): ApolloResponse<FollowPublicationMutation.Data> =
        apolloClient.mutation(FollowPublicationMutation(slug, uuid)).execute()

    suspend fun unfollowPublication(
        slug: String,
        uuid: String,
    ): ApolloResponse<UnfollowPublicationMutation.Data> =
        apolloClient.mutation(UnfollowPublicationMutation(slug, uuid)).execute()

    suspend fun readArticle(
        articleId: String,
        uuid: String,
    ): ApolloResponse<ReadArticleMutation.Data> =
        apolloClient.mutation(ReadArticleMutation(articleId, uuid)).execute()

    suspend fun bookmarkArticle(
        uuid: String,
    ): ApolloResponse<BookmarkArticleMutation.Data> =
        apolloClient.mutation(BookmarkArticleMutation(uuid)).execute()
}
