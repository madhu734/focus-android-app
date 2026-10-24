package p019ai.focus;

import Fj.C0530m0;
import android.os.Bundle;
import androidx.annotation.Keep;
import p060c.AbstractC3122l;
import p060c.K;

/* JADX INFO: loaded from: classes.dex */
@Keep
public final class OverlayActivity extends K {
    public static final int $stable = 0;

    @Override // p470r.b, p019ai.focusapp.payments.ui.CheckoutActivity, Eg.r, androidx.fragment.app.A, Mg.m, p709zk.g, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initWindowSizeTracking();
        C0530m0 c0530m0 = new C0530m0(this);
        c0530m0.setContent(AbstractC3122l.f48788a);
        setContentView(c0530m0);
    }
}
