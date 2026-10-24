package p019ai.focus.services;

import A.t;
import A.z;
import A8.C0063b;
import A8.C0075n;
import A8.C0083w;
import A8.C0084x;
import Af.AbstractC0108j;
import B8.b;
import B8.h;
import C8.E;
import C8.s;
import D.g;
import D1.C0291a;
import D1.C0315z;
import Dv.e;
import G.c;
import Jp.AbstractC0815p;
import K3.C0853o;
import K3.x0;
import Lv.q;
import Mv.i;
import Mv.l;
import Mv.o;
import Mv.y;
import N2.V;
import Pv.C;
import Pv.InterfaceC1567z;
import Pv.M;
import Pv.g0;
import Pv.m0;
import Pv.r;
import Pv.v0;
import S1.a;
import S1.d;
import V1.C2082a;
import V1.u;
import Za.C2657a;
import Za.J;
import android.app.usage.UsageEvents;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import androidx.annotation.Keep;
import com.google.android.gms.internal.play_billing.M0;
import com.revenuecat.purchases.common.Constants;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.A;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.m;
import kotlin.p;
import l6.f;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p019ai.focus.broadcastReceivers.NotificationAndWidgetUpdateReceiver;
import p019ai.focus.broadcastReceivers.ToastReceiver;
import p019ai.focus.feature_app_limit.domain.model.AppLimit;
import p019ai.focus.feature_focus_timer.domain.model.FocusTimerSession;
import p019ai.focus.feature_focus_timer.domain.use_case.C2788j0;
import p019ai.focus.feature_focus_timer.domain.use_case.N;
import p019ai.focus.feature_focus_timer.domain.use_case.T;
import p019ai.focus.floating_bubble.PMFloatingButtonService;
import p019ai.focus.services.domain.model.MyAccessibilityNodeInfo;
import p019ai.focus.services.domain.model.MyAccessibilityNodeInfoWrapper;
import p019ai.focus.services.domain.model.NodeBounds;
import p019ai.focus.services.domain.model.ShortFormContentPresent;
import p019ai.focusapp.R;
import p060c.AbstractC3102b;
import p077ch.qos.logback.classic.Level;
import p077ch.qos.logback.core.AsyncAppenderBase;
import p077ch.qos.logback.core.CoreConstants;
import p113dq.C3929d;
import p278k0.C4385f;
import p278k0.EnumC4350a;
import p315la.j;
import p451qa.C4887v;
import p451qa.P;
import p468qv.k;
import p468qv.n;
import p468qv.v;
import p468qv.w;
import p471r0.C4896a;
import p520sr.AbstractC4984e0;
import p535te.s0;
import p536tf.AbstractC5063h;
import p536tf.AbstractC5078x;
import p536tf.B;
import p536tf.C5057b;
import p536tf.C5074t;
import p536tf.C5079y;
import p536tf.C5080z;
import p536tf.G;
import p536tf.H;
import p536tf.I;
import p536tf.K;
import p536tf.L;
import p536tf.Q;
import p536tf.U;
import p536tf.W;
import p536tf.X;
import p536tf.Y;
import p536tf.a0;
import p536tf.b0;
import p536tf.c0;
import p536tf.d0;
import p536tf.f0;
import p536tf.h0;
import p536tf.i0;
import p637x2.C5518e;

/* JADX INFO: loaded from: classes.dex */
@Keep
public final class MyMonitorInAppBlockService extends AbstractC5063h {
    public static final int $stable = 8;
    public static final String ACTION_DUMP_YT_NODES = "ai.focusapp.DUMP_YT_NODES";
    private static final int MAX_TREE_WALK_NODES = 2000;
    public static final String YOUTUBE_APP_ID = "com.google.android.youtube";
    public static final String YT_CAPTURE_DIR = "yt_corpus";
    public static final String YT_CAPTURE_LOG_TAG = "YTNodeCapture";
    private static volatile long accessibilityScrapeCalls;
    private static volatile long accessibilityScrapeTotalMs;
    private static long blockServiceCheckLastHeartBeat;
    private static boolean isRunning;
    private static Boolean isTabletDevice;
    private static List<Integer> lastLikesDigest;
    private static Long lastLoggedInMixPanelBS;
    private static String lastProcessedChannelName;
    private static String lastProcessedDomainName;
    private static long processedTimeStamp;
    private static int reelsScrolledCount;
    public C0291a appUsageStatsUseCases;
    public C2082a blockFeatureUseCases;
    private volatile d blockProtectionWindow;
    public a blockProtectionWindowFactory;
    public C5518e blockWebsiteUseCases;
    public V c68PendingAlarmGuard;
    private long computeUsageStatsExpiry;
    private h contentBlockWindow;
    public b contentBlockWindowFactory;
    private g0 currentChannelCategoriseJob;
    private g0 currentChannelHandleCategoriseJob;
    public p638x3.a distractingAppsUseCases;
    public T focusTimerUseCases;
    public C0063b ftOverlayStateManager;
    public C4887v getInstagramChallengeActive;
    public c getLauncherAppId;
    public G.d getSettingsApps;
    public p595vf.b handleEducationAppNotification;
    public Gc.V isExistingUserBeforeLaunch;
    public t isInternalTesting;
    public u isStrictModeActiveForBlockFeatureAppId;
    private final r job;
    private String lastChannelHandleCategorise;
    private String lastChannelNameCategorise;
    private long lastDbHeartbeat;
    private String lastDetectedChannelName;
    private p567uf.b lastDetectedContentType;
    private String lastRecentAppId;
    private String lastToastChannel;
    private String lastToastWebsite;
    private BroadcastReceiver localBroadcastReceiver;
    public p471r0.r logAnalyticsEvent;
    public p471r0.u logAnalyticsUserProperty;
    public g logAppHeartbeat;
    private final String logServiceTag;
    private final String logSpotlightTag;
    private final String logTag;
    private final String logTag1;
    private final String logTagEvent;
    public C0083w monitorServiceStateManager;
    public C0084x monitorServiceUseCases;
    private e navigationHandler;
    public P permissionUseCases;
    private final String pmOverlayLogTag;
    public J productiveModeUseCases;
    private final long resetPMTime;
    private final InterfaceC1567z scope;
    private final List<String> youtubeMiniPlayerViewIds;
    private final h0 ytNodeDumpReceiver;
    public s0 zenModeUseCases;
    public static final C5074t Companion = new C5074t();
    private static List<String> settingsApps = p468qv.u.f71758e;
    private static final Map<String, Long> lastProcessedPMTimes = new LinkedHashMap();
    private static boolean shouldShowProductiveMode = true;

