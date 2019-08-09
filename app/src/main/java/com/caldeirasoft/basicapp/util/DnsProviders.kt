package com.caldeirasoft.basicapp.util

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.net.UnknownHostException

object DnsProviders {
    internal fun buildGoogle(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://dns.google.com/experimental".toHttpUrl())
                .bootstrapDnsHosts(getByIp("216.58.204.78"), getByIp("2a00:1450:4009:814:0:0:0:200e"))
                .build()
    }

    internal fun buildGooglePost(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://dns.google.com/experimental".toHttpUrl())
                .bootstrapDnsHosts(getByIp("216.58.204.78"), getByIp("2a00:1450:4009:814:0:0:0:200e"))
                .post(true)
                .build()
    }

    internal fun buildCloudflareIp(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://1.1.1.1/dns-query".toHttpUrl())
                .includeIPv6(false)
                .build()
    }

    internal fun buildCloudflare(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(getByIp("1.1.1.1"))
                .includeIPv6(false)
                .build()
    }

    internal fun buildCloudflarePost(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
                .bootstrapDnsHosts(
                        getByIp("104.16.111.25"), getByIp("104.16.112.25"),
                        getByIp("2400:cb00:2048:1:0:0:6810:7019"), getByIp("2400:cb00:2048:1:0:0:6810:6f19")
                )
                .includeIPv6(false)
                .post(true)
                .build()
    }

    internal fun buildCleanBrowsing(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://doh.cleanbrowsing.org/doh/family-filter/".toHttpUrl())
                .includeIPv6(false)
                .build()
    }

    internal fun buildChantra(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://dns.dnsoverhttps.net/dns-query".toHttpUrl())
                .includeIPv6(false)
                .build()
    }

    internal fun buildCryptoSx(bootstrapClient: OkHttpClient): DnsOverHttps {
        return DnsOverHttps.Builder().client(bootstrapClient)
                .url("https://doh.crypto.sx/dns-query".toHttpUrl())
                .includeIPv6(false)
                .build()
    }

    fun providers(
            client: OkHttpClient, http2Only: Boolean,
            workingOnly: Boolean, getOnly: Boolean
    ): List<DnsOverHttps> {

        val result = ArrayList<DnsOverHttps>()

        result.add(buildGoogle(client))
        if (!getOnly) {
            result.add(buildGooglePost(client))
        }
        result.add(buildCloudflare(client))
        result.add(buildCloudflareIp(client))
        if (!getOnly) {
            result.add(buildCloudflarePost(client))
        }
        if (!workingOnly) {
            //result.add(buildCleanBrowsing(client)); // timeouts
            result.add(buildCryptoSx(client)) // 521 - server down
        }
        result.add(buildChantra(client))

        return result
    }

    private fun getByIp(host: String): InetAddress {
        try {
            return InetAddress.getByName(host)
        } catch (e: UnknownHostException) {
            // unlikely
            throw RuntimeException(e)
        }

    }
}