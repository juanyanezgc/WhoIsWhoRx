package tab.com.whoiswhorx.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import tab.com.whoiswhorx.R;
import tab.com.whoiswhorx.utils.Debug;


public class SplashScreenActivity extends Activity {

    private Thread mWaitingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (mWaitingThread == null) {
            mWaitingThread = new Thread(this::waitPeriod);
            mWaitingThread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWaitingThread != null) {
            mWaitingThread.interrupt();
        }
    }

    private void waitPeriod() {
        try {
            Thread.sleep(getResources().getInteger(R.integer.splash_screen_time));

            if (!isFinishing()) {
                runOnUiThread(this::launchActivity);
            }

        } catch (InterruptedException e) {
            Debug.logWarning(e.getMessage());
        }
    }

    private void launchActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, WhoIsWhoActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
