package p019ai.focus.services;

import A8.C0063b;
import A8.C0075n;
import A8.C0083w;
import A8.C0084x;
import C8.n;
import D.g;
import D1.C0291a;
import E.x;
import G.d;
import Hu.I;
import Mv.y;
import N2.V;
import Pv.C;
import Pv.M;
import Pv.t0;
import Pv.v0;
import R8.J;
import Uc.i;
import Uc.t;
import Wv.e;
import android.app.Application;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import kotlin.A;
import kotlin.jvm.internal.m;
import org.json.JSONException;
import org.json.JSONObject;
import p019ai.focus.feature_app_usage_stats.domain.model.EventData;
import p019ai.focus.feature_focus_timer.domain.model.FocusTimerSession;
import p019ai.focus.floating_bubble.FTOverlayService;
import p060c.AbstractC3102b;
import p060c.C3104c;
import p060c.C3141v;
import p060c.J0;
import p077ch.qos.logback.classic.Level;
import p090d.H;
import p170fv.j;
import p221hv.b;
import p278k0.C4364c;
import p278k0.EnumC4350a;
import p278k0.EnumC4406i;
import p373nf.E;
import p451qa.C4885t;
import p471r0.r;
import p471r0.u;
import p536tf.n0;
import p536tf.r0;
import p536tf.s0;
import p536tf.w0;
import p536tf.x0;
import p581uv.a;
import vv.c;

/* JADX INFO: loaded from: classes.dex */
public final class MyMonitorService extends Service implements b {

    /* JADX INFO: renamed from: L0, reason: collision with root package name */
    public static boolean f45185L0;
    public static long M0;

    /* JADX INFO: renamed from: N0, reason: collision with root package name */
    public static Long f45186N0;

    /* JADX INFO: renamed from: O0, reason: collision with root package name */
    public static Long f45187O0;
    public static Long P0;

    /* JADX INFO: renamed from: Q0, reason: collision with root package name */
    public static Long f45188Q0;

    /* JADX INFO: renamed from: R0, reason: collision with root package name */
    public static boolean f45189R0;

    /* JADX INFO: renamed from: A0, reason: collision with root package name */
    public d f45190A0;

    /* JADX INFO: renamed from: B0, reason: collision with root package name */
    public x f45191B0;

    /* JADX INFO: renamed from: C0, reason: collision with root package name */
    public C4885t f45192C0;

    /* JADX INFO: renamed from: D0, reason: collision with root package name */
    public List f45193D0;
    public final String E0;

    /* JADX INFO: renamed from: F0, reason: collision with root package name */
    public final String f45194F0;

    /* JADX INFO: renamed from: G0, reason: collision with root package name */
    public final String f45195G0;

    /* JADX INFO: renamed from: H0, reason: collision with root package name */
    public n f45196H0;

    /* JADX INFO: renamed from: I0, reason: collision with root package name */
    public final H f45198I0;

    /* JADX INFO: renamed from: J0, reason: collision with root package name */
    public final n0 f45199J0;

    /* JADX INFO: renamed from: K0, reason: collision with root package name */
    public final n0 f45200K0;

    /* JADX INFO: renamed from: Y, reason: collision with root package name */
    public final v0 f45202Y;

    /* JADX INFO: renamed from: Z, reason: collision with root package name */
    public final Uv.d f45203Z;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public volatile j f45204e;

    /* JADX INFO: renamed from: l0, reason: collision with root package name */
    public String f45205l0;

    /* JADX INFO: renamed from: m0, reason: collision with root package name */
    public long f45206m0;

    /* JADX INFO: renamed from: n0, reason: collision with root package name */
    public long f45207n0;

    /* JADX INFO: renamed from: o0, reason: collision with root package name */
    public t0 f45208o0;

    /* JADX INFO: renamed from: p0, reason: collision with root package name */
    public long f45209p0;

    /* JADX INFO: renamed from: q0, reason: collision with root package name */
    public C0291a f45210q0;

    /* JADX INFO: renamed from: r0, reason: collision with root package name */
    public u f45211r0;

    /* JADX INFO: renamed from: s0, reason: collision with root package name */
    public r f45212s0;
    public J t0;

