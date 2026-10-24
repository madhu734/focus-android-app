package p019ai.focus;

import Eg.C0434q;
import Fj.C0530m0;
import android.os.Bundle;
import p060c.AbstractC3120k;
import p060c.C3128o;
import p060c.C3141v;
import p060c.InterfaceC3110f;
import p076cg.a;
import p470r.b;

/* JADX INFO: loaded from: classes.dex */
public final class BlockShortsOverlayActivity extends b {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f43164e = false;

    public BlockShortsOverlayActivity() {
        addOnContextAvailableListener(new C0434q(this, 5));
    }

    @Override // Eg.r
    public final void inject() {
        if (this.f43164e) {
            return;
        }
        this.f43164e = true;
        C3128o c3128o = (C3128o) ((InterfaceC3110f) generatedComponent());
        C3141v c3141v = c3128o.f48803a;
        this.repository = (a) c3141v.f48941Z.get();
        this.logAnalyticsEvent = c3141v.x3();
        this.getABBoolValueUsingStringVariants = c3128o.b();
        this.checkAndSetProxyLauncherEnabled = c3128o.a();
    }

    @Override // p470r.b, p019ai.focusapp.payments.ui.CheckoutActivity, Eg.r, androidx.fragment.app.A, Mg.m, p709zk.g, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initWindowSizeTracking();
        C0530m0 c0530m0 = new C0530m0(this);
        c0530m0.setContent(AbstractC3120k.f48783a);
        setContentView(c0530m0);
    }
}
