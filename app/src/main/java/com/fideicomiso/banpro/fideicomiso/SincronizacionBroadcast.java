package com.fideicomiso.banpro.fideicomiso;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by root on 11/01/18.
 */

public class SincronizacionBroadcast  extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        ctx.startService(new Intent(ctx, SincronizacionService.class));
    }
}
