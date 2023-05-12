package com.rkh24.starexchange.data

data class InsightsResponse(
    val number_of_transactions: Map<String, Int>,
    val transactions_to_buy_usd: Map<String, Int>,
    val transactions_to_sell_usd: Map<String, Int>,
    val usd_amount_buy: Map<String, Float>,
    val usd_amount_sell: Map<String, Float>,
    val highest_rate: Map<String, Float>,
    val lowest_rate: Map<String, Float>
)