    /* JADX INFO: renamed from: u0, reason: collision with root package name */
    public p595vf.b f45213u0;

    /* JADX INFO: renamed from: v0, reason: collision with root package name */
    public C0084x f45214v0;

    /* JADX INFO: renamed from: w0, reason: collision with root package name */
    public V f45215w0;

    /* JADX INFO: renamed from: x0, reason: collision with root package name */
    public C0063b f45216x0;

    /* JADX INFO: renamed from: y0, reason: collision with root package name */
    public C0083w f45217y0;
    public g z0;

    /* JADX INFO: renamed from: I, reason: collision with root package name */
    public final Object f45197I = new Object();

    /* JADX INFO: renamed from: X, reason: collision with root package name */
    public boolean f45201X = false;

    public MyMonitorService() {
        v0 v0VarF = C.f();
        this.f45202Y = v0VarF;
        e eVar = M.f24563a;
        Wv.d dVar = Wv.d.f37341X;
        dVar.getClass();
        this.f45203Z = C.c(p315la.j.l(dVar, v0VarF));
        this.f45193D0 = p468qv.u.f71758e;
        this.E0 = "MyMonitorService";
        this.f45194F0 = "MyMonitorServiceWhich";
        this.f45195G0 = "MonitorRecovery";
        this.f45198I0 = new H();
        this.f45199J0 = new n0(this, 1);
        this.f45200K0 = new n0(this, 0);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:102:0x022c  */
    /* JADX WARN: Code duplicated, block: B:105:0x02c0  */
    /* JADX WARN: Code duplicated, block: B:108:0x02e0  */
    /* JADX WARN: Code duplicated, block: B:110:0x02e4  */
    /* JADX WARN: Code duplicated, block: B:112:0x02e8  */
    /* JADX WARN: Code duplicated, block: B:117:0x0301  */
    /* JADX WARN: Code duplicated, block: B:119:0x0305  */
    /* JADX WARN: Code duplicated, block: B:121:0x0309  */
    /* JADX WARN: Code duplicated, block: B:124:0x031d  */
    /* JADX WARN: Code duplicated, block: B:126:0x0321 A[PHI: r1 r3 r4 r19
  0x0321: PHI (r1v20 java.lang.String) = (r1v18 java.lang.String), (r1v21 java.lang.String) binds: [B:107:0x02de, B:116:0x02fe] A[DONT_GENERATE, DONT_INLINE]
  0x0321: PHI (r3v17 z8.a) = (r3v13 z8.a), (r3v20 z8.a) binds: [B:107:0x02de, B:116:0x02fe] A[DONT_GENERATE, DONT_INLINE]
  0x0321: PHI (r4v10 ai.focus.services.MyMonitorService) = (r4v8 ai.focus.services.MyMonitorService), (r4v11 ai.focus.services.MyMonitorService) binds: [B:107:0x02de, B:116:0x02fe] A[DONT_GENERATE, DONT_INLINE]
  0x0321: PHI (r19v5 java.lang.Throwable) = (r19v3 java.lang.Throwable), (r19v6 java.lang.Throwable) binds: [B:107:0x02de, B:116:0x02fe] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:128:0x0325  */
    /* JADX WARN: Code duplicated, block: B:130:0x032e  */
    /* JADX WARN: Code duplicated, block: B:133:0x0342  */
    /* JADX WARN: Code duplicated, block: B:136:0x0348  */
    /* JADX WARN: Code duplicated, block: B:140:0x035f  */
    /* JADX WARN: Code duplicated, block: B:142:0x0363  */
    /* JADX WARN: Code duplicated, block: B:144:0x0367  */
    /* JADX WARN: Code duplicated, block: B:147:0x0379  */
    /* JADX WARN: Code duplicated, block: B:149:0x037f  */
    /* JADX WARN: Code duplicated, block: B:151:0x038d  */
    /* JADX WARN: Code duplicated, block: B:155:0x0399  */
    /* JADX WARN: Code duplicated, block: B:158:0x03a0  */
    /* JADX WARN: Code duplicated, block: B:161:0x03b2  */
    /* JADX WARN: Code duplicated, block: B:163:0x03b8  */
    /* JADX WARN: Code duplicated, block: B:165:0x03c3  */
    /* JADX WARN: Code duplicated, block: B:168:0x03d8  */
    /* JADX WARN: Code duplicated, block: B:170:0x03dc  */
    /* JADX WARN: Code duplicated, block: B:172:0x03e2  */
    /* JADX WARN: Code duplicated, block: B:175:0x03f7  */
    /* JADX WARN: Code duplicated, block: B:177:0x03fb  */
    /* JADX WARN: Code duplicated, block: B:179:0x0407  */
    /* JADX WARN: Code duplicated, block: B:182:0x041c  */
    /* JADX WARN: Code duplicated, block: B:184:0x0420  */
    /* JADX WARN: Code duplicated, block: B:186:0x0426  */
    /* JADX WARN: Code duplicated, block: B:189:0x043b  */
    /* JADX WARN: Code duplicated, block: B:191:0x043f  */
    /* JADX WARN: Code duplicated, block: B:193:0x045f  */
    /* JADX WARN: Code duplicated, block: B:196:0x0475  */
    /* JADX WARN: Code duplicated, block: B:198:0x047a  */
    /* JADX WARN: Code duplicated, block: B:200:0x049a  */
    /* JADX WARN: Code duplicated, block: B:203:0x04b0  */
    /* JADX WARN: Code duplicated, block: B:205:0x04b5  */
    /* JADX WARN: Code duplicated, block: B:207:0x04b9 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:210:0x04bf  */
    /* JADX WARN: Code duplicated, block: B:213:0x04d4  */
    /* JADX WARN: Code duplicated, block: B:215:0x04d8  */
    /* JADX WARN: Code duplicated, block: B:217:0x04dc A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:220:0x04e2  */
    /* JADX WARN: Code duplicated, block: B:223:0x04f7  */
    /* JADX WARN: Code duplicated, block: B:225:0x04fb  */
    /* JADX WARN: Code duplicated, block: B:227:0x0509  */
    /* JADX WARN: Code duplicated, block: B:230:0x051e  */
    /* JADX WARN: Code duplicated, block: B:232:0x0522  */
    /* JADX WARN: Code duplicated, block: B:234:0x052e  */
    /* JADX WARN: Code duplicated, block: B:237:0x0543  */
    /* JADX WARN: Code duplicated, block: B:239:0x0547  */
    /* JADX WARN: Code duplicated, block: B:241:0x0553  */
    /* JADX WARN: Code duplicated, block: B:244:0x0567  */
    /* JADX WARN: Code duplicated, block: B:246:0x056b  */
    /* JADX WARN: Code duplicated, block: B:248:0x057c  */
    /* JADX WARN: Code duplicated, block: B:253:0x0598  */
    /* JADX WARN: Code duplicated, block: B:255:0x059d  */
    /* JADX WARN: Code duplicated, block: B:257:0x05ae  */
    /* JADX WARN: Code duplicated, block: B:259:0x05c0 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:262:0x05c9  */
    /* JADX WARN: Code duplicated, block: B:264:0x05cf  */
    /* JADX WARN: Code duplicated, block: B:268:0x05df A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:8:0x001b  */
    /* JADX WARN: Code duplicated, block: B:94:0x01d8  */
    /* JADX WARN: Code duplicated, block: B:96:0x01e4  */
    /* JADX WARN: Code duplicated, block: B:97:0x01fc  */
    /* JADX WARN: Code duplicated, block: B:99:0x0206  */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x02f8, code lost:
    
        if (r0.invoke(r5, r14) == r2) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x02fc, code lost:
    
        r0 = r3;
        r3 = r4;
     */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x0319, code lost:
    
        if (r0.invoke(r5, r14) == r2) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x0355, code lost:
    
        if (r4.invoke(r5, r14) == r2) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x058d, code lost:
    
        if (r1.invoke(r5, r14) == r2) goto L259;
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x05be, code lost:
    
        if (r1.invoke(r5, r14) == r2) goto L259;
     */
    /* JADX WARN: Instruction removed from duplicated block: B:191:0x043f, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:198:0x047a, please report this as an issue */
    /* JADX WARN: Type inference failed for: r4v19, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v20, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v21, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v22, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v23, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v32, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v33, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v34, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v35, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r4v39, types: [ai.focus.services.MyMonitorService, java.lang.Object, java.lang.Throwable] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static final java.lang.Object a(p019ai.focus.services.MyMonitorService r21, vv.c r22) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 1586
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: p019ai.focus.services.MyMonitorService.a(ai.focus.services.MyMonitorService, vv.c):java.lang.Object");
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0016  */
    public static final Object b(MyMonitorService myMonitorService, long j6, c cVar) {
        r0 r0Var;
        long jCurrentTimeMillis;
        myMonitorService.getClass();
        if (cVar instanceof r0) {
            r0Var = (r0) cVar;
            int i8 = r0Var.f76083Z;
            if ((i8 & Level.ALL_INT) != 0) {
                r0Var.f76083Z = i8 - Level.ALL_INT;
            } else {
                r0Var = new r0(myMonitorService, cVar);
            }
        } else {
            r0Var = new r0(myMonitorService, cVar);
        }
        Object obj = r0Var.f76081X;
        a aVar = a.f79130e;
        int i10 = r0Var.f76083Z;
        A a10 = A.f64853a;
        if (i10 == 0) {
            U8.b.F(obj);
            if (!f()) {
                jCurrentTimeMillis = System.currentTimeMillis();
                if (jCurrentTimeMillis - myMonitorService.f45207n0 >= j6) {
                    g gVar = myMonitorService.z0;
                    if (gVar == null) {
                        m.p("logAppHeartbeat");
                        throw null;
                    }
                    p635x.c cVar2 = p635x.c.MY_MONITOR_SERVICE;
                    r0Var.f76084e = myMonitorService;
                    r0Var.f76080I = jCurrentTimeMillis;
                    r0Var.f76083Z = 1;
                    if (gVar.a(cVar2, r0Var) == aVar) {
                        return aVar;
                    }
                }
            }
            return a10;
        }
        if (i10 != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        long j10 = r0Var.f76080I;
        MyMonitorService myMonitorService2 = r0Var.f76084e;
        U8.b.F(obj);
        jCurrentTimeMillis = j10;
        myMonitorService = myMonitorService2;
        myMonitorService.f45207n0 = jCurrentTimeMillis;
        return a10;
    }

    /* JADX WARN: Code duplicated, block: B:44:0x0093  */
    /* JADX WARN: Code duplicated, block: B:45:0x0095 A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:47:0x0099 A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:49:0x009d A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:52:0x00ac  */
    /* JADX WARN: Code duplicated, block: B:54:0x00af A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:56:0x00b5 A[Catch: Exception -> 0x003c, PHI: r4 r13
  0x00b5: PHI (r4v6 ai.focus.services.MyMonitorService) = (r4v5 ai.focus.services.MyMonitorService), (r4v7 ai.focus.services.MyMonitorService) binds: [B:46:0x0097, B:53:0x00ad] A[DONT_GENERATE, DONT_INLINE]
  0x00b5: PHI (r13v7 A8.n) = (r13v5 A8.n), (r13v10 A8.n) binds: [B:46:0x0097, B:53:0x00ad] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:58:0x00c2 A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:59:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:61:0x00ca A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:63:0x00de A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:65:0x00e4 A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:66:0x00ee  */
    /* JADX WARN: Code duplicated, block: B:69:0x0118 A[Catch: Exception -> 0x003c, TryCatch #0 {Exception -> 0x003c, blocks: (B:14:0x0037, B:56:0x00b5, B:58:0x00c2, B:61:0x00ca, B:63:0x00de, B:65:0x00e4, B:67:0x00ef, B:69:0x0118, B:70:0x0121, B:21:0x004b, B:42:0x008b, B:45:0x0095, B:47:0x0099, B:49:0x009d, B:54:0x00af, B:55:0x00b4, B:24:0x0051, B:30:0x006a, B:33:0x0070, B:36:0x0076, B:38:0x007a, B:72:0x0125, B:73:0x012a, B:27:0x0058), top: B:76:0x002b }] */
    /* JADX WARN: Code duplicated, block: B:7:0x001a  */
    public static final Object c(MyMonitorService myMonitorService, c cVar) {
        p536tf.t0 t0Var;
        MyMonitorService myMonitorService2;
        C0075n c0075n;
        x xVar;
        p635x.n nVar;
        MyMonitorService myMonitorService3;
        EventData eventDataA;
        String appId;
        String packageName;
        FocusTimerSession focusTimerSession;
        Integer num;
        myMonitorService.getClass();
        if (cVar instanceof p536tf.t0) {
            t0Var = (p536tf.t0) cVar;
            int i8 = t0Var.f76091Z;
            if ((i8 & Level.ALL_INT) != 0) {
                t0Var.f76091Z = i8 - Level.ALL_INT;
            } else {
                t0Var = new p536tf.t0(myMonitorService, cVar);
            }
        } else {
            t0Var = new p536tf.t0(myMonitorService, cVar);
        }
        Object objO = t0Var.f76089X;
        a aVar = a.f79130e;
        int i10 = t0Var.f76091Z;
        A a10 = A.f64853a;
        try {
            if (i10 == 0) {
                U8.b.F(objO);
                p536tf.v0 v0Var = new p536tf.v0(myMonitorService, null);
                t0Var.f76092e = myMonitorService;
                t0Var.f76091Z = 1;
                objO = C.O(15000L, v0Var, t0Var);
                if (objO == aVar) {
                }
                return aVar;
            }
            if (i10 == 1) {
                myMonitorService = t0Var.f76092e;
                U8.b.F(objO);
            } else {
                if (i10 == 2) {
                    c0075n = t0Var.f76088I;
                    myMonitorService2 = t0Var.f76092e;
                    U8.b.F(objO);
                    if (((Boolean) objO).booleanValue()) {
                        if (!c0075n.f808g) {
                            xVar = myMonitorService2.f45191B0;
                            if (xVar != null) {
                                m.p("insertOrReplaceUserPrefBoolean");
                                throw null;
                            }
                            nVar = p635x.n.SHOW_FT_BUBBLE;
                            t0Var.f76092e = myMonitorService2;
                            t0Var.f76088I = c0075n;
                            t0Var.f76091Z = 3;
                            if (xVar.b(nVar, true, t0Var) != aVar) {
                                myMonitorService3 = myMonitorService2;
                            }
                            return aVar;
                        }
                        Context applicationContext = myMonitorService2.getApplicationContext();
                        m.g(applicationContext, "getApplicationContext(...)");
                        eventDataA = p705zf.a.a(applicationContext);
                        if (eventDataA != null) {
                            appId = eventDataA.getAppId();
                        } else {
                            appId = null;
                        }
                        if (appId != null) {
                            packageName = myMonitorService2.getApplicationContext().getPackageName();
                            m.g(packageName, "getPackageName(...)");
                            if (!y.X(appId, packageName, false)) {
                                String str = myMonitorService2.f45195G0;
                                focusTimerSession = c0075n.f803a;
                                if (focusTimerSession != null) {
                                    num = new Integer(focusTimerSession.getSessionId());
                                } else {
                                    num = null;
                                }
                                p118e.b.e(str, "FT_BUBBLE_RECOVERY: session " + num + " active, bubble down after process start — starting it directly (foreground=" + appId + ")");
                                Context applicationContext2 = myMonitorService2.getApplicationContext();
                                m.g(applicationContext2, "getApplicationContext(...)");
                                E e9 = E.MINIMIZE;
                                FocusTimerSession focusTimerSession2 = c0075n.f803a;
                                Zq.a.S(applicationContext2, e9, focusTimerSession2 != null ? new Integer(focusTimerSession2.getSessionId()) : null);
                                return a10;
                            }
                        }
                    }
                    return a10;
                }
                if (i10 != 3) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                c0075n = t0Var.f76088I;
                myMonitorService3 = t0Var.f76092e;
                U8.b.F(objO);
            }
            myMonitorService2 = myMonitorService3;
            Context applicationContext3 = myMonitorService2.getApplicationContext();
            m.g(applicationContext3, "getApplicationContext(...)");
            eventDataA = p705zf.a.a(applicationContext3);
            if (eventDataA != null) {
                appId = eventDataA.getAppId();
            } else {
                appId = null;
            }
            if (appId != null) {
                packageName = myMonitorService2.getApplicationContext().getPackageName();
                m.g(packageName, "getPackageName(...)");
                if (!y.X(appId, packageName, false)) {
                    String str2 = myMonitorService2.f45195G0;
                    focusTimerSession = c0075n.f803a;
                    if (focusTimerSession != null) {
                        num = new Integer(focusTimerSession.getSessionId());
                    } else {
                        num = null;
                    }
                    p118e.b.e(str2, "FT_BUBBLE_RECOVERY: session " + num + " active, bubble down after process start — starting it directly (foreground=" + appId + ")");
                    Context applicationContext4 = myMonitorService2.getApplicationContext();
                    m.g(applicationContext4, "getApplicationContext(...)");
                    E e10 = E.MINIMIZE;
                    FocusTimerSession focusTimerSession3 = c0075n.f803a;
                    Zq.a.S(applicationContext4, e10, focusTimerSession3 != null ? new Integer(focusTimerSession3.getSessionId()) : null);
                    return a10;
                }
            }
            return a10;
            C0075n c0075n2 = (C0075n) objO;
            if (c0075n2 != null && !FTOverlayService.f45064h2) {
                C4885t c4885t = myMonitorService.f45192C0;
                if (c4885t == null) {
                    m.p("getFtBubbleEnabled");
                    throw null;
                }
                t0Var.f76092e = myMonitorService;
                t0Var.f76088I = c0075n2;
                t0Var.f76091Z = 2;
                Object objB = c4885t.b(t0Var);
                if (objB != aVar) {
                    myMonitorService2 = myMonitorService;
                    c0075n = c0075n2;
                    objO = objB;
                    if (((Boolean) objO).booleanValue()) {
                        if (!c0075n.f808g) {
                            Context applicationContext5 = myMonitorService2.getApplicationContext();
                            m.g(applicationContext5, "getApplicationContext(...)");
                            eventDataA = p705zf.a.a(applicationContext5);
                            if (eventDataA != null) {
                                appId = eventDataA.getAppId();
                            } else {
                                appId = null;
                            }
                            if (appId != null) {
                                packageName = myMonitorService2.getApplicationContext().getPackageName();
                                m.g(packageName, "getPackageName(...)");
                                if (!y.X(appId, packageName, false)) {
                                    String str3 = myMonitorService2.f45195G0;
                                    focusTimerSession = c0075n.f803a;
                                    if (focusTimerSession != null) {
                                        num = new Integer(focusTimerSession.getSessionId());
                                    } else {
                                        num = null;
                                    }
                                    p118e.b.e(str3, "FT_BUBBLE_RECOVERY: session " + num + " active, bubble down after process start — starting it directly (foreground=" + appId + ")");
                                    Context applicationContext6 = myMonitorService2.getApplicationContext();
                                    m.g(applicationContext6, "getApplicationContext(...)");
                                    E e11 = E.MINIMIZE;
                                    FocusTimerSession focusTimerSession4 = c0075n.f803a;
                                    Zq.a.S(applicationContext6, e11, focusTimerSession4 != null ? new Integer(focusTimerSession4.getSessionId()) : null);
                                    return a10;
                                }
                            }
                        } else {
                            xVar = myMonitorService2.f45191B0;
                            if (xVar != null) {
                                m.p("insertOrReplaceUserPrefBoolean");
                                throw null;
                            }
                            nVar = p635x.n.SHOW_FT_BUBBLE;
                            t0Var.f76092e = myMonitorService2;
                            t0Var.f76088I = c0075n;
                            t0Var.f76091Z = 3;
                            if (xVar.b(nVar, true, t0Var) != aVar) {
                                myMonitorService3 = myMonitorService2;
                                myMonitorService2 = myMonitorService3;
                                Context applicationContext7 = myMonitorService2.getApplicationContext();
                                m.g(applicationContext7, "getApplicationContext(...)");
                                eventDataA = p705zf.a.a(applicationContext7);
                                if (eventDataA != null) {
                                    appId = eventDataA.getAppId();
                                } else {
                                    appId = null;
                                }
                                if (appId != null) {
                                    packageName = myMonitorService2.getApplicationContext().getPackageName();
                                    m.g(packageName, "getPackageName(...)");
                                    if (!y.X(appId, packageName, false)) {
                                        String str4 = myMonitorService2.f45195G0;
                                        focusTimerSession = c0075n.f803a;
                                        if (focusTimerSession != null) {
                                            num = new Integer(focusTimerSession.getSessionId());
                                        } else {
                                            num = null;
                                        }
                                        p118e.b.e(str4, "FT_BUBBLE_RECOVERY: session " + num + " active, bubble down after process start — starting it directly (foreground=" + appId + ")");
                                        Context applicationContext8 = myMonitorService2.getApplicationContext();
                                        m.g(applicationContext8, "getApplicationContext(...)");
                                        E e12 = E.MINIMIZE;
                                        FocusTimerSession focusTimerSession5 = c0075n.f803a;
                                        Zq.a.S(applicationContext8, e12, focusTimerSession5 != null ? new Integer(focusTimerSession5.getSessionId()) : null);
                                        return a10;
                                    }
                                }
                            }
                        }
                    }
                }
                return aVar;
            }
        } catch (Exception e13) {
            Js.c.a().b(e13);
        }
        return a10;
    }

    public static boolean f() {
        defpackage.c cVar = defpackage.c.f48597a;
        long jCurrentTimeMillis = System.currentTimeMillis();
        MyMonitorInAppBlockService.Companion.getClass();
        return jCurrentTimeMillis - MyMonitorInAppBlockService.blockServiceCheckLastHeartBeat < 5000;
    }

    public static /* synthetic */ void h(MyMonitorService myMonitorService, String str) {
        myMonitorService.g(str, new JSONObject(), EnumC4406i.f63896X);
    }

    public final r d() {
        r rVar = this.f45212s0;
        if (rVar != null) {
            return rVar;
        }
        m.p("logAnalyticsEvent");
        throw null;
    }

    public final C0083w e() {
        C0083w c0083w = this.f45217y0;
        if (c0083w != null) {
            return c0083w;
        }
        m.p("monitorServiceStateManager");
        throw null;
    }

    public final void g(String str, JSONObject jSONObject, EnumC4406i enumC4406i) {
        try {
            r.a(d(), new C4364c("Monitor Service: ".concat(str), enumC4406i), jSONObject, 4);
        } catch (Exception e9) {
            Js.c.a().b(e9);
        }
    }

    @Override // p221hv.b
    public final Object generatedComponent() {
        if (this.f45204e == null) {
            synchronized (this.f45197I) {
                try {
                    if (this.f45204e == null) {
                        this.f45204e = new j(this);
                    }
                } catch (Throwable th2) {
                    throw th2;
                }
            }
        }
        return this.f45204e.generatedComponent();
    }

    public final void i() {
        if (!this.f45201X) {
            this.f45201X = true;
            p060c.r rVar = (p060c.r) ((x0) generatedComponent());
            C3141v c3141v = rVar.f48819a;
            this.f45210q0 = (C0291a) c3141v.f48911S0.get();
            this.f45211r0 = c3141v.y3();
            this.f45212s0 = c3141v.x3();
            c3141v.c();
            this.t0 = (J) c3141v.W0.get();
            C3141v c3141v2 = rVar.f48819a;
            Context context = c3141v2.f48946a.f6074I;
            i.q(context);
            this.f45213u0 = new p595vf.b(context, new p520sr.A(12), c3141v2.o2(), c3141v2.V2());
            this.f45214v0 = (C0084x) c3141v.f48917T2.get();
            this.f45215w0 = (V) c3141v.f48835A3.get();
            c3141v.w1();
            this.f45216x0 = (C0063b) c3141v.f48913S2.get();
            this.f45217y0 = (C0083w) c3141v.f48987h4.get();
            Application applicationX = t.x(c3141v2.f48946a.f6074I);
            i.q(applicationX);
            this.z0 = new g(applicationX, (p692z.a) c3141v2.f48996k.get());
            this.f45190A0 = c3141v.Q1();
            this.f45191B0 = c3141v.U2();
            this.f45192C0 = new C4885t(c3141v2.m2(), c3141v2.n2(), c3141v2.o2());
        }
        super.onCreate();
    }

    public final void j(String str) {
        t0 t0Var = this.f45208o0;
        if (t0Var == null || !t0Var.c()) {
            this.f45208o0 = C.A(this.f45203Z, null, null, new w0(this, str, null), 3);
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        m.h(intent, "intent");
        return null;
    }

    @Override // android.app.Service
    public final void onCreate() {
        i();
        C0084x c0084x = this.f45214v0;
        if (c0084x == null) {
            m.p("monitorServiceUseCases");
            throw null;
        }
        Notification notificationA = c0084x.f861g.a("focus", "App Reminder Service");
        AbstractC3102b.u(J0.a().f48721a, "GAME_AR_PROGRESS_NOTIFICATION_DISMISSED", false);
        p709zk.d.f(this, 101, notificationA, Build.VERSION.SDK_INT >= 34 ? 1073741824 : 0);
        Context applicationContext = getApplicationContext();
        m.g(applicationContext, "getApplicationContext(...)");
        C0063b c0063b = this.f45216x0;
        if (c0063b == null) {
            m.p("ftOverlayStateManager");
            throw null;
        }
        this.f45196H0 = new n(false, c0063b, applicationContext, null);
        h(this, "OnCreate");
        e().d();
        C.A(this.f45203Z, null, null, new s0(this, null), 3);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        registerReceiver(this.f45200K0, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.SCREEN_OFF");
        intentFilter2.addAction("android.intent.action.USER_PRESENT");
        intentFilter2.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(this.f45198I0, intentFilter2);
        Nl.b.a(this).b(this.f45199J0, new IntentFilter("CHECK_OVERLAY_POPUP_PERMISSION"));
        j("onCreate");
    }

    @Override // android.app.Service
    public final void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.f45200K0);
        unregisterReceiver(this.f45198I0);
        Nl.b.a(this).d(this.f45199J0);
        h(this, "OnDestroy");
        f45185L0 = false;
        this.f45208o0 = null;
        C.j(this.f45203Z, null);
        this.f45202Y.g(null);
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public final void onLowMemory() {
        super.onLowMemory();
        h(this, "OnLowMemory");
    }

    @Override // android.app.Service
    public final void onRebind(Intent intent) {
        super.onRebind(intent);
        h(this, "OnRebind");
    }

    @Override // android.app.Service
    public final int onStartCommand(Intent intent, int i8, int i10) throws JSONException {
        h(this, "OnStart");
        f45185L0 = true;
        if (J0.a().e("MONITOR_SERVICE_HEART_BEAT") != 0) {
            long jCurrentTimeMillis = System.currentTimeMillis() - J0.a().e("MONITOR_SERVICE_HEART_BEAT");
            defpackage.c cVar = defpackage.c.f48597a;
            if (jCurrentTimeMillis > 600000 * ((long) 10)) {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("service_not_running_duration_in_hrs", I.H(jCurrentTimeMillis));
                jSONObject.put("service_last_heart_beat_epoch", J0.a().e("MONITOR_SERVICE_HEART_BEAT"));
                String str = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).format(new Date(J0.a().e("MONITOR_SERVICE_HEART_BEAT")));
                m.g(str, "format(...)");
                jSONObject.put("service_last_heart_beat", str);
                r.a(d(), EnumC4350a.f63307j2, jSONObject, 4);
                C3104c c3104cA = J0.a();
                c3104cA.f48721a.edit().putLong("MONITOR_SERVICE_LAST_SEEN_PROBLEM", System.currentTimeMillis()).apply();
                I.N(jCurrentTimeMillis);
            } else {
                I.N(jCurrentTimeMillis);
            }
        }
        J0.a().f48721a.edit().putBoolean("USAGE_STANDING_NOTIFICATION_DISMISSED", false).apply();
        j("onStartCommand");
        return 1;
    }

    @Override // android.app.Service
    public final void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        h(this, "OnTaskRemoved");
    }

    @Override // android.app.Service
    public final boolean onUnbind(Intent intent) {
        h(this, "OnUnbind");
        return super.onUnbind(intent);
    }
}
