package io.zoemeow.dutschedule.ui.view.account

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.dutwrapper.dutwrapper.AccountInformation
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.account.SubjectResult
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import io.zoemeow.dutschedule.utils.toNonAccent
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Activity_Account_TrainingSubjectResult(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    // TODO: onMessageReceived when copy a property
    @Suppress("UNUSED_PARAMETER") onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Search area (true to display them)
    val searchEnabled = remember { mutableStateOf(false) }
    // Search query to begin filter
    val searchQuery = remember { mutableStateOf("") }
    // School year show option to choose
    val schYearShowOption = remember { mutableStateOf(false) }
    // School year option to begin filter
    val schYearOptionText = remember { mutableStateOf("") }

    val modalBottomSheetEnabled = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val selectedSubject = remember { mutableStateOf<AccountInformation.SubjectResult?>(null) }

    fun subjectResultToMap(item: AccountInformation.SubjectResult): Map<String, String?> {
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
                if (item.resultByChar.isNullOrEmpty()) "(${context.getString(R.string.data_noscore)})" else item.resultByChar
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
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
                                    onBack()
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
                    if (mainViewModel.accountSession.accountTrainingStatus.processState.value == ProcessState.Running) {
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
                    if (mainViewModel.accountSession.accountTrainingStatus.processState.value != ProcessState.Running) {
                        FloatingActionButton(
                            onClick = {
                                focusManager.clearFocus(force = true)
                                mainViewModel.accountSession.fetchAccountTrainingStatus(force = true)
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
                            .padding(horizontal = 10.dp)
                            .padding(bottom = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        stickyHeader {
                            if (!searchEnabled.value) {
                                Column {
                                    if (schYearOptionText.value.isNotEmpty()) {
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
                        items(mainViewModel.accountSession.accountTrainingStatus.data.value?.subjectResultList?.filter { p -> run {
                            // Filter with school year
                            if (schYearOptionText.value.isEmpty()) return@run true
                            if (p.schoolYear == schYearOptionText.value) return@run true
                            return@run false
                        } }?.filter { p -> run {
                            // Filter with search query
                            if (searchQuery.value.isEmpty()) return@run true
                            if (p.name.toNonAccent().lowercase().contains(searchQuery.value.toNonAccent().lowercase())) return@run true
                            return@run false
                        } }?.reversed() ?: listOf()) { subjectItem ->
                            SubjectResult(
                                modifier = Modifier.padding(vertical = 3.dp),
                                subjectResult = subjectItem,
                                onClick = {
                                    selectedSubject.value = subjectItem
                                    modalBottomSheetEnabled.value = true
                                },
                                opacity = appearanceState.componentOpacity
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
                                it
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
                                expanded = schYearShowOption.value,
                                onExpandedChange = { schYearShowOption.value = !schYearShowOption.value },
                                content = {
                                    OutlinedTextField(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        label = { Text(context.getString(R.string.account_trainingstatus_subjectresult_schoolyear)) },
                                        readOnly = true,
                                        value = schYearOptionText.value.ifEmpty { context.getString(R.string.account_trainingstatus_subjectresult_allschoolyears) },
                                        onValueChange = { }
                                    )
                                    DropdownMenu(
                                        modifier = Modifier.fillMaxWidth(),
                                        expanded = schYearShowOption.value,
                                        onDismissRequest = { schYearShowOption.value = false},
                                        content = {
                                            DropdownMenuItem(
                                                modifier = Modifier.background(
                                                    color = when (schYearOptionText.value == "") {
                                                        true -> MaterialTheme.colorScheme.secondaryContainer
                                                        false -> MaterialTheme.colorScheme.surface
                                                    }
                                                ),
                                                text = { Text(context.getString(R.string.account_trainingstatus_subjectresult_allschoolyears)) },
                                                onClick = {
                                                    schYearOptionText.value = ""
                                                    schYearShowOption.value = false
                                                }
                                            )
                                            (mainViewModel.accountSession.accountTrainingStatus.data.value?.subjectResultList?.map { it.schoolYear }?.toList()?.distinct()?.reversed() ?: listOf()).forEach {
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
                                                        schYearShowOption.value = false
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
                                        focusManager.clearFocus(force = true)
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
            mainViewModel.accountSession.fetchAccountTrainingStatus()
            hasRun.value = true
        }
    }
}