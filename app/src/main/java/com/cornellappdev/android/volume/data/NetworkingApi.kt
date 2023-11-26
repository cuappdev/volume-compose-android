package com.cornellappdev.android.volume.data

import android.content.Context
import android.net.Uri
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.cornellappdev.android.volume.AllArticlesQuery
import com.cornellappdev.android.volume.AllMagazinesQuery
import com.cornellappdev.android.volume.AllOrganizationsQuery
import com.cornellappdev.android.volume.AllPublicationSlugsQuery
import com.cornellappdev.android.volume.AllPublicationsQuery
import com.cornellappdev.android.volume.ArticleByIDQuery
import com.cornellappdev.android.volume.ArticlesByIDsQuery
import com.cornellappdev.android.volume.ArticlesByPublicationSlugQuery
import com.cornellappdev.android.volume.ArticlesByPublicationSlugsQuery
import com.cornellappdev.android.volume.BookmarkArticleMutation
import com.cornellappdev.android.volume.BookmarkMagazineMutation
import com.cornellappdev.android.volume.BuildConfig
import com.cornellappdev.android.volume.CheckAccessCodeQuery
import com.cornellappdev.android.volume.CreateUserMutation
import com.cornellappdev.android.volume.DeleteFlyerMutation
import com.cornellappdev.android.volume.FeaturedMagazinesQuery
import com.cornellappdev.android.volume.FlyerByIDQuery
import com.cornellappdev.android.volume.FlyersAfterDateQuery
import com.cornellappdev.android.volume.FlyersBeforeDateQuery
import com.cornellappdev.android.volume.FlyersByCategorySlugQuery
import com.cornellappdev.android.volume.FlyersByIDsQuery
import com.cornellappdev.android.volume.FlyersByOrganizationSlugQuery
import com.cornellappdev.android.volume.FollowOrganizationMutation
import com.cornellappdev.android.volume.FollowPublicationMutation
import com.cornellappdev.android.volume.GetUserQuery
import com.cornellappdev.android.volume.IncrementMagazineShoutoutsMutation
import com.cornellappdev.android.volume.IncrementShoutoutsMutation
import com.cornellappdev.android.volume.IncrementTimesClickedMutation
import com.cornellappdev.android.volume.MagazineByIdQuery
import com.cornellappdev.android.volume.MagazinesByIDsQuery
import com.cornellappdev.android.volume.MagazinesByPublicationSlugQuery
import com.cornellappdev.android.volume.MagazinesBySemesterQuery
import com.cornellappdev.android.volume.OrganizationBySlugQuery
import com.cornellappdev.android.volume.OrganizationsByIdQuery
import com.cornellappdev.android.volume.PublicationBySlugQuery
import com.cornellappdev.android.volume.ReadArticleMutation
import com.cornellappdev.android.volume.SearchArticlesQuery
import com.cornellappdev.android.volume.SearchFlyersQuery
import com.cornellappdev.android.volume.SearchMagazinesQuery
import com.cornellappdev.android.volume.ShuffledArticlesByPublicationSlugsQuery
import com.cornellappdev.android.volume.TrendingArticlesQuery
import com.cornellappdev.android.volume.TrendingFlyersQuery
import com.cornellappdev.android.volume.UnfollowOrganizationMutation
import com.cornellappdev.android.volume.UnfollowPublicationMutation
import com.cornellappdev.android.volume.data.models.Flyer
import com.cornellappdev.android.volume.util.deriveFileName
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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

    suspend fun fetchAllOrganizations(): ApolloResponse<AllOrganizationsQuery.Data> =
        apolloClient.query(AllOrganizationsQuery()).execute()

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

    suspend fun fetchFlyersByOrganizationSlug(slug: String): ApolloResponse<FlyersByOrganizationSlugQuery.Data> =
        apolloClient.query(FlyersByOrganizationSlugQuery(slug = slug)).execute()

    suspend fun fetchOrganizationBySlug(slug: String): ApolloResponse<OrganizationBySlugQuery.Data> =
        apolloClient.query(OrganizationBySlugQuery(slug = slug)).execute()

    suspend fun fetchOrganizationById(id: String): ApolloResponse<OrganizationsByIdQuery.Data> =
        apolloClient.query(OrganizationsByIdQuery(id = id)).execute()

    suspend fun fetchFlyerById(id: String): ApolloResponse<FlyerByIDQuery.Data> =
        apolloClient.query(FlyerByIDQuery(id = id)).execute()


    /**
     * Function to mutate a Flyer.
     * Takes the intended Flyer object, an imageUri for a new image for the flyer,
     * @param flyer the flyer to send to the backend
     * @param imageUri the image URI to use to update the flyer
     * @param context application context
     * @param organizationId the id of the organization uploading the flyer
     * @param isUpdating whether the Flyer should be created or updated
     * @return boolean that represents whether the mutation was successful
     */
    fun mutateFlyer(
        flyer: Flyer,
        imageUri: Uri?,
        context: Context,
        isUpdating: Boolean,
    ): Boolean {
        val client = OkHttpClient()

        val formBody =
            createFlyerFormData(flyer, imageUri, isUpdating = isUpdating, context = context)

        val expressEndpoint = BuildConfig.ENDPOINT.replace("/graphql", "")

        val request =
            Request.Builder()
                .url(if (isUpdating) "$expressEndpoint/flyers/edit/" else "$expressEndpoint/flyers/")
                .post(formBody)
                .build()

        client.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    /**
     * Returns a MultipartBody for form data requests based on provided flyer
     * As with other functions in NetworkApi, this function is unsafe and should only be used in
     * a try-catch statement.
     */
    private fun createFlyerFormData(
        flyer: Flyer,
        imageUri: Uri?,
        isUpdating: Boolean,
        context: Context,
    ) =
        MultipartBody.Builder().apply {
            setType(MultipartBody.FORM)

            // Using a non-null assertions since all of the following has to be non-null for the upload to succeed.
            // Also this method will only be called by network API which should be put in try catch
            if (!isUpdating) {
                // Assert that the image URI is non-null if they are creating a new Flyer
                imageUri!!
            } else {
                // If we are updating a flyer we need to know the id of the flyer we should update
                assert(flyer.id.isNotBlank())
                addFormDataPart("flyerID", flyer.id)
            }
            val contentResolver = context.contentResolver

            addFormDataPart("categorySlug", flyer.categorySlug)
            addFormDataPart("endDate", flyer.endDate)
            addFormDataPart("flyerURL", flyer.flyerURL ?: flyer.organization.websiteURL)
            addFormDataPart("location", flyer.location)
            addFormDataPart("organizationID", flyer.organization.id)
            addFormDataPart("startDate", flyer.startDate)
            addFormDataPart("title", flyer.title)
            imageUri?.let {
                val type = contentResolver.getType(it)!!.toMediaType()
                val filename = deriveFileName(it, context)!!
                val file = File(context.cacheDir, filename)

                contentResolver.openInputStream(imageUri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                addFormDataPart("image", filename, file.asRequestBody(type))
            }

            if (isUpdating) {
                addFormDataPart("id", flyer.id)
            }
        }.build()


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

    suspend fun verifyAccessCode(
        accessCode: String,
        organizationSlug: String,
    ): ApolloResponse<CheckAccessCodeQuery.Data> = apolloClient.query(
        CheckAccessCodeQuery(
            accessCode = accessCode,
            slug = organizationSlug
        )
    ).execute()

    suspend fun deleteFlyer(id: String): ApolloResponse<DeleteFlyerMutation.Data> =
        apolloClient.mutation(DeleteFlyerMutation(id)).execute()

    suspend fun getUser(uuid: String): ApolloResponse<GetUserQuery.Data> =
        apolloClient.query(GetUserQuery(uuid)).execute()

    suspend fun followPublication(
        slug: String,
        uuid: String,
    ): ApolloResponse<FollowPublicationMutation.Data> =
        apolloClient.mutation(FollowPublicationMutation(slug, uuid)).execute()

    suspend fun followOrganization(
        slug: String,
        uuid: String,
    ): ApolloResponse<FollowOrganizationMutation.Data> =
        apolloClient.mutation(FollowOrganizationMutation(slug, uuid)).execute()

    suspend fun unfollowPublication(
        slug: String,
        uuid: String,
    ): ApolloResponse<UnfollowPublicationMutation.Data> =
        apolloClient.mutation(UnfollowPublicationMutation(slug, uuid)).execute()

    suspend fun unfollowOrganization(
        slug: String,
        uuid: String,
    ): ApolloResponse<UnfollowOrganizationMutation.Data> =
        apolloClient.mutation(UnfollowOrganizationMutation(slug, uuid)).execute()

    suspend fun readArticle(
        articleId: String,
        uuid: String,
    ): ApolloResponse<ReadArticleMutation.Data> =
        apolloClient.mutation(ReadArticleMutation(articleId, uuid)).execute()

    suspend fun bookmarkArticle(
        articleId: String,
        uuid: String,
    ): ApolloResponse<BookmarkArticleMutation.Data> =
        apolloClient.mutation(BookmarkArticleMutation(articleId, uuid)).execute()

    suspend fun bookmarkMagazine(
        magazineId: String,
        uuid: String,
    ): ApolloResponse<BookmarkMagazineMutation.Data> =
        apolloClient.mutation(BookmarkMagazineMutation(magazineId, uuid)).execute()

    suspend fun bookmarkFlyer(
        magazineId: String,
        uuid: String,
    ): ApolloResponse<BookmarkMagazineMutation.Data> =
        apolloClient.mutation(BookmarkMagazineMutation(magazineId, uuid)).execute()
}
