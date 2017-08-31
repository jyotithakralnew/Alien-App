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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Mask[] inventoryMask;
    RFIDCallback inventoryCallBack;
    HashMap<String,Integer> tagMap = new HashMap<String, Integer>();
    RFIDReader reader;

    public void readTag(View view) {
        try {
            // Get global RFID Reader instance
            reader = RFID.open();
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

    public void continousInventory(View view) throws ReaderException{
        try {
            // Get global RFID Reader instance
            reader = RFID.open();
            if (reader.isRunning()) {

                reader.inventory(new RFIDCallback() {
                                     @Override
                                     public void onTagRead(Tag tag) {

                                         String epc = tag.getEPC();
                                         double rssi = tag.getRSSI();
                                         Toast.makeText(getApplicationContext(),"Inside Inventory\n EPC : "+ epc + "\nRSSI : "+ rssi,Toast.LENGTH_LONG).show();

                                         addTag(tag);
                                     }
                                 }, Mask.maskEPC("3035")
                );

            }
            reader.stop();
        } catch (ReaderException e) {
            Log.d("DEMO", "continuousInventory: " + e);
        }
    }


    public void addTag(final Tag tag) {
        //check for empty tag
        if(tag.getEPC().isEmpty()) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // for now we are just storing the tag.EPC but we can store the whole tag object
                if(tagMap.containsKey(tag.getEPC())){
                    tagMap.put(tag.getEPC(),tagMap.get(tag.getEPC())+1);
                }else{
                    tagMap.put(tag.getEPC(),1);
                }
                Toast.makeText(getApplicationContext(),"Yaayy!!! EPC Stored",Toast.LENGTH_LONG).show();
                Log.d("Recent tag stored : ", tag.getEPC());
                for(Map.Entry<String, Integer> m : tagMap.entrySet()){
                    Log.d("All Tags Stored : ","EPC : " +m.getKey() + "Quantity : "+ m.getValue());
                }
            }
        });
    }



    public RFIDResult writeTag(View view) throws ReaderException {
        try {
            reader = RFID.open();
            Toast.makeText(this, "Adding default data", Toast.LENGTH_LONG).show();
            // For now I am just writing a default value to the tags so that I can validate it later
            RFIDResult writeResult = reader.write(Bank.USER, 0, "AABB", Mask.maskEPC("3035"));
            if (writeResult.isSuccess()) {
                // Write operation succeeded.
                Toast.makeText(this, "write successful", Toast.LENGTH_LONG).show();
                return writeResult;
            }

        } catch (ReaderException ex) {
            Toast.makeText(this, "error writing to memory" + ex, Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public RFIDResult lock(LockFields fieldBitmap, LockType lockType, Mask mask[], String accessPassword[]) throws ReaderException {
        try {
            reader = RFID.open();
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
