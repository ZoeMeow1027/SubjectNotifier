package io.zoemeow.dutschedule.repository

import io.dutwrapper.dutwrapper.Account
import io.dutwrapper.dutwrapper.News
import io.dutwrapper.dutwrapper.model.accounts.AccountInformation
import io.dutwrapper.dutwrapper.model.accounts.SubjectFeeItem
import io.dutwrapper.dutwrapper.model.accounts.SubjectScheduleItem
import io.dutwrapper.dutwrapper.model.accounts.trainingresult.AccountTrainingStatus
import io.dutwrapper.dutwrapper.model.enums.NewsSearchType
import io.dutwrapper.dutwrapper.model.news.NewsGlobalItem
import io.dutwrapper.dutwrapper.model.news.NewsSubjectItem
import io.zoemeow.dutschedule.model.account.AccountSession
import io.zoemeow.dutschedule.model.account.SchoolYearItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DutRequestRepository {
    fun getNewsGlobal(
        page: Int = 1,
        searchType: NewsSearchType? = null,
        searchQuery: String? = null
    ): ArrayList<NewsGlobalItem> {
        return try {
            News.getNewsGlobal(page, searchType, searchQuery)
        } catch (ex: Exception) {
            ex.printStackTrace()
            arrayListOf()
        }
    }

    fun getNewsSubject(
        page: Int = 1,
        searchType: NewsSearchType? = null,
        searchQuery: String? = null
    ): ArrayList<NewsSubjectItem> {
        return try {
            News.getNewsSubject(page, searchType, searchQuery)
        } catch (ex: Exception) {
            ex.printStackTrace()
            arrayListOf()
        }
    }

    /**
     * Login your account on sv.dut.udn.vn.
     *
     * @param accountSession Your account information.
     * @param forceLogin Make this task force login, whenever current session is logged in.
     * @param onSessionChanged (SessionID, SessionIDDuration) Triggered only when session ID has changed.
     * @return true if successful, otherwise false.
     */
    fun login(
        accountSession: AccountSession,
        forceLogin: Boolean = false,
        onSessionChanged: ((String?, Long?, String?, String?) -> Unit)? = null
    ): Boolean {
        return when {
            run {
                if (accountSession.sessionId == null) return@run false
                if (System.currentTimeMillis() > accountSession.sessionLastRequest + (1000 * 60 * 5)) return@run false
                if (!Account.isLoggedIn(accountSession.toAccountSessionSuper())) return@run false
                if (forceLogin) return@run false
                return@run true
            } -> true
            (accountSession.accountAuth.isValidLogin()) -> {
                try {
                    val session = Account.getSession()

                    Account.login(
                        session,
                        Account.AuthInfo(
                            accountSession.accountAuth.username,
                            accountSession.accountAuth.password
                        )
                    )

                    // Return here, after send data on result
                    // If successful
                    if (Account.isLoggedIn(session)) {
                        onSessionChanged?.let { it(
                            session.sessionId,
                            System.currentTimeMillis(),
                            session.viewState,
                            session.viewStateGenerator
                        ) }
                        return true
                    }
                    // If failed
                    else {
                        onSessionChanged?.let { it(null, 0, null, null) }
                        return false
                    }
                } catch (_: Exception) {
                    onSessionChanged?.let { it(null, 0, null, null) }
                    return false
                }
            }
            else -> false
        }
    }

    /**
     * Logout your account from sv.dut.udn.vn. If you have account exist in system,
     * it will be removed there to avoid auto logging in.
     *
     * @param accountSession Your account information.
     * @return Result when this function completed execute. True means completed logout.
     * Exception and otherwise false.
     */
    fun logout(accountSession: AccountSession): Boolean {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    Account.logout(accountSession.toAccountSessionSuper())
                }
            }
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            true
        }
    }

    fun getSubjectSchedule(
        accountSession: AccountSession,
        schoolYearItem: SchoolYearItem
    ): ArrayList<SubjectScheduleItem>? {
        return try {
            if (Account.isLoggedIn(accountSession.toAccountSessionSuper())) {
                Account.fetchSubjectSchedule(
                    accountSession.toAccountSessionSuper(),
                    schoolYearItem.year,
                    schoolYearItem.semester
                )
            } else null
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun getSubjectFee(
        accountSession: AccountSession,
        schoolYearItem: SchoolYearItem
    ): ArrayList<SubjectFeeItem>? {
        return try {
            if (Account.isLoggedIn(accountSession.toAccountSessionSuper())) {
                Account.fetchSubjectFee(
                    accountSession.toAccountSessionSuper(),
                    schoolYearItem.year,
                    schoolYearItem.semester
                )
            } else null
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun getAccountInformation(
        accountSession: AccountSession
    ): AccountInformation? {
        return try {
            if (Account.isLoggedIn(accountSession.toAccountSessionSuper())) {
                Account.fetchAccountInformation(accountSession.toAccountSessionSuper())
            }
            else null
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    fun getAccountTrainingStatus(
        accountSession: AccountSession
    ): AccountTrainingStatus? {
        return try {
            if (Account.isLoggedIn(accountSession.toAccountSessionSuper())) {
                Account.fetchAccountTrainingStatus(accountSession.toAccountSessionSuper())
            }
            else null
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}