package dhafer.tunisietelecom;

import android.app.Application;
import com.firebase.client.Firebase;

/**
 * Created by Dhafer ZAHROUNI on 31/03/2016.
 */
public class SendDataToFireBase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
