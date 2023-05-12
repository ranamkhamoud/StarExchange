package com.rkh24.starexchange.api.model
import com.google.gson.annotations.SerializedName
class ExchangeRates {
    @SerializedName("usd_to_lbp")
    var usdToLbp: Float? = null
    @SerializedName("lbp_to_usd")
    var lbpToUsd: Float? = null
}