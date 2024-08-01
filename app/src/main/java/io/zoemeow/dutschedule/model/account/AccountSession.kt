package io.zoemeow.dutschedule.model.account

import com.google.gson.annotations.SerializedName
import io.dutwrapper.dutwrapper.Accounts
import java.io.Serializable

data class AccountSession(
    @SerializedName("account.session.auth")
    val accountAuth: AccountAuth = AccountAuth(),

    @SerializedName("account.session.id")
    val sessionId: String? = null,

    @SerializedName("account.session.lastrequest")
    val sessionLastRequest: Long = 0,

    @SerializedName("account.session.viewstate")
    val viewState: String? = null,

    @SerializedName("account.session.viewstategenerator")
    val viewStateGenerator: String? = null
): Serializable {
    fun clone(
        accountAuth: AccountAuth? = null,
        sessionId: String? = null,
        sessionLastRequest: Long? = null,
        viewState: String? = null,
        viewStateGenerator: String? = null
    ): AccountSession {
        return AccountSession(
            accountAuth = accountAuth ?: this.accountAuth.clone(),
            sessionId = sessionId ?: this.sessionId,
            sessionLastRequest = sessionLastRequest ?: this.sessionLastRequest,
            viewState = viewState ?: this.viewState,
            viewStateGenerator = viewStateGenerator ?: this.viewStateGenerator
        )
    }

    fun toAccountSessionSuper(): Accounts.Session {
        return Accounts.Session(
            sessionId,
            viewState,
            viewStateGenerator
        )
    }

    fun isValidLogin(): Boolean {
        return accountAuth.isValidLogin()
    }
}