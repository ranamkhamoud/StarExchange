package com.rkh24.starexchange.data

data class GraphResponse(
    val average_buy_last_week: List<Double>,
    val average_sell_last_week: List<Double>,
    val date_last_week: List<String>
)