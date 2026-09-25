package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {
    const val APP_ID = "ca-app-pub-1131981412237081~8236896823"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-1131981412237081/3852842370"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1131981412237081/6740972008"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-1131981412237081/9367135343"

    private const val TAG = "AdMobManager"
    private var isInitialized = false

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                MobileAds.initialize(context) {
                    isInitialized = true
                    Log.d(TAG, "MobileAds initialized successfully")
                    // Pre-load initial ads
                    preloadInterstitial(context)
                    preloadRewarded(context)
                }
            } catch (e: Exception) {
                Log.e(TAG, "MobileAds init failed: ${e.message}")
            }
        }
    }

    // ================= INTERSTITIAL AD =================

    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context.applicationContext,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun showInterstitial(context: Context, onDismiss: () -> Unit = {}) {
        val activity = context.findActivity()
        val ad = interstitialAd

        if (activity != null && ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitial(context)
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    preloadInterstitial(context)
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            // Not ready or no activity, proceed immediately and try loading next
            preloadInterstitial(context)
            onDismiss()
        }
    }

    // ================= REWARDED AD =================

    fun preloadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context.applicationContext,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded ad loaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                }
            }
        )
    }

    fun showRewardedAd(
        context: Context,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit = {},
        onAdNotReady: () -> Unit = {}
    ) {
        val activity = context.findActivity()
        val ad = rewardedAd

        if (activity != null && ad != null) {
            var userEarnedReward = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded(context)
                    if (userEarnedReward) {
                        onRewardEarned()
                    }
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    preloadRewarded(context)
                    // Fallback: grant reward or notify
                    onRewardEarned()
                    onAdClosed()
                }
            }

            ad.show(activity) { _ ->
                userEarnedReward = true
            }
        } else {
            // Ad wasn't loaded in time - fallback so player isn't penalized
            preloadRewarded(context)
            onAdNotReady()
        }
    }

    private fun Context.findActivity(): Activity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }
}

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.BANNER_AD_UNIT_ID
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                val adRequest = AdRequest.Builder().build()
                loadAd(adRequest)
            }
        }
    )
}
