package com.qryde.qryderiderapp.data.mapper

import com.qryde.qryderiderapp.domain.model.ServerConfig


/*
*
* QTIP2_TestServer_BASEURL~https://stgq.qryde.net;
* QREST2_TestServer_IPPORT~reststg.qryde.net,443;
* QMAP2_TestServer_IPPORT~reststg.qryde.net,443;
* MNJS_TestServer_BASEURL_SIO~mnjs.qryde.net,80;
* QTIP3_TestServer_BASEURL~https://arn.qryde.net/arn/;
* QREST3_TestServer_IPPORT~arn.qryde.net,443;
* QMAP3_TestServer_IPPORT~arn.qryde.net,443;
*
*
* */

fun String.toServerConfig(): ServerConfig {
    val endpoints = split(";")
        .mapNotNull { entry ->
            val key = entry.substringBefore("~", missingDelimiterValue = "").trim()
            val rawValue = entry.substringAfter("~", missingDelimiterValue = "").trim()
            if (key.isEmpty() || rawValue.isEmpty()) null else key to rawValue.toResolvedUrl()
        }
        .toMap()

    require(endpoints.isNotEmpty()) { "Config server response did not contain any endpoints: $this" }

    return ServerConfig(endpoints)
}

private fun String.toResolvedUrl(): String =
    if (startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)) {
        this
    } else {
        // "host,port" -> "https://host:port"
        "https://${replace(",", ":")}"
    }