    public MyMonitorInAppBlockService() {
        v0 v0VarF = C.f();
        this.job = v0VarF;
        Wv.e eVar = M.f24563a;
        Wv.d dVar = Wv.d.f37341X;
        dVar.getClass();
        this.scope = C.c(j.l(dVar, v0VarF));
        this.logTag = "BlockService";
        this.logTag1 = "BlockService";
        this.logTagEvent = "EventTag";
        this.logServiceTag = "ServiceRunningTag";
        this.pmOverlayLogTag = "shouldShowPMTagOverlay";
        this.logSpotlightTag = "SpotlightBlock";
        this.resetPMTime = 900000L;
        this.ytNodeDumpReceiver = new h0();
        this.youtubeMiniPlayerViewIds = n.I("com.google.android.youtube:id/floaty_bar_controls_view", "com.google.android.youtube:id/floaty_close_button", "com.google.android.youtube:id/floaty_play_pause_button", "com.google.android.youtube:id/modern_miniplayer_close");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void captureYTNodeDump(String label, String subscribeState) throws JSONException, IOException {
        Object obj;
        p567uf.a aVar;
        Map<Integer, ? extends List<p567uf.c>> map;
        Map<Integer, ? extends List<p567uf.c>> map2;
        p646xf.c cVar;
        String str;
        String str2;
        Object next;
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            p118e.b.d(YT_CAPTURE_LOG_TAG, "capture '" + label + "' aborted - rootInActiveWindow is null");
            return;
        }
        CharSequence packageName = rootInActiveWindow.getPackageName();
        String string = packageName != null ? packageName.toString() : null;
        boolean zIsScreenInPortraitOrientation = isScreenInPortraitOrientation();
        p567uf.j jVar = (isTablet() || zIsScreenInPortraitOrientation) ? p567uf.j.f77960e : p567uf.j.f77958I;
        String strK = AbstractC3102b.k("YTNodeCapture-", label);
        p567uf.a aVarConvertToHashMap = convertToHashMap(strK, rootInActiveWindow, jVar);
        boolean zIsYTHomePage = isYTHomePage(aVarConvertToHashMap.b, true, strK);
        Map<Integer, ? extends List<p567uf.c>> map3 = aVarConvertToHashMap.f77942a;
        boolean z10 = aVarConvertToHashMap.f77944d;
        Map<Integer, ? extends List<p567uf.c>> map4 = aVarConvertToHashMap.b;
        C5057b c5057bFindYTChannelName = (zIsScreenInPortraitOrientation || isTablet()) ? findYTChannelName(map4, z10, strK) : findYTChannelNameInLandscapeMode(map3, strK);
        ArrayList<p646xf.d> arrayList = new ArrayList(map4.size());
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it = map4.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ? extends List<p567uf.c>> next2 = it.next();
            int iIntValue = next2.getKey().intValue();
            boolean z11 = zIsScreenInPortraitOrientation;
            List<p567uf.c> value = next2.getValue();
            p567uf.j jVar2 = jVar;
            C5057b c5057bFindChannelNameFromNodeDetails = findChannelNameFromNodeDetails(value, z10);
            Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it2 = it;
            String str3 = c5057bFindChannelNameFromNodeDetails != null ? c5057bFindChannelNameFromNodeDetails.b : null;
            String str4 = c5057bFindChannelNameFromNodeDetails != null ? c5057bFindChannelNameFromNodeDetails.f75973a : null;
            p646xf.a aVarDetectHandleFromNodes = detectHandleFromNodes(value);
            String str5 = aVarDetectHandleFromNodes != null ? aVarDetectHandleFromNodes.f81952a : null;
            o oVar = p646xf.b.f81953a;
            arrayList.add(new p646xf.d(iIntValue, str3, str4, str5, p646xf.b.a(strK + "-" + iIntValue, value)));
            jVar = jVar2;
            zIsScreenInPortraitOrientation = z11;
            it = it2;
        }
        boolean z12 = zIsScreenInPortraitOrientation;
        p567uf.j jVar3 = jVar;
        long jCurrentTimeMillis = System.currentTimeMillis();
        Object appVersionName = getAppVersionName(string);
        Object obj2 = !z12 ? "landscape" : "portrait";
        boolean zIsTablet = isTablet();
        Object objFindSubscribeButtonLabel = findSubscribeButtonLabel(map4);
        Object obj3 = string;
        Object verticalAnchor = jVar3.name();
        List list = p646xf.e.f81959a;
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it3 = map4.entrySet().iterator();
        Object obj4 = null;
        loop1: while (true) {
            obj = appVersionName;
            if (!it3.hasNext()) {
                aVar = aVarConvertToHashMap;
                map = map3;
                map2 = map4;
                str = null;
                cVar = new p646xf.c(null, (String) obj4);
                break;
            }
            for (p567uf.c cVar2 : (List) it3.next().getValue()) {
                map = map3;
                if (!cVar2.f77954g || ((str2 = cVar2.f77949a) == null && (str2 = cVar2.b) == null)) {
                    aVar = aVarConvertToHashMap;
                    map2 = map4;
                } else {
                    String lowerCase = Mv.r.P0(str2).toString().toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                    Iterator it4 = p646xf.e.f81959a.iterator();
                    while (true) {
                        Iterator it5 = it4;
                        if (!it4.hasNext()) {
                            aVar = aVarConvertToHashMap;
                            map2 = map4;
                            next = null;
                            break;
                        }
                        next = it5.next();
                        map2 = map4;
                        String str6 = (String) next;
                        if (lowerCase.equals(str6)) {
                            aVar = aVarConvertToHashMap;
                            break;
                        }
                        aVar = aVarConvertToHashMap;
                        if (y.X(lowerCase, str6 + " ", false)) {
                            break;
                        }
                        it4 = it5;
                        map4 = map2;
                        aVarConvertToHashMap = aVar;
                    }
                    String str7 = (String) next;
                    if (str7 != null) {
                        cVar = new p646xf.c(str7, (String) obj4);
                        str = null;
                        break loop1;
                    }
                    if (obj4 == null) {
                        Iterator it6 = p646xf.e.b.iterator();
                        while (true) {
                            if (!it6.hasNext()) {
                                obj4 = null;
                                break;
                            }
                            Object next3 = it6.next();
                            Iterator it7 = it6;
                            String str8 = (String) next3;
                            if (!lowerCase.equals(str8)) {
                                if (!y.X(lowerCase, str8 + " ", false)) {
                                    it6 = it7;
                                }
                            }
                            obj4 = next3;
                            break;
                        }
                    }
                    map3 = map;
                    map4 = map2;
                    aVarConvertToHashMap = aVar;
                }
                map3 = map;
                map4 = map2;
                aVarConvertToHashMap = aVar;
            }
            appVersionName = obj;
        }
        String str9 = c5057bFindYTChannelName != null ? c5057bFindYTChannelName.f75973a : str;
        String str10 = c5057bFindYTChannelName != null ? c5057bFindYTChannelName.b : null;
        String strInferWinningPass = inferWinningPass(c5057bFindYTChannelName != null ? c5057bFindYTChannelName.f75973a : null, c5057bFindYTChannelName != null ? c5057bFindYTChannelName.b : null, arrayList);
        m.h(label, "label");
        String str11 = str9;
        m.h(subscribeState, "subscribeState");
        m.h(verticalAnchor, "verticalAnchor");
        List list2 = p646xf.e.f81959a;
        JSONObject jSONObject = new JSONObject();
        String str12 = str10;
        jSONObject.put("schema_version", 1);
        jSONObject.put("label", label);
        jSONObject.put("captured_at", jCurrentTimeMillis);
        jSONObject.put("app_version", "71.3.2049");
        jSONObject.put("youtube_app_id", obj3 == null ? "null" : obj3);
        jSONObject.put("youtube_version", obj == null ? "null" : obj);
        jSONObject.put("orientation", obj2);
        jSONObject.put("is_tablet", zIsTablet);
        jSONObject.put("subscribe_state", subscribeState);
        jSONObject.put("subscribe_button_label", objFindSubscribeButtonLabel == null ? "null" : objFindSubscribeButtonLabel);
        jSONObject.put("vertical_anchor", verticalAnchor);
        jSONObject.put("is_video_page", z10);
        jSONObject.put("is_home_page", zIsYTHomePage);
        Object obj5 = cVar.f81954a;
        jSONObject.put("is_ad_playing", obj5 != null);
        Object obj6 = cVar.b;
        if (obj5 == null) {
            obj5 = obj6;
        }
        if (obj5 == null) {
            obj5 = "null";
        }
        jSONObject.put("ad_marker", obj5);
        if (obj6 == null) {
            obj6 = "null";
        }
        jSONObject.put("ad_furniture_marker", obj6);
        jSONObject.put("has_youtube_logo", true);
        jSONObject.put("max_right", aVar.f77943c);
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("channel_name", str11 == null ? "null" : str11);
        jSONObject2.put("channel_tag", str12 == null ? "null" : str12);
        jSONObject2.put("winning_pass", strInferWinningPass == null ? "null" : strInferWinningPass);
        jSONObject.put("resolved", jSONObject2);
        Object obj7 = JSONObject.NULL;
        jSONObject.put("expected_channel_handle", obj7);
        jSONObject.put("expected_channel_name", obj7);
        JSONArray jSONArray = new JSONArray();
        for (p646xf.d dVar : arrayList) {
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("group_key", dVar.f81955a);
            jSONObject3.put("group_type", "horizontal");
            String str13 = dVar.b;
            if (str13 == null) {
                str13 = "null";
            }
            jSONObject3.put("pass_a1_handle", str13);
            String str14 = dVar.f81956c;
            if (str14 == null) {
                str14 = "null";
            }
            jSONObject3.put("pass_a2_name", str14);
            String str15 = dVar.f81957d;
            if (str15 == null) {
                str15 = "null";
            }
            jSONObject3.put("pass_b_handle", str15);
            String str16 = dVar.f81958e;
            if (str16 == null) {
                str16 = "null";
            }
            jSONObject3.put("pass_c_name", str16);
            jSONArray.put(jSONObject3);
        }
        jSONObject.put("pass_results_by_row", jSONArray);
        jSONObject.put("horizontal_groups", p646xf.e.a("horizontal", map2));
        jSONObject.put("vertical_groups", p646xf.e.a("vertical", map));
        String string2 = jSONObject.toString(2);
        m.g(string2, "toString(...)");
        writeYTCapture(label, string2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:43:0x00a0  */
    /* JADX WARN: Code duplicated, block: B:45:0x00c3 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:46:0x00c5  */
    /* JADX WARN: Code duplicated, block: B:47:0x00c8  */
    /* JADX WARN: Code duplicated, block: B:49:0x00ea  */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object checkAnOpenBlockProtectionOverlay(p552tv.e<? super A> eVar) {
        C5080z c5080z;
        boolean z10;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        boolean z11;
        MyMonitorInAppBlockService myMonitorInAppBlockService2;
        boolean zIsProtectedfocusSettingsPage;
        R1.a aVar;
        if (eVar instanceof C5080z) {
            c5080z = (C5080z) eVar;
            int i8 = c5080z.f76117Z;
            if ((i8 & Level.ALL_INT) != 0) {
                c5080z.f76117Z = i8 - Level.ALL_INT;
            } else {
                c5080z = new C5080z(this, eVar);
            }
        } else {
            c5080z = new C5080z(this, eVar);
        }
        Object obj = c5080z.f76115X;
        p581uv.a aVar2 = p581uv.a.f79130e;
        int i10 = c5080z.f76117Z;
        boolean z12 = false;
        if (i10 == 0) {
            U8.b.F(obj);
            Sv.v0 v0Var = getMonitorServiceStateManager().f852g;
            p699z8.c cVar = ((C0075n) v0Var.getValue()).f813l;
            p699z8.c cVar2 = ((C0075n) v0Var.getValue()).f813l;
            FocusTimerSession focusTimerSession = ((C0075n) v0Var.getValue()).f803a;
            z10 = cVar.f85326a || (cVar2.b && (focusTimerSession != null && focusTimerSession.isUninstallBlocked()));
            if (isInternalTesting().a()) {
                C4887v getInstagramChallengeActive = getGetInstagramChallengeActive();
                c5080z.f76118e = this;
                c5080z.f76114I = z10;
                c5080z.f76117Z = 1;
                Object objA = getInstagramChallengeActive.a(c5080z);
                if (objA == aVar2) {
                    return aVar2;
                }
                z11 = z10;
                obj = objA;
                myMonitorInAppBlockService = this;
            } else {
                myMonitorInAppBlockService = this;
            }
            z11 = z10;
            myMonitorInAppBlockService2 = myMonitorInAppBlockService;
            if (z11 || z12) {
                zIsProtectedfocusSettingsPage = myMonitorInAppBlockService2.isProtectedfocusSettingsPage(convertToHashMap$default(myMonitorInAppBlockService2, AbstractC3102b.k("SettingHashMapGroup-", myMonitorInAppBlockService2.getRandomString(5)), myMonitorInAppBlockService2.getRootInActiveWindow(), null, 4, null));
                M0.A("isProtectedfocusSettingsPage: ", "UninstallProtection", zIsProtectedfocusSettingsPage);
                if (zIsProtectedfocusSettingsPage) {
                    if (z12) {
                        aVar = R1.a.INSTAGRAM_CHALLENGE_PROTECTION;
                    } else {
                        aVar = R1.a.UNINSTALL_PROTECTION;
                    }
                    p118e.b.d(myMonitorInAppBlockService2.logTag, "Blocking protected settings page - showing " + aVar.name() + " screen");
                    myMonitorInAppBlockService2.showBlockProtectionScreen(aVar);
                } else {
                    p118e.b.d(myMonitorInAppBlockService2.logTag, "Uninstall protection - Not a protective settings page, not showing protection screen");
                }
            }
            return A.f64853a;
        }
        if (i10 != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        z11 = c5080z.f76114I;
        myMonitorInAppBlockService = c5080z.f76118e;
        U8.b.F(obj);
        if (((Boolean) obj).booleanValue()) {
            z12 = true;
        } else {
            z10 = z11;
            z11 = z10;
        }
        myMonitorInAppBlockService2 = myMonitorInAppBlockService;
        if (z11) {
            zIsProtectedfocusSettingsPage = myMonitorInAppBlockService2.isProtectedfocusSettingsPage(convertToHashMap$default(myMonitorInAppBlockService2, AbstractC3102b.k("SettingHashMapGroup-", myMonitorInAppBlockService2.getRandomString(5)), myMonitorInAppBlockService2.getRootInActiveWindow(), null, 4, null));
            M0.A("isProtectedfocusSettingsPage: ", "UninstallProtection", zIsProtectedfocusSettingsPage);
            if (zIsProtectedfocusSettingsPage) {
                if (z12) {
                    aVar = R1.a.INSTAGRAM_CHALLENGE_PROTECTION;
                } else {
                    aVar = R1.a.UNINSTALL_PROTECTION;
                }
                p118e.b.d(myMonitorInAppBlockService2.logTag, "Blocking protected settings page - showing " + aVar.name() + " screen");
                myMonitorInAppBlockService2.showBlockProtectionScreen(aVar);
            } else {
                p118e.b.d(myMonitorInAppBlockService2.logTag, "Uninstall protection - Not a protective settings page, not showing protection screen");
            }
        } else {
            zIsProtectedfocusSettingsPage = myMonitorInAppBlockService2.isProtectedfocusSettingsPage(convertToHashMap$default(myMonitorInAppBlockService2, AbstractC3102b.k("SettingHashMapGroup-", myMonitorInAppBlockService2.getRandomString(5)), myMonitorInAppBlockService2.getRootInActiveWindow(), null, 4, null));
            M0.A("isProtectedfocusSettingsPage: ", "UninstallProtection", zIsProtectedfocusSettingsPage);
            if (zIsProtectedfocusSettingsPage) {
                if (z12) {
                    aVar = R1.a.INSTAGRAM_CHALLENGE_PROTECTION;
                } else {
                    aVar = R1.a.UNINSTALL_PROTECTION;
                }
                p118e.b.d(myMonitorInAppBlockService2.logTag, "Blocking protected settings page - showing " + aVar.name() + " screen");
                myMonitorInAppBlockService2.showBlockProtectionScreen(aVar);
            } else {
                p118e.b.d(myMonitorInAppBlockService2.logTag, "Uninstall protection - Not a protective settings page, not showing protection screen");
            }
        }
        return A.f64853a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:8:0x001e  */
    public final Object checkAndEnterPMChannelEvent(String str, p567uf.b bVar, String str2, String str3, List<Integer> list, p552tv.e<? super A> eVar) {
        p536tf.A a10;
        String str4;
        String str5;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        List<Integer> list2;
        String str6;
        p567uf.b bVar2;
        MyMonitorInAppBlockService myMonitorInAppBlockService2;
        MyMonitorInAppBlockService myMonitorInAppBlockService3;
        p567uf.b bVar3 = bVar;
        String str7 = str2;
        List<Integer> list3 = list;
        if (eVar instanceof p536tf.A) {
            a10 = (p536tf.A) eVar;
            int i8 = a10.f75826o0;
            if ((i8 & Level.ALL_INT) != 0) {
                a10.f75826o0 = i8 - Level.ALL_INT;
            } else {
                a10 = new p536tf.A(this, eVar);
            }
        } else {
            a10 = new p536tf.A(this, eVar);
        }
        p536tf.A a11 = a10;
        Object obj = a11.f75824m0;
        p581uv.a aVar = p581uv.a.f79130e;
        int i10 = a11.f75826o0;
        if (i10 == 0) {
            U8.b.F(obj);
            int iOrdinal = bVar3.ordinal();
            str4 = "checkAndEnterPMChannelEvent";
            if (iOrdinal != 0) {
                if (iOrdinal != 1) {
                    if (iOrdinal != 2) {
                        throw new NoWhenBranchMatchedException();
                    }
                    C2657a c2657a = getProductiveModeUseCases().f41486T;
                    a11.f75822e = this;
                    a11.f75818I = bVar3;
                    a11.f75819X = str7;
                    a11.f75820Y = "checkAndEnterPMChannelEvent";
                    a11.f75826o0 = 1;
                    if (c2657a.b(bVar3, str7, str3, str, a11) != aVar) {
                        myMonitorInAppBlockService3 = this;
                        p118e.b.d(str4, "HomePage detected event added");
                    }
                } else if (m.c(this.lastDetectedChannelName, str7) && this.lastDetectedContentType == bVar3) {
                    AbstractC3102b.A("Skipping enterProductiveMode for channelName: ", str7, "checkAndEnterPMChannelEvent");
                    bVar3 = bVar;
                    myMonitorInAppBlockService3 = this;
                } else {
                    C2657a c2657a2 = getProductiveModeUseCases().f41486T;
                    a11.f75822e = this;
                    a11.f75818I = bVar3;
                    a11.f75819X = str7;
                    a11.f75820Y = "checkAndEnterPMChannelEvent";
                    a11.f75826o0 = 2;
                    if (c2657a2.b(bVar3, str7, str3, str, a11) != aVar) {
                        myMonitorInAppBlockService3 = this;
                        AbstractC3102b.A("LongVideo detected event added, channelName: ", str7, str4);
                    }
                }
                return aVar;
            }
            str5 = str3;
            if (lastLikesDigest == null) {
                C2657a c2657a3 = getProductiveModeUseCases().f41486T;
                a11.f75822e = this;
                a11.f75818I = bVar3;
                a11.f75819X = str7;
                a11.f75820Y = str5;
                a11.f75821Z = list3;
                a11.f75823l0 = "checkAndEnterPMChannelEvent";
                a11.f75826o0 = 3;
                if (c2657a3.b(bVar3, str7, str5, str, a11) != aVar) {
                    myMonitorInAppBlockService2 = this;
                    lastLikesDigest = list3;
                    p118e.b.d(str4, str4 + " ShortVideo detected event added, channelName: " + str7 + ", channelHandle: " + str5 + ", likeCommentDigest: " + list3);
                    myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                }
            } else if (checkIfScrolled("checkAndEnterPMChannelEvent", str, list3)) {
                C2657a c2657a4 = getProductiveModeUseCases().f41486T;
                a11.f75822e = this;
                a11.f75818I = bVar3;
                a11.f75819X = str7;
                a11.f75820Y = str5;
                a11.f75821Z = list3;
                a11.f75823l0 = "checkAndEnterPMChannelEvent";
                a11.f75826o0 = 4;
                if (c2657a4.b(bVar3, str7, str5, str, a11) != aVar) {
                    myMonitorInAppBlockService = this;
                    list2 = list3;
                    str6 = "checkAndEnterPMChannelEvent";
                    bVar2 = bVar;
                    p118e.b.d(str6, str6 + " ShortVideo detected event added, channelName: " + str7 + ", channelHandle: " + str5 + ", likeCommentDigest: " + list2);
                    myMonitorInAppBlockService3 = myMonitorInAppBlockService;
                    bVar3 = bVar2;
                }
            } else {
                StringBuilder sbP = AbstractC3102b.p("checkAndEnterPMChannelEvent Skipping enterProductiveMode for channelName: ", str7, ", channelHandle: ", str5, ", likeCommentDigest: ");
                sbP.append(list3);
                p118e.b.d("checkAndEnterPMChannelEvent", sbP.toString());
                bVar3 = bVar;
                myMonitorInAppBlockService3 = this;
            }
            return aVar;
        }
        if (i10 == 1) {
            String str8 = a11.f75820Y;
            str7 = a11.f75819X;
            p567uf.b bVar4 = a11.f75818I;
            myMonitorInAppBlockService3 = a11.f75822e;
            U8.b.F(obj);
            str4 = str8;
            bVar3 = bVar4;
            p118e.b.d(str4, "HomePage detected event added");
        } else if (i10 == 2) {
            String str9 = a11.f75820Y;
            str7 = a11.f75819X;
            p567uf.b bVar5 = a11.f75818I;
            myMonitorInAppBlockService3 = a11.f75822e;
            U8.b.F(obj);
            str4 = str9;
            bVar3 = bVar5;
            AbstractC3102b.A("LongVideo detected event added, channelName: ", str7, str4);
        } else if (i10 == 3) {
            String str10 = a11.f75823l0;
            List<Integer> list4 = a11.f75821Z;
            str5 = a11.f75820Y;
            String str11 = a11.f75819X;
            p567uf.b bVar6 = a11.f75818I;
            myMonitorInAppBlockService2 = a11.f75822e;
            U8.b.F(obj);
            str4 = str10;
            bVar3 = bVar6;
            list3 = list4;
            str7 = str11;
            lastLikesDigest = list3;
            p118e.b.d(str4, str4 + " ShortVideo detected event added, channelName: " + str7 + ", channelHandle: " + str5 + ", likeCommentDigest: " + list3);
            myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
        } else {
            if (i10 != 4) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            str6 = a11.f75823l0;
            List<Integer> list5 = a11.f75821Z;
            str5 = a11.f75820Y;
            String str12 = a11.f75819X;
            bVar2 = a11.f75818I;
            myMonitorInAppBlockService = a11.f75822e;
            U8.b.F(obj);
            list2 = list5;
            str7 = str12;
            p118e.b.d(str6, str6 + " ShortVideo detected event added, channelName: " + str7 + ", channelHandle: " + str5 + ", likeCommentDigest: " + list2);
            myMonitorInAppBlockService3 = myMonitorInAppBlockService;
            bVar3 = bVar2;
        }
        myMonitorInAppBlockService3.lastDetectedChannelName = str7;
        myMonitorInAppBlockService3.lastDetectedContentType = bVar3;
        return A.f64853a;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ Object checkAndEnterPMChannelEvent$default(MyMonitorInAppBlockService myMonitorInAppBlockService, String str, p567uf.b bVar, String str2, String str3, List list, p552tv.e eVar, int i8, Object obj) {
        if ((i8 & 4) != 0) {
            str2 = null;
        }
        if ((i8 & 8) != 0) {
            str3 = null;
        }
        if ((i8 & 16) != 0) {
            list = null;
        }
        return myMonitorInAppBlockService.checkAndEnterPMChannelEvent(str, bVar, str2, str3, list, eVar);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object checkAndOpenBLOverlay(String str, p552tv.e<? super A> eVar) {
        Wv.e eVar2 = M.f24563a;
        Object objM = C.M(Wv.d.f37341X, new B(this, str, null), eVar);
        return objM == p581uv.a.f79130e ? objM : A.f64853a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object checkAndOpenBWOverlay(String str, p552tv.e<? super A> eVar) {
        Wv.e eVar2 = M.f24563a;
        Object objM = C.M(Wv.d.f37341X, new p536tf.C(this, str, null), eVar);
        return objM == p581uv.a.f79130e ? objM : A.f64853a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:100:0x0288  */
    /* JADX WARN: Code duplicated, block: B:104:0x0298  */
    /* JADX WARN: Code duplicated, block: B:106:0x02b3  */
    /* JADX WARN: Code duplicated, block: B:107:0x02b6  */
    /* JADX WARN: Code duplicated, block: B:110:0x02be  */
    /* JADX WARN: Code duplicated, block: B:114:0x02cd  */
    /* JADX WARN: Code duplicated, block: B:116:0x02d5  */
    /* JADX WARN: Code duplicated, block: B:119:0x02e8 A[PHI: r3 r10 r11 r12 r18
  0x02e8: PHI (r3v13 java.lang.String) = (r3v10 java.lang.String), (r3v10 java.lang.String), (r3v19 java.lang.String) binds: [B:115:0x02d3, B:117:0x02e4, B:53:0x015c] A[DONT_GENERATE, DONT_INLINE]
  0x02e8: PHI (r10v7 ai.focus.feature_app_usage_stats.domain.model.EventData) = 
  (r10v4 ai.focus.feature_app_usage_stats.domain.model.EventData)
  (r10v4 ai.focus.feature_app_usage_stats.domain.model.EventData)
  (r10v19 ai.focus.feature_app_usage_stats.domain.model.EventData)
 binds: [B:115:0x02d3, B:117:0x02e4, B:53:0x015c] A[DONT_GENERATE, DONT_INLINE]
  0x02e8: PHI (r11v6 java.lang.String) = (r11v3 java.lang.String), (r11v3 java.lang.String), (r11v16 java.lang.String) binds: [B:115:0x02d3, B:117:0x02e4, B:53:0x015c] A[DONT_GENERATE, DONT_INLINE]
  0x02e8: PHI (r12v4 ai.focus.services.MyMonitorInAppBlockService) = 
  (r12v2 ai.focus.services.MyMonitorInAppBlockService)
  (r12v2 ai.focus.services.MyMonitorInAppBlockService)
  (r12v12 ai.focus.services.MyMonitorInAppBlockService)
 binds: [B:115:0x02d3, B:117:0x02e4, B:53:0x015c] A[DONT_GENERATE, DONT_INLINE]
  0x02e8: PHI (r18v3 java.lang.String) = (r9v0 java.lang.String), (r9v0 java.lang.String), (r18v5 java.lang.String) binds: [B:115:0x02d3, B:117:0x02e4, B:53:0x015c] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:121:0x02f6  */
    /* JADX WARN: Code duplicated, block: B:123:0x0304  */
    /* JADX WARN: Code duplicated, block: B:126:0x0311  */
    /* JADX WARN: Code duplicated, block: B:134:0x03c9  */
    /* JADX WARN: Code duplicated, block: B:137:0x03f4  */
    /* JADX WARN: Code duplicated, block: B:139:0x03f8  */
    /* JADX WARN: Code duplicated, block: B:141:0x03fc  */
    /* JADX WARN: Code duplicated, block: B:146:0x0419  */
    /* JADX WARN: Code duplicated, block: B:148:0x041d  */
    /* JADX WARN: Code duplicated, block: B:150:0x0421  */
    /* JADX WARN: Code duplicated, block: B:153:0x0439  */
    /* JADX WARN: Code duplicated, block: B:155:0x043d A[PHI: r0 r1 r3 r7 r11 r12 r22
  0x043d: PHI (r0v10 java.lang.Throwable) = (r0v92 java.lang.Throwable), (r0v93 java.lang.Throwable) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]
  0x043d: PHI (r1v33 z8.a) = (r1v32 z8.a), (r1v34 z8.a) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]
  0x043d: PHI (r3v24 java.lang.String) = (r3v20 java.lang.String), (r3v28 java.lang.String) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]
  0x043d: PHI (r7v4 tf.D) = (r7v3 tf.D), (r7v5 tf.D) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]
  0x043d: PHI (r11v20 java.lang.String) = (r11v17 java.lang.String), (r11v21 java.lang.String) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]
  0x043d: PHI (r12v15 ai.focus.services.MyMonitorInAppBlockService) = (r12v13 ai.focus.services.MyMonitorInAppBlockService), (r12v16 ai.focus.services.MyMonitorInAppBlockService) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]
  0x043d: PHI (r22v3 java.lang.String) = (r22v1 java.lang.String), (r22v4 java.lang.String) binds: [B:136:0x03f2, B:145:0x0416] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:157:0x0444  */
    /* JADX WARN: Code duplicated, block: B:159:0x044f  */
    /* JADX WARN: Code duplicated, block: B:162:0x0468  */
    /* JADX WARN: Code duplicated, block: B:165:0x046e  */
    /* JADX WARN: Code duplicated, block: B:170:0x0487  */
    /* JADX WARN: Code duplicated, block: B:172:0x048b  */
    /* JADX WARN: Code duplicated, block: B:174:0x048f  */
    /* JADX WARN: Code duplicated, block: B:177:0x049d  */
    /* JADX WARN: Code duplicated, block: B:179:0x04a3  */
    /* JADX WARN: Code duplicated, block: B:181:0x04af  */
    /* JADX WARN: Code duplicated, block: B:184:0x04bd  */
    /* JADX WARN: Code duplicated, block: B:188:0x04d9  */
    /* JADX WARN: Code duplicated, block: B:190:0x04f2  */
    /* JADX WARN: Code duplicated, block: B:191:0x04f4  */
    /* JADX WARN: Code duplicated, block: B:195:0x0520  */
    /* JADX WARN: Code duplicated, block: B:198:0x052c A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:199:0x052e  */
    /* JADX WARN: Code duplicated, block: B:202:0x0546  */
    /* JADX WARN: Code duplicated, block: B:204:0x054a  */
    /* JADX WARN: Code duplicated, block: B:207:0x0563  */
    /* JADX WARN: Code duplicated, block: B:209:0x0567  */
    /* JADX WARN: Code duplicated, block: B:211:0x056b  */
    /* JADX WARN: Code duplicated, block: B:214:0x0584  */
    /* JADX WARN: Code duplicated, block: B:216:0x0588  */
    /* JADX WARN: Code duplicated, block: B:218:0x0592  */
    /* JADX WARN: Code duplicated, block: B:221:0x05ab  */
    /* JADX WARN: Code duplicated, block: B:223:0x05af  */
    /* JADX WARN: Code duplicated, block: B:226:0x05c7  */
    /* JADX WARN: Code duplicated, block: B:229:0x05d4 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:235:0x05f4  */
    /* JADX WARN: Code duplicated, block: B:245:0x0628  */
    /* JADX WARN: Code duplicated, block: B:247:0x0648  */
    /* JADX WARN: Code duplicated, block: B:250:0x0661  */
    /* JADX WARN: Code duplicated, block: B:252:0x0665  */
    /* JADX WARN: Code duplicated, block: B:254:0x0685  */
    /* JADX WARN: Code duplicated, block: B:257:0x069e  */
    /* JADX WARN: Code duplicated, block: B:259:0x06a2  */
    /* JADX WARN: Code duplicated, block: B:261:0x06a6  */
    /* JADX WARN: Code duplicated, block: B:263:0x06aa  */
    /* JADX WARN: Code duplicated, block: B:266:0x06c3  */
    /* JADX WARN: Code duplicated, block: B:268:0x06c8  */
    /* JADX WARN: Code duplicated, block: B:270:0x06cc A[PHI: r0 r4 r5 r7 r11
  0x06cc: PHI (r0v57 ??) = (r0v80 ??), (r0v81 ??) binds: [B:260:0x06a4, B:267:0x06c5] A[DONT_GENERATE, DONT_INLINE]
  0x06cc: PHI (r4v69 java.lang.String) = (r4v29 java.lang.String), (r4v71 java.lang.String) binds: [B:260:0x06a4, B:267:0x06c5] A[DONT_GENERATE, DONT_INLINE]
  0x06cc: PHI (r5v38 ai.focus.services.MyMonitorInAppBlockService) = (r5v21 ai.focus.services.MyMonitorInAppBlockService), (r5v39 ai.focus.services.MyMonitorInAppBlockService) binds: [B:260:0x06a4, B:267:0x06c5] A[DONT_GENERATE, DONT_INLINE]
  0x06cc: PHI (r7v13 tf.D) = (r7v7 tf.D), (r7v14 tf.D) binds: [B:260:0x06a4, B:267:0x06c5] A[DONT_GENERATE, DONT_INLINE]
  0x06cc: PHI (r11v33 java.lang.String) = (r11v27 java.lang.String), (r11v34 java.lang.String) binds: [B:260:0x06a4, B:267:0x06c5] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:273:0x06e1  */
    /* JADX WARN: Code duplicated, block: B:275:0x06ec  */
    /* JADX WARN: Code duplicated, block: B:277:0x06f0  */
    /* JADX WARN: Code duplicated, block: B:280:0x0709  */
    /* JADX WARN: Code duplicated, block: B:282:0x070e  */
    /* JADX WARN: Code duplicated, block: B:284:0x0712 A[PHI: r0 r4 r5 r7 r11
  0x0712: PHI (r0v63 ??) = (r0v77 ??), (r0v78 ??) binds: [B:274:0x06ea, B:281:0x070b] A[DONT_GENERATE, DONT_INLINE]
  0x0712: PHI (r4v74 java.lang.String) = (r4v29 java.lang.String), (r4v76 java.lang.String) binds: [B:274:0x06ea, B:281:0x070b] A[DONT_GENERATE, DONT_INLINE]
  0x0712: PHI (r5v41 ai.focus.services.MyMonitorInAppBlockService) = (r5v21 ai.focus.services.MyMonitorInAppBlockService), (r5v42 ai.focus.services.MyMonitorInAppBlockService) binds: [B:274:0x06ea, B:281:0x070b] A[DONT_GENERATE, DONT_INLINE]
  0x0712: PHI (r7v15 tf.D) = (r7v7 tf.D), (r7v16 tf.D) binds: [B:274:0x06ea, B:281:0x070b] A[DONT_GENERATE, DONT_INLINE]
  0x0712: PHI (r11v35 java.lang.String) = (r11v27 java.lang.String), (r11v36 java.lang.String) binds: [B:274:0x06ea, B:281:0x070b] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:286:0x0730  */
    /* JADX WARN: Code duplicated, block: B:289:0x0744  */
    /* JADX WARN: Code duplicated, block: B:291:0x0748  */
    /* JADX WARN: Code duplicated, block: B:293:0x074c  */
    /* JADX WARN: Code duplicated, block: B:296:0x0765  */
    /* JADX WARN: Code duplicated, block: B:298:0x076c  */
    /* JADX WARN: Code duplicated, block: B:300:0x0770 A[PHI: r0 r4 r5 r7 r11
  0x0770: PHI (r0v50 ??) = (r0v83 ??), (r0v84 ??) binds: [B:290:0x0746, B:297:0x0768] A[DONT_GENERATE, DONT_INLINE]
  0x0770: PHI (r4v67 java.lang.String) = (r4v29 java.lang.String), (r4v68 java.lang.String) binds: [B:290:0x0746, B:297:0x0768] A[DONT_GENERATE, DONT_INLINE]
  0x0770: PHI (r5v33 ai.focus.services.MyMonitorInAppBlockService) = (r5v21 ai.focus.services.MyMonitorInAppBlockService), (r5v35 ai.focus.services.MyMonitorInAppBlockService) binds: [B:290:0x0746, B:297:0x0768] A[DONT_GENERATE, DONT_INLINE]
  0x0770: PHI (r7v11 tf.D) = (r7v7 tf.D), (r7v12 tf.D) binds: [B:290:0x0746, B:297:0x0768] A[DONT_GENERATE, DONT_INLINE]
  0x0770: PHI (r11v31 java.lang.String) = (r11v27 java.lang.String), (r11v32 java.lang.String) binds: [B:290:0x0746, B:297:0x0768] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:302:0x078e  */
    /* JADX WARN: Code duplicated, block: B:304:0x0794  */
    /* JADX WARN: Code duplicated, block: B:306:0x07ae  */
    /* JADX WARN: Code duplicated, block: B:309:0x07c6  */
    /* JADX WARN: Code duplicated, block: B:311:0x07e6  */
    /* JADX WARN: Code duplicated, block: B:314:0x07ff  */
    /* JADX WARN: Code duplicated, block: B:316:0x0803  */
    /* JADX WARN: Code duplicated, block: B:318:0x0823  */
    /* JADX WARN: Code duplicated, block: B:321:0x083c  */
    /* JADX WARN: Code duplicated, block: B:323:0x0840  */
    /* JADX WARN: Code duplicated, block: B:325:0x0860  */
    /* JADX WARN: Code duplicated, block: B:328:0x0877  */
    /* JADX WARN: Code duplicated, block: B:331:0x0890  */
    /* JADX WARN: Code duplicated, block: B:333:0x0894  */
    /* JADX WARN: Code duplicated, block: B:335:0x08b4  */
    /* JADX WARN: Code duplicated, block: B:338:0x08cd  */
    /* JADX WARN: Code duplicated, block: B:341:0x08e6  */
    /* JADX WARN: Code duplicated, block: B:343:0x08ea  */
    /* JADX WARN: Code duplicated, block: B:345:0x090c A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:356:0x093b  */
    /* JADX WARN: Code duplicated, block: B:358:0x0952 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:359:0x0953  */
    /* JADX WARN: Code duplicated, block: B:362:0x096b  */
    /* JADX WARN: Code duplicated, block: B:365:0x0985 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:75:0x01d6  */
    /* JADX WARN: Code duplicated, block: B:8:0x0018  */
    /* JADX WARN: Code duplicated, block: B:93:0x022e  */
    /* JADX WARN: Code duplicated, block: B:96:0x0273  */
    /* JADX WARN: Code duplicated, block: B:98:0x0284  */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x0410, code lost:
    
        if (r4.invoke(r5, r7) == r2) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x0414, code lost:
    
        r10 = r3;
        r3 = r1;
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x0435, code lost:
    
        if (r4.invoke(r5, r7) == r2) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x0480, code lost:
    
        if (r1.invoke(r4, r7) == r2) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x05ed, code lost:
    
        if (r1.a(r4, r7) == r2) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x05f1, code lost:
    
        r4 = r3;
        r3 = r10;
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x0607, code lost:
    
        if (checkForBlockContent$default(r3, r4, r5, false, r7, 4, null) == r2) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:350:0x0929, code lost:
    
        if (r5.pauseYouTubeVideo(r7) == r2) goto L358;
     */
    /* JADX WARN: Instruction removed from duplicated block: B:245:0x0628, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:252:0x0665, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:284:0x0712, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:300:0x0770, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:304:0x0794, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:309:0x07c6, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:316:0x0803, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:323:0x0840, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:333:0x0894, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:343:0x08ea, please report this as an issue */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v15, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v39, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v42 */
    /* JADX WARN: Type inference failed for: r0v43, types: [java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v46 */
    /* JADX WARN: Type inference failed for: r0v50, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v55 */
    /* JADX WARN: Type inference failed for: r0v56 */
    /* JADX WARN: Type inference failed for: r0v57, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v61 */
    /* JADX WARN: Type inference failed for: r0v62 */
    /* JADX WARN: Type inference failed for: r0v63, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v66 */
    /* JADX WARN: Type inference failed for: r0v67 */
    /* JADX WARN: Type inference failed for: r0v68, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r0v69 */
    /* JADX WARN: Type inference failed for: r0v70, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r0v73 */
    /* JADX WARN: Type inference failed for: r0v74 */
    /* JADX WARN: Type inference failed for: r0v75 */
    /* JADX WARN: Type inference failed for: r0v76 */
    /* JADX WARN: Type inference failed for: r0v77 */
    /* JADX WARN: Type inference failed for: r0v78 */
    /* JADX WARN: Type inference failed for: r0v79 */
    /* JADX WARN: Type inference failed for: r0v80 */
    /* JADX WARN: Type inference failed for: r0v81 */
    /* JADX WARN: Type inference failed for: r0v82 */
    /* JADX WARN: Type inference failed for: r0v83 */
    /* JADX WARN: Type inference failed for: r0v84 */
    /* JADX WARN: Type inference failed for: r0v85 */
    /* JADX WARN: Type inference failed for: r0v86 */
    /* JADX WARN: Type inference failed for: r0v88 */
    /* JADX WARN: Type inference failed for: r0v89 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object checkAndOpenOverlay(p552tv.e<? super kotlin.A> r24) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 2546
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorInAppBlockService.checkAndOpenOverlay(tv.e):java.lang.Object");
    }

    private final void checkAndUpdateRecentApp(String str, p699z8.a aVar) {
        p118e.b.d(this.logTag, this.logTag1 + " checkAndUpdateRecentApp");
        if (m.c(this.lastRecentAppId, str)) {
            if (System.currentTimeMillis() > this.computeUsageStatsExpiry) {
                Context applicationContext = getApplicationContext();
                m.g(applicationContext, "getApplicationContext(...)");
                sendUpdateBroadcast(applicationContext, str);
                this.computeUsageStatsExpiry = System.currentTimeMillis() + 60000;
                return;
            }
            return;
        }
        this.lastRecentAppId = str;
        Context applicationContext2 = getApplicationContext();
        m.g(applicationContext2, "getApplicationContext(...)");
        sendUpdateBroadcast(applicationContext2, str);
        m.h(aVar, "<this>");
        p699z8.b bVar = p699z8.b.f85315Y;
        p699z8.b bVar2 = aVar.f85302c;
        monitorPMFloatingButtonService(str, bVar2 == bVar || bVar2 == p699z8.b.f85316Z, aVar.f85306g);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:100:0x01b7  */
    /* JADX WARN: Code duplicated, block: B:102:0x01ba A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:103:0x01bc A[Catch: all -> 0x010b, TryCatch #4 {all -> 0x010b, blocks: (B:94:0x01ad, B:103:0x01bc, B:105:0x01c0, B:56:0x00ef, B:60:0x010f, B:61:0x0114, B:62:0x0115, B:64:0x011a, B:66:0x011e, B:69:0x0124, B:71:0x012a, B:78:0x0136, B:81:0x0149, B:83:0x016a, B:84:0x016e, B:112:0x01de, B:51:0x00df), top: B:172:0x00df }] */
    /* JADX WARN: Code duplicated, block: B:104:0x01bf  */
    /* JADX WARN: Code duplicated, block: B:107:0x01d4  */
    /* JADX WARN: Code duplicated, block: B:127:0x0246  */
    /* JADX WARN: Code duplicated, block: B:128:0x0248 A[Catch: all -> 0x027f, PHI: r0 r2 r3 r4 r8 r13 r15 r17
  0x0248: PHI (r0v52 boolean) = (r0v48 boolean), (r0v59 boolean) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r2v17 java.lang.String) = (r2v15 java.lang.String), (r2v18 java.lang.String) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r3v24 ai.focus.services.domain.model.ShortFormContentPresent) = 
  (r3v21 ai.focus.services.domain.model.ShortFormContentPresent)
  (r3v27 ai.focus.services.domain.model.ShortFormContentPresent)
 binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r4v28 java.lang.Object) = (r4v24 java.lang.Object), (r4v32 java.lang.Object) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r8v19 ai.focus.services.MyMonitorInAppBlockService) = (r8v17 ai.focus.services.MyMonitorInAppBlockService), (r8v20 ai.focus.services.MyMonitorInAppBlockService) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r13v11 long) = (r13v7 long), (r13v12 long) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r15v6 int) = (r15v4 int), (r15v7 int) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE]
  0x0248: PHI (r17v19 long) = (r17v15 long), (r17v20 long) binds: [B:126:0x0244, B:26:0x005c] A[DONT_GENERATE, DONT_INLINE], TryCatch #7 {all -> 0x027f, blocks: (B:128:0x0248, B:130:0x024c, B:132:0x0252, B:136:0x0259, B:138:0x025f, B:125:0x0233, B:121:0x0215, B:147:0x0289, B:149:0x0290, B:152:0x02b6, B:154:0x02d2, B:157:0x02f8), top: B:165:0x002d }] */
    /* JADX WARN: Code duplicated, block: B:130:0x024c A[Catch: all -> 0x027f, TryCatch #7 {all -> 0x027f, blocks: (B:128:0x0248, B:130:0x024c, B:132:0x0252, B:136:0x0259, B:138:0x025f, B:125:0x0233, B:121:0x0215, B:147:0x0289, B:149:0x0290, B:152:0x02b6, B:154:0x02d2, B:157:0x02f8), top: B:165:0x002d }] */
    /* JADX WARN: Code duplicated, block: B:134:0x0256  */
    /* JADX WARN: Code duplicated, block: B:135:0x0258  */
    /* JADX WARN: Code duplicated, block: B:138:0x025f A[Catch: all -> 0x027f, TryCatch #7 {all -> 0x027f, blocks: (B:128:0x0248, B:130:0x024c, B:132:0x0252, B:136:0x0259, B:138:0x025f, B:125:0x0233, B:121:0x0215, B:147:0x0289, B:149:0x0290, B:152:0x02b6, B:154:0x02d2, B:157:0x02f8), top: B:165:0x002d }] */
    /* JADX WARN: Code duplicated, block: B:140:0x027a  */
    /* JADX WARN: Code duplicated, block: B:8:0x001c  */
    /* JADX WARN: Code duplicated, block: B:90:0x01a2  */
    /* JADX WARN: Code duplicated, block: B:91:0x01a4 A[Catch: all -> 0x01da, PHI: r0 r2 r3 r7 r9 r10 r17
  0x01a4: PHI (r0v46 boolean) = (r0v35 boolean), (r0v47 boolean) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE]
  0x01a4: PHI (r2v11 java.lang.String) = (r2v6 java.lang.String), (r2v14 java.lang.String) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE]
  0x01a4: PHI (r3v18 java.lang.Object) = (r3v15 java.lang.Object), (r3v1 java.lang.Object) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE]
  0x01a4: PHI (r7v12 long) = (r7v5 long), (r7v13 long) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE]
  0x01a4: PHI (r9v7 tf.b) = (r9v4 tf.b), (r9v8 tf.b) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE]
  0x01a4: PHI (r10v13 ai.focus.services.MyMonitorInAppBlockService) = (r10v10 ai.focus.services.MyMonitorInAppBlockService), (r10v14 ai.focus.services.MyMonitorInAppBlockService) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE]
  0x01a4: PHI (r17v13 long) = (r17v8 long), (r17v14 long) binds: [B:89:0x01a0, B:38:0x0099] A[DONT_GENERATE, DONT_INLINE], TRY_LEAVE, TryCatch #3 {all -> 0x01da, blocks: (B:91:0x01a4, B:88:0x018e), top: B:170:0x018e }] */
    /* JADX WARN: Code duplicated, block: B:94:0x01ad A[Catch: all -> 0x010b, TRY_ENTER, TryCatch #4 {all -> 0x010b, blocks: (B:94:0x01ad, B:103:0x01bc, B:105:0x01c0, B:56:0x00ef, B:60:0x010f, B:61:0x0114, B:62:0x0115, B:64:0x011a, B:66:0x011e, B:69:0x0124, B:71:0x012a, B:78:0x0136, B:81:0x0149, B:83:0x016a, B:84:0x016e, B:112:0x01de, B:51:0x00df), top: B:172:0x00df }] */
    /* JADX WARN: Code duplicated, block: B:96:0x01b1  */
    /* JADX WARN: Code duplicated, block: B:97:0x01b3  */
    /* JADX WARN: Code duplicated, block: B:99:0x01b6  */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x02f5, code lost:
    
        if (checkAndOpenBLOverlay(r2, r11) == r12) goto L156;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00a1: MOVE (r4 I:??[long, double]) = (r8 I:??[long, double]) (LINE:162), block:B:40:0x00a1 */
    /* JADX WARN: Type inference failed for: r13v0 */
    /* JADX WARN: Type inference failed for: r13v1 */
    /* JADX WARN: Type inference failed for: r13v2 */
    /* JADX WARN: Type inference failed for: r13v4 */
    /* JADX WARN: Type inference failed for: r4v17 */
    /* JADX WARN: Type inference failed for: r4v3, types: [int] */
    /* JADX WARN: Type inference failed for: r4v4 */
    /* JADX WARN: Type inference failed for: r4v40 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7, types: [long] */
    /* JADX WARN: Type inference failed for: r4v8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object checkForBlockContent(java.lang.String r22, java.lang.String r23, boolean r24, p552tv.e<? super kotlin.A> r25) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 830
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorInAppBlockService.checkForBlockContent(java.lang.String, java.lang.String, boolean, tv.e):java.lang.Object");
    }

    public static /* synthetic */ Object checkForBlockContent$default(MyMonitorInAppBlockService myMonitorInAppBlockService, String str, String str2, boolean z10, p552tv.e eVar, int i8, Object obj) {
        if ((i8 & 4) != 0) {
            z10 = false;
        }
        return myMonitorInAppBlockService.checkForBlockContent(str, str2, z10, eVar);
    }

    private final boolean checkIfScrolled(String str, String str2, List<Integer> list) {
        List<Integer> list2;
        p118e.b.d("ScrollDetector", "checkIfScrolled called - appId: " + str2 + ", currentDigest: " + list + ", lastLikesDigest: " + lastLikesDigest);
        List<Integer> list3 = lastLikesDigest;
        Objects.toString(list);
        Objects.toString(list3);
        List<Integer> list4 = list;
        boolean z10 = false;
        if ((list4 != null && !list4.isEmpty()) || ((list2 = lastLikesDigest) != null && !list2.isEmpty())) {
            List<Integer> list5 = lastLikesDigest;
            if (list5 != null && !list5.isEmpty()) {
                if (list4 != null && !list4.isEmpty()) {
                    ArrayList arrayListP0 = p468qv.m.P0(list4);
                    List<Integer> list6 = lastLikesDigest;
                    m.e(list6);
                    ArrayList arrayListP1 = p468qv.m.P0(list6);
                    List<Integer> list7 = lastLikesDigest;
                    m.e(list7);
                    arrayListP0.removeAll(p468qv.m.S0(list7));
                    arrayListP1.removeAll(p468qv.m.S0(list));
                    if (arrayListP0.isEmpty() && arrayListP1.isEmpty()) {
                        lastLikesDigest = list;
                        return false;
                    }
                    if (arrayListP0.isEmpty() || !arrayListP1.isEmpty()) {
                        p468qv.r.R(arrayListP0);
                        p468qv.r.R(arrayListP1);
                        kotlin.j jVar = arrayListP0.size() <= arrayListP1.size() ? new kotlin.j(arrayListP0, arrayListP1) : new kotlin.j(arrayListP1, arrayListP0);
                        List list8 = (List) jVar.f64869e;
                        List list9 = (List) jVar.f64868I;
                        List list10 = list8;
                        if (!(list10 instanceof Collection) || !list10.isEmpty()) {
                            Iterator it = list10.iterator();
                            loop0: while (true) {
                                if (!it.hasNext()) {
                                    z10 = true;
                                    break;
                                }
                                int iIntValue = ((Number) it.next()).intValue();
                                List list11 = list9;
                                if (!(list11 instanceof Collection) || !list11.isEmpty()) {
                                    Iterator it2 = list11.iterator();
                                    do {
                                        if (!it2.hasNext()) {
                                            break loop0;
                                        }
                                    } while (Math.abs(iIntValue - ((Number) it2.next()).intValue()) > 2);
                                } else {
                                    break;
                                }
                            }
                        } else {
                            z10 = true;
                            break;
                        }
                        arrayListP0.toString();
                        arrayListP1.toString();
                        lastLikesDigest = list;
                        if (!z10) {
                            defpackage.c cVar = defpackage.c.f48597a;
                            if (defpackage.c.S(str2)) {
                                int i8 = reelsScrolledCount + 1;
                                reelsScrolledCount = i8;
                                AbstractC4984e0.v(i8, "YouTube Reels scrolled count incremented (allFit check): ", "ScrollDetector");
                            }
                        }
                        return !z10;
                    }
                    lastLikesDigest = list;
                    defpackage.c cVar2 = defpackage.c.f48597a;
                    if (defpackage.c.S(str2)) {
                        int i10 = reelsScrolledCount + 1;
                        reelsScrolledCount = i10;
                        AbstractC4984e0.v(i10, "YouTube Reels scrolled count incremented: ", "ScrollDetector");
                        return true;
                    }
                }
                return true;
            }
            lastLikesDigest = list;
        }
        return false;
    }

    private final void checkInstagramChallengeInPlayStore() {
        C.A(this.scope, null, null, new G(this, null), 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0017  */
    public final Object checkSplitScreenAndFloatingWindowProtection(p552tv.e<? super Boolean> eVar) {
        H h9;
        boolean z10;
        float f2;
        boolean z11;
        int i8;
        boolean z12;
        int i10;
        boolean z13;
        String strI;
        CharSequence packageName;
        if (eVar instanceof H) {
            h9 = (H) eVar;
            int i11 = h9.f75864X;
            if ((i11 & Level.ALL_INT) != 0) {
                h9.f75864X = i11 - Level.ALL_INT;
            } else {
                h9 = new H(this, eVar);
            }
        } else {
            h9 = new H(this, eVar);
        }
        Object obj = h9.f75865e;
        p581uv.a aVar = p581uv.a.f79130e;
        int i12 = h9.f75864X;
        if (i12 == 0) {
            U8.b.F(obj);
            p699z8.c cVar = ((C0075n) getMonitorServiceStateManager().f852g.getValue()).f813l;
            p699z8.c cVar2 = ((C0075n) getMonitorServiceStateManager().f852g.getValue()).f813l;
            boolean z14 = cVar.f85327c;
            boolean z15 = cVar2.f85328d;
            if (!z14 && !z15) {
                return Boolean.FALSE;
            }
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int i13 = displayMetrics.widthPixels;
            int i14 = displayMetrics.heightPixels;
            int i15 = i13 * i14;
            Set setW = l6.m.w("com.android.systemui");
            ArrayList<C5079y> arrayList = new ArrayList();
            List<AccessibilityWindowInfo> windows = getWindows();
            (windows != null ? new Integer(windows.size()) : "null").toString();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            if (windows != null) {
                Iterator it = windows.iterator();
                z10 = false;
                while (it.hasNext()) {
                    boolean z16 = z14;
                    AccessibilityWindowInfo accessibilityWindowInfo = (AccessibilityWindowInfo) it.next();
                    boolean z17 = z15;
                    int type = accessibilityWindowInfo.getType();
                    Iterator it2 = it;
                    if (type == 1) {
                        strI = "APP";
                    } else if (type == 2) {
                        strI = "INPUT";
                    } else if (type != 3) {
                        strI = type != 4 ? AbstractC0815p.i(type, "TYPE_") : "A11Y_OVERLAY";
                    } else {
                        strI = "SYSTEM";
                    }
                    AccessibilityNodeInfo root = accessibilityWindowInfo.getRoot();
                    String string = (root == null || (packageName = root.getPackageName()) == null) ? null : packageName.toString();
                    int i16 = i14;
                    Rect rect = new Rect();
                    accessibilityWindowInfo.getBoundsInScreen(rect);
                    int i17 = i13;
                    arrayList2.add(strI + Constants.SUBS_ID_BASE_PLAN_ID_SEPARATOR + string + "(" + rect.width() + "x" + rect.height() + ")");
                    if (accessibilityWindowInfo.getType() == 1) {
                        if (string == null) {
                            arrayList3.add("null_package");
                        } else if (string.equals("unknown")) {
                            arrayList3.add("unknown_package");
                        } else if (setW.contains(string)) {
                            arrayList3.add("system:".concat(string));
                        } else if (string.equals("com.oplus.pscanvas")) {
                            z10 = true;
                        } else {
                            arrayList.add(new C5079y(rect.width(), rect.height(), string, Build.VERSION.SDK_INT >= 26 ? accessibilityWindowInfo.isInPictureInPictureMode() : false));
                        }
                    }
                    z15 = z17;
                    z14 = z16;
                    it = it2;
                    i14 = i16;
                    i13 = i17;
                }
            } else {
                z10 = false;
            }
            boolean z18 = z14;
            boolean z19 = z15;
            int i18 = i14;
            int i19 = i13;
            arrayList2.toString();
            arrayList3.toString();
            C0083w monitorServiceStateManager = getMonitorServiceStateManager();
            Sv.v0 v0Var = monitorServiceStateManager.f852g;
            ArrayList arrayListT = p311l5.g.t(((C0075n) v0Var.getValue()).f814m);
            N nB = monitorServiceStateManager.b();
            ArrayList arrayListZ0 = p468qv.m.z0(arrayListT, nB != null ? nB.f43822a : p468qv.u.f71758e);
            Map map = ((C0075n) v0Var.getValue()).f805d;
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (Map.Entry entry : map.entrySet()) {
                if (p332ls.a.D((AppLimit) entry.getValue())) {
                    linkedHashMap.put(entry.getKey(), entry.getValue());
                }
            }
            ArrayList arrayList4 = new ArrayList(linkedHashMap.size());
            Iterator it3 = linkedHashMap.entrySet().iterator();
            while (it3.hasNext()) {
                arrayList4.add(((AppLimit) ((Map.Entry) it3.next()).getValue()).getAppId());
            }
            Set setS0 = p468qv.m.S0(p468qv.m.z0(p468qv.m.e0(p468qv.m.z0(arrayListZ0, arrayList4)), settingsApps));
            ArrayList arrayList5 = new ArrayList();
            Iterator it4 = arrayList.iterator();
            while (true) {
                f2 = 0.85f;
                if (!it4.hasNext()) {
                    break;
                }
                Object next = it4.next();
                C5079y c5079y = (C5079y) next;
                if ((c5079y.b * c5079y.f76112c) / i15 >= 0.85f) {
                    arrayList5.add(next);
                }
            }
            ArrayList arrayList6 = new ArrayList(p468qv.o.P(arrayList5, 10));
            Iterator it5 = arrayList5.iterator();
            while (it5.hasNext()) {
                arrayList6.add(((C5079y) it5.next()).f76111a);
            }
            Set setS1 = p468qv.m.S0(arrayList6);
            ArrayList arrayList7 = new ArrayList();
            for (Object obj2 : arrayList) {
                C5079y c5079y2 = (C5079y) obj2;
                float f4 = f2;
                String str = c5079y2.f76111a;
                boolean z20 = (((float) c5079y2.f76112c) * ((float) c5079y2.b)) / ((float) i15) < f4;
                if (z19 && setS0.contains(str) && (c5079y2.f76113d || (!z10 && z20 && !setS1.isEmpty() && !setS1.contains(str)))) {
                    arrayList7.add(obj2);
                }
                f2 = f4;
            }
            float f9 = f2;
            if (arrayList7.isEmpty()) {
                if (z10) {
                    String str2 = this.logTag;
                    ArrayList arrayList8 = new ArrayList(p468qv.o.P(arrayList, 10));
                    for (C5079y c5079y3 : arrayList) {
                        String str3 = c5079y3.f76111a;
                        int i20 = c5079y3.b;
                        int i21 = c5079y3.f76112c;
                        arrayList8.add(str3 + " (" + i20 + "x" + i21 + ", " + ((int) (((i20 * i21) * 100.0f) / i15)) + "%)");
                    }
                    p118e.b.d(str2, "windowProtection OnePlus/Oppo pscanvas detected. allAppWindows: " + arrayList8);
                    if (z18 && !arrayList.isEmpty()) {
                        ArrayList arrayList9 = new ArrayList(p468qv.o.P(arrayList, 10));
                        Iterator it6 = arrayList.iterator();
                        while (it6.hasNext()) {
                            arrayList9.add(((C5079y) it6.next()).f76111a);
                        }
                        ArrayList arrayList10 = new ArrayList();
                        for (Object obj3 : arrayList9) {
                            if (setS0.contains((String) obj3)) {
                                arrayList10.add(obj3);
                            }
                        }
                        if (!arrayList10.isEmpty()) {
                            p118e.b.d(this.logTag, "Blocked apps in OnePlus/Oppo split screen: " + arrayList10 + " - showing protection");
                            showBlockProtectionScreen(R1.a.SPLIT_SCREEN_PROTECTION);
                            return Boolean.TRUE;
                        }
                    }
                    return Boolean.FALSE;
                }
                if (arrayList.size() < 2) {
                    arrayList.size();
                    ArrayList arrayList11 = new ArrayList(p468qv.o.P(arrayList, 10));
                    for (C5079y c5079y4 : arrayList) {
                        StringBuilder sbP = z.p(c5079y4.f76111a, " (");
                        sbP.append(c5079y4.b);
                        sbP.append("x");
                        sbP.append(c5079y4.f76112c);
                        sbP.append(")");
                        arrayList11.add(sbP.toString());
                    }
                    arrayList11.toString();
                    return Boolean.FALSE;
                }
                String str4 = this.logTag;
                int size = arrayList.size();
                ArrayList arrayList12 = new ArrayList(p468qv.o.P(arrayList, 10));
                for (C5079y c5079y5 : arrayList) {
                    StringBuilder sbP2 = z.p(c5079y5.f76111a, " (");
                    sbP2.append(c5079y5.b);
                    sbP2.append("x");
                    sbP2.append(c5079y5.f76112c);
                    sbP2.append(")");
                    arrayList12.add(sbP2.toString());
                }
                p118e.b.d(str4, "windowProtection Found " + size + " app windows: " + arrayList12);
                ArrayList arrayList13 = new ArrayList();
                for (Object obj4 : arrayList) {
                    C5079y c5079y6 = (C5079y) obj4;
                    if ((c5079y6.b * c5079y6.f76112c) / i15 >= f9) {
                        arrayList13.add(obj4);
                    }
                }
                if (arrayList13.isEmpty()) {
                    ArrayList arrayList14 = new ArrayList(p468qv.o.P(arrayList, 10));
                    Iterator it7 = arrayList.iterator();
                    while (it7.hasNext()) {
                        arrayList14.add(((C5079y) it7.next()).f76111a);
                    }
                    List listE0 = p468qv.m.e0(arrayList14);
                    listE0.size();
                    arrayList.size();
                    ArrayList arrayList15 = new ArrayList(p468qv.o.P(arrayList, 10));
                    for (C5079y c5079y7 : arrayList) {
                        StringBuilder sbP3 = z.p(c5079y7.f76111a, "(");
                        sbP3.append(c5079y7.b);
                        sbP3.append("x");
                        sbP3.append(c5079y7.f76112c);
                        sbP3.append(")");
                        arrayList15.add(sbP3.toString());
                    }
                    listE0.toString();
                    arrayList15.toString();
                    if (listE0.size() == 1) {
                        arrayList.size();
                        Objects.toString(p468qv.m.j0(listE0));
                        return Boolean.FALSE;
                    }
                    if (arrayList.isEmpty()) {
                        z11 = true;
                    } else {
                        Iterator it8 = arrayList.iterator();
                        while (true) {
                            if (it8.hasNext()) {
                                C5079y c5079y8 = (C5079y) it8.next();
                                float f10 = (c5079y8.b * c5079y8.f76112c) / i15;
                                if (0.3f > f10 || f10 > 0.55f) {
                                    z11 = false;
                                }
                            } else {
                                z11 = true;
                            }
                        }
                    }
                    Iterator it9 = arrayList.iterator();
                    int i22 = 0;
                    while (it9.hasNext()) {
                        i22 += ((C5079y) it9.next()).f76112c;
                    }
                    Iterator it10 = arrayList.iterator();
                    int i23 = 0;
                    while (it10.hasNext()) {
                        i23 += ((C5079y) it10.next()).b;
                    }
                    double d10 = 0.9d;
                    if (!arrayList.isEmpty()) {
                        Iterator it11 = arrayList.iterator();
                        while (true) {
                            if (!it11.hasNext()) {
                                i8 = i19;
                                z12 = true;
                                break;
                            }
                            i8 = i19;
                            if (((C5079y) it11.next()).b < ((double) i8) * 0.9d) {
                                z12 = false;
                                break;
                            }
                            i19 = i8;
                        }
                    } else {
                        z12 = true;
                        i8 = i19;
                    }
                    if (!arrayList.isEmpty()) {
                        Iterator it12 = arrayList.iterator();
                        while (true) {
                            if (!it12.hasNext()) {
                                i10 = i18;
                                z13 = true;
                                break;
                            }
                            double d11 = d10;
                            i10 = i18;
                            if (((C5079y) it12.next()).f76112c < ((double) i10) * d11) {
                                z13 = false;
                                break;
                            }
                            i18 = i10;
                            d10 = d11;
                        }
                    } else {
                        z13 = true;
                        i10 = i18;
                    }
                    boolean z21 = z12 && ((double) i22) >= ((double) i10) * 0.85d;
                    boolean z22 = z13 && ((double) i23) >= ((double) i8) * 0.85d;
                    if (z11 || z21 || z22) {
                        String str5 = this.logTag;
                        ArrayList arrayList16 = new ArrayList(p468qv.o.P(arrayList, 10));
                        Iterator it13 = arrayList.iterator();
                        while (it13.hasNext()) {
                            arrayList16.add(((C5079y) it13.next()).f76111a);
                        }
                        p118e.b.d(str5, "Split screen mode detected (equal=" + z11 + ", vertical=" + z21 + ", horizontal=" + z22 + ") with apps: " + arrayList16);
                        if (z18) {
                            ArrayList arrayList17 = new ArrayList(p468qv.o.P(arrayList, 10));
                            Iterator it14 = arrayList.iterator();
                            while (it14.hasNext()) {
                                arrayList17.add(((C5079y) it14.next()).f76111a);
                            }
                            ArrayList arrayList18 = new ArrayList();
                            for (Object obj5 : arrayList17) {
                                if (setS0.contains((String) obj5)) {
                                    arrayList18.add(obj5);
                                }
                            }
                            if (!arrayList18.isEmpty()) {
                                p118e.b.d(this.logTag, "Blocked apps detected in split screen: " + arrayList18 + " - showing protection screen");
                                showBlockProtectionScreen(R1.a.SPLIT_SCREEN_PROTECTION);
                                return Boolean.TRUE;
                            }
                        }
                    }
                }
                return Boolean.FALSE;
            }
            Wv.e eVar2 = M.f24563a;
            Qv.d dVar = Uv.m.f33347a.f26485l0;
            I i24 = new I(this, null);
            h9.f75864X = 1;
            if (C.M(dVar, i24, h9) == aVar) {
                return aVar;
            }
        } else {
            if (i12 != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            U8.b.F(obj);
        }
        return Boolean.TRUE;
    }

    private final void checkUninstallDialogDuringChallenge() {
        C.A(this.scope, null, null, new p536tf.J(this, null), 3);
    }

    public static /* synthetic */ p567uf.a convertToHashMap$default(MyMonitorInAppBlockService myMonitorInAppBlockService, String str, AccessibilityNodeInfo accessibilityNodeInfo, p567uf.j jVar, int i8, Object obj) {
        if ((i8 & 4) != 0) {
            jVar = p567uf.j.f77960e;
        }
        return myMonitorInAppBlockService.convertToHashMap(str, accessibilityNodeInfo, jVar);
    }

    private final p646xf.a detectHandleFromNodes(List<p567uf.c> nodeDetails) {
        Jv.g gVarB;
        String str;
        o oVar = p646xf.b.f81953a;
        Set setF = U6.d.F("views");
        Set setF2 = U6.d.F("more");
        m.h(nodeDetails, "nodeDetails");
        boolean z10 = false;
        boolean z11 = false;
        p646xf.a aVar = null;
        for (p567uf.c cVar : nodeDetails) {
            String str2 = cVar.f77949a;
            if (aVar == null && str2 != null && (str = cVar.b) != null) {
                String string = Mv.r.P0(Mv.r.S0(Mv.r.P0(str2).toString(), CoreConstants.DOT)).toString();
                String string2 = Mv.r.P0(Mv.r.S0(Mv.r.P0(str).toString(), CoreConstants.DOT)).toString();
                l lVarD = p646xf.b.f81953a.d(string);
                if (lVarD != null && string.equals(string2) && string.length() <= 50) {
                    aVar = new p646xf.a((String) ((Mv.j) lVarD.a()).get(1), string);
                }
            }
            if (str2 != null) {
                Pattern patternCompile = Pattern.compile("^[-+]?\\d+(\\.\\d+)?");
                m.g(patternCompile, "compile(...)");
                Matcher matcher = patternCompile.matcher(str2);
                m.g(matcher, "matcher(...)");
                l lVarL = Mn.J.l(matcher, 0, str2);
                if (lVarL != null && (gVarB = lVarL.b()) != null && gVarB.f14223e == 0) {
                    String lowerCase = str2.toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                    Set set = setF;
                    if (!(set instanceof Collection) || !set.isEmpty()) {
                        Iterator it = set.iterator();
                        while (it.hasNext()) {
                            if (Mv.r.b0(lowerCase, (String) it.next(), false)) {
                                z10 = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (str2 != null) {
                String lowerCase2 = str2.toLowerCase(Locale.ROOT);
                m.g(lowerCase2, "toLowerCase(...)");
                String string3 = Mv.r.R0(lowerCase2).toString();
                Set<String> set2 = setF2;
                if (!(set2 instanceof Collection) || !set2.isEmpty()) {
                    for (String str3 : set2) {
                        if (!Mv.r.b0(string3, "..." + str3, false)) {
                            if (!Mv.r.b0(string3, "… " + str3, false)) {
                                if (y.O(string3, " " + str3, false)) {
                                }
                            }
                        }
                        z11 = true;
                    }
                }
            }
            if (aVar != null && z10 && z11) {
                break;
            }
        }
        if (aVar != null && z10 && z11) {
            return aVar;
        }
        return null;
    }

    private final List<String> extractAllHandles(C5057b c5057b) {
        if (c5057b == null) {
            return p468qv.u.f71758e;
        }
        String str = CoreConstants.EMPTY_STRING;
        String str2 = c5057b.f75973a;
        if (str2 == null) {
            str2 = CoreConstants.EMPTY_STRING;
        }
        String str3 = c5057b.b;
        if (str3 != null) {
            str = str3;
        }
        return Lv.n.R(new Lv.c(Lv.n.O(o.c(new o("@[A-Za-z0-9_.\\-]{1,50}"), AbstractC3102b.l(str2, " ", str)), new C4896a(12)), q.f18467I, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String extractAllHandles$lambda$69(i it) {
        m.h(it, "it");
        return ((l) it).c();
    }

    private final List<Integer> extractNumbersFromReels(String str, List<p567uf.c> list) {
        String string;
        String string2;
        String string3;
        String string4;
        if (list != null) {
            ArrayList arrayList = new ArrayList();
            for (p567uf.c cVar : list) {
                String str2 = cVar.f77949a;
                Integer numM = (str2 == null || (string4 = str2.toString()) == null) ? null : Fv.a.m(string4);
                String str3 = cVar.b;
                if (numM == null) {
                    numM = (str3 == null || (string3 = str3.toString()) == null) ? null : Fv.a.m(string3);
                }
                if (numM != null) {
                    arrayList.add(numM);
                    String str4 = cVar.f77949a;
                    p118e.b.d(str, "extractNumbersFromReels node: " + str4 + " textNumber: " + ((str4 == null || (string2 = str4.toString()) == null) ? null : Fv.a.m(string2)) + " contentDescription: " + str3 + " descNumber: " + ((str3 == null || (string = str3.toString()) == null) ? null : Fv.a.m(string)) + " finalNumber: " + numM);
                }
            }
            if (!arrayList.isEmpty()) {
                return arrayList;
            }
        }
        return null;
    }

    private final String extractYTChannelName(Map<Integer, ? extends List<p567uf.c>> map, int i8, String str) {
        List<p567uf.c> list = map.get(Integer.valueOf(i8));
        if (list == null) {
            return null;
        }
        String str2 = str + "-" + i8;
        list.toString();
        return p646xf.b.a(str2, list);
    }

    /* JADX WARN: Type inference failed for: r13v1, types: [java.lang.Object, java.util.Map] */
    private final C5057b findChannelNameFromNodeDetails(List<p567uf.c> list, boolean z10) {
        String str;
        String str2;
        List<p567uf.c> list2 = list;
        if (!(list2 instanceof Collection) || !list2.isEmpty()) {
            Iterator<T> it = list2.iterator();
            while (it.hasNext()) {
                if (m.c(((p567uf.c) it.next()).b, "Action menu")) {
                    p118e.b.d("ChannelDetection", "Action menu detected - returning null");
                    return null;
                }
            }
        }
        String str3 = null;
        String str4 = null;
        for (p567uf.c cVar : list) {
            if (str3 == null && (str = cVar.f77949a) != null && (str2 = cVar.b) != null && str.equals(str2) && y.X(str, "@", false) && !Mv.r.b0(str, " ", false)) {
                p118e.b.d("ChannelDetection", "Channel tag detected: " + str + " (matched in both text and description)");
                str3 = str;
            }
            if (str4 == null) {
                String str5 = cVar.f77949a;
                if (str5 == null && (str5 = cVar.b) == null) {
                    str5 = CoreConstants.EMPTY_STRING;
                }
                LinkedHashSet linkedHashSet = new LinkedHashSet();
                Locale locale = Locale.ROOT;
                String lowerCase = "subscribe_to".toLowerCase(locale);
                m.g(lowerCase, "toLowerCase(...)");
                linkedHashSet.add(lowerCase);
                ?? r13 = V1.B.f33390a;
                String lowerCase2 = "subscribe_to".toLowerCase(locale);
                m.g(lowerCase2, "toLowerCase(...)");
                Collection collection = (Set) r13.get(lowerCase2);
                linkedHashSet.addAll(collection != null ? collection : w.f71760e);
                for (String literal : p468qv.m.S0(linkedHashSet)) {
                    m.h(literal, "literal");
                    String strQuote = Pattern.quote(literal);
                    m.g(strQuote, "quote(...)");
                    String pattern = "(?i)" + strQuote + "\\s+(.+)\\.";
                    m.h(pattern, "pattern");
                    Pattern patternCompile = Pattern.compile(pattern);
                    m.g(patternCompile, "compile(...)");
                    Matcher matcher = patternCompile.matcher(str5);
                    m.g(matcher, "matcher(...)");
                    l lVarL = Mn.J.l(matcher, 0, str5);
                    if (lVarL != null) {
                        String str6 = (String) ((Mv.j) lVarL.a()).get(1);
                        if (!y.X(str6, "@", false)) {
                            p118e.b.d("ChannelDetection", "Channel name detected from subscribe text: ".concat(str6));
                            str4 = str6;
                        }
                    }
                }
            }
            if (str4 != null && str3 != null) {
                break;
            }
        }
        if (!z10) {
            p118e.b.d("ChannelDetection", "Not on video page - skipping channel name detection");
            return new C5057b(null, str3, null);
        }
        List<p567uf.c> list3 = list;
        if (!(list3 instanceof Collection) || !list3.isEmpty()) {
            Iterator<T> it2 = list3.iterator();
            while (it2.hasNext()) {
                if (m.c(((p567uf.c) it2.next()).f77952e, "android.widget.ImageView")) {
                    if (str4 != null) {
                        for (p567uf.c cVar2 : list) {
                            String str7 = cVar2.f77949a;
                            if (str7 == null && (str7 = cVar2.b) == null) {
                                str7 = CoreConstants.EMPTY_STRING;
                            }
                            if (str7.equals(str4)) {
                                p118e.b.d("ChannelDetection", "Channel name confirmed: ".concat(str4));
                                return new C5057b(str4, str3, list);
                            }
                        }
                    }
                    if (str4 == null && str3 == null) {
                        return null;
                    }
                    return new C5057b(str4, str3, list);
                }
            }
        }
        return new C5057b(null, str3, null);
    }

    private final List<AccessibilityNodeInfo> findNodesByViewIdInTree(AccessibilityNodeInfo accessibilityNodeInfo, String str) {
        ArrayList arrayList = new ArrayList();
        k kVar = new k();
        kVar.addLast(accessibilityNodeInfo);
        int i8 = 0;
        while (!kVar.isEmpty() && i8 < MAX_TREE_WALK_NODES) {
            AccessibilityNodeInfo accessibilityNodeInfo2 = (AccessibilityNodeInfo) kVar.removeFirst();
            i8++;
            if (m.c(accessibilityNodeInfo2.getViewIdResourceName(), str)) {
                arrayList.add(accessibilityNodeInfo2);
                return arrayList;
            }
            int childCount = accessibilityNodeInfo2.getChildCount();
            for (int i10 = 0; i10 < childCount; i10++) {
                AccessibilityNodeInfo child = accessibilityNodeInfo2.getChild(i10);
                if (child != null) {
                    kVar.addLast(child);
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:22:0x0065  */
    /* JADX WARN: Code duplicated, block: B:25:0x0086  */
    /* JADX WARN: Code duplicated, block: B:28:0x008e A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:29:0x008f  */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x0086 -> B:26:0x008a). Please report as a decompilation issue!!! */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:34:0x00b8 -> B:36:0x00bb). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public final java.lang.Object findProductiveHandle(java.util.List<java.lang.String> r10, p552tv.e<? super java.lang.String> r11) {
        /*
            Method dump skipped, instruction units count: 207
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorInAppBlockService.findProductiveHandle(java.util.List, tv.e):java.lang.Object");
    }

    private final String findSubscribeButtonLabel(Map<Integer, ? extends List<p567uf.c>> map) {
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            for (p567uf.c cVar : it.next().getValue()) {
                String str = cVar.f77949a;
                if (str != null || (str = cVar.b) != null) {
                    String lowerCase = str.toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                    if (y.X(lowerCase, "subscrib", false) || y.X(lowerCase, "unsubscrib", false)) {
                        return str;
                    }
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:19:0x0055  */
    /* JADX WARN: Code duplicated, block: B:25:0x0079  */
    /* JADX WARN: Code duplicated, block: B:31:0x0086  */
    /* JADX WARN: Code duplicated, block: B:34:0x008a  */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0071, code lost:
    
        if (r10 == r1) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00a0, code lost:
    
        if (r10 == r1) goto L36;
     */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:20:0x0071 -> B:22:0x0074). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object findWhitelistedStudyChannel(p536tf.C5057b r8, java.util.List<java.lang.String> r9, p552tv.e<? super java.lang.String> r10) {
        /*
            r7 = this;
            boolean r0 = r10 instanceof p536tf.O
            if (r0 == 0) goto L13
            r0 = r10
            tf.O r0 = (p536tf.O) r0
            int r1 = r0.f75902l0
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L13
            int r1 = r1 - r2
            r0.f75902l0 = r1
            goto L18
        L13:
            tf.O r0 = new tf.O
            r0.<init>(r7, r10)
        L18:
            java.lang.Object r10 = r0.f75899Y
            uv.a r1 = p581uv.a.f79130e
            int r2 = r0.f75902l0
            r3 = 2
            r4 = 1
            r5 = 0
            if (r2 == 0) goto L44
            if (r2 == r4) goto L38
            if (r2 != r3) goto L30
            java.lang.Object r8 = r0.f75901e
            java.lang.String r8 = (java.lang.String) r8
            U8.b.F(r10)
            goto La3
        L30:
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException
            java.lang.String r9 = "call to 'resume' before 'invoke' with coroutine"
            r8.<init>(r9)
            throw r8
        L38:
            java.util.Iterator r8 = r0.f75898X
            tf.b r9 = r0.f75897I
            java.lang.Object r2 = r0.f75901e
            ai.focus.services.MyMonitorInAppBlockService r2 = (p019ai.focus.services.MyMonitorInAppBlockService) r2
            U8.b.F(r10)
            goto L74
        L44:
            U8.b.F(r10)
            java.util.Iterator r9 = r9.iterator()
            r2 = r9
            r9 = r8
            r8 = r2
            r2 = r7
        L4f:
            boolean r10 = r8.hasNext()
            if (r10 == 0) goto L79
            java.lang.Object r10 = r8.next()
            java.lang.String r10 = (java.lang.String) r10
            Za.J r6 = r2.getProductiveModeUseCases()
            Za.c r6 = r6.f41483P
            D1.x r10 = r6.b(r10)
            r0.f75901e = r2
            r0.f75897I = r9
            r0.f75898X = r8
            r0.f75902l0 = r4
            java.lang.Object r10 = Sv.r.s(r10, r0)
            if (r10 != r1) goto L74
            goto La2
        L74:
            java.lang.String r10 = (java.lang.String) r10
            if (r10 == 0) goto L4f
            return r10
        L79:
            if (r9 == 0) goto Lac
            java.lang.String r8 = r9.f75973a
            if (r8 == 0) goto Lac
            boolean r9 = Mv.r.p0(r8)
            if (r9 != 0) goto L86
            goto L87
        L86:
            r8 = r5
        L87:
            if (r8 != 0) goto L8a
            goto Lac
        L8a:
            Za.J r9 = r2.getProductiveModeUseCases()
            Za.d r9 = r9.f41523r
            D1.z r9 = r9.b(r8)
            r0.f75901e = r8
            r0.f75897I = r5
            r0.f75898X = r5
            r0.f75902l0 = r3
            java.lang.Object r10 = Sv.r.s(r9, r0)
            if (r10 != r1) goto La3
        La2:
            return r1
        La3:
            java.lang.Boolean r10 = (java.lang.Boolean) r10
            boolean r9 = r10.booleanValue()
            if (r9 == 0) goto Lac
            return r8
        Lac:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorInAppBlockService.findWhitelistedStudyChannel(tf.b, java.util.List, tv.e):java.lang.Object");
    }

    private final C5057b findYTChannelName(Map<Integer, ? extends List<p567uf.c>> map, boolean z10, String str) {
        Jv.g gVarB;
        p646xf.a aVarDetectHandleFromNodes;
        C5057b c5057b = null;
        String str2 = null;
        for (Map.Entry<Integer, ? extends List<p567uf.c>> entry : map.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            List<p567uf.c> value = entry.getValue();
            C5057b c5057bFindChannelNameFromNodeDetails = findChannelNameFromNodeDetails(value, z10);
            if ((c5057bFindChannelNameFromNodeDetails != null ? c5057bFindChannelNameFromNodeDetails.b : null) != null) {
                str2 = c5057bFindChannelNameFromNodeDetails.b;
            }
            if ((c5057bFindChannelNameFromNodeDetails != null ? c5057bFindChannelNameFromNodeDetails.f75973a : null) != null) {
                p118e.b.d(str, "findYTChannelName ChannelNameDetectedNewApproach Channel Info: " + c5057bFindChannelNameFromNodeDetails);
                return c5057bFindChannelNameFromNodeDetails;
            }
            if (c5057b == null && (aVarDetectHandleFromNodes = detectHandleFromNodes(value)) != null) {
                StringBuilder sb2 = new StringBuilder("findYTChannelName detectChannelHandleFromVideoInfoRow: handle='");
                String str3 = aVarDetectHandleFromNodes.f81952a;
                sb2.append(str3);
                sb2.append("' byline='");
                String str4 = aVarDetectHandleFromNodes.b;
                sb2.append(str4);
                sb2.append("', key=");
                sb2.append(iIntValue);
                p118e.b.d(str, sb2.toString());
                c5057b = new C5057b(str4, str3, value);
            }
            List<p567uf.c> list = value;
            boolean z11 = list instanceof Collection;
            if (!z11 || !list.isEmpty()) {
                Iterator<T> it = list.iterator();
                while (true) {
                    if (it.hasNext()) {
                        p567uf.c cVar = (p567uf.c) it.next();
                        String str5 = cVar.f77949a;
                        if (str5 == null && (str5 = cVar.b) == null) {
                            str5 = CoreConstants.EMPTY_STRING;
                        }
                        List listD0 = Mv.r.D0(str5, new String[]{" "}, 0, 6);
                        Pattern patternCompile = Pattern.compile("^[-+]?\\d+(\\.\\d+)?");
                        m.g(patternCompile, "compile(...)");
                        Matcher matcher = patternCompile.matcher(str5);
                        m.g(matcher, "matcher(...)");
                        l lVarL = Mn.J.l(matcher, 0, str5);
                        if (lVarL != null && (gVarB = lVarL.b()) != null && gVarB.f14223e == 0 && listD0.size() <= 4) {
                            if (!z11 || !list.isEmpty()) {
                                Iterator<T> it2 = list.iterator();
                                while (true) {
                                    if (it2.hasNext()) {
                                        if (m.c(((p567uf.c) it2.next()).f77952e, "android.widget.Button")) {
                                            String strExtractYTChannelName = extractYTChannelName(map, iIntValue, str);
                                            if (strExtractYTChannelName == null) {
                                                p118e.b.d(str, "findYTChannelName Key " + iIntValue + " ChannelNameNotDetected");
                                                break;
                                            }
                                            p118e.b.d(str, "findYTChannelName Key " + iIntValue + " ChannelNameDetected");
                                            return new C5057b(strExtractYTChannelName, c5057bFindChannelNameFromNodeDetails != null ? c5057bFindChannelNameFromNodeDetails.b : null, value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (c5057b != null) {
            p118e.b.d(str, "findYTChannelName Returning handleChannelInfo: " + c5057b);
        }
        if (c5057b != null) {
            return c5057b;
        }
        if (str2 != null) {
            return new C5057b(null, str2, null);
        }
        return null;
    }

    /* JADX WARN: Code duplicated, block: B:34:0x0083  */
    private final C5057b findYTChannelNameInLandscapeMode(Map<Integer, ? extends List<p567uf.c>> map, String str) {
        Integer numValueOf;
        String string;
        String str2;
        Object next;
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it = map.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                numValueOf = null;
                break;
            }
            Map.Entry<Integer, ? extends List<p567uf.c>> next2 = it.next();
            int iIntValue = next2.getKey().intValue();
            Iterator<T> it2 = next2.getValue().iterator();
            do {
                if (!it2.hasNext()) {
                    next = null;
                    break;
                }
                next = it2.next();
            } while (!m.c(((p567uf.c) next).f77951d, "com.google.android.youtube:id/player_video_heading"));
            if (((p567uf.c) next) != null) {
                numValueOf = Integer.valueOf(iIntValue);
                break;
            }
        }
        if (numValueOf == null) {
            string = null;
        } else {
            List<p567uf.c> list = map.get(numValueOf);
            p567uf.c cVar = list != null ? (p567uf.c) p468qv.m.r0(list) : null;
            string = (cVar == null || (str2 = cVar.f77949a) == null) ? null : str2.toString();
            if (string == null || Mv.r.p0(string) || string.length() > 50 || isTimestampPattern(string, str)) {
                string = null;
            }
        }
        if (numValueOf == null || string == null) {
            p118e.b.d(str, "findYTChannelNameInLandscapeMode channel Name is Null");
            return null;
        }
        p118e.b.d(str, "findYTChannelNameInLandscapeMode channel Name detected: matchingKey: " + numValueOf + ", channelName: " + string);
        String str3 = y.X(string, "@", false) ? string : null;
        List<p567uf.c> list2 = map.get(numValueOf);
        if (list2 == null) {
            list2 = p468qv.u.f71758e;
        }
        return new C5057b(string, str3, list2);
    }

    public static /* synthetic */ MyAccessibilityNodeInfoWrapper getAccessibilityNodeInfo$default(MyMonitorInAppBlockService myMonitorInAppBlockService, List list, boolean z10, int i8, Object obj) {
        if ((i8 & 2) != 0) {
            z10 = false;
        }
        return myMonitorInAppBlockService.getAccessibilityNodeInfo(list, z10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable getAccessibilityNodeInfo$lambda$36(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getTop());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable getAccessibilityNodeInfo$lambda$37(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getBottom());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable getAccessibilityNodeInfo$lambda$38(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getLeft());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable getAccessibilityNodeInfo$lambda$39(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getRight());
    }

    private final String getAppVersionName(String str) {
        Object next;
        if (str == null) {
            return null;
        }
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(0);
        m.g(installedPackages, "getInstalledPackages(...)");
        Iterator<T> it = installedPackages.iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
        } while (!m.c(((PackageInfo) next).packageName, str));
        PackageInfo packageInfo = (PackageInfo) next;
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return null;
    }

    private final kotlin.j getBestEvent(AccessibilityEvent accessibilityEvent, UsageEvents.Event event) {
        if (accessibilityEvent != null || event != null) {
            if (accessibilityEvent != null) {
                if (event == null) {
                    CharSequence packageName = accessibilityEvent.getPackageName();
                    String string = packageName != null ? packageName.toString() : null;
                    int i8 = i0.f76025a;
                    long eventTime = accessibilityEvent.getEventTime();
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    String.valueOf(jUptimeMillis);
                    return new kotlin.j(string, Long.valueOf((System.currentTimeMillis() - jUptimeMillis) + eventTime));
                }
                int i10 = i0.f76025a;
                long eventTime2 = accessibilityEvent.getEventTime();
                long jUptimeMillis2 = SystemClock.uptimeMillis();
                String.valueOf(jUptimeMillis2);
                long jCurrentTimeMillis = (System.currentTimeMillis() - jUptimeMillis2) + eventTime2;
                if (event.getTimeStamp() > jCurrentTimeMillis) {
                    event.getTimeStamp();
                    event.getPackageName();
                    event.getEventType();
                    return new kotlin.j(event.getPackageName(), Long.valueOf(event.getTimeStamp()));
                }
                CharSequence packageName2 = accessibilityEvent.getPackageName();
                accessibilityEvent.getEventType();
                Objects.toString(packageName2);
                CharSequence packageName3 = accessibilityEvent.getPackageName();
                return new kotlin.j(packageName3 != null ? packageName3.toString() : null, Long.valueOf(jCurrentTimeMillis));
            }
            if (event != null) {
                return new kotlin.j(event.getPackageName(), Long.valueOf(event.getTimeStamp()));
            }
        }
        return null;
    }

    private final p567uf.c getMatchingNode(List<p567uf.c> list, Set<String> set, int i8, String str) {
        String lowerCase;
        String string;
        String string2;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        list.size();
        set.size();
        for (p567uf.c cVar : list) {
            String str2 = cVar.f77949a;
            if (str2 == null || (string2 = str2.toString()) == null) {
                String str3 = cVar.b;
                if (str3 == null || (string = str3.toString()) == null) {
                    lowerCase = CoreConstants.EMPTY_STRING;
                } else {
                    lowerCase = string.toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                }
            } else {
                lowerCase = string2.toLowerCase(Locale.ROOT);
                m.g(lowerCase, "toLowerCase(...)");
            }
            Set<String> set2 = set;
            int i10 = 0;
            if (!(set2 instanceof Collection) || !set2.isEmpty()) {
                int i11 = 0;
                for (String str4 : set2) {
                    boolean zEquals = lowerCase.equals(str4);
                    boolean zB0 = Mv.r.b0(lowerCase, str4, false);
                    if (zEquals || zB0) {
                        if (linkedHashSet.contains(str4)) {
                            continue;
                        } else {
                            linkedHashSet.add(str4);
                            i11++;
                            if (i11 < 0) {
                                n.N();
                                throw null;
                            }
                        }
                    }
                }
                i10 = i11;
            }
            if (i10 >= i8) {
                cVar.toString();
                return cVar;
            }
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ p567uf.c getMatchingNode$default(MyMonitorInAppBlockService myMonitorInAppBlockService, List list, Set set, int i8, String str, int i10, Object obj) {
        if ((i10 & 2) != 0) {
            set = w.f71760e;
        }
        return myMonitorInAppBlockService.getMatchingNode(list, set, i8, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.Object, java.util.Map] */
    public final AccessibilityNodeInfo getPlayPauseButtonNode(String key) {
        CharSequence contentDescription;
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            return null;
        }
        List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/player_control_play_pause_replay_button");
        m.g(listFindAccessibilityNodeInfosByViewId, "findAccessibilityNodeInfosByViewId(...)");
        Object objL0 = p468qv.m.l0(listFindAccessibilityNodeInfosByViewId);
        if (objL0 == null) {
            List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByViewId2 = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/floaty_play_pause_button");
            m.g(listFindAccessibilityNodeInfosByViewId2, "findAccessibilityNodeInfosByViewId(...)");
            objL0 = p468qv.m.l0(listFindAccessibilityNodeInfosByViewId2);
        }
        m.h(key, "key");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Locale locale = Locale.ROOT;
        String lowerCase = key.toLowerCase(locale);
        m.g(lowerCase, "toLowerCase(...)");
        linkedHashSet.add(lowerCase);
        ?? r10 = V1.B.f33390a;
        String lowerCase2 = key.toLowerCase(locale);
        m.g(lowerCase2, "toLowerCase(...)");
        Collection collection = (Set) r10.get(lowerCase2);
        linkedHashSet.addAll(collection != null ? collection : w.f71760e);
        Set setS0 = p468qv.m.S0(linkedHashSet);
        AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) objL0;
        if (accessibilityNodeInfo == null || (contentDescription = accessibilityNodeInfo.getContentDescription()) == null) {
            return null;
        }
        String lowerCase3 = contentDescription.toString().toLowerCase(locale);
        m.g(lowerCase3, "toLowerCase(...)");
        if (setS0.contains(lowerCase3)) {
            return accessibilityNodeInfo;
        }
        return null;
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [java.lang.Object, java.util.Map] */
    private final Set<String> getProductiveModeTexts() {
        C0853o c0853o = getBlockFeatureUseCases().f33425q;
        List<String> listI = n.I("Stop ads", "Clip", "Dislike this video", "Share", "Remix", "Download", "thanks", "save", "report");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str : listI) {
            Locale locale = Locale.ROOT;
            String lowerCase = str.toLowerCase(locale);
            m.g(lowerCase, "toLowerCase(...)");
            linkedHashSet.add(lowerCase);
            ?? r10 = V1.B.f33390a;
            String lowerCase2 = str.toLowerCase(locale);
            m.g(lowerCase2, "toLowerCase(...)");
            Set set = (Set) r10.get(lowerCase2);
            if (set != null) {
                linkedHashSet.addAll(set);
            }
        }
        return p468qv.m.S0(linkedHashSet);
    }

    /* JADX WARN: Type inference failed for: r4v2, types: [java.lang.Object, java.util.Map] */
    private final Set<String> getShortsReelsText(boolean z10) {
        Hw.a aVar = getBlockFeatureUseCases().f33424p;
        ArrayList<String> arrayListK = n.K("like", "dislike", "comment", "share", "remix", "try remix", "like button", "view comments", "send", "whatsapp", "navigate to your reels", "reactions", "comments", "shares");
        if (!z10) {
            arrayListK.add("more");
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str : arrayListK) {
            Locale locale = Locale.ROOT;
            String lowerCase = str.toLowerCase(locale);
            m.g(lowerCase, "toLowerCase(...)");
            linkedHashSet.add(lowerCase);
            ?? r10 = V1.B.f33390a;
            String lowerCase2 = str.toLowerCase(locale);
            m.g(lowerCase2, "toLowerCase(...)");
            Set set = (Set) r10.get(lowerCase2);
            if (set != null) {
                linkedHashSet.addAll(set);
            }
        }
        return p468qv.m.S0(linkedHashSet);
    }

    public static /* synthetic */ Set getShortsReelsText$default(MyMonitorInAppBlockService myMonitorInAppBlockService, boolean z10, int i8, Object obj) {
        if ((i8 & 1) != 0) {
            z10 = false;
        }
        return myMonitorInAppBlockService.getShortsReelsText(z10);
    }

    /* JADX WARN: Code duplicated, block: B:106:0x01fa A[LOOP:6: B:104:0x01f4->B:106:0x01fa, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:110:0x021f  */
    /* JADX WARN: Code duplicated, block: B:116:0x0233 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:117:0x0235  */
    /* JADX WARN: Code duplicated, block: B:119:0x024f  */
    /* JADX WARN: Code duplicated, block: B:124:0x025b  */
    /* JADX WARN: Code duplicated, block: B:127:0x025f  */
    /* JADX WARN: Code duplicated, block: B:129:0x0264  */
    /* JADX WARN: Code duplicated, block: B:131:0x0269  */
    /* JADX WARN: Code duplicated, block: B:134:0x0277  */
    /* JADX WARN: Code duplicated, block: B:136:0x027a  */
    /* JADX WARN: Code duplicated, block: B:144:0x029e  */
    /* JADX WARN: Code duplicated, block: B:146:0x02a4 A[EDGE_INSN: B:146:0x02a4->B:160:0x02d4 BREAK  A[LOOP:4: B:148:0x02aa->B:206:?]] */
    /* JADX WARN: Code duplicated, block: B:147:0x02a6  */
    /* JADX WARN: Code duplicated, block: B:150:0x02b0  */
    /* JADX WARN: Code duplicated, block: B:152:0x02be  */
    /* JADX WARN: Code duplicated, block: B:157:0x02ce  */
    /* JADX WARN: Code duplicated, block: B:161:0x02d6  */
    /* JADX WARN: Code duplicated, block: B:164:0x02e5 A[LOOP:3: B:162:0x02df->B:164:0x02e5, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:168:0x030b  */
    /* JADX WARN: Code duplicated, block: B:183:0x0324 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:184:0x0324 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:202:0x02a4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:203:0x02d2 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:206:? A[LOOP:4: B:148:0x02aa->B:206:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:208:0x027e A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:211:0x022d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:82:0x014c  */
    /* JADX WARN: Code duplicated, block: B:85:0x0158  */
    /* JADX WARN: Code duplicated, block: B:87:0x0166  */
    /* JADX WARN: Code duplicated, block: B:90:0x016d  */
    /* JADX WARN: Code duplicated, block: B:98:0x019f  */
    /* JADX WARN: Code duplicated, block: B:99:0x01b0  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v13, types: [java.lang.Object, java.util.regex.Pattern] */
    /* JADX WARN: Type inference failed for: r1v19 */
    /* JADX WARN: Type inference failed for: r1v20 */
    /* JADX WARN: Type inference failed for: r1v5, types: [java.lang.CharSequence, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v9 */
    /* JADX WARN: Type inference failed for: r21v1 */
    /* JADX WARN: Type inference failed for: r21v2 */
    /* JADX WARN: Type inference failed for: r21v3, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r21v4 */
    /* JADX WARN: Type inference failed for: r21v5, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r21v6, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r21v7 */
    /* JADX WARN: Type inference failed for: r21v8 */
    /* JADX WARN: Type inference failed for: r2v3, types: [java.lang.Object, java.util.regex.Pattern] */
    /* JADX WARN: Type inference failed for: r5v13 */
    /* JADX WARN: Type inference failed for: r5v14 */
    /* JADX WARN: Type inference failed for: r5v17, types: [java.lang.Object] */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.util.LinkedHashSet, java.util.Set] */
    /* JADX WARN: Type inference failed for: r7v1 */
    /* JADX WARN: Type inference failed for: r7v13 */
    /* JADX WARN: Type inference failed for: r7v18 */
    /* JADX WARN: Type inference failed for: r7v2, types: [java.lang.Integer, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r7v7 */
    private final Integer getTopShortContentMatchingGroupKey(Map<Integer, ? extends List<p567uf.c>> map, Set<String> matchStrings, int i8, int i10, boolean z10, String str, String str2, String str3, String logTag, boolean z11, int i11) {
        int i12;
        ArrayList arrayList;
        List<p567uf.c> list;
        int i13;
        int i14;
        Integer num;
        String str4;
        String str5;
        String string;
        ?? r10;
        Matcher matcher;
        ArrayList arrayList2;
        int iEnd;
        List listB;
        ArrayList arrayList3;
        Iterator it;
        Iterator it2;
        ?? next;
        String str6;
        int i15;
        int i16;
        l lVarL;
        int i17;
        Jv.g gVarB;
        Iterator it3;
        p567uf.c cVar;
        int i18;
        int i19;
        NodeBounds nodeBounds;
        ArrayList arrayList4;
        Iterator it4;
        Set setS0;
        int i20;
        int i21;
        int i22;
        str = str;
        str2 = str2;
        str3 = str3;
        m.h(map, "map");
        m.h(matchStrings, "matchStrings");
        m.h(logTag, "logTag");
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it5 = map.entrySet().iterator();
        ?? r11 = 0;
        Integer numValueOf = null;
        int i23 = 0;
        int i24 = 0;
        while (it5.hasNext()) {
            Map.Entry<Integer, ? extends List<p567uf.c>> next2 = it5.next();
            int iIntValue = next2.getKey().intValue();
            List<p567uf.c> value = next2.getValue();
            if (str2 != null) {
                List<p567uf.c> list2 = value;
                if ((list2 instanceof Collection) && list2.isEmpty()) {
                    i22 = 0;
                } else {
                    Iterator it6 = list2.iterator();
                    i22 = 0;
                    while (it6.hasNext()) {
                        if (m.c(((p567uf.c) it6.next()).f77951d, str2) && (i22 = i22 + 1) < 0) {
                            n.N();
                            throw r11;
                        }
                    }
                }
                if (i22 > 0) {
                    return r11;
                }
            }
            if (str != null) {
                List<p567uf.c> list3 = value;
                boolean z12 = list3 instanceof Collection;
                if (z12 && list3.isEmpty()) {
                    r11 = r11;
                    i20 = 0;
                } else {
                    int i25 = 0;
                    ?? r12 = r11;
                    for (p567uf.c cVar2 : list3) {
                        ?? r21 = r12;
                        if ((m.c(cVar2.f77951d, str) && cVar2.f77949a != null) && (i25 = i25 + 1) < 0) {
                            n.N();
                            throw r21;
                        }
                        r12 = r21;
                    }
                    r11 = r12;
                    i20 = i25;
                }
                if (str3 == null || (z12 && list3.isEmpty())) {
                    i21 = 0;
                } else {
                    Iterator it7 = list3.iterator();
                    i21 = 0;
                    while (it7.hasNext()) {
                        if (m.c(((p567uf.c) it7.next()).f77951d, str3) && (i21 = i21 + 1) < 0) {
                            n.N();
                            throw r11;
                        }
                    }
                }
                i24 += i21;
                r11 = r11;
                if (i20 >= i10) {
                    i12 = 0;
                    if (((double) iIntValue) >= ((double) i11) * 0.75d) {
                        if (str3 == null) {
                            return Integer.valueOf(iIntValue);
                        }
                        if (i24 > 0) {
                            return Integer.valueOf(iIntValue);
                        }
                    }
                }
                ?? linkedHashSet = new LinkedHashSet();
                arrayList = new ArrayList();
                list = value;
                if ((list instanceof Collection) || !list.isEmpty()) {
                    i13 = i12;
                    for (p567uf.c cVar3 : list) {
                        str4 = cVar3.f77949a;
                        if ((str4 == null && (string = str4.toString()) != null) || ((str5 = cVar3.b) != null && (string = str5.toString()) != null)) {
                            String lowerCase = string.toLowerCase(Locale.ROOT);
                            m.g(lowerCase, "toLowerCase(...)");
                            r10 = lowerCase;
                        }
                        ?? Compile = Pattern.compile("\\s+");
                        m.g(Compile, "compile(...)");
                        Mv.r.B0(i12);
                        matcher = Compile.matcher(r10);
                        if (matcher.find()) {
                            arrayList2 = new ArrayList(10);
                            iEnd = i12;
                            do {
                                arrayList2.add(r10.subSequence(iEnd, matcher.start()).toString());
                                iEnd = matcher.end();
                            } while (matcher.find());
                            arrayList2.add(r10.subSequence(iEnd, r10.length()).toString());
                            listB = arrayList2;
                        } else {
                            listB = f.B(r10.toString());
                        }
                        List list4 = listB;
                        arrayList3 = new ArrayList(p468qv.o.P(list4, 10));
                        it = list4.iterator();
                        while (it.hasNext()) {
                            Iterator it8 = it;
                            String lowerCase2 = ((String) it.next()).toLowerCase(Locale.ROOT);
                            m.g(lowerCase2, "toLowerCase(...)");
                            arrayList3.add(lowerCase2);
                            it = it8;
                        }
                        it2 = matchStrings.iterator();
                        do {
                            if (!it2.hasNext()) {
                                next = r11;
                                break;
                            }
                            next = it2.next();
                        } while (!arrayList3.contains((String) next));
                        str6 = (String) next;
                        if (str6 == null) {
                            if (z10) {
                                ?? Compile2 = Pattern.compile("^[-+]?\\d+(\\.\\d+)?");
                                m.g(Compile2, "compile(...)");
                                Matcher matcher2 = Compile2.matcher(r10);
                                m.g(matcher2, "matcher(...)");
                                i15 = i12;
                                lVarL = Mn.J.l(matcher2, i15, r10);
                                if (lVarL == null && (gVarB = lVarL.b()) != null && gVarB.f14223e == 0) {
                                    i17 = 1;
                                } else {
                                    i17 = i15;
                                }
                                if (i17 == 0) {
                                }
                            } else {
                                i15 = i12;
                            }
                            r10 = r11;
                        } else {
                            i15 = i12;
                            r10 = str6;
                        }
                        if (r10 != 0 || linkedHashSet.contains(r10)) {
                            i16 = i15;
                        } else {
                            linkedHashSet.add(r10);
                            arrayList.add(cVar3);
                            i16 = 1;
                        }
                        if (i16 == 0 && (i13 = i13 + 1) < 0) {
                            n.N();
                            throw r11;
                        }
                        i12 = i15;
                        it5 = it5;
                        numValueOf = numValueOf;
                    }
                    i14 = i12;
                    it5 = it5;
                    num = numValueOf;
                } else {
                    i13 = i12;
                    i14 = i13;
                    it5 = it5;
                    num = numValueOf;
                }
                if (i13 >= i8) {
                    if (arrayList.isEmpty()) {
                        i19 = 1;
                        break;
                    }
                    it3 = arrayList.iterator();
                    while (true) {
                        if (it3.hasNext()) {
                            i19 = 1;
                            break;
                        }
                        cVar = (p567uf.c) it3.next();
                        if (cVar.f77950c.getLeft() >= 0) {
                            nodeBounds = cVar.f77950c;
                            if (nodeBounds.getLeft() < i11 || nodeBounds.getRight() <= 0) {
                                i18 = i14;
                            } else {
                                i18 = 1;
                            }
                        } else {
                            i18 = i14;
                        }
                        if (i18 == 0) {
                            i19 = i14;
                            break;
                        }
                    }
                    if (i13 > i23) {
                        arrayList4 = new ArrayList();
                        it4 = arrayList.iterator();
                        while (it4.hasNext()) {
                            arrayList4.add(Integer.valueOf(((p567uf.c) it4.next()).f77950c.getLeft()));
                        }
                        setS0 = p468qv.m.S0(arrayList4);
                        arrayList4.toString();
                        setS0.toString();
                        if ((setS0.size() != 1 ? i14 : 1) == 0 && i19 != 0) {
                            numValueOf = Integer.valueOf(iIntValue);
                            i23 = i13;
                        }
                    }
                }
                numValueOf = num;
            } else {
                r11 = r11;
            }
            i12 = 0;
            ?? linkedHashSet2 = new LinkedHashSet();
            arrayList = new ArrayList();
            list = value;
            if (list instanceof Collection) {
                i13 = i12;
                while (r11.hasNext()) {
                    str4 = cVar3.f77949a;
                    r10 = str4 == null ? CoreConstants.EMPTY_STRING : CoreConstants.EMPTY_STRING;
                    ?? Compile3 = Pattern.compile("\\s+");
                    m.g(Compile3, "compile(...)");
                    Mv.r.B0(i12);
                    matcher = Compile3.matcher(r10);
                    if (matcher.find()) {
                        listB = f.B(r10.toString());
                    } else {
                        arrayList2 = new ArrayList(10);
                        iEnd = i12;
                        do {
                            arrayList2.add(r10.subSequence(iEnd, matcher.start()).toString());
                            iEnd = matcher.end();
                        } while (matcher.find());
                        arrayList2.add(r10.subSequence(iEnd, r10.length()).toString());
                        listB = arrayList2;
                    }
                    List list5 = listB;
                    arrayList3 = new ArrayList(p468qv.o.P(list5, 10));
                    it = list5.iterator();
                    while (it.hasNext()) {
                        Iterator it9 = it;
                        String lowerCase3 = ((String) it.next()).toLowerCase(Locale.ROOT);
                        m.g(lowerCase3, "toLowerCase(...)");
                        arrayList3.add(lowerCase3);
                        it = it9;
                    }
                    it2 = matchStrings.iterator();
                    do {
                        if (!it2.hasNext()) {
                            next = r11;
                            break;
                        }
                        next = it2.next();
                    } while (!arrayList3.contains((String) next));
                    str6 = (String) next;
                    if (str6 == null) {
                        if (z10) {
                            ?? Compile4 = Pattern.compile("^[-+]?\\d+(\\.\\d+)?");
                            m.g(Compile4, "compile(...)");
                            Matcher matcher3 = Compile4.matcher(r10);
                            m.g(matcher3, "matcher(...)");
                            i15 = i12;
                            lVarL = Mn.J.l(matcher3, i15, r10);
                            if (lVarL == null) {
                                i17 = i15;
                            } else {
                                i17 = i15;
                            }
                            if (i17 == 0) {
                            }
                        } else {
                            i15 = i12;
                        }
                        r10 = r11;
                    } else {
                        i15 = i12;
                        r10 = str6;
                    }
                    if (r10 != 0) {
                        i16 = i15;
                    } else {
                        i16 = i15;
                    }
                    if (i16 == 0) {
                    }
                    i12 = i15;
                    it5 = it5;
                    numValueOf = numValueOf;
                }
                i14 = i12;
                it5 = it5;
                num = numValueOf;
            } else {
                i13 = i12;
                while (r11.hasNext()) {
                    str4 = cVar3.f77949a;
                    if (str4 == null) {
                    }
                    ?? Compile5 = Pattern.compile("\\s+");
                    m.g(Compile5, "compile(...)");
                    Mv.r.B0(i12);
                    matcher = Compile5.matcher(r10);
                    if (matcher.find()) {
                        listB = f.B(r10.toString());
                    } else {
                        arrayList2 = new ArrayList(10);
                        iEnd = i12;
                        do {
                            arrayList2.add(r10.subSequence(iEnd, matcher.start()).toString());
                            iEnd = matcher.end();
                        } while (matcher.find());
                        arrayList2.add(r10.subSequence(iEnd, r10.length()).toString());
                        listB = arrayList2;
                    }
                    List list6 = listB;
                    arrayList3 = new ArrayList(p468qv.o.P(list6, 10));
                    it = list6.iterator();
                    while (it.hasNext()) {
                        Iterator it10 = it;
                        String lowerCase4 = ((String) it.next()).toLowerCase(Locale.ROOT);
                        m.g(lowerCase4, "toLowerCase(...)");
                        arrayList3.add(lowerCase4);
                        it = it10;
                    }
                    it2 = matchStrings.iterator();
                    do {
                        if (!it2.hasNext()) {
                            next = r11;
                            break;
                        }
                        next = it2.next();
                    } while (!arrayList3.contains((String) next));
                    str6 = (String) next;
                    if (str6 == null) {
                        if (z10) {
                            ?? Compile6 = Pattern.compile("^[-+]?\\d+(\\.\\d+)?");
                            m.g(Compile6, "compile(...)");
                            Matcher matcher4 = Compile6.matcher(r10);
                            m.g(matcher4, "matcher(...)");
                            i15 = i12;
                            lVarL = Mn.J.l(matcher4, i15, r10);
                            if (lVarL == null) {
                                i17 = i15;
                            } else {
                                i17 = i15;
                            }
                            if (i17 == 0) {
                            }
                        } else {
                            i15 = i12;
                        }
                        r10 = r11;
                    } else {
                        i15 = i12;
                        r10 = str6;
                    }
                    if (r10 != 0) {
                        i16 = i15;
                    } else {
                        i16 = i15;
                    }
                    if (i16 == 0) {
                    }
                    i12 = i15;
                    it5 = it5;
                    numValueOf = numValueOf;
                }
                i14 = i12;
                it5 = it5;
                num = numValueOf;
            }
            if (i13 >= i8) {
                if (arrayList.isEmpty()) {
                    i19 = 1;
                    break;
                }
                it3 = arrayList.iterator();
                while (true) {
                    if (it3.hasNext()) {
                        i19 = 1;
                        break;
                    }
                    cVar = (p567uf.c) it3.next();
                    if (cVar.f77950c.getLeft() >= 0) {
                        nodeBounds = cVar.f77950c;
                        if (nodeBounds.getLeft() < i11) {
                            i18 = i14;
                        } else {
                            i18 = i14;
                        }
                    } else {
                        i18 = i14;
                    }
                    if (i18 == 0) {
                        i19 = i14;
                        break;
                    }
                }
                if (i13 > i23) {
                    arrayList4 = new ArrayList();
                    it4 = arrayList.iterator();
                    while (it4.hasNext()) {
                        arrayList4.add(Integer.valueOf(((p567uf.c) it4.next()).f77950c.getLeft()));
                    }
                    setS0 = p468qv.m.S0(arrayList4);
                    arrayList4.toString();
                    setS0.toString();
                    if ((setS0.size() != 1 ? i14 : 1) == 0) {
                    }
                }
            }
            numValueOf = num;
        }
        Integer num2 = numValueOf;
        matchStrings.toString();
        return num2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ Integer getTopShortContentMatchingGroupKey$default(MyMonitorInAppBlockService myMonitorInAppBlockService, Map map, Set set, int i8, int i10, boolean z10, String str, String str2, String str3, String str4, boolean z11, int i11, int i12, Object obj) {
        return myMonitorInAppBlockService.getTopShortContentMatchingGroupKey(map, (i12 & 2) != 0 ? w.f71760e : set, (i12 & 4) != 0 ? 3 : i8, (i12 & 8) != 0 ? 3 : i10, (i12 & 16) != 0 ? false : z10, (i12 & 32) != 0 ? null : str, (i12 & 64) != 0 ? null : str2, (i12 & 128) != 0 ? null : str3, str4, (i12 & 512) != 0 ? false : z11, i11);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:38:0x007d  */
    /* JADX WARN: Code duplicated, block: B:45:0x00a8  */
    public final String getWebsiteDomain(String str) {
        AccessibilityNodeInfo rootInActiveWindow;
        AccessibilityNodeInfo accessibilityNodeInfo;
        CharSequence contentDescription;
        String strS0;
        CharSequence charSequenceP0;
        String str2;
        List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByViewId;
        defpackage.c cVar = defpackage.c.f48597a;
        kotlin.j jVarD = defpackage.c.d(str);
        String str3 = (String) jVarD.f64869e;
        String str4 = (String) jVarD.f64868I;
        String strS = null;
        if (str3 != null && (rootInActiveWindow = getRootInActiveWindow()) != null) {
            List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByViewId2 = rootInActiveWindow.findAccessibilityNodeInfosByViewId(str3);
            List<AccessibilityNodeInfo> list = listFindAccessibilityNodeInfosByViewId2;
            if ((list == null || list.isEmpty()) && isComposeTestTag(str3)) {
                listFindAccessibilityNodeInfosByViewId2 = findNodesByViewIdInTree(rootInActiveWindow, str3);
            }
            List<AccessibilityNodeInfo> list2 = listFindAccessibilityNodeInfosByViewId2;
            if (list2 != null && !list2.isEmpty() && ((str4 == null || isTablet() || (listFindAccessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(str4)) == null || listFindAccessibilityNodeInfosByViewId.isEmpty()) && (accessibilityNodeInfo = (AccessibilityNodeInfo) p468qv.m.l0(listFindAccessibilityNodeInfosByViewId2)) != null)) {
                p118e.b.d("checkAndOpenBWOverlay", "getWebsiteDomain: matchingNode found");
                CharSequence text = accessibilityNodeInfo.getText();
                if (text == null || (strS0 = text.toString()) == null) {
                    contentDescription = accessibilityNodeInfo.getContentDescription();
                    if (contentDescription != null || (charSequenceP0 = Mv.r.P0(contentDescription)) == null || (str2 = (String) p468qv.m.l0(Mv.r.D0(charSequenceP0, new String[]{" "}, 0, 6))) == null) {
                        strS0 = CoreConstants.EMPTY_STRING;
                    } else {
                        strS0 = Mv.r.S0(str2, CoreConstants.DOT);
                    }
                } else {
                    if (Mv.r.p0(strS0)) {
                        strS0 = null;
                    }
                    if (strS0 == null) {
                        contentDescription = accessibilityNodeInfo.getContentDescription();
                        if (contentDescription != null) {
                            strS0 = CoreConstants.EMPTY_STRING;
                        } else {
                            strS0 = CoreConstants.EMPTY_STRING;
                        }
                    }
                }
                p118e.b.d("checkAndOpenBWOverlay", "getWebsiteDomain: " + strS0);
                String strL = Fv.a.l(strS0.toString());
                strS = strL != null ? Fv.a.s(strL) : null;
                AbstractC3102b.A("getWebsiteDomain: extracted domain ", strS, "checkAndOpenBWOverlay");
            }
        }
        return strS;
    }

    /* JADX WARN: Code duplicated, block: B:34:0x00cb  */
    private final p getYoutubeChannelNameAndContentType(boolean z10) {
        C5057b c5057bFindYTChannelName;
        String str;
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            p118e.b.d(this.logTag1, "rootNode is null");
            return new p(null, null, null);
        }
        String strK = AbstractC3102b.k("HashMapGroup-", getRandomString(5));
        boolean zIsScreenInPortraitOrientation = isScreenInPortraitOrientation();
        p567uf.j jVar = (isTablet() || zIsScreenInPortraitOrientation) ? p567uf.j.f77960e : p567uf.j.f77958I;
        p567uf.a aVarConvertToHashMap = convertToHashMap(strK, rootInActiveWindow, jVar);
        Map<Integer, ? extends List<p567uf.c>> map = aVarConvertToHashMap.b;
        System.currentTimeMillis();
        defpackage.c cVar = defpackage.c.f48597a;
        if (tw.d.p().c("SHOW_YT_HOME_PAGE_DIVERSION_SCREEN_V2") ? isYTHomePage(map, true, strK) : false) {
            p118e.b.d(strK, "YT Home Page detected");
            return new p(null, p567uf.b.f77946X, null);
        }
        boolean z11 = aVarConvertToHashMap.f77944d;
        if (zIsScreenInPortraitOrientation) {
            p118e.b.d(strK, "isPortraitMode");
            c5057bFindYTChannelName = findYTChannelName(map, z11, strK);
        } else if (isTablet()) {
            p118e.b.d(strK, "isLandscapeMode, isTablet");
            c5057bFindYTChannelName = findYTChannelName(map, z11, strK);
        } else {
            p118e.b.d(strK, "isLandscapeMode");
            c5057bFindYTChannelName = findYTChannelNameInLandscapeMode(aVarConvertToHashMap.f77942a, strK);
        }
        if (c5057bFindYTChannelName != null && (str = c5057bFindYTChannelName.f75973a) != null) {
            p118e.b.d(strK, "getYoutubeChannelNameAndContentType ChannelDetected Channel Name: " + str + " isProduction: true");
            Locale locale = Locale.ROOT;
            String lowerCase = str.toLowerCase(locale);
            m.g(lowerCase, "toLowerCase(...)");
            if (Mv.r.b0(lowerCase, "seconds", false)) {
                printHashMapGroups(strK, aVarConvertToHashMap);
            } else {
                String lowerCase2 = str.toLowerCase(locale);
                m.g(lowerCase2, "toLowerCase(...)");
                if (Mv.r.b0(lowerCase2, "minutes", false)) {
                    printHashMapGroups(strK, aVarConvertToHashMap);
                }
            }
            System.currentTimeMillis();
            return new p(c5057bFindYTChannelName, p567uf.b.f77945I, null);
        }
        System.currentTimeMillis();
        Set<String> shortsReelsText = getShortsReelsText(true);
        int i8 = aVarConvertToHashMap.f77943c;
        C5057b c5057b = c5057bFindYTChannelName;
        Map map2 = aVarConvertToHashMap.f77942a;
        Integer topShortContentMatchingGroupKey$default = getTopShortContentMatchingGroupKey$default(this, map2, shortsReelsText, 0, 1, false, null, null, null, strK, true, i8, 212, null);
        if (topShortContentMatchingGroupKey$default == null) {
            printHashMapGroups(strK, aVarConvertToHashMap);
            return new p(null, null, null);
        }
        double dIntValue = topShortContentMatchingGroupKey$default.intValue();
        int i10 = aVarConvertToHashMap.f77943c;
        if (!(dIntValue >= ((double) i10) * 0.75d)) {
            p118e.b.d("Shorts", "Shorts detected in left half maxRight: " + i10 + " shortsMatchKey: " + topShortContentMatchingGroupKey$default);
            printHashMapGroups(strK, aVarConvertToHashMap);
            return new p(null, null, null);
        }
        List<Integer> listExtractNumbersFromReels = extractNumbersFromReels(strK, (List) map2.get(topShortContentMatchingGroupKey$default));
        if (isInternalTesting().a()) {
            printHashMapGroups(strK, aVarConvertToHashMap);
        }
        p118e.b.d(strK, "getYoutubeChannelNameAndContentType YT Shorts detected " + topShortContentMatchingGroupKey$default + " channelTag: " + (c5057b != null ? c5057b.b : null) + " likeCommentDigest: " + listExtractNumbersFromReels);
        List list = (List) map2.get(topShortContentMatchingGroupKey$default);
        return new p(c5057b != null ? new C5057b(c5057b.f75973a, c5057b.b, list) : new C5057b(null, null, list), p567uf.b.f77948e, listExtractNumbersFromReels);
    }

    public static /* synthetic */ p getYoutubeChannelNameAndContentType$default(MyMonitorInAppBlockService myMonitorInAppBlockService, boolean z10, int i8, Object obj) {
        if ((i8 & 1) != 0) {
            z10 = false;
        }
        return myMonitorInAppBlockService.getYoutubeChannelNameAndContentType(z10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object handleBrowserProductiveMode(String str, p552tv.e<? super A> eVar) {
        Wv.e eVar2 = M.f24563a;
        Object objM = C.M(Wv.d.f37341X, new p536tf.P(this, str, null), eVar);
        return objM == p581uv.a.f79130e ? objM : A.f64853a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:106:0x0263  */
    /* JADX WARN: Code duplicated, block: B:108:0x0266  */
    /* JADX WARN: Code duplicated, block: B:116:0x028c A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:117:0x028e  */
    /* JADX WARN: Code duplicated, block: B:118:0x0296  */
    /* JADX WARN: Code duplicated, block: B:119:0x0299  */
    /* JADX WARN: Code duplicated, block: B:121:0x029d  */
    /* JADX WARN: Code duplicated, block: B:122:0x02a5  */
    /* JADX WARN: Code duplicated, block: B:126:0x02bd  */
    /* JADX WARN: Code duplicated, block: B:128:0x02c3  */
    /* JADX WARN: Code duplicated, block: B:131:0x02f7  */
    /* JADX WARN: Code duplicated, block: B:133:0x0300  */
    /* JADX WARN: Code duplicated, block: B:134:0x0303  */
    /* JADX WARN: Code duplicated, block: B:138:0x031d  */
    /* JADX WARN: Code duplicated, block: B:140:0x0320  */
    /* JADX WARN: Code duplicated, block: B:141:0x0323  */
    /* JADX WARN: Code duplicated, block: B:143:0x0326  */
    /* JADX WARN: Code duplicated, block: B:145:0x0334  */
    /* JADX WARN: Code duplicated, block: B:148:0x033b  */
    /* JADX WARN: Code duplicated, block: B:151:0x033f  */
    /* JADX WARN: Code duplicated, block: B:155:0x036d  */
    /* JADX WARN: Code duplicated, block: B:159:0x03a7  */
    /* JADX WARN: Code duplicated, block: B:161:0x03ae  */
    /* JADX WARN: Code duplicated, block: B:163:0x03cf A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:164:0x03d0  */
    /* JADX WARN: Code duplicated, block: B:166:0x03d4  */
    /* JADX WARN: Code duplicated, block: B:169:0x03db  */
    /* JADX WARN: Code duplicated, block: B:172:0x03e4  */
    /* JADX WARN: Code duplicated, block: B:174:0x03e7  */
    /* JADX WARN: Code duplicated, block: B:176:0x03ef A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:179:0x03f5  */
    /* JADX WARN: Code duplicated, block: B:182:0x040a  */
    /* JADX WARN: Code duplicated, block: B:36:0x0122  */
    /* JADX WARN: Code duplicated, block: B:37:0x0124  */
    /* JADX WARN: Code duplicated, block: B:40:0x0131 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:41:0x0133 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:42:0x0135  */
    /* JADX WARN: Code duplicated, block: B:44:0x0138  */
    /* JADX WARN: Code duplicated, block: B:45:0x013f  */
    /* JADX WARN: Code duplicated, block: B:47:0x0145  */
    /* JADX WARN: Code duplicated, block: B:48:0x014c  */
    /* JADX WARN: Code duplicated, block: B:51:0x0156 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:52:0x0158  */
    /* JADX WARN: Code duplicated, block: B:53:0x015b  */
    /* JADX WARN: Code duplicated, block: B:55:0x015e A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:56:0x0160  */
    /* JADX WARN: Code duplicated, block: B:57:0x0163  */
    /* JADX WARN: Code duplicated, block: B:59:0x0166  */
    /* JADX WARN: Code duplicated, block: B:64:0x0177  */
    /* JADX WARN: Code duplicated, block: B:72:0x018c  */
    /* JADX WARN: Code duplicated, block: B:74:0x01a6  */
    /* JADX WARN: Code duplicated, block: B:75:0x01a8  */
    /* JADX WARN: Code duplicated, block: B:81:0x01b5  */
    /* JADX WARN: Code duplicated, block: B:8:0x001a  */
    /* JADX WARN: Code duplicated, block: B:91:0x01e6  */
    /* JADX WARN: Code duplicated, block: B:94:0x020f  */
    /* JADX WARN: Code duplicated, block: B:97:0x0225  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r14v12 */
    /* JADX WARN: Type inference failed for: r14v16, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.String] */
    /* JADX WARN: Type inference failed for: r14v17 */
    /* JADX WARN: Type inference failed for: r14v20, types: [ai.focus.services.MyMonitorInAppBlockService, java.lang.Object, java.lang.String, tf.b] */
    /* JADX WARN: Type inference failed for: r14v21 */
    /* JADX WARN: Type inference failed for: r14v32 */
    /* JADX WARN: Type inference failed for: r4v18, types: [ai.focus.services.MyMonitorInAppBlockService] */
    /* JADX WARN: Type inference failed for: r4v22 */
    /* JADX WARN: Type inference failed for: r4v38 */
    /* JADX WARN: Type inference failed for: r5v14 */
    /* JADX WARN: Type inference failed for: r5v15, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r5v24 */
    /* JADX WARN: Type inference failed for: r7v3 */
    /* JADX WARN: Type inference failed for: r7v4, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6 */
    /* JADX WARN: Type inference failed for: r8v18 */
    /* JADX WARN: Type inference failed for: r8v19, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v20 */
    /* JADX WARN: Type inference failed for: r8v21 */
    /* JADX WARN: Type inference failed for: r8v26, types: [ai.focus.services.MyMonitorInAppBlockService, android.content.Context] */
    /* JADX WARN: Type inference failed for: r8v37 */
    public final Object handleYoutubeProductiveMode(String str, boolean z10, p552tv.e<? super A> eVar) {
        Q q2;
        C5057b c5057b;
        List list;
        p567uf.b bVar;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        Set set;
        int i8;
        boolean zContains;
        String str2;
        List<String> listExtractAllHandles;
        String str3;
        String str4;
        String input;
        boolean z11;
        boolean z12;
        int i10;
        String str5;
        List list2;
        MyMonitorInAppBlockService myMonitorInAppBlockService2;
        String str6;
        List<String> list3;
        Pattern patternCompile;
        String str7;
        String str8;
        List list4;
        String str9;
        String channelName;
        String str10;
        MyMonitorInAppBlockService myMonitorInAppBlockService3;
        C5057b c5057b2;
        List list5;
        C5057b c5057b3;
        p567uf.b bVar2;
        ?? r14;
        String str11;
        String str12;
        Context applicationContext;
        String string;
        MyMonitorInAppBlockService myMonitorInAppBlockService4;
        ?? r10;
        String str13;
        ?? r11;
        ?? r12;
        boolean z13;
        ?? r13;
        String str14;
        C5057b c5057b4;
        String str15;
        ?? r15;
        ?? r16;
        MyAccessibilityNodeInfoWrapper accessibilityNodeInfo;
        Context applicationContext2;
        String string2;
        boolean zBooleanValue;
        p567uf.b bVar3;
        C5057b c5057b5;
        MyMonitorInAppBlockService myMonitorInAppBlockService5;
        String str16;
        String str17;
        int i11;
        String str18;
        String str19;
        String str20;
        C5057b c5057b6;
        MyMonitorInAppBlockService myMonitorInAppBlockService6;
        g0 g0Var;
        g0 g0Var2;
        boolean z14;
        g0 g0Var3;
        Context applicationContext3;
        String string3;
        String str21 = str;
        if (eVar instanceof Q) {
            q2 = (Q) eVar;
            int i12 = q2.f75924p0;
            if ((i12 & Level.ALL_INT) != 0) {
                q2.f75924p0 = i12 - Level.ALL_INT;
            } else {
                q2 = new Q(this, eVar);
            }
        } else {
            q2 = new Q(this, eVar);
        }
        Q q8 = q2;
        Object objS = q8.f75922n0;
        p581uv.a aVar = p581uv.a.f79130e;
        int i13 = q8.f75924p0;
        A a10 = A.f64853a;
        switch (i13) {
            case 0:
                U8.b.F(objS);
                p youtubeChannelNameAndContentType = getYoutubeChannelNameAndContentType(z10);
                C5057b c5057b7 = (C5057b) youtubeChannelNameAndContentType.f64896e;
                p567uf.b bVar4 = (p567uf.b) youtubeChannelNameAndContentType.f64894I;
                List list6 = (List) youtubeChannelNameAndContentType.f64895X;
                C2788j0 c2788j0 = getFocusTimerUseCases().f43987T;
                q8.f75919e = this;
                q8.f75915I = str21;
                q8.f75916X = c5057b7;
                q8.f75917Y = bVar4;
                q8.f75918Z = list6;
                q8.f75924p0 = 1;
                Object objA = c2788j0.a(str21, q8);
                if (objA != aVar) {
                    c5057b = c5057b7;
                    list = list6;
                    objS = objA;
                    bVar = bVar4;
                    myMonitorInAppBlockService = this;
                    set = (Set) objS;
                    if (bVar == null) {
                        i8 = -1;
                    } else {
                        i8 = AbstractC5078x.f76110a[bVar.ordinal()];
                    }
                    if (i8 == -1) {
                        zContains = set.contains(x0.f14558X);
                    } else if (i8 != 1) {
                        zContains = set.contains(x0.f14561e);
                    } else if (i8 != 2) {
                        zContains = set.contains(x0.f14558X);
                    } else {
                        if (i8 == 3) {
                            throw new NoWhenBranchMatchedException();
                        }
                        zContains = set.contains(x0.f14557I);
                    }
                    if (zContains) {
                        if (c5057b != null) {
                            str2 = c5057b.f75973a;
                        } else {
                            str2 = null;
                        }
                        if (str2 != null) {
                            String str22 = c5057b.f75973a;
                            Objects.toString(bVar);
                        } else {
                            if (c5057b != null) {
                                str8 = c5057b.b;
                            } else {
                                str8 = null;
                            }
                            if (str8 != null) {
                                String str23 = c5057b.f75973a;
                                Objects.toString(bVar);
                            }
                        }
                        listExtractAllHandles = myMonitorInAppBlockService.extractAllHandles(c5057b);
                        str3 = CoreConstants.EMPTY_STRING;
                        if (c5057b != null || (str4 = c5057b.f75973a) == null) {
                            str4 = CoreConstants.EMPTY_STRING;
                        }
                        if (c5057b != null && (str7 = c5057b.b) != null) {
                            str3 = str7;
                        }
                        input = AbstractC3102b.l(str4, " ", str3);
                        if (listExtractAllHandles.size() == 1) {
                            patternCompile = Pattern.compile("and \\d+ (more|others?)|\\s\\+\\d+(\\s|$)");
                            m.g(patternCompile, "compile(...)");
                            m.h(input, "input");
                            if (patternCompile.matcher(input).find()) {
                                z11 = true;
                            } else {
                                z11 = false;
                            }
                        } else {
                            z11 = false;
                        }
                        if (listExtractAllHandles.size() < 2 || z11) {
                            z12 = true;
                        } else {
                            z12 = false;
                        }
                        if (z12 || bVar != p567uf.b.f77945I) {
                            i10 = R.string.you_are_watching_a_productive_channel;
                            MyMonitorInAppBlockService myMonitorInAppBlockService7 = myMonitorInAppBlockService;
                            str5 = str21;
                            list2 = list;
                            myMonitorInAppBlockService2 = myMonitorInAppBlockService7;
                            if (bVar == null && bVar == p567uf.b.f77946X) {
                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "YT Home Page detected");
                                q8.f75919e = myMonitorInAppBlockService2;
                                q8.f75915I = str5;
                                q8.f75916X = null;
                                q8.f75917Y = null;
                                q8.f75918Z = null;
                                q8.f75920l0 = null;
                                q8.f75924p0 = 4;
                                r14 = 0;
                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService2, str5, bVar, null, null, null, q8, 28, null) != aVar) {
                                    str11 = str5;
                                    myMonitorInAppBlockService4 = myMonitorInAppBlockService2;
                                    q8.f75919e = r14;
                                    q8.f75915I = r14;
                                    q8.f75924p0 = 5;
                                    if (myMonitorInAppBlockService4.showPMDiversionScreen(str11, q8) == aVar) {
                                    }
                                }
                            } else {
                                list4 = null;
                                if (bVar == null && bVar == p567uf.b.f77948e) {
                                    q8.f75919e = myMonitorInAppBlockService2;
                                    q8.f75915I = str5;
                                    q8.f75916X = c5057b;
                                    q8.f75917Y = bVar;
                                    q8.f75918Z = list2;
                                    q8.f75920l0 = null;
                                    q8.f75924p0 = 6;
                                    objS = myMonitorInAppBlockService2.findWhitelistedStudyChannel(c5057b, listExtractAllHandles, q8);
                                    if (objS != aVar) {
                                        C5057b c5057b8 = c5057b;
                                        list5 = list2;
                                        c5057b3 = c5057b8;
                                        bVar2 = bVar;
                                        r10 = myMonitorInAppBlockService2;
                                        str13 = (String) objS;
                                        if (str13 == null) {
                                            r11 = str13;
                                        } else if (c5057b3 != null) {
                                            r11 = c5057b3.f75973a;
                                        } else {
                                            r11 = list4;
                                        }
                                        if (c5057b3 != null) {
                                            r12 = c5057b3.b;
                                        } else {
                                            r12 = list4;
                                        }
                                        q8.f75919e = r10;
                                        q8.f75915I = str5;
                                        q8.f75916X = c5057b3;
                                        q8.f75917Y = str13;
                                        q8.f75918Z = list4;
                                        q8.f75924p0 = 7;
                                        z13 = true;
                                        if (r10.checkAndEnterPMChannelEvent(str5, bVar2, r11, r12, list5, q8) != aVar) {
                                            r13 = r10;
                                            str14 = str5;
                                            c5057b4 = c5057b3;
                                            str15 = str13;
                                            if (str15 != null) {
                                                r15 = list4;
                                                p118e.b.d(r13.logTag1, "Short allowed from Study Mode channel: ".concat(str15));
                                                applicationContext2 = r13.getApplicationContext();
                                                m.g(applicationContext2, "getApplicationContext(...)");
                                                string2 = r13.getString(R.string.channel_in_study_channels, str15);
                                                m.g(string2, "getString(...)");
                                                q8.f75919e = r15;
                                                q8.f75915I = r15;
                                                q8.f75916X = r15;
                                                q8.f75917Y = r15;
                                                q8.f75924p0 = 8;
                                                if (r13.sendChannelToastBroadcast(applicationContext2, string2, str15, q8) != aVar) {
                                                }
                                            } else {
                                                r15 = list4;
                                                p118e.b.d(r13.logTag1, "Show Block Shorts overlay from Productive Mode");
                                                if (c5057b4 != null) {
                                                    r16 = c5057b4.f75974c;
                                                } else {
                                                    r16 = r15;
                                                }
                                                accessibilityNodeInfo = r13.getAccessibilityNodeInfo(r16, z13);
                                                q8.f75919e = r15;
                                                q8.f75915I = r15;
                                                q8.f75916X = r15;
                                                q8.f75917Y = r15;
                                                q8.f75924p0 = 9;
                                                if (r13.showBLOverlay(str14, false, accessibilityNodeInfo, q8) != aVar) {
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (c5057b != null) {
                                        str9 = c5057b.f75973a;
                                    } else {
                                        str9 = null;
                                    }
                                    if (str9 == null) {
                                        p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                        return a10;
                                    }
                                    o oVar = p646xf.b.f81953a;
                                    channelName = c5057b.f75973a;
                                    m.h(channelName, "channelName");
                                    str10 = c5057b.b;
                                    if (str10 != null) {
                                        if (!y.X(channelName, str10, false)) {
                                            str10 = null;
                                        }
                                        if (str10 != null) {
                                            channelName = str10;
                                        }
                                    }
                                    p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                                    C0315z c0315zB = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                                    q8.f75919e = myMonitorInAppBlockService2;
                                    q8.f75915I = str5;
                                    q8.f75916X = c5057b;
                                    q8.f75917Y = channelName;
                                    q8.f75918Z = null;
                                    q8.f75920l0 = null;
                                    q8.f75924p0 = 10;
                                    objS = Sv.r.s(c0315zB, q8);
                                    if (objS != aVar) {
                                        C5057b c5057b9 = c5057b;
                                        myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                        c5057b2 = c5057b9;
                                        zBooleanValue = ((Boolean) objS).booleanValue();
                                        String str24 = c5057b2.b;
                                        int i14 = i10;
                                        bVar3 = p567uf.b.f77945I;
                                        q8.f75919e = myMonitorInAppBlockService3;
                                        q8.f75915I = str5;
                                        q8.f75916X = c5057b2;
                                        q8.f75917Y = channelName;
                                        q8.f75921m0 = zBooleanValue;
                                        q8.f75924p0 = 11;
                                        c5057b5 = c5057b2;
                                        myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                        str16 = channelName;
                                        str17 = "getApplicationContext(...)";
                                        i11 = i14;
                                        str18 = "getString(...)";
                                        if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                            str19 = str5;
                                            str20 = str16;
                                            c5057b6 = c5057b5;
                                            myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                            if (zBooleanValue) {
                                                applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                                m.g(applicationContext3, str17);
                                                string3 = myMonitorInAppBlockService6.getString(i11);
                                                m.g(string3, str18);
                                                q8.f75919e = null;
                                                q8.f75915I = null;
                                                q8.f75916X = null;
                                                q8.f75917Y = null;
                                                q8.f75924p0 = 12;
                                                if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                                }
                                            } else {
                                                g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                if (g0Var != null) {
                                                    g0Var.c();
                                                }
                                                g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                if (g0Var2 == null && g0Var2.c()) {
                                                    z14 = true;
                                                } else {
                                                    z14 = false;
                                                }
                                                if (z14 || !m.c(myMonitorInAppBlockService6.lastChannelNameCategorise, str20)) {
                                                    g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                    if (g0Var3 != null) {
                                                        g0Var3.g(null);
                                                    }
                                                    myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                                    myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                                    return a10;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            listExtractAllHandles.toString();
                            Objects.toString(bVar);
                            q8.f75919e = myMonitorInAppBlockService;
                            q8.f75915I = str21;
                            q8.f75916X = c5057b;
                            q8.f75917Y = bVar;
                            q8.f75918Z = list;
                            q8.f75920l0 = listExtractAllHandles;
                            q8.f75924p0 = 2;
                            Object objFindProductiveHandle = myMonitorInAppBlockService.findProductiveHandle(listExtractAllHandles, q8);
                            if (objFindProductiveHandle != aVar) {
                                str6 = str21;
                                list3 = listExtractAllHandles;
                                objS = objFindProductiveHandle;
                                str12 = (String) objS;
                                if (str12 != null) {
                                    applicationContext = myMonitorInAppBlockService.getApplicationContext();
                                    m.g(applicationContext, "getApplicationContext(...)");
                                    string = myMonitorInAppBlockService.getString(R.string.you_are_watching_a_productive_channel);
                                    m.g(string, "getString(...)");
                                    q8.f75919e = null;
                                    q8.f75915I = null;
                                    q8.f75916X = null;
                                    q8.f75917Y = null;
                                    q8.f75918Z = null;
                                    q8.f75920l0 = null;
                                    q8.f75924p0 = 3;
                                    if (myMonitorInAppBlockService.sendChannelToastBroadcast(applicationContext, string, str12, q8) == aVar) {
                                    }
                                } else {
                                    i10 = R.string.you_are_watching_a_productive_channel;
                                    Objects.toString(list3);
                                    listExtractAllHandles = list3;
                                    list2 = list;
                                    myMonitorInAppBlockService2 = myMonitorInAppBlockService;
                                    str5 = str6;
                                    if (bVar == null) {
                                        list4 = null;
                                        if (bVar == null) {
                                            if (c5057b != null) {
                                                str9 = c5057b.f75973a;
                                            } else {
                                                str9 = null;
                                            }
                                            if (str9 == null) {
                                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                                return a10;
                                            }
                                            o oVar2 = p646xf.b.f81953a;
                                            channelName = c5057b.f75973a;
                                            m.h(channelName, "channelName");
                                            str10 = c5057b.b;
                                            if (str10 != null) {
                                                if (!y.X(channelName, str10, false)) {
                                                    str10 = null;
                                                }
                                                if (str10 != null) {
                                                    channelName = str10;
                                                }
                                            }
                                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                                            C0315z c0315zB2 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                                            q8.f75919e = myMonitorInAppBlockService2;
                                            q8.f75915I = str5;
                                            q8.f75916X = c5057b;
                                            q8.f75917Y = channelName;
                                            q8.f75918Z = null;
                                            q8.f75920l0 = null;
                                            q8.f75924p0 = 10;
                                            objS = Sv.r.s(c0315zB2, q8);
                                            if (objS != aVar) {
                                                C5057b c5057b10 = c5057b;
                                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                                c5057b2 = c5057b10;
                                                zBooleanValue = ((Boolean) objS).booleanValue();
                                                String str25 = c5057b2.b;
                                                int i15 = i10;
                                                bVar3 = p567uf.b.f77945I;
                                                q8.f75919e = myMonitorInAppBlockService3;
                                                q8.f75915I = str5;
                                                q8.f75916X = c5057b2;
                                                q8.f75917Y = channelName;
                                                q8.f75921m0 = zBooleanValue;
                                                q8.f75924p0 = 11;
                                                c5057b5 = c5057b2;
                                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                                str16 = channelName;
                                                str17 = "getApplicationContext(...)";
                                                i11 = i15;
                                                str18 = "getString(...)";
                                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                                    str19 = str5;
                                                    str20 = str16;
                                                    c5057b6 = c5057b5;
                                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                                    if (zBooleanValue) {
                                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var != null) {
                                                            g0Var.c();
                                                        }
                                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var2 == null) {
                                                            z14 = false;
                                                        } else {
                                                            z14 = false;
                                                        }
                                                        if (z14) {
                                                        }
                                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var3 != null) {
                                                            g0Var3.g(null);
                                                        }
                                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                                        return a10;
                                                    }
                                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                                    m.g(applicationContext3, str17);
                                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                                    m.g(string3, str18);
                                                    q8.f75919e = null;
                                                    q8.f75915I = null;
                                                    q8.f75916X = null;
                                                    q8.f75917Y = null;
                                                    q8.f75924p0 = 12;
                                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                                    }
                                                }
                                            }
                                        } else {
                                            if (c5057b != null) {
                                                str9 = c5057b.f75973a;
                                            } else {
                                                str9 = null;
                                            }
                                            if (str9 == null) {
                                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                                return a10;
                                            }
                                            o oVar3 = p646xf.b.f81953a;
                                            channelName = c5057b.f75973a;
                                            m.h(channelName, "channelName");
                                            str10 = c5057b.b;
                                            if (str10 != null) {
                                                if (!y.X(channelName, str10, false)) {
                                                    str10 = null;
                                                }
                                                if (str10 != null) {
                                                    channelName = str10;
                                                }
                                            }
                                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                                            C0315z c0315zB3 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                                            q8.f75919e = myMonitorInAppBlockService2;
                                            q8.f75915I = str5;
                                            q8.f75916X = c5057b;
                                            q8.f75917Y = channelName;
                                            q8.f75918Z = null;
                                            q8.f75920l0 = null;
                                            q8.f75924p0 = 10;
                                            objS = Sv.r.s(c0315zB3, q8);
                                            if (objS != aVar) {
                                                C5057b c5057b11 = c5057b;
                                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                                c5057b2 = c5057b11;
                                                zBooleanValue = ((Boolean) objS).booleanValue();
                                                String str26 = c5057b2.b;
                                                int i16 = i10;
                                                bVar3 = p567uf.b.f77945I;
                                                q8.f75919e = myMonitorInAppBlockService3;
                                                q8.f75915I = str5;
                                                q8.f75916X = c5057b2;
                                                q8.f75917Y = channelName;
                                                q8.f75921m0 = zBooleanValue;
                                                q8.f75924p0 = 11;
                                                c5057b5 = c5057b2;
                                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                                str16 = channelName;
                                                str17 = "getApplicationContext(...)";
                                                i11 = i16;
                                                str18 = "getString(...)";
                                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                                    str19 = str5;
                                                    str20 = str16;
                                                    c5057b6 = c5057b5;
                                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                                    if (zBooleanValue) {
                                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var != null) {
                                                            g0Var.c();
                                                        }
                                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var2 == null) {
                                                            z14 = false;
                                                        } else {
                                                            z14 = false;
                                                        }
                                                        if (z14) {
                                                        }
                                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var3 != null) {
                                                            g0Var3.g(null);
                                                        }
                                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                                        return a10;
                                                    }
                                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                                    m.g(applicationContext3, str17);
                                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                                    m.g(string3, str18);
                                                    q8.f75919e = null;
                                                    q8.f75915I = null;
                                                    q8.f75916X = null;
                                                    q8.f75917Y = null;
                                                    q8.f75924p0 = 12;
                                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        list4 = null;
                                        if (bVar == null) {
                                            if (c5057b != null) {
                                                str9 = c5057b.f75973a;
                                            } else {
                                                str9 = null;
                                            }
                                            if (str9 == null) {
                                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                                return a10;
                                            }
                                            o oVar4 = p646xf.b.f81953a;
                                            channelName = c5057b.f75973a;
                                            m.h(channelName, "channelName");
                                            str10 = c5057b.b;
                                            if (str10 != null) {
                                                if (!y.X(channelName, str10, false)) {
                                                    str10 = null;
                                                }
                                                if (str10 != null) {
                                                    channelName = str10;
                                                }
                                            }
                                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                                            C0315z c0315zB4 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                                            q8.f75919e = myMonitorInAppBlockService2;
                                            q8.f75915I = str5;
                                            q8.f75916X = c5057b;
                                            q8.f75917Y = channelName;
                                            q8.f75918Z = null;
                                            q8.f75920l0 = null;
                                            q8.f75924p0 = 10;
                                            objS = Sv.r.s(c0315zB4, q8);
                                            if (objS != aVar) {
                                                C5057b c5057b12 = c5057b;
                                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                                c5057b2 = c5057b12;
                                                zBooleanValue = ((Boolean) objS).booleanValue();
                                                String str27 = c5057b2.b;
                                                int i17 = i10;
                                                bVar3 = p567uf.b.f77945I;
                                                q8.f75919e = myMonitorInAppBlockService3;
                                                q8.f75915I = str5;
                                                q8.f75916X = c5057b2;
                                                q8.f75917Y = channelName;
                                                q8.f75921m0 = zBooleanValue;
                                                q8.f75924p0 = 11;
                                                c5057b5 = c5057b2;
                                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                                str16 = channelName;
                                                str17 = "getApplicationContext(...)";
                                                i11 = i17;
                                                str18 = "getString(...)";
                                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                                    str19 = str5;
                                                    str20 = str16;
                                                    c5057b6 = c5057b5;
                                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                                    if (zBooleanValue) {
                                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var != null) {
                                                            g0Var.c();
                                                        }
                                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var2 == null) {
                                                            z14 = false;
                                                        } else {
                                                            z14 = false;
                                                        }
                                                        if (z14) {
                                                        }
                                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var3 != null) {
                                                            g0Var3.g(null);
                                                        }
                                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                                        return a10;
                                                    }
                                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                                    m.g(applicationContext3, str17);
                                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                                    m.g(string3, str18);
                                                    q8.f75919e = null;
                                                    q8.f75915I = null;
                                                    q8.f75916X = null;
                                                    q8.f75917Y = null;
                                                    q8.f75924p0 = 12;
                                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                                    }
                                                }
                                            }
                                        } else {
                                            if (c5057b != null) {
                                                str9 = c5057b.f75973a;
                                            } else {
                                                str9 = null;
                                            }
                                            if (str9 == null) {
                                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                                return a10;
                                            }
                                            o oVar5 = p646xf.b.f81953a;
                                            channelName = c5057b.f75973a;
                                            m.h(channelName, "channelName");
                                            str10 = c5057b.b;
                                            if (str10 != null) {
                                                if (!y.X(channelName, str10, false)) {
                                                    str10 = null;
                                                }
                                                if (str10 != null) {
                                                    channelName = str10;
                                                }
                                            }
                                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                                            C0315z c0315zB5 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                                            q8.f75919e = myMonitorInAppBlockService2;
                                            q8.f75915I = str5;
                                            q8.f75916X = c5057b;
                                            q8.f75917Y = channelName;
                                            q8.f75918Z = null;
                                            q8.f75920l0 = null;
                                            q8.f75924p0 = 10;
                                            objS = Sv.r.s(c0315zB5, q8);
                                            if (objS != aVar) {
                                                C5057b c5057b13 = c5057b;
                                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                                c5057b2 = c5057b13;
                                                zBooleanValue = ((Boolean) objS).booleanValue();
                                                String str28 = c5057b2.b;
                                                int i18 = i10;
                                                bVar3 = p567uf.b.f77945I;
                                                q8.f75919e = myMonitorInAppBlockService3;
                                                q8.f75915I = str5;
                                                q8.f75916X = c5057b2;
                                                q8.f75917Y = channelName;
                                                q8.f75921m0 = zBooleanValue;
                                                q8.f75924p0 = 11;
                                                c5057b5 = c5057b2;
                                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                                str16 = channelName;
                                                str17 = "getApplicationContext(...)";
                                                i11 = i18;
                                                str18 = "getString(...)";
                                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                                    str19 = str5;
                                                    str20 = str16;
                                                    c5057b6 = c5057b5;
                                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                                    if (zBooleanValue) {
                                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var != null) {
                                                            g0Var.c();
                                                        }
                                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var2 == null) {
                                                            z14 = false;
                                                        } else {
                                                            z14 = false;
                                                        }
                                                        if (z14) {
                                                        }
                                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                                        if (g0Var3 != null) {
                                                            g0Var3.g(null);
                                                        }
                                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                                        return a10;
                                                    }
                                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                                    m.g(applicationContext3, str17);
                                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                                    m.g(string3, str18);
                                                    q8.f75919e = null;
                                                    q8.f75915I = null;
                                                    q8.f75916X = null;
                                                    q8.f75917Y = null;
                                                    q8.f75924p0 = 12;
                                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return a10;
                }
                return aVar;
            case 1:
                List list7 = q8.f75918Z;
                p567uf.b bVar5 = (p567uf.b) q8.f75917Y;
                C5057b c5057b14 = q8.f75916X;
                String str29 = q8.f75915I;
                MyMonitorInAppBlockService myMonitorInAppBlockService8 = q8.f75919e;
                U8.b.F(objS);
                list = list7;
                str21 = str29;
                bVar = bVar5;
                c5057b = c5057b14;
                myMonitorInAppBlockService = myMonitorInAppBlockService8;
                set = (Set) objS;
                if (bVar == null) {
                    i8 = -1;
                } else {
                    i8 = AbstractC5078x.f76110a[bVar.ordinal()];
                }
                if (i8 == -1) {
                    zContains = set.contains(x0.f14558X);
                } else if (i8 != 1) {
                    zContains = set.contains(x0.f14561e);
                } else if (i8 != 2) {
                    zContains = set.contains(x0.f14558X);
                } else {
                    if (i8 == 3) {
                        throw new NoWhenBranchMatchedException();
                    }
                    zContains = set.contains(x0.f14557I);
                }
                if (zContains) {
                    if (c5057b != null) {
                        str2 = c5057b.f75973a;
                    } else {
                        str2 = null;
                    }
                    if (str2 != null) {
                        String str210 = c5057b.f75973a;
                        Objects.toString(bVar);
                    } else {
                        if (c5057b != null) {
                            str8 = c5057b.b;
                        } else {
                            str8 = null;
                        }
                        if (str8 != null) {
                            String str211 = c5057b.f75973a;
                            Objects.toString(bVar);
                        }
                    }
                    listExtractAllHandles = myMonitorInAppBlockService.extractAllHandles(c5057b);
                    str3 = CoreConstants.EMPTY_STRING;
                    if (c5057b != null) {
                        str4 = CoreConstants.EMPTY_STRING;
                    } else {
                        str4 = CoreConstants.EMPTY_STRING;
                    }
                    if (c5057b != null) {
                        str3 = str7;
                    }
                    input = AbstractC3102b.l(str4, " ", str3);
                    if (listExtractAllHandles.size() == 1) {
                        patternCompile = Pattern.compile("and \\d+ (more|others?)|\\s\\+\\d+(\\s|$)");
                        m.g(patternCompile, "compile(...)");
                        m.h(input, "input");
                        if (patternCompile.matcher(input).find()) {
                            z11 = true;
                        } else {
                            z11 = false;
                        }
                    } else {
                        z11 = false;
                    }
                    if (listExtractAllHandles.size() < 2) {
                        z12 = true;
                    } else {
                        z12 = true;
                    }
                    if (z12) {
                        break;
                    }
                    i10 = R.string.you_are_watching_a_productive_channel;
                    MyMonitorInAppBlockService myMonitorInAppBlockService9 = myMonitorInAppBlockService;
                    str5 = str21;
                    list2 = list;
                    myMonitorInAppBlockService2 = myMonitorInAppBlockService9;
                    if (bVar == null) {
                        list4 = null;
                        if (bVar == null) {
                            if (c5057b != null) {
                                str9 = c5057b.f75973a;
                            } else {
                                str9 = null;
                            }
                            if (str9 == null) {
                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                return a10;
                            }
                            o oVar6 = p646xf.b.f81953a;
                            channelName = c5057b.f75973a;
                            m.h(channelName, "channelName");
                            str10 = c5057b.b;
                            if (str10 != null) {
                                if (!y.X(channelName, str10, false)) {
                                    str10 = null;
                                }
                                if (str10 != null) {
                                    channelName = str10;
                                }
                            }
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                            C0315z c0315zB6 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                            q8.f75919e = myMonitorInAppBlockService2;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b;
                            q8.f75917Y = channelName;
                            q8.f75918Z = null;
                            q8.f75920l0 = null;
                            q8.f75924p0 = 10;
                            objS = Sv.r.s(c0315zB6, q8);
                            if (objS != aVar) {
                                C5057b c5057b15 = c5057b;
                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                c5057b2 = c5057b15;
                                zBooleanValue = ((Boolean) objS).booleanValue();
                                String str212 = c5057b2.b;
                                int i19 = i10;
                                bVar3 = p567uf.b.f77945I;
                                q8.f75919e = myMonitorInAppBlockService3;
                                q8.f75915I = str5;
                                q8.f75916X = c5057b2;
                                q8.f75917Y = channelName;
                                q8.f75921m0 = zBooleanValue;
                                q8.f75924p0 = 11;
                                c5057b5 = c5057b2;
                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                str16 = channelName;
                                str17 = "getApplicationContext(...)";
                                i11 = i19;
                                str18 = "getString(...)";
                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                    str19 = str5;
                                    str20 = str16;
                                    c5057b6 = c5057b5;
                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                    if (zBooleanValue) {
                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var != null) {
                                            g0Var.c();
                                        }
                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var2 == null) {
                                            z14 = false;
                                        } else {
                                            z14 = false;
                                        }
                                        if (z14) {
                                            break;
                                        }
                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var3 != null) {
                                            g0Var3.g(null);
                                        }
                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                        return a10;
                                    }
                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                    m.g(applicationContext3, str17);
                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                    m.g(string3, str18);
                                    q8.f75919e = null;
                                    q8.f75915I = null;
                                    q8.f75916X = null;
                                    q8.f75917Y = null;
                                    q8.f75924p0 = 12;
                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                    }
                                }
                            }
                        } else {
                            if (c5057b != null) {
                                str9 = c5057b.f75973a;
                            } else {
                                str9 = null;
                            }
                            if (str9 == null) {
                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                return a10;
                            }
                            o oVar7 = p646xf.b.f81953a;
                            channelName = c5057b.f75973a;
                            m.h(channelName, "channelName");
                            str10 = c5057b.b;
                            if (str10 != null) {
                                if (!y.X(channelName, str10, false)) {
                                    str10 = null;
                                }
                                if (str10 != null) {
                                    channelName = str10;
                                }
                            }
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                            C0315z c0315zB7 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                            q8.f75919e = myMonitorInAppBlockService2;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b;
                            q8.f75917Y = channelName;
                            q8.f75918Z = null;
                            q8.f75920l0 = null;
                            q8.f75924p0 = 10;
                            objS = Sv.r.s(c0315zB7, q8);
                            if (objS != aVar) {
                                C5057b c5057b16 = c5057b;
                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                c5057b2 = c5057b16;
                                zBooleanValue = ((Boolean) objS).booleanValue();
                                String str213 = c5057b2.b;
                                int i110 = i10;
                                bVar3 = p567uf.b.f77945I;
                                q8.f75919e = myMonitorInAppBlockService3;
                                q8.f75915I = str5;
                                q8.f75916X = c5057b2;
                                q8.f75917Y = channelName;
                                q8.f75921m0 = zBooleanValue;
                                q8.f75924p0 = 11;
                                c5057b5 = c5057b2;
                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                str16 = channelName;
                                str17 = "getApplicationContext(...)";
                                i11 = i110;
                                str18 = "getString(...)";
                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                    str19 = str5;
                                    str20 = str16;
                                    c5057b6 = c5057b5;
                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                    if (zBooleanValue) {
                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var != null) {
                                            g0Var.c();
                                        }
                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var2 == null) {
                                            z14 = false;
                                        } else {
                                            z14 = false;
                                        }
                                        if (z14) {
                                            break;
                                        }
                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var3 != null) {
                                            g0Var3.g(null);
                                        }
                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                        return a10;
                                    }
                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                    m.g(applicationContext3, str17);
                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                    m.g(string3, str18);
                                    q8.f75919e = null;
                                    q8.f75915I = null;
                                    q8.f75916X = null;
                                    q8.f75917Y = null;
                                    q8.f75924p0 = 12;
                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                    }
                                }
                            }
                        }
                    } else {
                        list4 = null;
                        if (bVar == null) {
                            if (c5057b != null) {
                                str9 = c5057b.f75973a;
                            } else {
                                str9 = null;
                            }
                            if (str9 == null) {
                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                return a10;
                            }
                            o oVar8 = p646xf.b.f81953a;
                            channelName = c5057b.f75973a;
                            m.h(channelName, "channelName");
                            str10 = c5057b.b;
                            if (str10 != null) {
                                if (!y.X(channelName, str10, false)) {
                                    str10 = null;
                                }
                                if (str10 != null) {
                                    channelName = str10;
                                }
                            }
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                            C0315z c0315zB8 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                            q8.f75919e = myMonitorInAppBlockService2;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b;
                            q8.f75917Y = channelName;
                            q8.f75918Z = null;
                            q8.f75920l0 = null;
                            q8.f75924p0 = 10;
                            objS = Sv.r.s(c0315zB8, q8);
                            if (objS != aVar) {
                                C5057b c5057b17 = c5057b;
                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                c5057b2 = c5057b17;
                                zBooleanValue = ((Boolean) objS).booleanValue();
                                String str214 = c5057b2.b;
                                int i111 = i10;
                                bVar3 = p567uf.b.f77945I;
                                q8.f75919e = myMonitorInAppBlockService3;
                                q8.f75915I = str5;
                                q8.f75916X = c5057b2;
                                q8.f75917Y = channelName;
                                q8.f75921m0 = zBooleanValue;
                                q8.f75924p0 = 11;
                                c5057b5 = c5057b2;
                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                str16 = channelName;
                                str17 = "getApplicationContext(...)";
                                i11 = i111;
                                str18 = "getString(...)";
                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                    str19 = str5;
                                    str20 = str16;
                                    c5057b6 = c5057b5;
                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                    if (zBooleanValue) {
                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var != null) {
                                            g0Var.c();
                                        }
                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var2 == null) {
                                            z14 = false;
                                        } else {
                                            z14 = false;
                                        }
                                        if (z14) {
                                            break;
                                        }
                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var3 != null) {
                                            g0Var3.g(null);
                                        }
                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                        return a10;
                                    }
                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                    m.g(applicationContext3, str17);
                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                    m.g(string3, str18);
                                    q8.f75919e = null;
                                    q8.f75915I = null;
                                    q8.f75916X = null;
                                    q8.f75917Y = null;
                                    q8.f75924p0 = 12;
                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                    }
                                }
                            }
                        } else {
                            if (c5057b != null) {
                                str9 = c5057b.f75973a;
                            } else {
                                str9 = null;
                            }
                            if (str9 == null) {
                                p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                                return a10;
                            }
                            o oVar9 = p646xf.b.f81953a;
                            channelName = c5057b.f75973a;
                            m.h(channelName, "channelName");
                            str10 = c5057b.b;
                            if (str10 != null) {
                                if (!y.X(channelName, str10, false)) {
                                    str10 = null;
                                }
                                if (str10 != null) {
                                    channelName = str10;
                                }
                            }
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                            C0315z c0315zB9 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                            q8.f75919e = myMonitorInAppBlockService2;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b;
                            q8.f75917Y = channelName;
                            q8.f75918Z = null;
                            q8.f75920l0 = null;
                            q8.f75924p0 = 10;
                            objS = Sv.r.s(c0315zB9, q8);
                            if (objS != aVar) {
                                C5057b c5057b18 = c5057b;
                                myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                c5057b2 = c5057b18;
                                zBooleanValue = ((Boolean) objS).booleanValue();
                                String str215 = c5057b2.b;
                                int i112 = i10;
                                bVar3 = p567uf.b.f77945I;
                                q8.f75919e = myMonitorInAppBlockService3;
                                q8.f75915I = str5;
                                q8.f75916X = c5057b2;
                                q8.f75917Y = channelName;
                                q8.f75921m0 = zBooleanValue;
                                q8.f75924p0 = 11;
                                c5057b5 = c5057b2;
                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                str16 = channelName;
                                str17 = "getApplicationContext(...)";
                                i11 = i112;
                                str18 = "getString(...)";
                                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                    str19 = str5;
                                    str20 = str16;
                                    c5057b6 = c5057b5;
                                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                    if (zBooleanValue) {
                                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var != null) {
                                            g0Var.c();
                                        }
                                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var2 == null) {
                                            z14 = false;
                                        } else {
                                            z14 = false;
                                        }
                                        if (z14) {
                                            break;
                                        }
                                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                        if (g0Var3 != null) {
                                            g0Var3.g(null);
                                        }
                                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                        return a10;
                                    }
                                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                    m.g(applicationContext3, str17);
                                    string3 = myMonitorInAppBlockService6.getString(i11);
                                    m.g(string3, str18);
                                    q8.f75919e = null;
                                    q8.f75915I = null;
                                    q8.f75916X = null;
                                    q8.f75917Y = null;
                                    q8.f75924p0 = 12;
                                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                    }
                                }
                            }
                        }
                    }
                    return aVar;
                }
                return a10;
            case 2:
                list3 = q8.f75920l0;
                list = q8.f75918Z;
                bVar = (p567uf.b) q8.f75917Y;
                c5057b = q8.f75916X;
                str6 = q8.f75915I;
                myMonitorInAppBlockService = q8.f75919e;
                U8.b.F(objS);
                str12 = (String) objS;
                if (str12 != null) {
                    applicationContext = myMonitorInAppBlockService.getApplicationContext();
                    m.g(applicationContext, "getApplicationContext(...)");
                    string = myMonitorInAppBlockService.getString(R.string.you_are_watching_a_productive_channel);
                    m.g(string, "getString(...)");
                    q8.f75919e = null;
                    q8.f75915I = null;
                    q8.f75916X = null;
                    q8.f75917Y = null;
                    q8.f75918Z = null;
                    q8.f75920l0 = null;
                    q8.f75924p0 = 3;
                    if (myMonitorInAppBlockService.sendChannelToastBroadcast(applicationContext, string, str12, q8) == aVar) {
                        return aVar;
                    }
                    return a10;
                }
                i10 = R.string.you_are_watching_a_productive_channel;
                Objects.toString(list3);
                listExtractAllHandles = list3;
                list2 = list;
                myMonitorInAppBlockService2 = myMonitorInAppBlockService;
                str5 = str6;
                if (bVar == null) {
                    list4 = null;
                    if (bVar == null) {
                        if (c5057b != null) {
                            str9 = c5057b.f75973a;
                        } else {
                            str9 = null;
                        }
                        if (str9 == null) {
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                            return a10;
                        }
                        o oVar10 = p646xf.b.f81953a;
                        channelName = c5057b.f75973a;
                        m.h(channelName, "channelName");
                        str10 = c5057b.b;
                        if (str10 != null) {
                            if (!y.X(channelName, str10, false)) {
                                str10 = null;
                            }
                            if (str10 != null) {
                                channelName = str10;
                            }
                        }
                        p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                        C0315z c0315zB10 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                        q8.f75919e = myMonitorInAppBlockService2;
                        q8.f75915I = str5;
                        q8.f75916X = c5057b;
                        q8.f75917Y = channelName;
                        q8.f75918Z = null;
                        q8.f75920l0 = null;
                        q8.f75924p0 = 10;
                        objS = Sv.r.s(c0315zB10, q8);
                        if (objS != aVar) {
                            C5057b c5057b19 = c5057b;
                            myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                            c5057b2 = c5057b19;
                            zBooleanValue = ((Boolean) objS).booleanValue();
                            String str216 = c5057b2.b;
                            int i113 = i10;
                            bVar3 = p567uf.b.f77945I;
                            q8.f75919e = myMonitorInAppBlockService3;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b2;
                            q8.f75917Y = channelName;
                            q8.f75921m0 = zBooleanValue;
                            q8.f75924p0 = 11;
                            c5057b5 = c5057b2;
                            myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                            str16 = channelName;
                            str17 = "getApplicationContext(...)";
                            i11 = i113;
                            str18 = "getString(...)";
                            if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                str19 = str5;
                                str20 = str16;
                                c5057b6 = c5057b5;
                                myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                if (zBooleanValue) {
                                    g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var != null) {
                                        g0Var.c();
                                    }
                                    g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var2 == null) {
                                        z14 = false;
                                    } else {
                                        z14 = false;
                                    }
                                    if (z14) {
                                        break;
                                    }
                                    g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var3 != null) {
                                        g0Var3.g(null);
                                    }
                                    myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                    myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                    return a10;
                                }
                                applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                m.g(applicationContext3, str17);
                                string3 = myMonitorInAppBlockService6.getString(i11);
                                m.g(string3, str18);
                                q8.f75919e = null;
                                q8.f75915I = null;
                                q8.f75916X = null;
                                q8.f75917Y = null;
                                q8.f75924p0 = 12;
                                if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                }
                                return a10;
                            }
                        }
                    } else {
                        if (c5057b != null) {
                            str9 = c5057b.f75973a;
                        } else {
                            str9 = null;
                        }
                        if (str9 == null) {
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                            return a10;
                        }
                        o oVar11 = p646xf.b.f81953a;
                        channelName = c5057b.f75973a;
                        m.h(channelName, "channelName");
                        str10 = c5057b.b;
                        if (str10 != null) {
                            if (!y.X(channelName, str10, false)) {
                                str10 = null;
                            }
                            if (str10 != null) {
                                channelName = str10;
                            }
                        }
                        p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                        C0315z c0315zB11 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                        q8.f75919e = myMonitorInAppBlockService2;
                        q8.f75915I = str5;
                        q8.f75916X = c5057b;
                        q8.f75917Y = channelName;
                        q8.f75918Z = null;
                        q8.f75920l0 = null;
                        q8.f75924p0 = 10;
                        objS = Sv.r.s(c0315zB11, q8);
                        if (objS != aVar) {
                            C5057b c5057b110 = c5057b;
                            myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                            c5057b2 = c5057b110;
                            zBooleanValue = ((Boolean) objS).booleanValue();
                            String str217 = c5057b2.b;
                            int i114 = i10;
                            bVar3 = p567uf.b.f77945I;
                            q8.f75919e = myMonitorInAppBlockService3;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b2;
                            q8.f75917Y = channelName;
                            q8.f75921m0 = zBooleanValue;
                            q8.f75924p0 = 11;
                            c5057b5 = c5057b2;
                            myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                            str16 = channelName;
                            str17 = "getApplicationContext(...)";
                            i11 = i114;
                            str18 = "getString(...)";
                            if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                str19 = str5;
                                str20 = str16;
                                c5057b6 = c5057b5;
                                myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                if (zBooleanValue) {
                                    g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var != null) {
                                        g0Var.c();
                                    }
                                    g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var2 == null) {
                                        z14 = false;
                                    } else {
                                        z14 = false;
                                    }
                                    if (z14) {
                                        break;
                                    }
                                    g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var3 != null) {
                                        g0Var3.g(null);
                                    }
                                    myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                    myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                    return a10;
                                }
                                applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                m.g(applicationContext3, str17);
                                string3 = myMonitorInAppBlockService6.getString(i11);
                                m.g(string3, str18);
                                q8.f75919e = null;
                                q8.f75915I = null;
                                q8.f75916X = null;
                                q8.f75917Y = null;
                                q8.f75924p0 = 12;
                                if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                }
                                return a10;
                            }
                        }
                    }
                } else {
                    list4 = null;
                    if (bVar == null) {
                        if (c5057b != null) {
                            str9 = c5057b.f75973a;
                        } else {
                            str9 = null;
                        }
                        if (str9 == null) {
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                            return a10;
                        }
                        o oVar12 = p646xf.b.f81953a;
                        channelName = c5057b.f75973a;
                        m.h(channelName, "channelName");
                        str10 = c5057b.b;
                        if (str10 != null) {
                            if (!y.X(channelName, str10, false)) {
                                str10 = null;
                            }
                            if (str10 != null) {
                                channelName = str10;
                            }
                        }
                        p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                        C0315z c0315zB12 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                        q8.f75919e = myMonitorInAppBlockService2;
                        q8.f75915I = str5;
                        q8.f75916X = c5057b;
                        q8.f75917Y = channelName;
                        q8.f75918Z = null;
                        q8.f75920l0 = null;
                        q8.f75924p0 = 10;
                        objS = Sv.r.s(c0315zB12, q8);
                        if (objS != aVar) {
                            C5057b c5057b111 = c5057b;
                            myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                            c5057b2 = c5057b111;
                            zBooleanValue = ((Boolean) objS).booleanValue();
                            String str218 = c5057b2.b;
                            int i115 = i10;
                            bVar3 = p567uf.b.f77945I;
                            q8.f75919e = myMonitorInAppBlockService3;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b2;
                            q8.f75917Y = channelName;
                            q8.f75921m0 = zBooleanValue;
                            q8.f75924p0 = 11;
                            c5057b5 = c5057b2;
                            myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                            str16 = channelName;
                            str17 = "getApplicationContext(...)";
                            i11 = i115;
                            str18 = "getString(...)";
                            if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                str19 = str5;
                                str20 = str16;
                                c5057b6 = c5057b5;
                                myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                if (zBooleanValue) {
                                    g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var != null) {
                                        g0Var.c();
                                    }
                                    g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var2 == null) {
                                        z14 = false;
                                    } else {
                                        z14 = false;
                                    }
                                    if (z14) {
                                        break;
                                    }
                                    g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var3 != null) {
                                        g0Var3.g(null);
                                    }
                                    myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                    myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                    return a10;
                                }
                                applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                m.g(applicationContext3, str17);
                                string3 = myMonitorInAppBlockService6.getString(i11);
                                m.g(string3, str18);
                                q8.f75919e = null;
                                q8.f75915I = null;
                                q8.f75916X = null;
                                q8.f75917Y = null;
                                q8.f75924p0 = 12;
                                if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                }
                                return a10;
                            }
                        }
                    } else {
                        if (c5057b != null) {
                            str9 = c5057b.f75973a;
                        } else {
                            str9 = null;
                        }
                        if (str9 == null) {
                            p118e.b.d(myMonitorInAppBlockService2.logTag1, "Channel name is null");
                            return a10;
                        }
                        o oVar13 = p646xf.b.f81953a;
                        channelName = c5057b.f75973a;
                        m.h(channelName, "channelName");
                        str10 = c5057b.b;
                        if (str10 != null) {
                            if (!y.X(channelName, str10, false)) {
                                str10 = null;
                            }
                            if (str10 != null) {
                                channelName = str10;
                            }
                        }
                        p118e.b.d(myMonitorInAppBlockService2.logTag1, "checkAndShowPMTagOverlay called for ".concat(channelName));
                        C0315z c0315zB13 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41523r.b(channelName);
                        q8.f75919e = myMonitorInAppBlockService2;
                        q8.f75915I = str5;
                        q8.f75916X = c5057b;
                        q8.f75917Y = channelName;
                        q8.f75918Z = null;
                        q8.f75920l0 = null;
                        q8.f75924p0 = 10;
                        objS = Sv.r.s(c0315zB13, q8);
                        if (objS != aVar) {
                            C5057b c5057b112 = c5057b;
                            myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                            c5057b2 = c5057b112;
                            zBooleanValue = ((Boolean) objS).booleanValue();
                            String str219 = c5057b2.b;
                            int i116 = i10;
                            bVar3 = p567uf.b.f77945I;
                            q8.f75919e = myMonitorInAppBlockService3;
                            q8.f75915I = str5;
                            q8.f75916X = c5057b2;
                            q8.f75917Y = channelName;
                            q8.f75921m0 = zBooleanValue;
                            q8.f75924p0 = 11;
                            c5057b5 = c5057b2;
                            myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                            str16 = channelName;
                            str17 = "getApplicationContext(...)";
                            i11 = i116;
                            str18 = "getString(...)";
                            if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                                str19 = str5;
                                str20 = str16;
                                c5057b6 = c5057b5;
                                myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                                if (zBooleanValue) {
                                    g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var != null) {
                                        g0Var.c();
                                    }
                                    g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var2 == null) {
                                        z14 = false;
                                    } else {
                                        z14 = false;
                                    }
                                    if (z14) {
                                        break;
                                    }
                                    g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                                    if (g0Var3 != null) {
                                        g0Var3.g(null);
                                    }
                                    myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                                    myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                                    return a10;
                                }
                                applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                                m.g(applicationContext3, str17);
                                string3 = myMonitorInAppBlockService6.getString(i11);
                                m.g(string3, str18);
                                q8.f75919e = null;
                                q8.f75915I = null;
                                q8.f75916X = null;
                                q8.f75917Y = null;
                                q8.f75924p0 = 12;
                                if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                                }
                                return a10;
                            }
                        }
                    }
                }
                return aVar;
            case 3:
                U8.b.F(objS);
                return a10;
            case 4:
                str11 = q8.f75915I;
                MyMonitorInAppBlockService myMonitorInAppBlockService10 = q8.f75919e;
                U8.b.F(objS);
                r14 = 0;
                myMonitorInAppBlockService4 = myMonitorInAppBlockService10;
                q8.f75919e = r14;
                q8.f75915I = r14;
                q8.f75924p0 = 5;
                if (myMonitorInAppBlockService4.showPMDiversionScreen(str11, q8) == aVar) {
                    return aVar;
                }
                return a10;
            case 5:
                U8.b.F(objS);
                return a10;
            case 6:
                List list8 = q8.f75918Z;
                p567uf.b bVar6 = (p567uf.b) q8.f75917Y;
                C5057b c5057b20 = q8.f75916X;
                String str30 = q8.f75915I;
                MyMonitorInAppBlockService myMonitorInAppBlockService11 = q8.f75919e;
                U8.b.F(objS);
                list5 = list8;
                c5057b3 = c5057b20;
                bVar2 = bVar6;
                r10 = myMonitorInAppBlockService11;
                str5 = str30;
                list4 = null;
                str13 = (String) objS;
                if (str13 == null) {
                    r11 = str13;
                } else if (c5057b3 != null) {
                    r11 = c5057b3.f75973a;
                } else {
                    r11 = list4;
                }
                if (c5057b3 != null) {
                    r12 = c5057b3.b;
                } else {
                    r12 = list4;
                }
                q8.f75919e = r10;
                q8.f75915I = str5;
                q8.f75916X = c5057b3;
                q8.f75917Y = str13;
                q8.f75918Z = list4;
                q8.f75924p0 = 7;
                z13 = true;
                if (r10.checkAndEnterPMChannelEvent(str5, bVar2, r11, r12, list5, q8) != aVar) {
                    r13 = r10;
                    str14 = str5;
                    c5057b4 = c5057b3;
                    str15 = str13;
                    if (str15 != null) {
                        r15 = list4;
                        p118e.b.d(r13.logTag1, "Short allowed from Study Mode channel: ".concat(str15));
                        applicationContext2 = r13.getApplicationContext();
                        m.g(applicationContext2, "getApplicationContext(...)");
                        string2 = r13.getString(R.string.channel_in_study_channels, str15);
                        m.g(string2, "getString(...)");
                        q8.f75919e = r15;
                        q8.f75915I = r15;
                        q8.f75916X = r15;
                        q8.f75917Y = r15;
                        q8.f75924p0 = 8;
                        if (r13.sendChannelToastBroadcast(applicationContext2, string2, str15, q8) != aVar) {
                            return a10;
                        }
                    } else {
                        r15 = list4;
                        p118e.b.d(r13.logTag1, "Show Block Shorts overlay from Productive Mode");
                        if (c5057b4 != null) {
                            r16 = c5057b4.f75974c;
                        } else {
                            r16 = r15;
                        }
                        accessibilityNodeInfo = r13.getAccessibilityNodeInfo(r16, z13);
                        q8.f75919e = r15;
                        q8.f75915I = r15;
                        q8.f75916X = r15;
                        q8.f75917Y = r15;
                        q8.f75924p0 = 9;
                        if (r13.showBLOverlay(str14, false, accessibilityNodeInfo, q8) != aVar) {
                            return a10;
                        }
                    }
                }
                return aVar;
            case 7:
                str15 = (String) q8.f75917Y;
                c5057b4 = q8.f75916X;
                str14 = q8.f75915I;
                MyMonitorInAppBlockService myMonitorInAppBlockService12 = q8.f75919e;
                U8.b.F(objS);
                z13 = true;
                r15 = 0;
                r13 = myMonitorInAppBlockService12;
                if (str15 != null) {
                    r15 = list4;
                    p118e.b.d(r13.logTag1, "Short allowed from Study Mode channel: ".concat(str15));
                    applicationContext2 = r13.getApplicationContext();
                    m.g(applicationContext2, "getApplicationContext(...)");
                    string2 = r13.getString(R.string.channel_in_study_channels, str15);
                    m.g(string2, "getString(...)");
                    q8.f75919e = r15;
                    q8.f75915I = r15;
                    q8.f75916X = r15;
                    q8.f75917Y = r15;
                    q8.f75924p0 = 8;
                    if (r13.sendChannelToastBroadcast(applicationContext2, string2, str15, q8) != aVar) {
                        return aVar;
                    }
                    return a10;
                }
                r15 = list4;
                p118e.b.d(r13.logTag1, "Show Block Shorts overlay from Productive Mode");
                if (c5057b4 != null) {
                    r16 = c5057b4.f75974c;
                } else {
                    r16 = r15;
                }
                accessibilityNodeInfo = r13.getAccessibilityNodeInfo(r16, z13);
                q8.f75919e = r15;
                q8.f75915I = r15;
                q8.f75916X = r15;
                q8.f75917Y = r15;
                q8.f75924p0 = 9;
                if (r13.showBLOverlay(str14, false, accessibilityNodeInfo, q8) != aVar) {
                    return aVar;
                }
                return a10;
            case 8:
                U8.b.F(objS);
                return a10;
            case 9:
                U8.b.F(objS);
                return a10;
            case 10:
                channelName = (String) q8.f75917Y;
                c5057b2 = q8.f75916X;
                String str31 = q8.f75915I;
                MyMonitorInAppBlockService myMonitorInAppBlockService13 = q8.f75919e;
                U8.b.F(objS);
                str5 = str31;
                i10 = R.string.you_are_watching_a_productive_channel;
                myMonitorInAppBlockService3 = myMonitorInAppBlockService13;
                zBooleanValue = ((Boolean) objS).booleanValue();
                String str2110 = c5057b2.b;
                int i117 = i10;
                bVar3 = p567uf.b.f77945I;
                q8.f75919e = myMonitorInAppBlockService3;
                q8.f75915I = str5;
                q8.f75916X = c5057b2;
                q8.f75917Y = channelName;
                q8.f75921m0 = zBooleanValue;
                q8.f75924p0 = 11;
                c5057b5 = c5057b2;
                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                str16 = channelName;
                str17 = "getApplicationContext(...)";
                i11 = i117;
                str18 = "getString(...)";
                if (checkAndEnterPMChannelEvent$default(myMonitorInAppBlockService5, str5, bVar3, str16, null, null, q8, 24, null) != aVar) {
                    str19 = str5;
                    str20 = str16;
                    c5057b6 = c5057b5;
                    myMonitorInAppBlockService6 = myMonitorInAppBlockService5;
                    if (zBooleanValue) {
                        g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                        if (g0Var != null) {
                            g0Var.c();
                        }
                        g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                        if (g0Var2 == null) {
                            z14 = false;
                        } else {
                            z14 = false;
                        }
                        if (z14) {
                            break;
                        }
                        g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                        if (g0Var3 != null) {
                            g0Var3.g(null);
                        }
                        myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                        myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                        return a10;
                    }
                    applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                    m.g(applicationContext3, str17);
                    string3 = myMonitorInAppBlockService6.getString(i11);
                    m.g(string3, str18);
                    q8.f75919e = null;
                    q8.f75915I = null;
                    q8.f75916X = null;
                    q8.f75917Y = null;
                    q8.f75924p0 = 12;
                    if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                    }
                    return a10;
                }
                return aVar;
            case 11:
                boolean z15 = q8.f75921m0;
                String str32 = (String) q8.f75917Y;
                C5057b c5057b21 = q8.f75916X;
                String str33 = q8.f75915I;
                MyMonitorInAppBlockService myMonitorInAppBlockService14 = q8.f75919e;
                U8.b.F(objS);
                zBooleanValue = z15;
                str20 = str32;
                c5057b6 = c5057b21;
                str19 = str33;
                str18 = "getString(...)";
                myMonitorInAppBlockService6 = myMonitorInAppBlockService14;
                i11 = R.string.you_are_watching_a_productive_channel;
                str17 = "getApplicationContext(...)";
                if (zBooleanValue) {
                    g0Var = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                    if (g0Var != null) {
                        g0Var.c();
                    }
                    g0Var2 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                    if (g0Var2 == null) {
                        z14 = false;
                    } else {
                        z14 = false;
                    }
                    if (z14) {
                        break;
                    }
                    g0Var3 = myMonitorInAppBlockService6.currentChannelCategoriseJob;
                    if (g0Var3 != null) {
                        g0Var3.g(null);
                    }
                    myMonitorInAppBlockService6.lastChannelNameCategorise = str20;
                    myMonitorInAppBlockService6.currentChannelCategoriseJob = C.A(myMonitorInAppBlockService6.scope, null, null, new p536tf.T(myMonitorInAppBlockService6, str20, c5057b6, str19, null), 3);
                    return a10;
                }
                applicationContext3 = myMonitorInAppBlockService6.getApplicationContext();
                m.g(applicationContext3, str17);
                string3 = myMonitorInAppBlockService6.getString(i11);
                m.g(string3, str18);
                q8.f75919e = null;
                q8.f75915I = null;
                q8.f75916X = null;
                q8.f75917Y = null;
                q8.f75924p0 = 12;
                if (myMonitorInAppBlockService6.sendChannelToastBroadcast(applicationContext3, string3, str20, q8) == aVar) {
                    return aVar;
                }
                return a10;
            case 12:
                U8.b.F(objS);
                return a10;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [java.lang.Object, java.util.Map] */
    private final boolean hasValidChannelButton(Map<Integer, ? extends List<p567uf.c>> map, int i8, String str) {
        String lowerCase;
        List<p567uf.c> list = map.get(Integer.valueOf(i8));
        if (list == null) {
            return false;
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Locale locale = Locale.ROOT;
        String lowerCase2 = "subscribe".toLowerCase(locale);
        m.g(lowerCase2, "toLowerCase(...)");
        linkedHashSet.add(lowerCase2);
        ?? r10 = V1.B.f33390a;
        String lowerCase3 = "subscribe".toLowerCase(locale);
        m.g(lowerCase3, "toLowerCase(...)");
        Collection collection = (Set) r10.get(lowerCase3);
        linkedHashSet.addAll(collection != null ? collection : w.f71760e);
        Set setS0 = p468qv.m.S0(linkedHashSet);
        List<p567uf.c> list2 = list;
        if ((list2 instanceof Collection) && list2.isEmpty()) {
            return false;
        }
        for (p567uf.c cVar : list2) {
            boolean zC = m.c(cVar.f77952e, "android.widget.Button");
            String lowerCase4 = CoreConstants.EMPTY_STRING;
            String str2 = cVar.f77949a;
            if (str2 != null) {
                lowerCase = str2.toLowerCase(Locale.ROOT);
                m.g(lowerCase, "toLowerCase(...)");
            } else {
                lowerCase = CoreConstants.EMPTY_STRING;
            }
            String str3 = cVar.b;
            if (str3 != null) {
                lowerCase4 = str3.toLowerCase(Locale.ROOT);
                m.g(lowerCase4, "toLowerCase(...)");
            }
            if (zC) {
                return true;
            }
            Set<String> set = setS0;
            if (!(set instanceof Collection) || !set.isEmpty()) {
                for (String str4 : set) {
                    if (lowerCase.equals(str4) || lowerCase4.equals(str4)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final String inferWinningPass(String str, String str2, List<p646xf.d> list) {
        if (str == null && str2 == null) {
            return null;
        }
        for (p646xf.d dVar : list) {
            if (str != null && str.equals(dVar.f81956c)) {
                return "A2";
            }
            if (str != null && str.equals(dVar.f81958e)) {
                return "C";
            }
            if (str != null && str.equals(dVar.f81957d)) {
                return "B";
            }
            if (str2 != null && str2.equals(dVar.b)) {
                return "A1";
            }
        }
        return "LANDSCAPE_OR_UNMATCHED";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:51:0x00f7 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:53:0x0054 A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r3v1, types: [java.lang.Object, java.util.Map] */
    public final boolean isChallengeAppPlayStorePage(Map<Integer, ? extends List<p567uf.c>> map) {
        String lowerCase;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        Locale locale = Locale.ROOT;
        String lowerCase2 = "install".toLowerCase(locale);
        m.g(lowerCase2, "toLowerCase(...)");
        linkedHashSet.add(lowerCase2);
        ?? r10 = V1.B.f33390a;
        String lowerCase3 = "install".toLowerCase(locale);
        m.g(lowerCase3, "toLowerCase(...)");
        Collection collection = (Set) r10.get(lowerCase3);
        linkedHashSet.addAll(collection != null ? collection : w.f71760e);
        Set setS0 = p468qv.m.S0(linkedHashSet);
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it = map.entrySet().iterator();
        boolean z10 = false;
        boolean z11 = false;
        while (it.hasNext()) {
            for (p567uf.c cVar : it.next().getValue()) {
                String str = cVar.f77949a;
                String lowerCase4 = CoreConstants.EMPTY_STRING;
                if (str != null) {
                    lowerCase = str.toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                } else {
                    lowerCase = CoreConstants.EMPTY_STRING;
                }
                String str2 = cVar.b;
                if (str2 != null) {
                    lowerCase4 = str2.toLowerCase(Locale.ROOT);
                    m.g(lowerCase4, "toLowerCase(...)");
                }
                String strL = AbstractC3102b.l(lowerCase, " ", lowerCase4);
                defpackage.c cVar2 = defpackage.c.f48597a;
                List listI = n.I("instagram", "twitter");
                if (!(listI instanceof Collection) || !listI.isEmpty()) {
                    Iterator it2 = listI.iterator();
                    while (it2.hasNext()) {
                        if (Mv.r.b0(strL, (String) it2.next(), false)) {
                            z10 = true;
                            break;
                        }
                    }
                }
                Set set = setS0;
                if (!(set instanceof Collection) || !set.isEmpty()) {
                    Iterator it3 = set.iterator();
                    while (true) {
                        if (it3.hasNext()) {
                            if (m.c(Mv.r.P0(strL).toString(), (String) it3.next())) {
                            }
                        } else if (m.c(Mv.r.P0(lowerCase4).toString(), "install")) {
                        }
                        z11 = true;
                    }
                } else if (m.c(Mv.r.P0(lowerCase4).toString(), "install")) {
                    z11 = true;
                }
            }
        }
        return z10 && z11;
    }

    private final boolean isComposeTestTag(String str) {
        return !Mv.r.b0(str, ":id/", false);
    }

    private final boolean isKeyboardOpen() {
        List<AccessibilityWindowInfo> windows = getWindows();
        if (windows == null) {
            return false;
        }
        for (AccessibilityWindowInfo accessibilityWindowInfo : windows) {
            p118e.b.d("isKeyboardOpen", "windowInfo.type = " + accessibilityWindowInfo.getType() + " windowInfo.isFocused = " + accessibilityWindowInfo.isFocused());
            if (accessibilityWindowInfo.getType() == 2) {
                return true;
            }
        }
        return false;
    }

    private final boolean isProtectedfocusSettingsPage(p567uf.a aVar) {
        Map<Integer, ? extends List<p567uf.c>> map = aVar.b;
        return isfocusDeviceAdminPage(map) || isfocusAppInfoPage(map);
    }

    private final boolean isReelsInProgress(String str) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        try {
            defpackage.c cVar = defpackage.c.f48597a;
            boolean z10 = false;
            if (!defpackage.c.S(str)) {
                if (!defpackage.c.O(str)) {
                    return false;
                }
                ShortFormContentPresent shortFormContentPresentIsShortFormContentPresent$default = isShortFormContentPresent$default(this, str, false, null, "com.instagram.android:id/layout_comment_thread_edittext", 0, true, null, 86, null);
                p118e.b.d("isReelsInProgress", "appId: " + str + " isShortFormContentPresent: " + shortFormContentPresentIsShortFormContentPresent$default);
                if (shortFormContentPresentIsShortFormContentPresent$default.isShortFormContentPresent() && shortFormContentPresentIsShortFormContentPresent$default.isFirstReel()) {
                    z10 = true;
                }
                return z10;
            }
            p youtubeChannelNameAndContentType = getYoutubeChannelNameAndContentType(true);
            C5057b c5057b = (C5057b) youtubeChannelNameAndContentType.f64896e;
            p567uf.b bVar = (p567uf.b) youtubeChannelNameAndContentType.f64894I;
            List<Integer> list = (List) youtubeChannelNameAndContentType.f64895X;
            p118e.b.d("isReelsInProgress", "appId: " + str + " channelInfo: " + c5057b + " contentType: " + bVar);
            boolean zCheckIfScrolled = checkIfScrolled(this.logSpotlightTag, str, list);
            if (bVar == p567uf.b.f77948e && !zCheckIfScrolled) {
                z10 = true;
            }
            return z10;
        } finally {
            accessibilityScrapeCalls++;
            accessibilityScrapeTotalMs = (System.currentTimeMillis() - jCurrentTimeMillis) + accessibilityScrapeTotalMs;
        }
    }

    /* JADX WARN: Code duplicated, block: B:31:0x009f  */
    /* JADX WARN: Code duplicated, block: B:34:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:39:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:42:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:45:0x00d1  */
    /* JADX WARN: Code duplicated, block: B:63:0x00dd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:64:? A[LOOP:2: B:43:0x00cb->B:64:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:65:0x00b6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:66:0x00dd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:? A[LOOP:3: B:32:0x00a3->B:67:?, LOOP_END, SYNTHETIC] */
    private final boolean isfocusAppInfoPage(Map<Integer, ? extends List<p567uf.c>> map) {
        String lowerCase;
        Set set;
        Iterator it;
        Set set2;
        Iterator it2;
        Set setF = U6.d.F("uninstall");
        Set setF2 = U6.d.F("app info");
        Set setF3 = U6.d.F("force stop");
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it3 = map.entrySet().iterator();
        boolean z10 = false;
        boolean z11 = false;
        boolean z12 = false;
        while (it3.hasNext()) {
            for (p567uf.c cVar : it3.next().getValue()) {
                String str = cVar.f77949a;
                String lowerCase2 = CoreConstants.EMPTY_STRING;
                if (str != null) {
                    lowerCase = str.toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                } else {
                    lowerCase = CoreConstants.EMPTY_STRING;
                }
                String str2 = cVar.b;
                if (str2 != null) {
                    lowerCase2 = str2.toLowerCase(Locale.ROOT);
                    m.g(lowerCase2, "toLowerCase(...)");
                }
                String strL = AbstractC3102b.l(lowerCase, " ", lowerCase2);
                Set set3 = setF;
                if (!(set3 instanceof Collection) || !set3.isEmpty()) {
                    Iterator it4 = set3.iterator();
                    while (true) {
                        if (!it4.hasNext()) {
                            set = setF2;
                            if ((set instanceof Collection) || !set.isEmpty()) {
                                it = set.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        set2 = setF3;
                                        if ((set2 instanceof Collection) || !set2.isEmpty()) {
                                            it2 = set2.iterator();
                                            while (it2.hasNext()) {
                                                if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                                }
                                            }
                                        }
                                    } else if (Mv.r.b0(strL, (String) it.next(), false)) {
                                    }
                                }
                            } else {
                                set2 = setF3;
                                if (set2 instanceof Collection) {
                                    it2 = set2.iterator();
                                    while (it2.hasNext()) {
                                        if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                        }
                                    }
                                } else {
                                    it2 = set2.iterator();
                                    while (it2.hasNext()) {
                                        if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                        }
                                    }
                                }
                            }
                        } else if (Mv.r.b0(strL, (String) it4.next(), false)) {
                        }
                        z11 = true;
                        break;
                    }
                }
                set = setF2;
                if (set instanceof Collection) {
                    it = set.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            set2 = setF3;
                            if (set2 instanceof Collection) {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            } else {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            }
                        } else if (Mv.r.b0(strL, (String) it.next(), false)) {
                        }
                    }
                } else {
                    it = set.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            set2 = setF3;
                            if (set2 instanceof Collection) {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            } else {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            }
                        } else if (Mv.r.b0(strL, (String) it.next(), false)) {
                        }
                    }
                }
                z11 = true;
                break;
                if (Mv.r.b0(strL, "focus", false)) {
                    z12 = true;
                }
            }
        }
        if (z11 && z12) {
            z10 = true;
        }
        p118e.b.d("AppInfoDetection", "isfocusAppInfoPage: " + z10 + " (pattern: " + z11 + ", focus: " + z12 + ")");
        return z10;
    }

    /* JADX WARN: Code duplicated, block: B:31:0x009f  */
    /* JADX WARN: Code duplicated, block: B:34:0x00a9  */
    /* JADX WARN: Code duplicated, block: B:39:0x00bd  */
    /* JADX WARN: Code duplicated, block: B:42:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:45:0x00d1  */
    /* JADX WARN: Code duplicated, block: B:63:0x00dd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:64:? A[LOOP:2: B:43:0x00cb->B:64:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:65:0x00b6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:66:0x00dd A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:? A[LOOP:3: B:32:0x00a3->B:67:?, LOOP_END, SYNTHETIC] */
    private final boolean isfocusDeviceAdminPage(Map<Integer, ? extends List<p567uf.c>> map) {
        String lowerCase;
        Set set;
        Iterator it;
        Set set2;
        Iterator it2;
        Set setF = U6.d.F("device admin");
        Set setF2 = U6.d.F("device administrator");
        Set setF3 = U6.d.F("deactivate");
        Iterator<Map.Entry<Integer, ? extends List<p567uf.c>>> it3 = map.entrySet().iterator();
        boolean z10 = false;
        boolean z11 = false;
        boolean z12 = false;
        while (it3.hasNext()) {
            for (p567uf.c cVar : it3.next().getValue()) {
                String str = cVar.f77949a;
                String lowerCase2 = CoreConstants.EMPTY_STRING;
                if (str != null) {
                    lowerCase = str.toLowerCase(Locale.ROOT);
                    m.g(lowerCase, "toLowerCase(...)");
                } else {
                    lowerCase = CoreConstants.EMPTY_STRING;
                }
                String str2 = cVar.b;
                if (str2 != null) {
                    lowerCase2 = str2.toLowerCase(Locale.ROOT);
                    m.g(lowerCase2, "toLowerCase(...)");
                }
                String strL = AbstractC3102b.l(lowerCase, " ", lowerCase2);
                Set set3 = setF;
                if (!(set3 instanceof Collection) || !set3.isEmpty()) {
                    Iterator it4 = set3.iterator();
                    while (true) {
                        if (!it4.hasNext()) {
                            set = setF2;
                            if ((set instanceof Collection) || !set.isEmpty()) {
                                it = set.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        set2 = setF3;
                                        if ((set2 instanceof Collection) || !set2.isEmpty()) {
                                            it2 = set2.iterator();
                                            while (it2.hasNext()) {
                                                if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                                }
                                            }
                                        }
                                    } else if (Mv.r.b0(strL, (String) it.next(), false)) {
                                    }
                                }
                            } else {
                                set2 = setF3;
                                if (set2 instanceof Collection) {
                                    it2 = set2.iterator();
                                    while (it2.hasNext()) {
                                        if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                        }
                                    }
                                } else {
                                    it2 = set2.iterator();
                                    while (it2.hasNext()) {
                                        if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                        }
                                    }
                                }
                            }
                        } else if (Mv.r.b0(strL, (String) it4.next(), false)) {
                        }
                        z11 = true;
                        break;
                    }
                }
                set = setF2;
                if (set instanceof Collection) {
                    it = set.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            set2 = setF3;
                            if (set2 instanceof Collection) {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            } else {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            }
                        } else if (Mv.r.b0(strL, (String) it.next(), false)) {
                        }
                    }
                } else {
                    it = set.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            set2 = setF3;
                            if (set2 instanceof Collection) {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            } else {
                                it2 = set2.iterator();
                                while (it2.hasNext()) {
                                    if (Mv.r.b0(strL, (String) it2.next(), false)) {
                                    }
                                }
                            }
                        } else if (Mv.r.b0(strL, (String) it.next(), false)) {
                        }
                    }
                }
                z11 = true;
                break;
                if (Mv.r.b0(strL, "focus", false)) {
                    z12 = true;
                }
            }
        }
        if (z11 && z12) {
            z10 = true;
        }
        p118e.b.d("DeviceAdminDetection", "isfocusDeviceAdminPage: " + z10 + " (pattern: " + z11 + ", focus: " + z12 + ")");
        return z10;
    }

    private final boolean isScreenInPortraitOrientation() {
        return getResources().getConfiguration().orientation == 1;
    }

    private final ShortFormContentPresent isShortFormContentPresent(String str, boolean z10, String str2, String str3, int i8, boolean z11, String str4) {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        p468qv.u uVar = p468qv.u.f71758e;
        if (rootInActiveWindow == null) {
            return new ShortFormContentPresent(false, false, uVar);
        }
        String strK = AbstractC3102b.k("HashMapGroup-", getRandomString(5));
        p567uf.a aVarConvertToHashMap$default = convertToHashMap$default(this, strK, rootInActiveWindow, null, 4, null);
        Map map = aVarConvertToHashMap$default.f77942a;
        Integer topShortContentMatchingGroupKey$default = getTopShortContentMatchingGroupKey$default(this, map, getShortsReelsText$default(this, false, 1, null), 0, i8, z10, str2, str3, str4, strK, true, aVarConvertToHashMap$default.f77943c, 4, null);
        if (topShortContentMatchingGroupKey$default == null) {
            resetReels();
            printHashMapGroups(strK, aVarConvertToHashMap$default);
            return new ShortFormContentPresent(false, false, uVar);
        }
        List list = (List) map.get(topShortContentMatchingGroupKey$default);
        if (list == null) {
            list = uVar;
        }
        double dIntValue = topShortContentMatchingGroupKey$default.intValue();
        int i10 = aVarConvertToHashMap$default.f77943c;
        if (!(dIntValue >= ((double) i10) * 0.75d)) {
            p118e.b.d("Shorts", "Shorts detected in left half maxRight: " + i10 + " shortsMatchKey: " + topShortContentMatchingGroupKey$default);
            resetReels();
            printHashMapGroups(strK, aVarConvertToHashMap$default);
            return new ShortFormContentPresent(false, false, list);
        }
        if (!z11) {
            return new ShortFormContentPresent(true, false, list);
        }
        List<Integer> listExtractNumbersFromReels = extractNumbersFromReels(strK, (List) map.get(topShortContentMatchingGroupKey$default));
        boolean zCheckIfScrolled = checkIfScrolled(strK, str, listExtractNumbersFromReels);
        p118e.b.d(strK, str + " Shorts blocked: " + zCheckIfScrolled + " " + topShortContentMatchingGroupKey$default + " likeCommentDigest: " + listExtractNumbersFromReels);
        return new ShortFormContentPresent(true, !zCheckIfScrolled, list);
    }

    public static /* synthetic */ ShortFormContentPresent isShortFormContentPresent$default(MyMonitorInAppBlockService myMonitorInAppBlockService, String str, boolean z10, String str2, String str3, int i8, boolean z11, String str4, int i10, Object obj) {
        if ((i10 & 2) != 0) {
            z10 = false;
        }
        if ((i10 & 4) != 0) {
            str2 = null;
        }
        if ((i10 & 8) != 0) {
            str3 = null;
        }
        if ((i10 & 16) != 0) {
            i8 = 3;
        }
        if ((i10 & 32) != 0) {
            z11 = false;
        }
        if ((i10 & 64) != 0) {
            str4 = null;
        }
        return myMonitorInAppBlockService.isShortFormContentPresent(str, z10, str2, str3, i8, z11, str4);
    }

    private final boolean isTimestampPattern(String str, String str2) {
        Iterator it = n.I(new o("^/?\\s*\\d{1,2}:\\d{2}:\\d{2}$"), new o("^/?\\s*\\d{1,2}:\\d{2}$"), new o("^\\d{1,2}:\\d{2}(:\\d{2})?$")).iterator();
        while (it.hasNext()) {
            if (((o) it.next()).e(Mv.r.P0(str).toString())) {
                p118e.b.d(str2, "Detected timestamp pattern: ".concat(str));
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r5v5, types: [java.lang.Object, java.util.Map] */
    private final boolean isYTHomePage(Map<Integer, ? extends List<p567uf.c>> map, boolean z10, String str) {
        p118e.b.d(str, "isYTHomePage called");
        if (!z10) {
            p118e.b.d(str, "isYTHomePage hasYoutubeLogo false");
            return false;
        }
        Jf.a aVar = getBlockFeatureUseCases().f33406B;
        List<String> listB = f.B("explore menu");
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (String str2 : listB) {
            Locale locale = Locale.ROOT;
            String lowerCase = str2.toLowerCase(locale);
            m.g(lowerCase, "toLowerCase(...)");
            linkedHashSet.add(lowerCase);
            ?? r10 = V1.B.f33390a;
            String lowerCase2 = str2.toLowerCase(locale);
            m.g(lowerCase2, "toLowerCase(...)");
            Set set = (Set) r10.get(lowerCase2);
            if (set != null) {
                linkedHashSet.addAll(set);
            }
        }
        Set setS0 = p468qv.m.S0(linkedHashSet);
        for (Map.Entry<Integer, ? extends List<p567uf.c>> entry : map.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            List<p567uf.c> value = entry.getValue();
            if (!(value instanceof Collection) || !value.isEmpty()) {
                for (p567uf.c cVar : value) {
                    String str3 = cVar.f77949a;
                    if (str3 == null && (str3 = cVar.b) == null) {
                        str3 = CoreConstants.EMPTY_STRING;
                    }
                    String lowerCase3 = str3.toLowerCase(Locale.ROOT);
                    m.g(lowerCase3, "toLowerCase(...)");
                    if (setS0.contains(lowerCase3)) {
                        AbstractC4984e0.v(iIntValue, " isYTHomePage Explore Menu Button detected, key: ", str);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final void logDuplicateEvent() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        int iAbs = (int) Math.abs((System.currentTimeMillis() - processedTimeStamp) / ((long) AsyncAppenderBase.DEFAULT_MAX_FLUSH_TIME));
        if (iAbs < 60 || iAbs % 300 != 0) {
            return;
        }
        jSONObject.put("timeStamp", processedTimeStamp);
        jSONObject.put("current_timestamp", System.currentTimeMillis());
        jSONObject.put("diff_in_secs", iAbs);
        p471r0.r.a(getLogAnalyticsEvent(), EnumC4350a.f62911V8, jSONObject, 4);
        p118e.b.d("Debug", "Duplicate event logged " + iAbs);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void logHeartBeat() throws JSONException {
        if (isRunning) {
            blockServiceCheckLastHeartBeat = System.currentTimeMillis();
            logHeartBeatInMixPanel();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object logHeartBeatInDb(long j6, p552tv.e<? super A> eVar) {
        U u3;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        long j10;
        if (eVar instanceof U) {
            u3 = (U) eVar;
            int i8 = u3.f75939Z;
            if ((i8 & Level.ALL_INT) != 0) {
                u3.f75939Z = i8 - Level.ALL_INT;
            } else {
                u3 = new U(this, eVar);
            }
        } else {
            u3 = new U(this, eVar);
        }
        Object obj = u3.f75937X;
        p581uv.a aVar = p581uv.a.f79130e;
        int i10 = u3.f75939Z;
        if (i10 == 0) {
            U8.b.F(obj);
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.lastDbHeartbeat >= j6) {
                g logAppHeartbeat = getLogAppHeartbeat();
                p635x.c cVar = p635x.c.MY_MONITOR_IN_APP_BLOCK_SERVICE;
                u3.f75940e = this;
                u3.f75936I = jCurrentTimeMillis;
                u3.f75939Z = 1;
                if (logAppHeartbeat.a(cVar, u3) == aVar) {
                    return aVar;
                }
                myMonitorInAppBlockService = this;
                j10 = jCurrentTimeMillis;
            }
            return A.f64853a;
        }
        if (i10 != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        j10 = u3.f75936I;
        myMonitorInAppBlockService = u3.f75940e;
        U8.b.F(obj);
        myMonitorInAppBlockService.lastDbHeartbeat = j10;
        return A.f64853a;
    }

    private final void logHeartBeatInMixPanel() throws JSONException {
        long jCurrentTimeMillis = System.currentTimeMillis();
        Long l3 = lastLoggedInMixPanelBS;
        if (l3 != null) {
            m.e(l3);
            if (jCurrentTimeMillis - l3.longValue() < 14400000) {
                return;
            }
        }
        p471r0.u logAnalyticsUserProperty = getLogAnalyticsUserProperty();
        EnumC4350a enumC4350a = EnumC4350a.f62457G0;
        String str = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date(jCurrentTimeMillis));
        m.g(str, "format(...)");
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("source", "block_service");
        logAnalyticsUserProperty.a(enumC4350a, str, jSONObject);
        lastLoggedInMixPanelBS = Long.valueOf(jCurrentTimeMillis);
    }

    private final void monitorPMFloatingButtonService(String str, boolean z10, boolean z11) {
        AbstractC3102b.A(" monitorPMFloatingButtonService recentAppId: ", str, "PMFloatingButtonService");
        try {
            boolean zIsScreenInPortraitOrientation = isScreenInPortraitOrientation();
            if (z10) {
                defpackage.c cVar = defpackage.c.f48597a;
                if ((defpackage.c.R(str) || defpackage.c.M(str)) && zIsScreenInPortraitOrientation && !z11) {
                    if (PMFloatingButtonService.z0) {
                        return;
                    }
                    p118e.b.d("PMFloatingButtonService", "YouTube app detected");
                    Intent intent = new Intent(getApplicationContext(), (Class<?>) PMFloatingButtonService.class);
                    intent.putExtra("PM_FLOATING_BUTTON_APP_ID", str);
                    intent.putExtra("PM_FLOATING_BUTTON_IS_FT_ACTIVE", z11);
                    Context applicationContext = getApplicationContext();
                    m.g(applicationContext, "getApplicationContext(...)");
                    C4385f.b(applicationContext, intent, true);
                    p118e.b.d("PMFloatingButtonService", "Service started");
                    return;
                }
            }
            if (PMFloatingButtonService.z0) {
                Context applicationContext2 = getApplicationContext();
                m.g(applicationContext2, "getApplicationContext(...)");
                applicationContext2.stopService(new Intent(applicationContext2, (Class<?>) PMFloatingButtonService.class));
                p118e.b.d("PMFloatingButtonService", "Service stopped");
            }
        } catch (Exception e9) {
            Js.c.a().b(e9);
            p118e.b.f("monitorPMFloatingButtonService", "Exception: " + e9);
            Js.c.a().b(e9);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:23:0x0075  */
    /* JADX WARN: Code duplicated, block: B:25:0x0085 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:28:0x008e  */
    /* JADX WARN: Code duplicated, block: B:30:0x0091  */
    /* JADX WARN: Code duplicated, block: B:32:0x00aa  */
    /* JADX WARN: Code duplicated, block: B:34:0x00d3  */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x0083 -> B:26:0x0086). Please report as a decompilation issue!!! */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public final java.lang.Object pauseYouTubeVideo(p552tv.e<? super java.lang.Boolean> r10) {
        /*
            Method dump skipped, instruction units count: 216
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorInAppBlockService.pauseYouTubeVideo(tv.e):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean performClickAction(AccessibilityNodeInfo accessibilityNodeInfo) {
        return accessibilityNodeInfo.performAction(16);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean performTextResetAction(AccessibilityNodeInfo accessibilityNodeInfo) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", CoreConstants.EMPTY_STRING);
        return accessibilityNodeInfo.performAction(2097152, bundle);
    }

    public static /* synthetic */ void printHashMap$default(MyMonitorInAppBlockService myMonitorInAppBlockService, String str, Map map, boolean z10, int i8, Object obj) {
        if ((i8 & 4) != 0) {
            z10 = false;
        }
        myMonitorInAppBlockService.printHashMap(str, map, z10);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable printHashMap$lambda$32(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getTop());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable printHashMap$lambda$33(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getBottom());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable printHashMap$lambda$34(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getLeft());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable printHashMap$lambda$35(p567uf.c it) {
        m.h(it, "it");
        return Integer.valueOf(it.f77950c.getRight());
    }

    private final void resetLastProcessedChannelName() {
        lastProcessedChannelName = null;
    }

    private final void resetLastProcessedDomainName() {
        lastProcessedDomainName = null;
    }

    private final void resetReels() {
        lastLikesDigest = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object sendChannelToastBroadcast(Context context, String message, String str, p552tv.e<? super A> eVar) {
        b0 b0Var;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        if (eVar instanceof b0) {
            b0Var = (b0) eVar;
            int i8 = b0Var.f75978Z;
            if ((i8 & Level.ALL_INT) != 0) {
                b0Var.f75978Z = i8 - Level.ALL_INT;
            } else {
                b0Var = new b0(this, eVar);
            }
        } else {
            b0Var = new b0(this, eVar);
        }
        Object obj = b0Var.f75976X;
        p581uv.a aVar = p581uv.a.f79130e;
        int i10 = b0Var.f75978Z;
        if (i10 == 0) {
            U8.b.F(obj);
            String str2 = this.lastToastChannel;
            if (str2 == null || !str2.equals(str)) {
                m.h(context, "context");
                m.h(message, "message");
                Intent intent = new Intent(context, (Class<?>) ToastReceiver.class);
                intent.putExtra("message", message);
                context.sendBroadcast(intent);
                b0Var.f75979e = this;
                b0Var.f75975I = str;
                b0Var.f75978Z = 1;
                if (C.n(100L, b0Var) == aVar) {
                    return aVar;
                }
                myMonitorInAppBlockService = this;
            }
            return A.f64853a;
        }
        if (i10 != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        str = b0Var.f75975I;
        myMonitorInAppBlockService = b0Var.f75979e;
        U8.b.F(obj);
        myMonitorInAppBlockService.lastToastChannel = str;
        return A.f64853a;
    }

    private final void sendUpdateBroadcast(Context context, String appId) {
        m.h(context, "context");
        m.h(appId, "appId");
        Intent intent = new Intent(context, (Class<?>) NotificationAndWidgetUpdateReceiver.class);
        intent.putExtra("appId", appId);
        p118e.b.d(this.logTag, this.logTag1 + " Sending broadcast with appId: " + appId);
        context.sendBroadcast(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object sendWebsiteToastBroadcast(Context context, String message, String str, p552tv.e<? super A> eVar) {
        c0 c0Var;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        if (eVar instanceof c0) {
            c0Var = (c0) eVar;
            int i8 = c0Var.f75985Z;
            if ((i8 & Level.ALL_INT) != 0) {
                c0Var.f75985Z = i8 - Level.ALL_INT;
            } else {
                c0Var = new c0(this, eVar);
            }
        } else {
            c0Var = new c0(this, eVar);
        }
        Object obj = c0Var.f75983X;
        p581uv.a aVar = p581uv.a.f79130e;
        int i10 = c0Var.f75985Z;
        if (i10 == 0) {
            U8.b.F(obj);
            String str2 = this.lastToastWebsite;
            if (str2 == null || !str2.equals(str)) {
                m.h(context, "context");
                m.h(message, "message");
                Intent intent = new Intent(context, (Class<?>) ToastReceiver.class);
                intent.putExtra("message", message);
                context.sendBroadcast(intent);
                c0Var.f75986e = this;
                c0Var.f75982I = str;
                c0Var.f75985Z = 1;
                if (C.n(100L, c0Var) == aVar) {
                    return aVar;
                }
                myMonitorInAppBlockService = this;
            }
            return A.f64853a;
        }
        if (i10 != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        str = c0Var.f75982I;
        myMonitorInAppBlockService = c0Var.f75986e;
        U8.b.F(obj);
        myMonitorInAppBlockService.lastToastWebsite = str;
        return A.f64853a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object showBLOverlay(String str, boolean z10, MyAccessibilityNodeInfoWrapper myAccessibilityNodeInfoWrapper, p552tv.e<? super A> eVar) {
        LinkedHashMap linkedHashMap = AbstractC0108j.f1256a;
        long jCurrentTimeMillis = System.currentTimeMillis();
        LinkedHashMap linkedHashMap2 = AbstractC0108j.f1256a;
        long jLongValue = jCurrentTimeMillis - ((Number) linkedHashMap2.getOrDefault("Call Every X ms", 0L)).longValue();
        A a10 = A.f64853a;
        if (jLongValue > 1000) {
            linkedHashMap2.put("Call Every X ms", Long.valueOf(jCurrentTimeMillis));
            resetReels();
            performBackPress();
            e eVar2 = this.navigationHandler;
            if (eVar2 == null) {
                m.p("navigationHandler");
                throw null;
            }
            Object objInvoke = eVar2.invoke(new C8.r(str, z10, myAccessibilityNodeInfoWrapper), eVar);
            if (objInvoke == p581uv.a.f79130e) {
                return objInvoke;
            }
        }
        return a10;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object showBWOverlay(String str, String str2, boolean z10, p552tv.e<? super A> eVar) {
        resetLastProcessedDomainName();
        e eVar2 = this.navigationHandler;
        if (eVar2 != null) {
            Object objInvoke = eVar2.invoke(new s(str, str2, z10), eVar);
            return objInvoke == p581uv.a.f79130e ? objInvoke : A.f64853a;
        }
        m.p("navigationHandler");
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void showBlockProtectionScreen(R1.a aVar) {
        InterfaceC1567z interfaceC1567z = this.scope;
        Wv.e eVar = M.f24563a;
        C.A(interfaceC1567z, Uv.m.f33347a.f26485l0, null, new d0(this, aVar, null), 2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object showBrowserProductiveModeOverlay(String str, String str2, p552tv.e<? super A> eVar) {
        resetLastProcessedDomainName();
        e eVar2 = this.navigationHandler;
        if (eVar2 != null) {
            Object objInvoke = eVar2.invoke(new C8.y(str, str2), eVar);
            return objInvoke == p581uv.a.f79130e ? objInvoke : A.f64853a;
        }
        m.p("navigationHandler");
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x007c, code lost:
    
        if (r11.invoke(r4, r0) == r1) goto L24;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object showPMBlockScreen(java.lang.String r7, java.lang.String r8, Wa.b r9, p019ai.focus.services.domain.model.MyAccessibilityNodeInfoWrapper r10, p552tv.e<? super kotlin.A> r11) {
        /*
            r6 = this;
            boolean r0 = r11 instanceof p536tf.e0
            if (r0 == 0) goto L13
            r0 = r11
            tf.e0 r0 = (p536tf.e0) r0
            int r1 = r0.f76004n0
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r3 = r1 & r2
            if (r3 == 0) goto L13
            int r1 = r1 - r2
            r0.f76004n0 = r1
            goto L18
        L13:
            tf.e0 r0 = new tf.e0
            r0.<init>(r6, r11)
        L18:
            java.lang.Object r11 = r0.f76002l0
            uv.a r1 = p581uv.a.f79130e
            int r2 = r0.f76004n0
            r3 = 2
            r4 = 1
            if (r2 == 0) goto L40
            if (r2 == r4) goto L32
            if (r2 != r3) goto L2a
            U8.b.F(r11)
            goto L7f
        L2a:
            java.lang.IllegalStateException r7 = new java.lang.IllegalStateException
            java.lang.String r8 = "call to 'resume' before 'invoke' with coroutine"
            r7.<init>(r8)
            throw r7
        L32:
            ai.focus.services.domain.model.MyAccessibilityNodeInfoWrapper r10 = r0.f76000Z
            Wa.b r9 = r0.f75999Y
            java.lang.String r8 = r0.f75998X
            java.lang.String r7 = r0.f75997I
            ai.focus.services.MyMonitorInAppBlockService r2 = r0.f76001e
            U8.b.F(r11)
            goto L62
        L40:
            U8.b.F(r11)
            r6.resetReels()
            java.util.Objects.toString(r10)
            r6.performBackPress()
            r0.f76001e = r6
            r0.f75997I = r7
            r0.f75998X = r8
            r0.f75999Y = r9
            r0.f76000Z = r10
            r0.f76004n0 = r4
            r4 = 100
            java.lang.Object r11 = Pv.C.n(r4, r0)
            if (r11 != r1) goto L61
            goto L7e
        L61:
            r2 = r6
        L62:
            Dv.e r11 = r2.navigationHandler
            r2 = 0
            if (r11 == 0) goto L82
            C8.x r4 = new C8.x
            r4.<init>(r7, r8, r9, r10)
            r0.f76001e = r2
            r0.f75997I = r2
            r0.f75998X = r2
            r0.f75999Y = r2
            r0.f76000Z = r2
            r0.f76004n0 = r3
            java.lang.Object r7 = r11.invoke(r4, r0)
            if (r7 != r1) goto L7f
        L7e:
            return r1
        L7f:
            kotlin.A r7 = kotlin.A.f64853a
            return r7
        L82:
            java.lang.String r7 = "navigationHandler"
            kotlin.jvm.internal.m.p(r7)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorInAppBlockService.showPMBlockScreen(java.lang.String, java.lang.String, Wa.b, ai.focus.services.domain.model.MyAccessibilityNodeInfoWrapper, tv.e):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object showPMDiversionScreen(String str, p552tv.e<? super A> eVar) {
        resetReels();
        e eVar2 = this.navigationHandler;
        if (eVar2 != null) {
            Object objInvoke = eVar2.invoke(new E(str), eVar);
            return objInvoke == p581uv.a.f79130e ? objInvoke : A.f64853a;
        }
        m.p("navigationHandler");
        throw null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:28:0x00f9  */
    /* JADX WARN: Code duplicated, block: B:31:0x0108  */
    /* JADX WARN: Code duplicated, block: B:39:0x0145  */
    /* JADX WARN: Code duplicated, block: B:41:0x0149  */
    /* JADX WARN: Code duplicated, block: B:45:0x0161  */
    /* JADX WARN: Code duplicated, block: B:48:0x0167  */
    /* JADX WARN: Code duplicated, block: B:51:0x0181  */
    /* JADX WARN: Code duplicated, block: B:54:0x018e  */
    /* JADX WARN: Code duplicated, block: B:57:0x01ba  */
    /* JADX WARN: Code duplicated, block: B:60:0x01c2  */
    /* JADX WARN: Code duplicated, block: B:62:0x01d8 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:63:0x01d9  */
    /* JADX WARN: Code duplicated, block: B:65:0x01dd A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    /* JADX WARN: Instruction removed from duplicated block: B:54:0x018e, please report this as an issue */
    public final Object showProductiveModeOverlay(String str, String str2, MyAccessibilityNodeInfoWrapper myAccessibilityNodeInfoWrapper, p552tv.e<? super A> eVar) {
        f0 f0Var;
        String str3;
        String str4;
        MyMonitorInAppBlockService myMonitorInAppBlockService;
        boolean zBooleanValue;
        Object objS;
        String str5;
        boolean z10;
        MyMonitorInAppBlockService myMonitorInAppBlockService2;
        String str6;
        String str7;
        boolean zBooleanValue2;
        String str8;
        MyAccessibilityNodeInfoWrapper myAccessibilityNodeInfoWrapper2;
        String str9;
        String str10;
        MyMonitorInAppBlockService myMonitorInAppBlockService3;
        MyAccessibilityNodeInfoWrapper myAccessibilityNodeInfoWrapper3;
        String str11;
        String str12;
        MyMonitorInAppBlockService myMonitorInAppBlockService4;
        e eVar2;
        C8.A a10;
        MyAccessibilityNodeInfoWrapper myAccessibilityNodeInfoWrapper4;
        String str13;
        String str14;
        MyMonitorInAppBlockService myMonitorInAppBlockService5;
        e eVar3;
        C8.z zVar;
        if (eVar instanceof f0) {
            f0Var = (f0) eVar;
            int i8 = f0Var.f76017o0;
            if ((i8 & Level.ALL_INT) != 0) {
                f0Var.f76017o0 = i8 - Level.ALL_INT;
            } else {
                f0Var = new f0(this, eVar);
            }
        } else {
            f0Var = new f0(this, eVar);
        }
        Object objS2 = f0Var.f76015m0;
        p581uv.a aVar = p581uv.a.f79130e;
        int i10 = f0Var.f76017o0;
        A a11 = A.f64853a;
        switch (i10) {
            case 0:
                U8.b.F(objS2);
                p118e.b.d(this.logTag, "showProductiveModeOverlay checkAndShowPMTagOverlay called for " + str2);
                C0315z c0315zB = getProductiveModeUseCases().f41523r.b(str2);
                f0Var.f76013e = this;
                f0Var.f76009I = str;
                f0Var.f76010X = str2;
                f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                f0Var.f76012Z = "showProductiveModeOverlay";
                f0Var.f76017o0 = 1;
                objS2 = Sv.r.s(c0315zB, f0Var);
                if (objS2 != aVar) {
                    str3 = str;
                    str4 = "showProductiveModeOverlay";
                    myMonitorInAppBlockService = this;
                    zBooleanValue = ((Boolean) objS2).booleanValue();
                    p118e.b.d(myMonitorInAppBlockService.logTag, str4 + " User is watching Productive Channel: " + zBooleanValue);
                    C0315z c0315zA = myMonitorInAppBlockService.getProductiveModeUseCases().f41525s.a(str2);
                    f0Var.f76013e = myMonitorInAppBlockService;
                    f0Var.f76009I = str3;
                    f0Var.f76010X = str2;
                    f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                    f0Var.f76012Z = str4;
                    f0Var.f76014l0 = zBooleanValue;
                    f0Var.f76017o0 = 2;
                    objS = Sv.r.s(c0315zA, f0Var);
                    if (objS != aVar) {
                        String str15 = str2;
                        str5 = str4;
                        z10 = zBooleanValue;
                        objS2 = objS;
                        myMonitorInAppBlockService2 = myMonitorInAppBlockService;
                        str6 = str3;
                        str7 = str15;
                        zBooleanValue2 = ((Boolean) objS2).booleanValue();
                        if (z10 || m.c(str7, lastProcessedChannelName)) {
                            if (zBooleanValue2) {
                                Vn.j jVar = myMonitorInAppBlockService2.getProductiveModeUseCases().f41487U;
                                f0Var.f76013e = myMonitorInAppBlockService2;
                                f0Var.f76009I = str6;
                                f0Var.f76010X = str7;
                                f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                                f0Var.f76012Z = str5;
                                f0Var.f76017o0 = 5;
                                objS2 = jVar.v(str6, f0Var);
                                if (objS2 != aVar) {
                                    str8 = str5;
                                    myAccessibilityNodeInfoWrapper2 = myAccessibilityNodeInfoWrapper;
                                    str9 = str7;
                                    str10 = str6;
                                    myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                                    if (((Boolean) objS2).booleanValue()) {
                                        p118e.b.d(myMonitorInAppBlockService3.logTag, str8 + " User is watching whitelisted Channel");
                                        lastProcessedChannelName = str9;
                                        f0Var.f76013e = myMonitorInAppBlockService3;
                                        f0Var.f76009I = str10;
                                        f0Var.f76010X = str9;
                                        f0Var.f76011Y = myAccessibilityNodeInfoWrapper2;
                                        f0Var.f76012Z = null;
                                        f0Var.f76017o0 = 6;
                                        if (myMonitorInAppBlockService3.pauseYouTubeVideo(f0Var) != aVar) {
                                            myAccessibilityNodeInfoWrapper4 = myAccessibilityNodeInfoWrapper2;
                                            str13 = str9;
                                            str14 = str10;
                                            myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                            eVar3 = myMonitorInAppBlockService5.navigationHandler;
                                            if (eVar3 == null) {
                                                m.p("navigationHandler");
                                                throw null;
                                            }
                                            zVar = new C8.z(str14, str13, myAccessibilityNodeInfoWrapper4);
                                            f0Var.f76013e = null;
                                            f0Var.f76009I = null;
                                            f0Var.f76010X = null;
                                            f0Var.f76011Y = null;
                                            f0Var.f76017o0 = 7;
                                            if (eVar3.invoke(zVar, f0Var) == aVar) {
                                            }
                                        }
                                    }
                                }
                            }
                            return a11;
                        }
                        p118e.b.d(myMonitorInAppBlockService2.logTag, str5 + " User is watching Productive Channel");
                        lastProcessedChannelName = str7;
                        f0Var.f76013e = myMonitorInAppBlockService2;
                        f0Var.f76009I = str6;
                        f0Var.f76010X = str7;
                        f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                        f0Var.f76012Z = null;
                        f0Var.f76017o0 = 3;
                        if (myMonitorInAppBlockService2.pauseYouTubeVideo(f0Var) != aVar) {
                            myAccessibilityNodeInfoWrapper3 = myAccessibilityNodeInfoWrapper;
                            str11 = str7;
                            str12 = str6;
                            myMonitorInAppBlockService4 = myMonitorInAppBlockService2;
                            eVar2 = myMonitorInAppBlockService4.navigationHandler;
                            if (eVar2 != null) {
                                m.p("navigationHandler");
                                throw null;
                            }
                            if (str11 == null) {
                                str11 = CoreConstants.EMPTY_STRING;
                            }
                            a10 = new C8.A(str12, str11, myAccessibilityNodeInfoWrapper3);
                            f0Var.f76013e = null;
                            f0Var.f76009I = null;
                            f0Var.f76010X = null;
                            f0Var.f76011Y = null;
                            f0Var.f76017o0 = 4;
                            if (eVar2.invoke(a10, f0Var) != aVar) {
                                return a11;
                            }
                        }
                    }
                }
                return aVar;
            case 1:
                str4 = f0Var.f76012Z;
                myAccessibilityNodeInfoWrapper = f0Var.f76011Y;
                str2 = f0Var.f76010X;
                str3 = f0Var.f76009I;
                myMonitorInAppBlockService = f0Var.f76013e;
                U8.b.F(objS2);
                zBooleanValue = ((Boolean) objS2).booleanValue();
                p118e.b.d(myMonitorInAppBlockService.logTag, str4 + " User is watching Productive Channel: " + zBooleanValue);
                C0315z c0315zA2 = myMonitorInAppBlockService.getProductiveModeUseCases().f41525s.a(str2);
                f0Var.f76013e = myMonitorInAppBlockService;
                f0Var.f76009I = str3;
                f0Var.f76010X = str2;
                f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                f0Var.f76012Z = str4;
                f0Var.f76014l0 = zBooleanValue;
                f0Var.f76017o0 = 2;
                objS = Sv.r.s(c0315zA2, f0Var);
                if (objS != aVar) {
                    String str16 = str2;
                    str5 = str4;
                    z10 = zBooleanValue;
                    objS2 = objS;
                    myMonitorInAppBlockService2 = myMonitorInAppBlockService;
                    str6 = str3;
                    str7 = str16;
                    zBooleanValue2 = ((Boolean) objS2).booleanValue();
                    if (z10) {
                        break;
                    }
                    if (zBooleanValue2) {
                        Vn.j jVar2 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41487U;
                        f0Var.f76013e = myMonitorInAppBlockService2;
                        f0Var.f76009I = str6;
                        f0Var.f76010X = str7;
                        f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                        f0Var.f76012Z = str5;
                        f0Var.f76017o0 = 5;
                        objS2 = jVar2.v(str6, f0Var);
                        if (objS2 != aVar) {
                            str8 = str5;
                            myAccessibilityNodeInfoWrapper2 = myAccessibilityNodeInfoWrapper;
                            str9 = str7;
                            str10 = str6;
                            myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                            if (((Boolean) objS2).booleanValue()) {
                                p118e.b.d(myMonitorInAppBlockService3.logTag, str8 + " User is watching whitelisted Channel");
                                lastProcessedChannelName = str9;
                                f0Var.f76013e = myMonitorInAppBlockService3;
                                f0Var.f76009I = str10;
                                f0Var.f76010X = str9;
                                f0Var.f76011Y = myAccessibilityNodeInfoWrapper2;
                                f0Var.f76012Z = null;
                                f0Var.f76017o0 = 6;
                                if (myMonitorInAppBlockService3.pauseYouTubeVideo(f0Var) != aVar) {
                                    myAccessibilityNodeInfoWrapper4 = myAccessibilityNodeInfoWrapper2;
                                    str13 = str9;
                                    str14 = str10;
                                    myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                    eVar3 = myMonitorInAppBlockService5.navigationHandler;
                                    if (eVar3 == null) {
                                        m.p("navigationHandler");
                                        throw null;
                                    }
                                    zVar = new C8.z(str14, str13, myAccessibilityNodeInfoWrapper4);
                                    f0Var.f76013e = null;
                                    f0Var.f76009I = null;
                                    f0Var.f76010X = null;
                                    f0Var.f76011Y = null;
                                    f0Var.f76017o0 = 7;
                                    if (eVar3.invoke(zVar, f0Var) == aVar) {
                                    }
                                }
                            }
                        }
                    }
                    return a11;
                }
                return aVar;
            case 2:
                z10 = f0Var.f76014l0;
                str5 = f0Var.f76012Z;
                myAccessibilityNodeInfoWrapper = f0Var.f76011Y;
                str7 = f0Var.f76010X;
                str6 = f0Var.f76009I;
                myMonitorInAppBlockService2 = f0Var.f76013e;
                U8.b.F(objS2);
                zBooleanValue2 = ((Boolean) objS2).booleanValue();
                if (z10) {
                    break;
                }
                if (zBooleanValue2) {
                    Vn.j jVar3 = myMonitorInAppBlockService2.getProductiveModeUseCases().f41487U;
                    f0Var.f76013e = myMonitorInAppBlockService2;
                    f0Var.f76009I = str6;
                    f0Var.f76010X = str7;
                    f0Var.f76011Y = myAccessibilityNodeInfoWrapper;
                    f0Var.f76012Z = str5;
                    f0Var.f76017o0 = 5;
                    objS2 = jVar3.v(str6, f0Var);
                    if (objS2 != aVar) {
                        str8 = str5;
                        myAccessibilityNodeInfoWrapper2 = myAccessibilityNodeInfoWrapper;
                        str9 = str7;
                        str10 = str6;
                        myMonitorInAppBlockService3 = myMonitorInAppBlockService2;
                        if (((Boolean) objS2).booleanValue()) {
                            p118e.b.d(myMonitorInAppBlockService3.logTag, str8 + " User is watching whitelisted Channel");
                            lastProcessedChannelName = str9;
                            f0Var.f76013e = myMonitorInAppBlockService3;
                            f0Var.f76009I = str10;
                            f0Var.f76010X = str9;
                            f0Var.f76011Y = myAccessibilityNodeInfoWrapper2;
                            f0Var.f76012Z = null;
                            f0Var.f76017o0 = 6;
                            if (myMonitorInAppBlockService3.pauseYouTubeVideo(f0Var) != aVar) {
                                myAccessibilityNodeInfoWrapper4 = myAccessibilityNodeInfoWrapper2;
                                str13 = str9;
                                str14 = str10;
                                myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                                eVar3 = myMonitorInAppBlockService5.navigationHandler;
                                if (eVar3 == null) {
                                    m.p("navigationHandler");
                                    throw null;
                                }
                                zVar = new C8.z(str14, str13, myAccessibilityNodeInfoWrapper4);
                                f0Var.f76013e = null;
                                f0Var.f76009I = null;
                                f0Var.f76010X = null;
                                f0Var.f76011Y = null;
                                f0Var.f76017o0 = 7;
                                if (eVar3.invoke(zVar, f0Var) == aVar) {
                                }
                            }
                        }
                    }
                    return aVar;
                }
                return a11;
            case 3:
                myAccessibilityNodeInfoWrapper3 = f0Var.f76011Y;
                str11 = f0Var.f76010X;
                str12 = f0Var.f76009I;
                myMonitorInAppBlockService4 = f0Var.f76013e;
                U8.b.F(objS2);
                eVar2 = myMonitorInAppBlockService4.navigationHandler;
                if (eVar2 != null) {
                    m.p("navigationHandler");
                    throw null;
                }
                if (str11 == null) {
                    str11 = CoreConstants.EMPTY_STRING;
                }
                a10 = new C8.A(str12, str11, myAccessibilityNodeInfoWrapper3);
                f0Var.f76013e = null;
                f0Var.f76009I = null;
                f0Var.f76010X = null;
                f0Var.f76011Y = null;
                f0Var.f76017o0 = 4;
                if (eVar2.invoke(a10, f0Var) != aVar) {
                    return aVar;
                }
                return a11;
            case 4:
                U8.b.F(objS2);
                return a11;
            case 5:
                str8 = f0Var.f76012Z;
                myAccessibilityNodeInfoWrapper2 = f0Var.f76011Y;
                str9 = f0Var.f76010X;
                str10 = f0Var.f76009I;
                myMonitorInAppBlockService3 = f0Var.f76013e;
                U8.b.F(objS2);
                if (((Boolean) objS2).booleanValue()) {
                    p118e.b.d(myMonitorInAppBlockService3.logTag, str8 + " User is watching whitelisted Channel");
                    lastProcessedChannelName = str9;
                    f0Var.f76013e = myMonitorInAppBlockService3;
                    f0Var.f76009I = str10;
                    f0Var.f76010X = str9;
                    f0Var.f76011Y = myAccessibilityNodeInfoWrapper2;
                    f0Var.f76012Z = null;
                    f0Var.f76017o0 = 6;
                    if (myMonitorInAppBlockService3.pauseYouTubeVideo(f0Var) != aVar) {
                        myAccessibilityNodeInfoWrapper4 = myAccessibilityNodeInfoWrapper2;
                        str13 = str9;
                        str14 = str10;
                        myMonitorInAppBlockService5 = myMonitorInAppBlockService3;
                        eVar3 = myMonitorInAppBlockService5.navigationHandler;
                        if (eVar3 == null) {
                            m.p("navigationHandler");
                            throw null;
                        }
                        zVar = new C8.z(str14, str13, myAccessibilityNodeInfoWrapper4);
                        f0Var.f76013e = null;
                        f0Var.f76009I = null;
                        f0Var.f76010X = null;
                        f0Var.f76011Y = null;
                        f0Var.f76017o0 = 7;
                        if (eVar3.invoke(zVar, f0Var) == aVar) {
                        }
                    }
                    return aVar;
                }
                return a11;
            case 6:
                myAccessibilityNodeInfoWrapper4 = f0Var.f76011Y;
                str13 = f0Var.f76010X;
                str14 = f0Var.f76009I;
                myMonitorInAppBlockService5 = f0Var.f76013e;
                U8.b.F(objS2);
                eVar3 = myMonitorInAppBlockService5.navigationHandler;
                if (eVar3 == null) {
                    m.p("navigationHandler");
                    throw null;
                }
                zVar = new C8.z(str14, str13, myAccessibilityNodeInfoWrapper4);
                f0Var.f76013e = null;
                f0Var.f76009I = null;
                f0Var.f76010X = null;
                f0Var.f76011Y = null;
                f0Var.f76017o0 = 7;
                if (eVar3.invoke(zVar, f0Var) == aVar) {
                    return aVar;
                }
                return a11;
            case 7:
                U8.b.F(objS2);
                return a11;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
    }

    private final void startBlockService() {
        defpackage.c cVar = defpackage.c.f48597a;
        p118e.b.d(this.logTag, "startBlockService called");
        C.A(this.scope, null, null, new p536tf.g0(this, null), 3);
    }

    private final void stopBlockService() {
        C.j(this.scope, null);
    }

    private final void writeYTCapture(String str, String str2) throws IOException {
        File file = new File(getExternalFilesDir(null), YT_CAPTURE_DIR);
        file.mkdirs();
        File file2 = new File(file, p148f0.d.l(str, ".json"));
        Av.j.Q(file2, str2);
        p118e.b.d(YT_CAPTURE_LOG_TAG, "capture '" + str + "' written to " + file2.getAbsolutePath());
    }

    public final void closeCurrentTab(String appId) {
        m.h(appId, "appId");
        p118e.b.d("PerformAccessibilityActionReceiver", "closeCurrentTab called for appId: ".concat(appId));
        defpackage.c cVar = defpackage.c.f48597a;
        String str = (String) defpackage.c.d(appId).f64869e;
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            return;
        }
        kotlin.jvm.internal.w wVar = new kotlin.jvm.internal.w();
        C.A(this.scope, null, null, new K(wVar, rootInActiveWindow, str, this, null), 3);
        if (wVar.f64882e) {
            return;
        }
        p118e.b.d("PerformAccessibilityActionReceiver", "Failed to close tab after 10 attempts");
    }

    public final void closeFloatingYoutubeVideoWhenMinimised() {
        p118e.b.d("closeFloatingYoutubeVideoWhenMinimised", "closeFloatingYoutubeVideoWhenMinimised called");
        C.A(this.scope, null, null, new L(this, n.I("com.google.android.youtube:id/floaty_close_button", "com.google.android.youtube:id/modern_miniplayer_close"), null), 3);
    }

    public final p567uf.a convertToHashMap(String logTag, AccessibilityNodeInfo accessibilityNodeInfo, p567uf.j verticalGroupAnchor) {
        m.h(logTag, "logTag");
        m.h(verticalGroupAnchor, "verticalGroupAnchor");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        if (accessibilityNodeInfo == null) {
            v vVar = v.f71759e;
            return new p567uf.a(vVar, vVar, 0, false);
        }
        LinkedList linkedList = new LinkedList();
        linkedList.add(accessibilityNodeInfo);
        int iMin = 0;
        boolean z10 = false;
        while (!linkedList.isEmpty()) {
            AccessibilityNodeInfo accessibilityNodeInfo2 = (AccessibilityNodeInfo) linkedList.poll();
            if (accessibilityNodeInfo2 != null) {
                if (m.c(accessibilityNodeInfo2.getViewIdResourceName(), "com.google.android.youtube:id/watch_panel_scrim")) {
                    p118e.b.d(logTag, "Found watch_panel_scrim - confirmed video page");
                    z10 = true;
                }
                if (m.c(accessibilityNodeInfo2.getViewIdResourceName(), "com.google.android.youtube:id/logo")) {
                    p118e.b.d(logTag, "Found youtube logo ");
                }
                NodeBounds nodeBounds = getNodeBounds(accessibilityNodeInfo2, verticalGroupAnchor);
                iMin = Math.max(iMin, nodeBounds.getRight());
                CharSequence text = accessibilityNodeInfo2.getText();
                String string = text != null ? text.toString() : null;
                CharSequence contentDescription = accessibilityNodeInfo2.getContentDescription();
                String string2 = contentDescription != null ? contentDescription.toString() : null;
                CharSequence className = accessibilityNodeInfo2.getClassName();
                p567uf.c cVar = new p567uf.c(string, string2, accessibilityNodeInfo2.getViewIdResourceName(), nodeBounds, className != null ? className.toString() : null, accessibilityNodeInfo2.isEnabled(), accessibilityNodeInfo2.isVisibleToUser());
                Integer numValueOf = Integer.valueOf(nodeBounds.getVerticalMidpoint());
                Object arrayList = linkedHashMap.get(numValueOf);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    linkedHashMap.put(numValueOf, arrayList);
                }
                ((List) arrayList).add(cVar);
                Integer numValueOf2 = Integer.valueOf(nodeBounds.getHorizontalMidpoint());
                Object arrayList2 = linkedHashMap2.get(numValueOf2);
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    linkedHashMap2.put(numValueOf2, arrayList2);
                }
                ((List) arrayList2).add(cVar);
                int childCount = accessibilityNodeInfo2.getChildCount();
                for (int i8 = 0; i8 < childCount; i8++) {
                    AccessibilityNodeInfo child = accessibilityNodeInfo2.getChild(i8);
                    if (child != null) {
                        linkedList.add(child);
                    }
                }
            }
        }
        Map<Integer, List<p567uf.c>> treeMap = new TreeMap<>(linkedHashMap);
        TreeMap treeMap2 = new TreeMap(linkedHashMap2);
        int iOrdinal = verticalGroupAnchor.ordinal();
        if (iOrdinal == 0) {
            treeMap = getMergedHashMap(treeMap);
        } else if (iOrdinal != 1) {
            throw new NoWhenBranchMatchedException();
        }
        Map<Integer, List<p567uf.c>> mergedHashMap = getMergedHashMap(treeMap2);
        int i10 = getResources().getDisplayMetrics().widthPixels;
        if (i10 > 0) {
            iMin = Math.min(iMin, i10);
        }
        return new p567uf.a(treeMap, mergedHashMap, iMin, z10);
    }

    public final MyAccessibilityNodeInfoWrapper getAccessibilityNodeInfo(List<p567uf.c> list, boolean z10) {
        if (list == null) {
            return new MyAccessibilityNodeInfoWrapper(p468qv.u.f71758e);
        }
        ArrayList arrayList = new ArrayList();
        for (p567uf.c cVar : p468qv.m.F0(list, z10 ? p317lc.i.l(new C4896a(8), new C4896a(9)) : p317lc.i.l(new C4896a(10), new C4896a(11)))) {
            String str = cVar.f77949a;
            String str2 = cVar.f77952e;
            arrayList.add(new MyAccessibilityNodeInfo(str, cVar.b, str2 != null ? str2.toString() : null, cVar.f77950c, cVar.f77951d, cVar.f77953f, cVar.f77954g));
        }
        return new MyAccessibilityNodeInfoWrapper(arrayList);
    }

    public final C0291a getAppUsageStatsUseCases() {
        C0291a c0291a = this.appUsageStatsUseCases;
        if (c0291a != null) {
            return c0291a;
        }
        m.p("appUsageStatsUseCases");
        throw null;
    }

    public final C2082a getBlockFeatureUseCases() {
        C2082a c2082a = this.blockFeatureUseCases;
        if (c2082a != null) {
            return c2082a;
        }
        m.p("blockFeatureUseCases");
        throw null;
    }

    public final a getBlockProtectionWindowFactory() {
        a aVar = this.blockProtectionWindowFactory;
        if (aVar != null) {
            return aVar;
        }
        m.p("blockProtectionWindowFactory");
        throw null;
    }

    public final C5518e getBlockWebsiteUseCases() {
        C5518e c5518e = this.blockWebsiteUseCases;
        if (c5518e != null) {
            return c5518e;
        }
        m.p("blockWebsiteUseCases");
        throw null;
    }

    public final V getC68PendingAlarmGuard() {
        V v4 = this.c68PendingAlarmGuard;
        if (v4 != null) {
            return v4;
        }
        m.p("c68PendingAlarmGuard");
        throw null;
    }

    public final b getContentBlockWindowFactory() {
        b bVar = this.contentBlockWindowFactory;
        if (bVar != null) {
            return bVar;
        }
        m.p("contentBlockWindowFactory");
        throw null;
    }

    public final p638x3.a getDistractingAppsUseCases() {
        p638x3.a aVar = this.distractingAppsUseCases;
        if (aVar != null) {
            return aVar;
        }
        m.p("distractingAppsUseCases");
        throw null;
    }

    public final T getFocusTimerUseCases() {
        T t2 = this.focusTimerUseCases;
        if (t2 != null) {
            return t2;
        }
        m.p("focusTimerUseCases");
        throw null;
    }

    public final C0063b getFtOverlayStateManager() {
        C0063b c0063b = this.ftOverlayStateManager;
        if (c0063b != null) {
            return c0063b;
        }
        m.p("ftOverlayStateManager");
        throw null;
    }

    public final C4887v getGetInstagramChallengeActive() {
        C4887v c4887v = this.getInstagramChallengeActive;
        if (c4887v != null) {
            return c4887v;
        }
        m.p("getInstagramChallengeActive");
        throw null;
    }

    public final c getGetLauncherAppId() {
        c cVar = this.getLauncherAppId;
        if (cVar != null) {
            return cVar;
        }
        m.p("getLauncherAppId");
        throw null;
    }

    public final G.d getGetSettingsApps() {
        G.d dVar = this.getSettingsApps;
        if (dVar != null) {
            return dVar;
        }
        m.p("getSettingsApps");
        throw null;
    }

    public final p595vf.b getHandleEducationAppNotification() {
        p595vf.b bVar = this.handleEducationAppNotification;
        if (bVar != null) {
            return bVar;
        }
        m.p("handleEducationAppNotification");
        throw null;
    }

    public final p471r0.r getLogAnalyticsEvent() {
        p471r0.r rVar = this.logAnalyticsEvent;
        if (rVar != null) {
            return rVar;
        }
        m.p("logAnalyticsEvent");
        throw null;
    }

    public final p471r0.u getLogAnalyticsUserProperty() {
        p471r0.u uVar = this.logAnalyticsUserProperty;
        if (uVar != null) {
            return uVar;
        }
        m.p("logAnalyticsUserProperty");
        throw null;
    }

    public final g getLogAppHeartbeat() {
        g gVar = this.logAppHeartbeat;
        if (gVar != null) {
            return gVar;
        }
        m.p("logAppHeartbeat");
        throw null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final Map<Integer, List<p567uf.c>> getMergedHashMap(Map<Integer, ? extends List<p567uf.c>> groupMap) {
        p468qv.u uVar;
        m.h(groupMap, "groupMap");
        if (groupMap.size() < 2) {
            return groupMap;
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        List listO0 = p468qv.m.O0(groupMap.keySet());
        listO0.toString();
        ArrayList arrayList = new ArrayList();
        arrayList.add(listO0.get(0));
        int size = listO0.size();
        int i8 = 1;
        while (true) {
            uVar = p468qv.u.f71758e;
            if (i8 >= size) {
                break;
            }
            if (((Number) listO0.get(i8)).intValue() - ((Number) listO0.get(i8 - 1)).intValue() <= 10) {
                arrayList.add(listO0.get(i8));
            } else {
                int iIntValue = ((Number) p468qv.m.j0(arrayList)).intValue();
                ArrayList arrayList2 = new ArrayList();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    List list = (List) groupMap.get(Integer.valueOf(((Number) it.next()).intValue()));
                    arrayList2.addAll(list != null ? list : uVar);
                }
                linkedHashMap.put(Integer.valueOf(iIntValue), arrayList2);
                arrayList.toString();
                arrayList = n.K(listO0.get(i8));
            }
            i8++;
        }
        if (!arrayList.isEmpty()) {
            int iIntValue2 = ((Number) p468qv.m.j0(arrayList)).intValue();
            ArrayList arrayList3 = new ArrayList();
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                List list2 = (List) groupMap.get(Integer.valueOf(((Number) it2.next()).intValue()));
                arrayList3.addAll(list2 != null ? list2 : uVar);
            }
            linkedHashMap.put(Integer.valueOf(iIntValue2), arrayList3);
            arrayList.toString();
        }
        p468qv.m.O0(linkedHashMap.keySet()).toString();
        return linkedHashMap;
    }

    public final C0083w getMonitorServiceStateManager() {
        C0083w c0083w = this.monitorServiceStateManager;
        if (c0083w != null) {
            return c0083w;
        }
        m.p("monitorServiceStateManager");
        throw null;
    }

    public final C0084x getMonitorServiceUseCases() {
        C0084x c0084x = this.monitorServiceUseCases;
        if (c0084x != null) {
            return c0084x;
        }
        m.p("monitorServiceUseCases");
        throw null;
    }

    public final NodeBounds getNodeBounds(AccessibilityNodeInfo node, p567uf.j verticalGroupAnchor) {
        int iRint;
        m.h(node, "node");
        m.h(verticalGroupAnchor, "verticalGroupAnchor");
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        int i8 = rect.left;
        int i10 = rect.right;
        int i11 = rect.top;
        int i12 = rect.bottom;
        int iOrdinal = verticalGroupAnchor.ordinal();
        if (iOrdinal == 0) {
            iRint = (int) Math.rint((i8 + i10) / 2.0f);
        } else {
            if (iOrdinal != 1) {
                throw new NoWhenBranchMatchedException();
            }
            iRint = i8;
        }
        return new NodeBounds(i8, i11, i10, i12, iRint, (int) Math.rint((i11 + i12) / 2.0f), verticalGroupAnchor);
    }

    public final P getPermissionUseCases() {
        P p10 = this.permissionUseCases;
        if (p10 != null) {
            return p10;
        }
        m.p("permissionUseCases");
        throw null;
    }

    public final J getProductiveModeUseCases() {
        J j6 = this.productiveModeUseCases;
        if (j6 != null) {
            return j6;
        }
        m.p("productiveModeUseCases");
        throw null;
    }

    public final String getRandomString(int i8) {
        ArrayList arrayListZ0;
        Iterable cVar = new Jv.c('A', 'Z');
        Jv.c cVar2 = new Jv.c('a', 'z');
        if (cVar instanceof Collection) {
            arrayListZ0 = p468qv.m.z0((Collection) cVar, cVar2);
        } else {
            ArrayList arrayList = new ArrayList();
            p468qv.s.T(arrayList, cVar);
            p468qv.s.T(arrayList, cVar2);
            arrayListZ0 = arrayList;
        }
        ArrayList arrayListZ1 = p468qv.m.z0(arrayListZ0, new Jv.c('0', '9'));
        Jv.g gVar = new Jv.g(1, i8, 1);
        ArrayList arrayList2 = new ArrayList(p468qv.o.P(gVar, 10));
        Iterator it = gVar.iterator();
        while (((Jv.f) it).f14225X) {
            ((p468qv.y) it).a();
            Hv.d dVar = Hv.e.f11762e;
            Character ch2 = (Character) p468qv.m.B0(arrayListZ1);
            ch2.getClass();
            arrayList2.add(ch2);
        }
        return p468qv.m.q0(arrayList2, CoreConstants.EMPTY_STRING, null, null, null, 62);
    }

    public final void getSnapshotAndPrint() {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        String strK = AbstractC3102b.k("HashMapGroup-", getRandomString(5));
        printHashMapGroups(strK, convertToHashMap$default(this, strK, rootInActiveWindow, null, 4, null));
    }

    public final s0 getZenModeUseCases() {
        s0 s0Var = this.zenModeUseCases;
        if (s0Var != null) {
            return s0Var;
        }
        m.p("zenModeUseCases");
        throw null;
    }

    public final Gc.V isExistingUserBeforeLaunch() {
        Gc.V v4 = this.isExistingUserBeforeLaunch;
        if (v4 != null) {
            return v4;
        }
        m.p("isExistingUserBeforeLaunch");
        throw null;
    }

    public final t isInternalTesting() {
        t tVar = this.isInternalTesting;
        if (tVar != null) {
            return tVar;
        }
        m.p("isInternalTesting");
        throw null;
    }

    public final u isStrictModeActiveForBlockFeatureAppId() {
        u uVar = this.isStrictModeActiveForBlockFeatureAppId;
        if (uVar != null) {
            return uVar;
        }
        m.p("isStrictModeActiveForBlockFeatureAppId");
        throw null;
    }

    public final boolean isTablet() {
        if (isTabletDevice == null) {
            defpackage.c cVar = defpackage.c.f48597a;
            Context applicationContext = getApplicationContext();
            m.g(applicationContext, "getApplicationContext(...)");
            isTabletDevice = Boolean.valueOf((applicationContext.getResources().getConfiguration().screenLayout & 15) >= 3);
        }
        Boolean bool = isTabletDevice;
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    public final boolean isYoutubeMiniPlayerVisible() {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) {
            return false;
        }
        Iterator<String> it = this.youtubeMiniPlayerViewIds.iterator();
        while (it.hasNext()) {
            List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByViewId = rootInActiveWindow.findAccessibilityNodeInfosByViewId(it.next());
            if (!listFindAccessibilityNodeInfosByViewId.isEmpty()) {
                List<AccessibilityNodeInfo> list = listFindAccessibilityNodeInfosByViewId;
                if (!(list instanceof Collection) || !list.isEmpty()) {
                    Iterator<T> it2 = list.iterator();
                    while (it2.hasNext()) {
                        if (((AccessibilityNodeInfo) it2.next()).isVisibleToUser()) {
                            listFindAccessibilityNodeInfosByViewId.size();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent event) {
        m.h(event, "event");
    }

    @Override // p536tf.AbstractC5063h, android.app.Service
    public void onCreate() {
        p118e.b.d(this.logTag, "onCreate called");
        super.onCreate();
        isRunning = true;
        this.localBroadcastReceiver = new C3929d(this, 12);
        IntentFilter intentFilter = new IntentFilter("ACCESSIBILITY_ACTION_INTENT_KEY");
        Nl.b bVarA = Nl.b.a(this);
        BroadcastReceiver broadcastReceiver = this.localBroadcastReceiver;
        if (broadcastReceiver == null) {
            m.p("localBroadcastReceiver");
            throw null;
        }
        bVarA.b(broadcastReceiver, intentFilter);
        Context applicationContext = getApplicationContext();
        m.g(applicationContext, "getApplicationContext(...)");
        C0063b ftOverlayStateManager = getFtOverlayStateManager();
        m.h(ftOverlayStateManager, "ftOverlayStateManager");
        this.navigationHandler = new W(new C8.n(false, ftOverlayStateManager, applicationContext, null), this, null);
        getMonitorServiceStateManager().d();
        C.A(this.scope, null, null, new X(this, null), 3);
        C.A(this.scope, null, null, new Y(this, null), 3);
        p118e.b.d(this.logTag, "onCreate finished and navigationHandler is set");
        startBlockService();
    }

    @Override // android.app.Service
    public void onDestroy() {
        h hVar = this.contentBlockWindow;
        if (hVar != null) {
            hVar.a();
            C.j(hVar.f2330i, null);
            Y.c cVar = hVar.f2326e;
            cVar.getClass();
            Y.c.a();
            cVar.f39041f = true;
            cVar.b();
        }
        this.contentBlockWindow = null;
        d dVar = this.blockProtectionWindow;
        if (dVar != null) {
            Y.c cVar2 = dVar.f29006c;
            cVar2.getClass();
            Y.c.a();
            cVar2.f39041f = true;
            cVar2.b();
        }
        this.blockProtectionWindow = null;
        getLogAnalyticsUserProperty().a(EnumC4350a.f62959X0, null, null);
        super.onDestroy();
        isRunning = false;
        stopBlockService();
        ((m0) this.job).g(null);
        Nl.b bVarA = Nl.b.a(this);
        BroadcastReceiver broadcastReceiver = this.localBroadcastReceiver;
        if (broadcastReceiver == null) {
            m.p("localBroadcastReceiver");
            throw null;
        }
        bVarA.d(broadcastReceiver);
        p471r0.r.a(getLogAnalyticsEvent(), EnumC4350a.f63756y9, null, 6);
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onServiceConnected() {
        p118e.b.d(this.logTag, "onServiceConnected called");
        super.onServiceConnected();
        d dVar = this.blockProtectionWindow;
        if (dVar != null) {
            Y.c cVar = dVar.f29006c;
            cVar.getClass();
            Y.c.a();
            cVar.f39041f = true;
            cVar.b();
        }
        a blockProtectionWindowFactory = getBlockProtectionWindowFactory();
        blockProtectionWindowFactory.getClass();
        this.blockProtectionWindow = new d(this, blockProtectionWindowFactory.f28999a);
        h hVar = this.contentBlockWindow;
        if (hVar != null) {
            hVar.a();
            C.j(hVar.f2330i, null);
            Y.c cVar2 = hVar.f2326e;
            cVar2.getClass();
            Y.c.a();
            cVar2.f39041f = true;
            cVar2.b();
        }
        b contentBlockWindowFactory = getContentBlockWindowFactory();
        contentBlockWindowFactory.getClass();
        this.contentBlockWindow = new h(this, contentBlockWindowFactory.f2298a, contentBlockWindowFactory.b, contentBlockWindowFactory.f2299c);
        isRunning = true;
        getLogAnalyticsUserProperty().a(EnumC4350a.f62959X0, Boolean.TRUE, null);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i8, int i10) {
        p118e.b.d(this.logTag, "onStartCommand called");
        return super.onStartCommand(intent, i8, i10);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        p118e.b.d(this.logTag, "onUnbind called");
        h hVar = this.contentBlockWindow;
        if (hVar != null) {
            hVar.a();
            C.j(hVar.f2330i, null);
            Y.c cVar = hVar.f2326e;
            cVar.getClass();
            Y.c.a();
            cVar.f39041f = true;
            cVar.b();
        }
        this.contentBlockWindow = null;
        d dVar = this.blockProtectionWindow;
        if (dVar != null) {
            Y.c cVar2 = dVar.f29006c;
            cVar2.getClass();
            Y.c.a();
            cVar2.f39041f = true;
            cVar2.b();
        }
        this.blockProtectionWindow = null;
        getLogAnalyticsUserProperty().a(EnumC4350a.f62959X0, null, null);
        isRunning = false;
        return super.onUnbind(intent);
    }

    public final void performBackPress() {
        p118e.b.d("PerformAccessibilityActionReceiver", "performBackPress called");
        M0.A("performBackPress result: ", "PerformAccessibilityActionReceiver", performGlobalAction(1));
    }

    public final boolean playYouTubeVideo() {
        kotlin.jvm.internal.w wVar = new kotlin.jvm.internal.w();
        p471r0.r.a(getLogAnalyticsEvent(), EnumC4350a.f62853Td, null, 6);
        C.A(this.scope, null, null, new a0(this, wVar, null), 3);
        return wVar.f64882e;
    }

    public final void printHashMap(String tag, Map<Integer, ? extends List<p567uf.c>> hashMap, boolean z10) {
        Ch.v vVarL;
        char c10 = 1;
        m.h(tag, "tag");
        m.h(hashMap, "hashMap");
        String str = z10 ? "Vertical" : "Horizontal";
        for (Map.Entry<Integer, ? extends List<p567uf.c>> entry : hashMap.entrySet()) {
            int iIntValue = entry.getKey().intValue();
            List<p567uf.c> value = entry.getValue();
            p118e.b.d(tag, str + " group with key: " + iIntValue);
            List<p567uf.c> list = value;
            if (z10) {
                C4896a c4896a = new C4896a(13);
                C4896a c4896a2 = new C4896a(14);
                Dv.c[] cVarArr = new Dv.c[2];
                cVarArr[0] = c4896a;
                cVarArr[c10] = c4896a2;
                vVarL = p317lc.i.l(cVarArr);
            } else {
                C4896a c4896a3 = new C4896a(15);
                C4896a c4896a4 = new C4896a(16);
                Dv.c[] cVarArr2 = new Dv.c[2];
                cVarArr2[0] = c4896a3;
                cVarArr2[c10] = c4896a4;
                vVarL = p317lc.i.l(cVarArr2);
            }
            for (p567uf.c cVar : p468qv.m.F0(list, vVarL)) {
                NodeBounds nodeBounds = cVar.f77950c;
                String str2 = "null";
                String str3 = cVar.f77949a;
                if (str3 == null) {
                    str3 = "null";
                }
                String str4 = cVar.b;
                if (str4 != null) {
                    str2 = str4;
                }
                int right = nodeBounds.getRight();
                int left = nodeBounds.getLeft();
                int top = nodeBounds.getTop();
                int bottom = nodeBounds.getBottom();
                StringBuilder sbP = AbstractC3102b.p("Node text: ", str3, ", contentDescription: ", str2, ", className: ");
                sbP.append(cVar.f77952e);
                sbP.append(", right: ");
                sbP.append(right);
                sbP.append(", left: ");
                AbstractC3102b.w(sbP, left, ", top: ", top, ", bottom: ");
                sbP.append(bottom);
                sbP.append(", id: ");
                sbP.append(cVar.f77951d);
                sbP.append(", enabled: ");
                sbP.append(cVar.f77953f);
                sbP.append(", visible: ");
                sbP.append(cVar.f77954g);
                p118e.b.d(tag, sbP.toString());
            }
            p118e.b.d(tag, "----------------------------------------");
            c10 = 1;
        }
    }

    public final void printHashMapGroups(String tag, p567uf.a groupMap) {
        m.h(tag, "tag");
        m.h(groupMap, "groupMap");
        printHashMap(tag, groupMap.f77942a, true);
        printHashMap(tag, groupMap.b, false);
    }

    public final void setAppUsageStatsUseCases(C0291a c0291a) {
        m.h(c0291a, "<set-?>");
        this.appUsageStatsUseCases = c0291a;
    }

    public final void setBlockFeatureUseCases(C2082a c2082a) {
        m.h(c2082a, "<set-?>");
        this.blockFeatureUseCases = c2082a;
    }

    public final void setBlockProtectionWindowFactory(a aVar) {
        m.h(aVar, "<set-?>");
        this.blockProtectionWindowFactory = aVar;
    }

    public final void setBlockWebsiteUseCases(C5518e c5518e) {
        m.h(c5518e, "<set-?>");
        this.blockWebsiteUseCases = c5518e;
    }

    public final void setC68PendingAlarmGuard(V v4) {
        m.h(v4, "<set-?>");
        this.c68PendingAlarmGuard = v4;
    }

    public final void setContentBlockWindowFactory(b bVar) {
        m.h(bVar, "<set-?>");
        this.contentBlockWindowFactory = bVar;
    }

    public final void setDistractingAppsUseCases(p638x3.a aVar) {
        m.h(aVar, "<set-?>");
        this.distractingAppsUseCases = aVar;
    }

    public final void setExistingUserBeforeLaunch(Gc.V v4) {
        m.h(v4, "<set-?>");
        this.isExistingUserBeforeLaunch = v4;
    }

    public final void setFocusTimerUseCases(T t2) {
        m.h(t2, "<set-?>");
        this.focusTimerUseCases = t2;
    }

    public final void setFtOverlayStateManager(C0063b c0063b) {
        m.h(c0063b, "<set-?>");
        this.ftOverlayStateManager = c0063b;
    }

    public final void setGetInstagramChallengeActive(C4887v c4887v) {
        m.h(c4887v, "<set-?>");
        this.getInstagramChallengeActive = c4887v;
    }

    public final void setGetLauncherAppId(c cVar) {
        m.h(cVar, "<set-?>");
        this.getLauncherAppId = cVar;
    }

    public final void setGetSettingsApps(G.d dVar) {
        m.h(dVar, "<set-?>");
        this.getSettingsApps = dVar;
    }

    public final void setHandleEducationAppNotification(p595vf.b bVar) {
        m.h(bVar, "<set-?>");
        this.handleEducationAppNotification = bVar;
    }

    public final void setInternalTesting(t tVar) {
        m.h(tVar, "<set-?>");
        this.isInternalTesting = tVar;
    }

    public final void setLogAnalyticsEvent(p471r0.r rVar) {
        m.h(rVar, "<set-?>");
        this.logAnalyticsEvent = rVar;
    }

    public final void setLogAnalyticsUserProperty(p471r0.u uVar) {
        m.h(uVar, "<set-?>");
        this.logAnalyticsUserProperty = uVar;
    }

    public final void setLogAppHeartbeat(g gVar) {
        m.h(gVar, "<set-?>");
        this.logAppHeartbeat = gVar;
    }

    public final void setMonitorServiceStateManager(C0083w c0083w) {
        m.h(c0083w, "<set-?>");
        this.monitorServiceStateManager = c0083w;
    }

    public final void setMonitorServiceUseCases(C0084x c0084x) {
        m.h(c0084x, "<set-?>");
        this.monitorServiceUseCases = c0084x;
    }

    public final void setPermissionUseCases(P p10) {
        m.h(p10, "<set-?>");
        this.permissionUseCases = p10;
    }

    public final void setProductiveModeUseCases(J j6) {
        m.h(j6, "<set-?>");
        this.productiveModeUseCases = j6;
    }

    public final void setStrictModeActiveForBlockFeatureAppId(u uVar) {
        m.h(uVar, "<set-?>");
        this.isStrictModeActiveForBlockFeatureAppId = uVar;
    }

    public final void setZenModeUseCases(s0 s0Var) {
        m.h(s0Var, "<set-?>");
        this.zenModeUseCases = s0Var;
    }
}
