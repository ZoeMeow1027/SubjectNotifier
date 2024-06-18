package io.zoemeow.dutschedule.ui.view.account

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.dutwrapper.dutwrapper.model.accounts.trainingresult.SubjectResult
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import io.zoemeow.dutschedule.utils.TableCell
import io.zoemeow.dutschedule.utils.toNonAccent
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountActivity.TrainingSubjectResult(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color
) {
    val focusRequester = remember { FocusRequester() }

    val searchEnabled = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val schYearOption = remember { mutableStateOf(false) }
    val schYearOptionText = remember { mutableStateOf("All school year items") }

    val modalBottomSheetEnabled = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val selectedSubject = remember { mutableStateOf<SubjectResult?>(null) }

    fun subjectResultToMap(item: SubjectResult): Map<String, String?> {
        return mapOf(
            context.getString(R.string.account_trainingstatus_subjectresult_schoolyear) to "${item.schoolYear ?: context.getString(R.string.data_unknown)}${ if (item.isExtendedSemester) " (${context.getString(R.string.account_trainingstatus_subjectresult_schoolyear_insummer)})" else "" }",
            context.getString(R.string.account_trainingstatus_subjectresult_subjectcode) to (item.id ?: context.getString(R.string.data_unknown)),
            context.getString(R.string.account_trainingstatus_subjectresult_credit) to item.credit.toString(),
            context.getString(R.string.account_trainingstatus_subjectresult_pointformula) to (item.pointFormula ?: context.getString(R.string.data_unknown)),
            "BT" to item.pointBT?.toString(),
            "BV" to item.pointBV?.toString(),
            "CC" to item.pointCC?.toString(),
            "CK" to item.pointCK?.toString(),
            "GK" to item.pointGK?.toString(),
            "QT" to item.pointQT?.toString(),
            "TH" to item.pointTH?.toString(),
            "TT" to item.pointTT?.toString(),
            context.getString(R.string.account_trainingstatus_subjectresult_finalscore) to String.format(
                Locale.ROOT,
                "%s - %s - %s",
                if (item.resultT10 != null) String.format(
                    Locale.ROOT,
                    "%.2f",
                    item.resultT10
                ) else context.getString(R.string.data_noscore),
                if (item.resultT4 != null) String.format(Locale.ROOT, "%.2f", item.resultT4) else context.getString(R.string.data_noscore),
                if (item.resultByCharacter.isNullOrEmpty()) "(${context.getString(R.string.data_noscore)})" else item.resultByCharacter
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                content = {
                    TopAppBar(
                        title = {
                            Text(context.getString(R.string.account_trainingstatus_subjectresult_title))
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    setResult(RESULT_CANCELED)
                                    finish()
                                },
                                content = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        context.getString(R.string.action_back),
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            )
                        }
                    )
                    if (getMainViewModel().accountSession.accountTrainingStatus.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    TextButton(
                        onClick = {
                            searchEnabled.value = !searchEnabled.value
                        },
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.size(5.dp))
                                Icon(ImageVector.vectorResource(id = R.drawable.ic_baseline_filter_list_alt_24), context.getString(R.string.account_trainingstatus_subjectresult_searchfilterbutton))
                                Spacer(modifier = Modifier.size(3.dp))
                                Text(context.getString(R.string.account_trainingstatus_subjectresult_searchfilterbutton))
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                        },
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = if (!searchEnabled.value) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            contentColor = if (!searchEnabled.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                floatingActionButton = {
                    if (getMainViewModel().accountSession.accountTrainingStatus.processState.value != ProcessState.Running) {
                        FloatingActionButton(
                            onClick = {
                                clearAllFocusAndHideKeyboard()
                                getMainViewModel().accountSession.fetchAccountTrainingStatus(force = true)
                            },
                            content = {
                                Icon(
                                    Icons.Default.Refresh,
                                    context.getString(R.string.action_refresh)
                                )
                            }
                        )
                    }
                },
                containerColor = Color.Transparent
            )
        },
        content = { padding ->
            Scaffold(
                modifier = Modifier.padding(padding),
                contentWindowInsets = WindowInsets(top = 0.dp),
                content = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                            .padding(horizontal = 5.dp)
                            .padding(bottom = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        stickyHeader {
                            if (!searchEnabled.value) {
                                Column {
                                    if (schYearOptionText.value != "All school year items") {
                                        Text(
                                            context.getString(
                                                R.string.account_trainingstatus_subjectresult_filteredschyear,
                                                schYearOptionText.value
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 7.dp)
                                                .padding(bottom = 5.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    if (searchQuery.value.isNotEmpty()) {
                                        Text(
                                            context.getString(
                                                R.string.account_trainingstatus_subjectresult_filteredquery,
                                                searchQuery.value
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 7.dp)
                                                .padding(bottom = 5.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        stickyHeader {
                            // Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp)
                                    .height(IntrinsicSize.Min),
                                content = {
                                    TableCell(
                                        modifier = Modifier.fillMaxHeight(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = getControlBackgroundAlpha()),
                                        text = "Index",
                                        textAlign = TextAlign.Center,
                                        weight = 0.17f
                                    )
                                    TableCell(
                                        modifier = Modifier.fillMaxHeight(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = getControlBackgroundAlpha()),
                                        text = "Subject name",
                                        textAlign = TextAlign.Center,
                                        weight = 0.58f
                                    )
                                    TableCell(
                                        modifier = Modifier.fillMaxHeight(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = getControlBackgroundAlpha()),
                                        text = "Result T10(T4)",
                                        textAlign = TextAlign.Center,
                                        weight = 0.25f
                                    )
                                }
                            )
                        }
                        items(getMainViewModel().accountSession.accountTrainingStatus.data.value?.subjectResultList?.filter {
                                p ->
                            (schYearOptionText.value == "All school year items" || p.schoolYear == schYearOptionText.value) &&
                                    (searchQuery.value.isEmpty()
                                            || p.name.toNonAccent().lowercase().contains(searchQuery.value.toNonAccent().lowercase()))
                        }?.reversed() ?: listOf()) { subjectItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 5.dp)
                                    .height(IntrinsicSize.Min)
                                    .clickable {
                                        selectedSubject.value = subjectItem
                                        modalBottomSheetEnabled.value = true
                                    },
                                content = {
                                    TableCell(
                                        modifier = Modifier.fillMaxHeight(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = getControlBackgroundAlpha()),
                                        text = "${subjectItem.index}",
                                        textAlign = TextAlign.Center,
                                        weight = 0.17f
                                    )
                                    TableCell(
                                        modifier = Modifier.fillMaxHeight(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = getControlBackgroundAlpha()),
                                        text = subjectItem.name,
                                        contentAlign = Alignment.CenterStart,
                                        textAlign = TextAlign.Start,
                                        weight = 0.58f
                                    )
                                    TableCell(
                                        modifier = Modifier.fillMaxHeight(),
                                        backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = getControlBackgroundAlpha()),
                                        text = String.format(
                                            "%s (%s)",
                                            subjectItem.resultT10?.toString() ?: "---",
                                            subjectItem.resultT4?.toString() ?: "---"
                                        ),
                                        textAlign = TextAlign.Center,
                                        weight = 0.25f
                                    )
                                }
                            )
                        }
                    }
                },
                containerColor = Color.Transparent,
                bottomBar = {
                    // School year filter and search query if requested
                    AnimatedVisibility(
                        visible = searchEnabled.value,
                        enter = slideInVertically(
                            initialOffsetY = {
                                it / 2
                            },
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = {
                                it
                            },
                        ),
                    ) {
                        Column {
                            ExposedDropdownMenuBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                expanded = schYearOption.value,
                                onExpandedChange = { schYearOption.value = !schYearOption.value },
                                content = {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        label = { Text(context.getString(R.string.account_trainingstatus_subjectresult_schoolyear)) },
                                        readOnly = true,
                                        value = schYearOptionText.value,
                                        onValueChange = { }
                                    )
                                    DropdownMenu(
                                        modifier = Modifier.fillMaxWidth(),
                                        expanded = schYearOption.value,
                                        onDismissRequest = { schYearOption.value = false},
                                        content = {
                                            DropdownMenuItem(
                                                modifier = Modifier.background(
                                                    color = when (schYearOptionText.value == "All school year items") {
                                                        true -> MaterialTheme.colorScheme.secondaryContainer
                                                        false -> MaterialTheme.colorScheme.surface
                                                    }
                                                ),
                                                text = { Text("All school year items") },
                                                onClick = {
                                                    schYearOptionText.value = "All school year items"
                                                    schYearOption.value = false
                                                }
                                            )
                                            (getMainViewModel().accountSession.accountTrainingStatus.data.value?.subjectResultList?.map { it.schoolYear }?.toList()?.distinct()?.reversed() ?: listOf()).forEach {
                                                DropdownMenuItem(
                                                    modifier = Modifier.background(
                                                        color = when (schYearOptionText.value == it) {
                                                            true -> MaterialTheme.colorScheme.secondaryContainer
                                                            false -> MaterialTheme.colorScheme.surface
                                                        }
                                                    ),
                                                    text = { Text(it) },
                                                    onClick = {
                                                        schYearOptionText.value = it
                                                        schYearOption.value = false
                                                    }
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                    .focusRequester(focusRequester),
                                value = searchQuery.value,
                                label = { Text(context.getString(R.string.account_trainingstatus_subjectresult_searchquery)) },
                                onValueChange = {
                                    if (searchEnabled.value) {
                                        searchQuery.value = it
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        clearAllFocusAndHideKeyboard()
                                    }
                                ),
                                trailingIcon = {
                                    if (searchQuery.value.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchQuery.value = "" },
                                            content = { Icon(Icons.Default.Clear, context.getString(R.string.action_clear)) }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            )
            if (modalBottomSheetEnabled.value) {
                ModalBottomSheet(
                    onDismissRequest = { modalBottomSheetEnabled.value = false },
                    sheetState = sheetState,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(
                                selectedSubject.value?.name ?: context.getString(R.string.data_nodata),
                                style = TextStyle(fontSize = 27.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                textAlign = TextAlign.Center
                            )
                            selectedSubject.value?.let { item ->
                                subjectResultToMap(item).forEach { (key, value) ->
                                    if (value != null) {
                                        OutlinedTextBox(
                                            modifier = Modifier.fillMaxWidth(),
                                            title = key,
                                            value = value
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    )
                }
            }
        }
    )

    val hasRun = remember { mutableStateOf(false) }
    run {
        if (!hasRun.value) {
            getMainViewModel().accountSession.fetchAccountTrainingStatus()
            hasRun.value = true
        }
    }
}