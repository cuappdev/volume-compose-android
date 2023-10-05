package com.cornellappdev.android.volume.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.android.volume.data.models.Organization
import com.cornellappdev.android.volume.ui.components.general.ErrorMessage
import com.cornellappdev.android.volume.ui.components.general.VolumeButton
import com.cornellappdev.android.volume.ui.components.general.VolumeInputContainer
import com.cornellappdev.android.volume.ui.components.general.VolumeTextField
import com.cornellappdev.android.volume.ui.theme.GrayFive
import com.cornellappdev.android.volume.ui.theme.GrayOne
import com.cornellappdev.android.volume.ui.theme.VolumeOrange
import com.cornellappdev.android.volume.ui.theme.lato
import com.cornellappdev.android.volume.ui.theme.notoserif
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
import java.time.format.DateTimeFormatter


@Composable
fun FlyerUploadScreen(organization: Organization) {
    val context = LocalContext.current

    var flyerName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var redirectLink: String? by remember { mutableStateOf(null) }
    var startDate: LocalDate? by remember { mutableStateOf(null) }
    var endDate: LocalDate? by remember { mutableStateOf(null) }
    var startTime: LocalTime? by remember { mutableStateOf(null) }
    var endTime: LocalTime? by remember { mutableStateOf(null) }
    var flyerImageUri: Uri? by remember { mutableStateOf(null) }
    var flyerCategory: String by remember { mutableStateOf("") }
    var hasTimeError: Boolean by remember { mutableStateOf(false) }

    var uploadEnabled by remember { mutableStateOf(false) }
    DisposableEffect(
        key1 = arrayOf(
            flyerName,
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
        uploadEnabled = flyerName.isNotBlank() &&
                location.isNotBlank() &&
                startDate != null &&
                endDate != null &&
                startTime != null &&
                endTime != null &&
                flyerImageUri != null &&
                flyerCategory.isNotBlank()
        // TODO figure out type errors
        letIfAllNotNull(startTime, startDate, endTime, endDate) {
            val (st, sd, et, ed) = it
            if (LocalDateTime.of(sd, st) > LocalDateTime.of(ed, et)) {

            }
        }
        onDispose { }
    }

    var categoryDropdownShowing by remember { mutableStateOf(false) }

    var fileName: String? by remember { mutableStateOf(null) }

    val startDatePickerDialog = rememberMaterialDialogState()
    val endDatePickerDialog = rememberMaterialDialogState()
    val startTimePickerDialog = rememberMaterialDialogState()
    val endTimePickerDialog = rememberMaterialDialogState()

    val categories: List<String> = FlyerConstants.FORMATTED_TAGS
    val slugs: List<String> = FlyerConstants.CATEGORY_SLUGS.split(",")

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            flyerImageUri = uri
        }
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

    val datePickerColors = DatePickerDefaults.colors(
        headerBackgroundColor = VolumeOrange,
        dateActiveBackgroundColor = VolumeOrange,
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


    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
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
            Text(text = organization.name, fontSize = 24.sp, fontFamily = notoserif)
        }
        // Flyer name input
        Column {
            Text(text = "Flyer Name", fontFamily = notoserif, fontSize = 16.sp, color = GrayFive)
            Spacer(Modifier.height(8.dp))
            VolumeTextField(
                value = flyerName,
                onValueChange = { flyerName = it },
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
            ErrorMessage(message = "End time must be after start time.")
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
                    onDismissRequest = { categoryDropdownShowing = false }) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category, fontFamily = lato, color = GrayFive) },
                            onClick = {
                                categoryDropdownShowing = !categoryDropdownShowing
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
                value = redirectLink.orEmpty(),
                onValueChange = { if (it.isBlank()) redirectLink = null else redirectLink = it },
                modifier = Modifier.height(48.dp)
            )
        }
        // Upload image button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, shape = RoundedCornerShape(4.dp), color = VolumeOrange)
                .clickable {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Camera",
                tint = VolumeOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = fileName ?: "Select an image...",
                color = VolumeOrange,
                fontFamily = lato
            )
        }
        // Upload flyer button
        VolumeButton(
            text = "Upload Flyer",
            onClick = { /*TODO*/ },
            enabled = uploadEnabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun formatDateTime(time: LocalTime?, date: LocalDate?): String {
    if (time == null || date == null) {
        return ""
    }
    val dateTime = LocalDateTime.of(date, time)
    val formatter = DateTimeFormatter.ofPattern("M/d h:mm a")
    return formatter.format(dateTime)
}

@Composable
@Preview
fun FlyerUploadPreview() {
    FlyerUploadScreen(Organization(name = "Cornell AppDev", categorySlug = "", websiteURL = ""))
}

