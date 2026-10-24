package p019ai.focus;

import Eg.C0434q;
import Gc.V1;
import Mg.q;
import Mk.L0;
import Mk.M0;
import Mk.N0;
import Ng.f;
import Pv.M;
import Pv.t0;
import Pv.v0;
import Um.I;
import Uv.d;
import Wv.e;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import androidx.lifecycle.Z;
import com.revenuecat.purchases.CustomerInfo;
import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener;
import java.util.Objects;
import kotlin.jvm.internal.m;
import p019ai.focus.feature_focus_timer.domain.use_case.T;
import p060c.A;
import p060c.C;
import p060c.C3149z;
import p060c.G;
import p315la.j;

/* JADX INFO: loaded from: classes.dex */
public final class FTOverlayActivity extends G implements UpdatedCustomerInfoListener {

    /* JADX INFO: renamed from: I, reason: collision with root package name */
    public T f43165I;

    /* JADX INFO: renamed from: X, reason: collision with root package name */
    public V1 f43166X;

    /* JADX INFO: renamed from: Y, reason: collision with root package name */
    public final d f43167Y;

    /* JADX INFO: renamed from: Z, reason: collision with root package name */
    public C f43168Z;

    /* JADX INFO: renamed from: l0, reason: collision with root package name */
    public t0 f43169l0;

    /* JADX INFO: renamed from: m0, reason: collision with root package name */
    public t0 f43170m0;

    /* JADX INFO: renamed from: n0, reason: collision with root package name */
    public I f43171n0;

    public FTOverlayActivity() {
        this.f48637e = false;
        addOnContextAvailableListener(new C0434q(this, 6));
        v0 v0VarF = Pv.C.f();
        e eVar = M.f24563a;
        Wv.d dVar = Wv.d.f37341X;
        dVar.getClass();
        this.f43167Y = Pv.C.c(j.l(dVar, v0VarF));
    }

    public final T i() {
        T t2 = this.f43165I;
        if (t2 != null) {
            return t2;
        }
        m.p("focusTimerUseCases");
        throw null;
    }

    public final void j(Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("from_c68_alarm", false);
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(booleanExtra);
            setTurnScreenOn(booleanExtra);
        } else if (booleanExtra) {
            getWindow().addFlags(2621440);
        } else {
            getWindow().clearFlags(2621440);
        }
    }

    @Override // p470r.b, p019ai.focusapp.payments.ui.CheckoutActivity, Eg.r, androidx.fragment.app.A, Mg.m, p709zk.g, android.app.Activity
    public final void onCreate(Bundle bundle) {
        com.bumptech.glide.d m2;
        q.b(this);
        super.onCreate(bundle);
        Intent intent = getIntent();
        m.g(intent, "getIntent(...)");
        j(intent);
        Window window = getWindow();
        B0.d dVar = new B0.d(getWindow().getDecorView());
        int i8 = Build.VERSION.SDK_INT;
        if (i8 >= 35) {
            m2 = new N0(window, dVar, 1);
        } else if (i8 >= 30) {
            m2 = new L0(window, dVar, 1);
        } else {
            m2 = i8 >= 26 ? new M0(window, dVar, 0) : new L0(window, dVar, 0);
        }
        m2.A(false);
        Purchases.Companion companion = Purchases.Companion;
        if (companion.isConfigured()) {
            companion.getSharedInstance().setUpdatedCustomerInfoListener(this);
        }
        initWindowSizeTracking();
        C c10 = new C(this, 0);
        this.f43168Z = c10;
        if (c10.canDetectOrientation()) {
            C c11 = this.f43168Z;
            if (c11 == null) {
                m.p("orientationEventListener");
                throw null;
            }
            c11.enable();
        }
        getWindow().addFlags(128);
        f.a(this, new Yi.d(new G4.d(this, 21), -229698564, true));
    }

    @Override // p019ai.focusapp.payments.ui.CheckoutActivity, Eg.r, Tg.AbstractActivityC1930k, androidx.fragment.app.A, android.app.Activity
    public final void onDestroy() {
        super.onDestroy();
        C c10 = this.f43168Z;
        if (c10 == null) {
            m.p("orientationEventListener");
            throw null;
        }
        c10.disable();
        getWindow().clearFlags(128);
        Purchases.Companion.getSharedInstance().removeUpdatedCustomerInfoListener();
    }

    @Override // Mg.m, android.app.Activity
    public final void onNewIntent(Intent intent) {
        I i8;
        m.h(intent, "intent");
        Objects.toString(intent.getData());
        super.onNewIntent(intent);
        setIntent(intent);
        j(intent);
        if (intent.getData() == null || (i8 = this.f43171n0) == null) {
            return;
        }
        i8.o(intent);
    }

    @Override // com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
    public final void onReceived(CustomerInfo customerInfo) {
        m.h(customerInfo, "customerInfo");
        Pv.C.A(Z.g(this), null, null, new A(this, customerInfo, null), 3);
    }

    @Override // p470r.b, androidx.fragment.app.A, android.app.Activity
    public final void onResume() {
        super.onResume();
        t0 t0Var = this.f43170m0;
        if (t0Var != null) {
            t0Var.g(null);
        }
        this.f43170m0 = Pv.C.A(this.f43167Y, null, null, new C3149z(this, null), 3);
    }
}
