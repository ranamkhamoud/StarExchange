package com.rkh24.starexchange.api.model

import com.google.gson.annotations.SerializedName

class ExchangeCard {
    @SerializedName("exchange_id")
    var exchangeId: Int? = null

    @SerializedName("request_user_id")
    var requestUserId: Int? = null

    @SerializedName("user_email")
    var userEmail: String? = null

    @SerializedName("sell_amount")
    var sellAmount: Float? = null

    @SerializedName("buy_amount")
    var buyAmount: Float? = null

    @SerializedName("usd_to_lbp")
    var usdToLbp: Boolean? = null

    @SerializedName("resolved")
    var resolved: Boolean? = null

    @SerializedName("resolved_by_user")
    var resolvedByUser: Int? = null
}
