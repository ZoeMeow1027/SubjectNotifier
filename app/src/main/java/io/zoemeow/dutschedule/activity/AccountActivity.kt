package io.zoemeow.dutschedule.activity

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import io.dutwrapper.dutwrapper.model.accounts.SubjectScheduleItem
import io.dutwrapper.dutwrapper.model.accounts.trainingresult.SubjectResult
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.account.AccountAuth
import io.zoemeow.dutschedule.ui.component.account.AccountInfoBanner
import io.zoemeow.dutschedule.ui.component.account.AccountSubjectFeeInformation
import io.zoemeow.dutschedule.ui.component.account.AccountSubjectInformation
import io.zoemeow.dutschedule.ui.component.account.AccountSubjectMoreInformation
import io.zoemeow.dutschedule.ui.component.account.LoginBannerNotLoggedIn
import io.zoemeow.dutschedule.ui.component.account.LoginDialog
import io.zoemeow.dutschedule.ui.component.account.LogoutDialog
import io.zoemeow.dutschedule.ui.component.base.ButtonBase
import io.zoemeow.dutschedule.ui.component.base.ExpandableContent
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import io.zoemeow.dutschedule.ui.component.base.SimpleCardItem
import io.zoemeow.dutschedule.utils.toNonAccent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountActivity: BaseActivity() {
    @Composable
    override fun OnPreloadOnce() {

    }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        when (intent.action) {
            "subject_information" -> {
                AccountSubjectInformationView(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            "subject_fee" -> {
                AccountSubjectFeeView(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            "acc_info" -> {
                AccountInformationView(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            "acc_training_result" -> {
                AccountTrainingResult(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            "acc_training_result_subjectresult" -> {
                AccountTrainingResult_SubjectResult(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            else -> {
                MainView(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountTrainingResult_SubjectResult(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        val selectedSubject = remember { mutableStateOf<SubjectResult?>(null) }
        val searchQuery = remember { mutableStateOf("") }
        val searchEnabled = remember { mutableStateOf(false) }
        val schYearOption = remember { mutableStateOf(false) }
        val schYearOptionText = remember { mutableStateOf("All school year items") }

        fun dismissSearchBar() {
            clearAllFocusAndHideKeyboard()
            searchQuery.value = ""
            searchEnabled.value = false
        }

        fun subjectResultToMap(item: SubjectResult): Map<String, String?> {
            return mapOf(
                "Subject Year" to (item.schoolYear ?: "(unknown)"),
                "Subject Code" to (item.id ?: "(unknown)"),
                "Credit" to item.credit.toString(),
                "Point formula" to (item.pointFormula ?: "(unknown)"),
                "BT" to item.pointBT?.toString(),
                "BV" to item.pointBV?.toString(),
                "CC" to item.pointCC?.toString(),
                "CK" to item.pointCK?.toString(),
                "GK" to item.pointGK?.toString(),
                "QT" to item.pointQT?.toString(),
                "TH" to item.pointTH?.toString(),
                "Point (T10 - T4 - By point char)" to String.format(
                    "%s - %s - %s",
                    if (item.resultT10 != null) String.format(
                        "%.2f",
                        item.resultT10
                    ) else "unscored",
                    if (item.resultT4 != null) String.format("%.2f", item.resultT4) else "unscored",
                    if (item.resultByCharacter.isNullOrEmpty()) "(unscored)" else item.resultByCharacter
                )
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = containerColor,
            contentColor = contentColor,
            topBar = {
                TopAppBar(
                    title = {
                        if (!searchEnabled.value) {
                            Text("Your Subjects Result")
                        } else {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(FocusRequester()),
                                value = searchQuery.value,
                                onValueChange = { searchQuery.value = it },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        clearAllFocusAndHideKeyboard()
                                    }
                                )
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (searchEnabled.value) {
                                    dismissSearchBar()
                                } else {
                                    setResult(RESULT_CANCELED)
                                    finish()
                                }
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                    actions = {
                        if (!searchEnabled.value) {
                            IconButton(
                                onClick = {
                                    searchEnabled.value = true
                                },
                                content = {
                                    Icon(Icons.Default.Search, "Search")
                                }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (getMainViewModel().accountTrainingStatus2.processState.value != ProcessState.Running) {
                    FloatingActionButton(
                        onClick = {
                            clearAllFocusAndHideKeyboard()
                            CoroutineScope(Dispatchers.IO).launch {
                                getMainViewModel().accountLogin(
                                    after = {
                                        if (it) { getMainViewModel().accountTrainingStatus2.refreshData(force = true) }
                                    }
                                )
                            }
                        },
                        content = {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    )
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable { clearAllFocusAndHideKeyboard() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        if (getMainViewModel().accountTrainingStatus2.processState.value == ProcessState.Running) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        // Filter
                        ExposedDropdownMenuBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp)
                                .padding(bottom = 5.dp),
                            expanded = schYearOption.value,
                            onExpandedChange = { schYearOption.value = !schYearOption.value },
                            content = {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    label = { Text("Select a school year to filter") },
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
                                        (getMainViewModel().accountTrainingStatus2.data.value?.subjectResultList?.map { it.schoolYear }?.toList()?.distinct()?.reversed() ?: listOf()).forEach {
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
                        // Data
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .clickable { clearAllFocusAndHideKeyboard() },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            content = {
                                getMainViewModel().accountTrainingStatus2.data.value?.subjectResultList?.filter {
                                        p ->
                                    (schYearOptionText.value == "All school year items" || p.schoolYear == schYearOptionText.value) &&
                                            (searchQuery.value.isEmpty()
                                                    || p.name.toNonAccent().lowercase().contains(searchQuery.value.toNonAccent().lowercase()))
                                }?.reversed()?.forEach { subjectItem ->
                                    ExpandableContent(
                                        title = {
                                            Text(
                                                text = String.format(
                                                    "%2d - %s (%s)",
                                                    subjectItem.index,
                                                    subjectItem.name,
                                                    if (subjectItem.resultT4 != null) String.format("%.2f", subjectItem.resultT4) else "unscored"
                                                ),
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(15.dp)
                                            )
                                        },
                                        isTitleCentered = false,
                                        onTitleClicked = {
                                            clearAllFocusAndHideKeyboard()
                                            selectedSubject.value = subjectItem
                                        },
                                        content = {
                                            subjectResultToMap(subjectItem).forEach { (key, value) ->
                                                if (value != null) {
                                                    OutlinedTextBox(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        title = key,
                                                        value = value
                                                    )
                                                }
                                            }
                                        },
                                        isContentVisible = selectedSubject.value?.id == subjectItem.id
                                    )
                                }
                            }
                        )
                    }
                )
            }
        )

        BackHandler(
            enabled = searchEnabled.value,
            onBack = {
                dismissSearchBar()
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().accountTrainingStatus2.refreshData()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountTrainingResult(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = containerColor,
            contentColor = contentColor,
            topBar = {
                LargeTopAppBar(
                    title = { Text("Account Training Result") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (getMainViewModel().accountTrainingStatus2.processState.value != ProcessState.Running) {
                    FloatingActionButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                getMainViewModel().accountLogin(
                                    after = {
                                        if (it) { getMainViewModel().accountTrainingStatus2.refreshData(force = true) }
                                    }
                                )
                            }
                        },
                        content = {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    )
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    content = {
                        if (getMainViewModel().accountTrainingStatus2.processState.value == ProcessState.Running) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        getMainViewModel().accountTrainingStatus2.data.value?.let {
                            fun graduateStatus(): String {
                                val owned = ArrayList<String>()
                                val missing = ArrayList<String>()
                                if (it.graduateStatus?.hasSigGDTC == true) {
                                    owned.add("GDTC certificate")
                                } else {
                                    missing.add("GDTC certificate")
                                }
                                if (it.graduateStatus?.hasSigGDQP == true) {
                                    owned.add("GDQP certificate")
                                } else {
                                    missing.add("GDQP certificate")
                                }
                                if (it.graduateStatus?.hasSigEnglish == true) {
                                    owned.add("English certificate")
                                } else {
                                    missing.add("English certificate")
                                }
                                if (it.graduateStatus?.hasSigIT == true) {
                                    owned.add("IT certificate")
                                } else {
                                    missing.add("IT certificate")
                                }
                                val hasQualifiedGraduate = it.graduateStatus?.hasQualifiedGraduate == true

                                val result = "- Owned certificate(s): ${owned.joinToString(", ")}\n- Missing certificate(s): ${missing.joinToString(", ")}\n- Has qualified graduate: ${if (hasQualifiedGraduate) "Yes" else "No (check information below)"}"
                                owned.clear()
                                missing.clear()
                                return result
                            }

                            SimpleCardItem(
                                title = "Your training result",
                                isTitleCentered = true,
                                padding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 7.dp),
                                opacity = getControlBackgroundAlpha(),
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                            .padding(bottom = 10.dp),
                                        content = {
                                            OutlinedTextBox(
                                                title = "Score (point per 4)",
                                                value = "${it.trainingSummary?.avgTrainingScore4 ?: "(unknown)"}",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                            OutlinedTextBox(
                                                title = "School year updated",
                                                value = it.trainingSummary?.schoolYearCurrent ?: "(unknown)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                            ButtonBase(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 5.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                content = {
                                                    Text("View your training details")
                                                },
                                                clicked = {
                                                    val intent = Intent(context, AccountActivity::class.java)
                                                    intent.action = "acc_training_result_subjectresult"
                                                    context.startActivity(intent)
                                                }
                                            )
                                        }
                                    )
                                },
                                clicked = {}
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            SimpleCardItem(
                                title = "Graduate status",
                                isTitleCentered = true,
                                padding = PaddingValues(horizontal = 10.dp),
                                opacity = getControlBackgroundAlpha(),
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                            .padding(bottom = 10.dp),
                                        content = {
                                            OutlinedTextBox(
                                                title = "Certificate & graduate result",
                                                value = graduateStatus(),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                            OutlinedTextBox(
                                                title = "Khen thuong",
                                                value = it.graduateStatus?.info1 ?: "(unknown)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                            OutlinedTextBox(
                                                title = "Ky luat",
                                                value = it.graduateStatus?.info2 ?: "(unknown)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                            OutlinedTextBox(
                                                title = "Thong tin xet do an tot nghiep",
                                                value = it.graduateStatus?.info3 ?: "(unknown)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                            OutlinedTextBox(
                                                title = "Approved graduate process information",
                                                value = it.graduateStatus?.approveGraduateProcessInfo ?: "(unknown)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 5.dp)
                                            )
                                        }
                                    )
                                },
                                clicked = {}
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    }
                )
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().accountTrainingStatus2.refreshData()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountSubjectInformationView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        val subjectScheduleItem: MutableState<SubjectScheduleItem?> = remember { mutableStateOf(null) }
        val subjectDetailVisible = remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = containerColor,
            contentColor = contentColor,
            topBar = {
                LargeTopAppBar(
                    title = { Text("Subject Information") },
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
                                    "Back to previous screen",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (getMainViewModel().subjectSchedule2.processState.value != ProcessState.Running) {
                    FloatingActionButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                getMainViewModel().accountLogin(
                                    after = {
                                        if (it) {
                                            getMainViewModel().subjectSchedule2.refreshData(force = true)
                                        }
                                    }
                                )
                            }
                        },
                        content = {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    )
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 7.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        if (getMainViewModel().subjectSchedule2.processState.value == ProcessState.Running) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        getMainViewModel().subjectSchedule2.data.value?.forEach { item ->
                            AccountSubjectInformation(
                                modifier = Modifier.padding(bottom = 7.dp),
                                item = item,
                                opacity = getControlBackgroundAlpha(),
                                onClick = {
                                    subjectScheduleItem.value = item
                                    subjectDetailVisible.value = true
                                }
                            )
                        }
                    }
                )
            }
        )
        AccountSubjectMoreInformation(
            item = subjectScheduleItem.value,
            isVisible = subjectDetailVisible.value,
            dismissClicked = {
                subjectDetailVisible.value = false
            },
            onAddToFilterRequested = { item ->
                if (getMainViewModel().appSettings.value.newsBackgroundFilterList.any { it.isEquals(item) }) {
                    showSnackBar(
                        text = "This subject has already exist in your news filter list!",
                        clearPrevious = true
                    )
                } else {
                    getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                        newsFilterList = getMainViewModel().appSettings.value.newsBackgroundFilterList.also {
                            it.add(item)
                        }
                    )
                    getMainViewModel().saveSettings()
                    showSnackBar(
                        text = "Successfully added $item to your news filter list!",
                        clearPrevious = true
                    )
                }
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().subjectSchedule2.refreshData()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountSubjectFeeView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = containerColor,
            contentColor = contentColor,
            topBar = {
                LargeTopAppBar(
                    title = { Text("Subject fee") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (getMainViewModel().subjectFee2.processState.value != ProcessState.Running) {
                    FloatingActionButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                getMainViewModel().accountLogin(
                                    after = {
                                        if (it) {
                                            getMainViewModel().subjectFee2.refreshData(force = true)
                                        }
                                    }
                                )
                            }
                        },
                        content = {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    )
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 7.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        if (getMainViewModel().subjectFee2.processState.value == ProcessState.Running) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        getMainViewModel().subjectFee2.data.value?.forEach { item ->
                            AccountSubjectFeeInformation(
                                modifier = Modifier.padding(bottom = 10.dp),
                                item = item,
                                opacity = getControlBackgroundAlpha(),
                                onClick = { }
                            )
                        }
                    }
                )
            }
        )

        val hasRun = remember { mutableStateOf(false) }
        run {
            if (!hasRun.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    getMainViewModel().accountLogin(
                        after = {
                            if (it) {
                                getMainViewModel().subjectFee2.refreshData()
                            }
                        }
                    )
                }
                hasRun.value = true
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AccountInformationView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = containerColor,
            contentColor = contentColor,
            topBar = {
                LargeTopAppBar(
                    title = { Text("Basic Information") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                if (getMainViewModel().accountInformation2.processState.value != ProcessState.Running) {
                    FloatingActionButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                getMainViewModel().accountLogin(
                                    after = {
                                        if (it) {
                                            getMainViewModel().accountInformation2.refreshData(force = true)
                                        }
                                    }
                                )
                            }
                        },
                        content = {
                            Icon(Icons.Default.Refresh, "Refresh")
                        }
                    )
                }
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    content = {
                        if (getMainViewModel().accountInformation2.processState.value == ProcessState.Running) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        getMainViewModel().accountInformation2.data.value?.let { data ->
                            val mapPersonalInfo = mapOf(
                                "Name" to (data.name ?: "(unknown)"),
                                "Date of birth" to (data.dateOfBirth ?: "(unknown)"),
                                "Place of birth" to (data.birthPlace ?: "(unknown)"),
                                "Gender" to (data.gender ?: "(unknown)"),
                                "National ID card" to (data.nationalIdCard ?: "(unknown)"),
                                "National card issue place and date" to ("${data.nationalIdCardIssuePlace ?: "(unknown)"} on ${data.nationalIdCardIssueDate ?: "(unknown)"}"),
                                "Citizen card date" to (data.citizenIdCardIssueDate ?: "(unknown)"),
                                "Citizen ID card" to (data.citizenIdCard ?: "(unknown)"),
                                "Bank card ID" to ("${data.accountBankId ?: "(unknown)"} (${data.accountBankName ?: "(unknown)"})"),
                                "Personal email" to (data.personalEmail ?: "(unknown)"),
                                "Phone number" to (data.phoneNumber ?: "(unknown)"),
                                "Class" to (data.schoolClass ?: "(unknown)"),
                                "Specialization" to (data.specialization ?: "(unknown)"),
                                "Training program plan" to (data.trainingProgramPlan ?: "(unknown)"),
                                "School email" to (data.schoolEmail ?: "(unknown)"),
                            )
                            Text("Click and hold a text field to show option to copy it.")
                            Spacer(modifier = Modifier.size(5.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                mapPersonalInfo.keys.forEach { title ->
                                    OutlinedTextBox(
                                        title = title,
                                        value = mapPersonalInfo[title] ?: "(unknown)",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        containerColor: Color,
        contentColor: Color
    ) {
        val loginDialogVisible = remember { mutableStateOf(false) }
        val loginDialogEnabled = remember { mutableStateOf(true) }
        val logoutDialogVisible = remember { mutableStateOf(false) }
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = containerColor,
            contentColor = contentColor,
            topBar = {
                LargeTopAppBar(
                    title = { Text("Account") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .verticalScroll(rememberScrollState()),
                    content = {
                        when (getMainViewModel().accountSession.value.processState) {
                            ProcessState.NotRunYet,
                            ProcessState.Failed -> {
                                LoginBannerNotLoggedIn(
                                    opacity = getControlBackgroundAlpha(),
                                    padding = PaddingValues(10.dp),
                                    clicked = {
                                        loginDialogVisible.value = true
                                    },
                                )
                            }
                            ProcessState.Running -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    content = {
                                        CircularProgressIndicator()
                                    }
                                )
                            }
                            ProcessState.Successful -> {
                                getMainViewModel().accountInformation2.let { accInfo ->
                                    AccountInfoBanner(
                                        opacity = getControlBackgroundAlpha(),
                                        padding = PaddingValues(10.dp),
                                        isLoading = accInfo.processState.value == ProcessState.Running,
                                        username = accInfo.data.value?.studentId ?: "(unknown)",
                                        schoolClass = accInfo.data.value?.schoolClass ?: "(unknown)",
                                        trainingProgramPlan = accInfo.data.value?.trainingProgramPlan ?: "(unknown)"
                                    )
                                }
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Subject Information") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    opacity = getControlBackgroundAlpha(),
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "subject_information"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Subject Fee") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    opacity = getControlBackgroundAlpha(),
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "subject_fee"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Account Information") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    opacity = getControlBackgroundAlpha(),
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "acc_info"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Account Training Result") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    opacity = getControlBackgroundAlpha(),
                                    clicked = {
                                        val intent = Intent(context, AccountActivity::class.java)
                                        intent.action = "acc_training_result"
                                        context.startActivity(intent)
                                    }
                                )
                                ButtonBase(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    modifierInside = Modifier.padding(vertical = 7.dp),
                                    content = { Text("Logout") },
                                    horizontalArrangement = Arrangement.Start,
                                    isOutlinedButton = true,
                                    opacity = getControlBackgroundAlpha(),
                                    clicked = {
                                        logoutDialogVisible.value = true
                                    }
                                )
                            }
                        }
                    }
                )
            }
        )
        LoginDialog(
            isVisible = loginDialogVisible.value,
            controlEnabled = loginDialogEnabled.value,
            loginClicked = { username, password, rememberLogin ->
                run {
                    CoroutineScope(Dispatchers.IO).launch {
                        getMainViewModel().accountLogin(
                            data = AccountAuth(
                                username = username,
                                password = password,
                                rememberLogin = rememberLogin
                            ),
                            before = {
                                loginDialogEnabled.value = false
                                showSnackBar(
                                    text = "Logging you in...",
                                    clearPrevious = true,
                                )
                            },
                            after = {
                                when (it) {
                                    true -> {
                                        loginDialogEnabled.value = true
                                        loginDialogVisible.value = false
                                        showSnackBar(
                                            text = "Successfully logged in!",
                                            clearPrevious = true,
                                        )
                                        getMainViewModel().accountInformation2.refreshData(force = true)
                                        // getMainViewModel().fetchAccountInformation()
                                    }
                                    false -> {
                                        loginDialogEnabled.value = true
                                        showSnackBar(
                                            text = "Login failed! Please check your login information and try again.",
                                            clearPrevious = true,
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            },
            cancelRequested = {
                loginDialogVisible.value = false
            },
            canDismiss = false,
            dismissClicked = {
                if (loginDialogEnabled.value) {
                    loginDialogVisible.value = false
                }
            }
        )
        LogoutDialog(
            isVisible = logoutDialogVisible.value,
            canDismiss = true,
            logoutClicked = {
                run {
                    getMainViewModel().accountLogout(
                        after = {
                            logoutDialogVisible.value = false
                            showSnackBar(
                                text = "Successfully logout!",
                                clearPrevious = true,
                            )
                        }
                    )
                }
            },
            dismissClicked = {
                logoutDialogVisible.value = false
            }
        )
        BackHandler(
            enabled = loginDialogVisible.value || logoutDialogVisible.value,
            onBack = {
                if (loginDialogVisible.value) {
                    loginDialogVisible.value = false
                }
                if (logoutDialogVisible.value) {
                    logoutDialogVisible.value = false
                }
            }
        )
    }
}