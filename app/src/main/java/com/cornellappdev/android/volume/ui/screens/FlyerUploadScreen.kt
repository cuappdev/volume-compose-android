package com.cornellappdev.android.volume.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cornellappdev.android.volume.ui.components.general.ErrorMessage
import com.cornellappdev.android.volume.ui.components.general.OutlinedVolumeButton
import com.cornellappdev.android.volume.ui.components.general.VolumeButton
import com.cornellappdev.android.volume.ui.components.general.VolumeInputContainer
import com.cornellappdev.android.volume.ui.components.general.VolumeLoading
import com.cornellappdev.android.volume.ui.components.general.VolumeTextField
import com.cornellappdev.android.volume.ui.states.ResponseState
import com.cornellappdev.android.volume.ui.theme.GrayFive
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
import com.cornellappdev.android.volume.ui.viewmodels.FlyerUploadViewModel
import com.cornellappdev.android.volume.util.FlyerConstants
import com.cornellappdev.android.volume.util.letIfAllNotNull
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun FlyerUploadScreen(
    organizationSlug: String,
    onFlyerUploadSuccess: () -> Unit,
    flyerUploadViewModel: FlyerUploadViewModel = hiltViewModel(),
    editingFlyerId: String? = null,
) {
    val context = LocalContext.current

    val isEditing = editingFlyerId != null

    var flyerTitle by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var redirectLink: String by remember { mutableStateOf("") }
    var startDate: LocalDate? by remember { mutableStateOf(null) }
    var endDate: LocalDate? by remember { mutableStateOf(null) }
    var startTime: LocalTime? by remember { mutableStateOf(null) }
    var endTime: LocalTime? by remember { mutableStateOf(null) }
    var flyerImageUri: Uri? by remember { mutableStateOf(null) }
    var flyerCategory: String by remember { mutableStateOf("") }
    var hasTimeError: Boolean by remember { mutableStateOf(false) }
    var currentErrorMessage: String by remember { mutableStateOf("Flyer upload failed") }

    var uploadEnabled by remember { mutableStateOf(false) }
    var hasTriedUpload by remember { mutableStateOf(false) }

    // Start by getting info about their organization and potentially the Flyer if they are editing
    LaunchedEffect(key1 = "launch") {
        flyerUploadViewModel.initViewModel(
            organizationSlug = organizationSlug,
            flyerId = editingFlyerId,
        )
    }

    val organization = flyerUploadViewModel.orgFlow.collectAsState().value
    val flyer = flyerUploadViewModel.flyerFlow.collectAsState().value
    val uploadResult = flyerUploadViewModel.uploadResultFlow.collectAsState().value

    DisposableEffect(key1 = flyer, effect = {
        if (flyer is ResponseState.Success) {
            val flyerData = flyer.data
            flyerTitle = flyerData.title
            location = flyerData.location
            redirectLink = flyerData.flyerURL ?: ""
            startDate = flyerData.startDateTime.toLocalDate()
            endDate = flyerData.endDateTime.toLocalDate()
            startTime = flyerData.startDateTime.toLocalTime()
            endTime = flyerData.endDateTime.toLocalTime()
            flyerCategory = flyerData.categorySlug

            val tags = FlyerConstants.CATEGORY_SLUGS.split(",")
            val displayedCategory = FlyerConstants.FORMATTED_TAGS.firstOrNull { s ->
                s.replace(" ", "").replaceFirstChar { it.lowercase() } == flyerData.categorySlug
            }
            flyerCategory = displayedCategory ?: "Error retrieving category, select again"
        }
        onDispose { }
    })

    // Effect to update whether button is enabled based on form values
    DisposableEffect(
        key1 = arrayOf(
            flyerTitle,
            location,
            redirectLink,
            startDate,
            endDate,
            startTime,
            endTime,
            flyerImageUri,
            flyerCategory
        )
    ) {
        letIfAllNotNull(startTime, endTime) { times ->
            val (st, et) = times
            letIfAllNotNull(startDate, endDate) { dates ->
                val (sd, ed) = dates
                hasTimeError = LocalDateTime.of(ed, et) < LocalDateTime.of(sd, st)
            }
            st
        }

        uploadEnabled = flyerTitle.isNotBlank() &&
                location.isNotBlank() &&
                startDate != null &&
                endDate != null &&
                startTime != null &&
                endTime != null &&
                flyerImageUri != null &&
                flyerCategory.isNotBlank() &&
                flyerCategory != "Error retrieving category, select again" &&
                !hasTimeError

        onDispose { }
    }

    var categoryDropdownShowing by remember { mutableStateOf(false) }

    var fileName: String? by remember { mutableStateOf(null) }

    val startDatePickerDialog = rememberMaterialDialogState()
    val endDatePickerDialog = rememberMaterialDialogState()
    val startTimePickerDialog = rememberMaterialDialogState()
    val endTimePickerDialog = rememberMaterialDialogState()

    val categories: List<String> = FlyerConstants.FORMATTED_TAGS

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            flyerImageUri = uri
        }

    // Effect to update image upload text based on flyer name
    DisposableEffect(key1 = flyerImageUri, effect = {
        val contentResolver = context.contentResolver
        flyerImageUri?.let { uri ->
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val colIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (colIndex != -1) {
                    val displayName = cursor.getString(colIndex)
                    fileName = displayName
                }
                cursor.close()
            }
            cursor?.close()
        }
        onDispose { }
    })

    // Styles for date and time pickers
    val datePickerColors = DatePickerDefaults.colors(
        headerBackgroundColor = VolumeOrange,
        dateActiveBackgroundColor = VolumeOrange,
        headerTextColor = Color.White,
        dateActiveTextColor = Color.White
    )
    val timePickerColors = TimePickerDefaults.colors(
        activeTextColor = Color.White,
        inactiveTextColor = Color.Black,
        selectorColor = VolumeOrange,
        activeBackgroundColor = VolumeOrange,
        inactiveBackgroundColor = Color.White,
        inactivePeriodBackground = Color.White,
        borderColor = Color.Black,
    )


    // Dialogs for date and time picking
    MaterialDialog(
        dialogState = startDatePickerDialog,
        buttons = {
            positiveButton("Ok", textStyle = TextStyle(color = VolumeOrange))
            negativeButton("Cancel", textStyle = TextStyle(color = VolumeOrange))
        }
    ) {
        datepicker(
            colors = datePickerColors
        ) { date: LocalDate ->
            startDate = date
            startTimePickerDialog.show()
        }
    }
    MaterialDialog(
        dialogState = endDatePickerDialog,
        buttons = {
            positiveButton("Ok", textStyle = TextStyle(color = VolumeOrange))
            negativeButton("Cancel", textStyle = TextStyle(color = VolumeOrange))
        }
    ) {
        datepicker(
            colors = datePickerColors
        ) { date: LocalDate ->
            endDate = date
            endTimePickerDialog.show()
        }
    }
    MaterialDialog(
        dialogState = startTimePickerDialog,
        buttons = {
            positiveButton("Ok", textStyle = TextStyle(color = VolumeOrange))
            negativeButton("Cancel", textStyle = TextStyle(color = VolumeOrange))
        }
    ) {
        timepicker(colors = timePickerColors) { time: LocalTime ->
            startTime = time
        }
    }
    MaterialDialog(
        dialogState = endTimePickerDialog,
        buttons = {
            positiveButton("Ok", textStyle = TextStyle(color = VolumeOrange))
            negativeButton("Cancel", textStyle = TextStyle(color = VolumeOrange))
        }
    ) {
        timepicker(colors = timePickerColors) { time: LocalTime ->
            endTime = time
        }
    }

    // Set up upload functionality
    fun onUploadClick() {
        hasTriedUpload = true
        try {
            val bytes =
                flyerImageUri?.let {
                    context.contentResolver.openInputStream(it)?.readBytes()
                }
            if (bytes == null) {
                currentErrorMessage = "Failed to upload flyer."
                flyerUploadViewModel.errorFlyerUpload()
            } else if (bytes.size >= (50000000)) {
                currentErrorMessage =
                    "Image too large, please use a lower quality image."
                flyerUploadViewModel.errorFlyerUpload()
            } else {
                letIfAllNotNull(startDate, endDate) { (sd, ed) ->
                    letIfAllNotNull(startTime, endTime) { (st, et) ->
                        val startDateString =
                            LocalDateTime.of(sd, st).atZone(ZoneId.systemDefault())
                                .format(
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                )

                        val endDateString =
                            LocalDateTime.of(ed, et)
                                .atZone(ZoneOffset.systemDefault())
                                .format(
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                )

                        flyerUploadViewModel.uploadFlyer(
                            title = flyerTitle,
                            startDate = startDateString,
                            location = location,
                            flyerURL = if (redirectLink.isBlank()) "" else (if (redirectLink.startsWith(
                                    "http://"
                                ) || redirectLink.startsWith("https://")
                            ) redirectLink else "http://$redirectLink"),
                            endDate = endDateString,
                            categorySlug = flyerCategory.replace(" ", "")
                                .replaceFirstChar { c -> c.lowercase() },
                            imageBase64 = Base64.encodeToString(
                                bytes,
                                Base64.DEFAULT
                            ),
                            organizationId = organizationSlug
                        )
                    }
                    sd
                }
            }
        } catch (ignored: Exception) {
            flyerUploadViewModel.errorFlyerUpload()
        }
    }


    // Screen starts
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Settings title
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontFamily = notoserif,
                modifier = Modifier.padding(top = 16.dp),
            )
        }

        // Organization name
        Column(modifier = Modifier.padding(top = 14.dp)) {
            Text(text = "Organization", fontFamily = notoserif, fontSize = 16.sp, color = GrayFive)
            when (organization) {
                is ResponseState.Success -> {
                    Text(text = organization.data.name, fontSize = 24.sp, fontFamily = notoserif)
                }

                is ResponseState.Error -> {
                    Text(
                        text = "Failed to load organization, try again",
                        fontSize = 24.sp,
                        fontFamily = notoserif
                    )
                }
            }
        }

        // Allow the user to delete their flyer if the user is editing
        if (isEditing) {
            OutlinedVolumeButton(
                text = "Remove Flyer",
                onClick = {/* TODO */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Removal icon",
                    tint = VolumeOrange
                )
            }
        }

        // Flyer name input
        Column {
            Text(text = "Flyer Name", fontFamily = notoserif, fontSize = 16.sp, color = GrayFive)
            Spacer(Modifier.height(8.dp))
            VolumeTextField(
                value = flyerTitle,
                onValueChange = { flyerTitle = it },
                modifier = Modifier.height(48.dp)
            )
        }

        // Date inputs
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start Time",
                        fontFamily = notoserif,
                        fontSize = 16.sp,
                        color = GrayFive
                    )
                    VolumeInputContainer(onClick = { startDatePickerDialog.show() }, icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = ""
                        )
                    }) {
                        Text(
                            text = formatDateTime(startTime, startDate),
                            fontSize = 14.sp,
                            color = GrayFive
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End Time",
                        fontFamily = notoserif,
                        fontSize = 16.sp,
                        color = GrayFive
                    )
                    VolumeInputContainer(onClick = { endDatePickerDialog.show() }, icon = {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = ""
                        )
                    }) {
                        Text(
                            text = formatDateTime(endTime, endDate),
                            fontSize = 14.sp,
                            color = GrayFive
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (hasTimeError) {
                ErrorMessage(message = "End time must be after start time.")
            }
        }

        // Location input
        Column {
            Column {
                Text(
                    text = "Location",
                    fontFamily = notoserif,
                    fontSize = 16.sp,
                    color = GrayFive
                )
                Spacer(Modifier.height(8.dp))
                VolumeTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.height(48.dp)
                )
            }
        }

        // Category input
        Column {
            Text(
                text = "Category",
                fontFamily = notoserif,
                fontSize = 16.sp,
                color = GrayFive
            )
            Spacer(Modifier.height(8.dp))
            Box {
                VolumeInputContainer(onClick = {
                    categoryDropdownShowing = !categoryDropdownShowing
                }, icon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = "Dropdown arrow"
                    )
                }) {
                    Text(
                        text = flyerCategory,
                        fontSize = 14.sp,
                        color = GrayFive,
                        fontFamily = lato
                    )
                }
                DropdownMenu(
                    expanded = categoryDropdownShowing,
                    onDismissRequest = {
                        categoryDropdownShowing = false
                    }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category, fontFamily = lato, color = GrayFive) },
                            onClick = {
                                categoryDropdownShowing = false
                                flyerCategory = category
                            })
                    }
                }
            }
        }

        // Flyer redirect link input
        Column {
            Text(
                text = "Flyer Redirect Link",
                fontFamily = notoserif,
                fontSize = 16.sp,
                color = GrayFive
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Optional: Clicking on the flyer in app will take you to this link",
                fontFamily = lato,
                fontSize = 10.sp,
                color = GrayOne
            )
            Spacer(Modifier.height(8.dp))
            VolumeTextField(
                value = redirectLink,
                onValueChange = { redirectLink = it.ifBlank { "" } },
                modifier = Modifier.height(48.dp)
            )
        }

        // Upload image button
        OutlinedVolumeButton(text = fileName ?: "Select an image...", onClick = {
            imagePickerLauncher.launch(
                PickVisualMediaRequest(
                    mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }, icon = {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Camera",
                tint = VolumeOrange,
                modifier = Modifier.size(16.dp)
            )
        }, modifier = Modifier.fillMaxWidth())

        // Upload flyer button
        Column {
            VolumeButton(
                text = if (isEditing) "Edit Flyer" else "Upload Flyer",
                onClick = { onUploadClick() },
                enabled = uploadEnabled,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            when (uploadResult) {
                is ResponseState.Error -> {
                    ErrorMessage(
                        message = uploadResult.errors.firstOrNull()?.message ?: currentErrorMessage
                    )
                }

                is ResponseState.Success -> {
                    onFlyerUploadSuccess()
                }

                ResponseState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (hasTriedUpload) {
                            VolumeLoading()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Formats the selected date and time to be displayed in the box during Flyer upload
 */
private fun formatDateTime(time: LocalTime?, date: LocalDate?): String {
    if (time == null || date == null) {
        return ""
    }
    val dateTime = LocalDateTime.of(date, time)
    val formatter = DateTimeFormatter.ofPattern("M/d h:mm a")
    return formatter.format(dateTime)
}

