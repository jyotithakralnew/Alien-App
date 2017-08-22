package alien.com.myapplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.alien.rfid.Bank;
import com.alien.rfid.InvalidParamException;
import com.alien.rfid.LockFields;
import com.alien.rfid.LockType;
import com.alien.rfid.Mask;
import com.alien.rfid.RFID;
import com.alien.rfid.RFIDCallback;
import com.alien.rfid.RFIDReader;
import com.alien.rfid.RFIDResult;
import com.alien.rfid.ReaderException;
import com.alien.rfid.Tag;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public void readTag(View view) {
        try {
            // Get global RFID Reader instance
            RFIDReader reader = RFID.open();
            // Read a single tag
            RFIDResult result = reader.read();
            if (!result.isSuccess()) {
                Toast.makeText(this, "No tags found ", Toast.LENGTH_LONG).show();
                return;
            }
            // Display tag EPC and RSSI
            Tag tag = (Tag) result.getData();
            String msg = tag.getEPC() + ", rssi=" + tag.getRSSI();
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } catch (ReaderException e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }


    public void inventory(RFIDCallback callback, Mask mask []) throws ReaderException{
        try {
            // Get global RFID Reader instance
            final RFIDReader reader = RFID.open();
            if (reader.isRunning()) {
                reader.inventory(new RFIDCallback() {
                                     @Override
                                     public void onTagRead(Tag tag) {
                                         String epc = tag.getEPC();
                                         double rssi = tag.getRSSI();
                                         Toast.makeText(getApplicationContext(),"EPC: "+ epc + "  " + "RSSI: " + rssi , Toast.LENGTH_LONG).show();
                                         Log.d("Continuous inventory: ",epc + "  " + rssi);

                                     }
                                 }, Mask.maskEPC("3035")
                );
            }

            reader.stop();
        } catch (ReaderException e) {
            Log.d("DEMO", "continuousInventory: " + e);
        }
    }



    public RFIDResult write(Bank bank, int wordOffset, String data, Mask mask [], String accessPassword []) throws ReaderException {
        try {
            final RFIDReader reader = RFID.open();
            // Write "AABB" to the beginning of the USER memory bank of the tag which
            // EPC starts with "3035"
            RFIDResult writeResult = reader.write(Bank.USER, 0, "AABB", Mask.maskEPC("3035"));
            if (writeResult.isSuccess()) {
                // Write operation succeeded.
                Toast.makeText(this, "write successful", Toast.LENGTH_LONG).show();
                return writeResult;
            }
            // Read 1 word from the beginning of the USER memory bank of the tag which
            // EPC starts with "3035"

        } catch (ReaderException ex) {
            Toast.makeText(this, "error writing to memory" + ex, Toast.LENGTH_LONG).show();
        }
        return null;
    }


    public RFIDResult read(Bank bank, int wordPointer, int wordCount, Mask mask [], String accessPassword []) throws ReaderException{
        try {
            final RFIDReader reader = RFID.open();
            RFIDResult readResult = reader.read(Bank.USER, 0, 1, Mask.maskEPC("3035"));
            if (readResult.isSuccess()) {
                // Read operation succeeded.
                String data = (String) readResult.getData(); // data returned as a hex string
                Toast.makeText(this, "read successful" + data, Toast.LENGTH_LONG).show();
                return readResult;
            }
        } catch (ReaderException ext) {
            Toast.makeText(this, "error reading from memory" + ext, Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public RFIDResult lock(LockFields fieldBitmap, LockType lockType, Mask mask[], String accessPassword[]) throws ReaderException {
        try {
            RFIDReader reader = RFID.open();
            RFIDResult result = reader.read();
            LockFields fields = new LockFields(
                    LockFields.EPC | LockFields.USER | LockFields.ACCESS_PWD | LockFields.KILL_PWD);


            RFIDResult lockResult = reader.lock(fields, LockType.LOCK, Mask.maskEPC("3035"));
            if (lockResult.isSuccess()) {
                String msg1 = (String) lockResult.getData();
                Toast.makeText(this, "lock operation successful" + msg1, Toast.LENGTH_LONG).show();
                return lockResult;

            }
        } catch (ReaderException ext) {
            Toast.makeText(this, "error locking the fields" + ext, Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void Mask(Bank bank, int bitOffset, int bitLength, String data){
        try {
            Mask mask;
            mask = new Mask(Bank.EPC, 32, 16, "3035");
        } catch (InvalidParamException e) {
            e.printStackTrace();
        }
    }


    public static Mask maskEPC(String data){
        try {
            Mask mask;
            mask = Mask.maskEPC("3035");
            return mask;
        } catch (InvalidParamException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();
       //Mask[] m = new Mask[2];
        //Mask m[0] = new Mask(Bank.EPC,12,15,);
        //Mask m[1] = new Mask(Bank.RESERVED,16,32,);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://alien.com.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://alien.com.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
