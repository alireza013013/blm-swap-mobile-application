package io.blmswap.blmswap.walletConnect

import androidx.multidex.MultiDexApplication
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.komputing.khex.extensions.toNoPrefixHexString
import org.walletconnect.Session
import org.walletconnect.impls.*
import org.walletconnect.nullOnThrow
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.*

class WalletConnect: MultiDexApplication() {
    private fun initClient() {
        client = OkHttpClient.Builder().build()
    }

    private fun initMoshi() {
        moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private fun initSessionStorage(addressCache : String) {
        storage = FileWCSessionStore(
            File(addressCache, "session_store.json").apply { createNewFile() }, moshi
        )
    }

    companion object {
        private lateinit var client: OkHttpClient
        private lateinit var moshi: Moshi
        private lateinit var storage: WCSessionStore
        lateinit var config: Session.Config
        lateinit var session: Session

        fun resetSession(addressCache : String) {
            WalletConnect().initClient()
            WalletConnect().initMoshi()
            WalletConnect().initSessionStorage(addressCache)
            val listOfIconUrlStrings: List<String> =
                listOf<String>("https://blmswap.io/img/logo.e235f260.png")
            nullOnThrow { session }?.clearCallbacks()
            val key = ByteArray(32).also { Random().nextBytes(it) }.toNoPrefixHexString()
            config =
                Session.Config(UUID.randomUUID().toString(), "wss://bridge.walletconnect.org", key)
            session = WCSession(
                config,
                MoshiPayloadAdapter(moshi),
                storage,
                OkHttpTransport.Builder(client, moshi),
                Session.PeerMeta(name = "BLM SWAP", url = "https://blmswap.io", icons = listOfIconUrlStrings)
            )
            session.offer()
        }
    }
}