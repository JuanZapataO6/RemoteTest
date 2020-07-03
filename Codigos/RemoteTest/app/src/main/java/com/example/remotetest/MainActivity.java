package com.example.remotetest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.os.Bundle;
import android.widget.VideoView;

import java.io.File;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageButton btn_on,btn_off,btn_right,btn_left,
            btn_ma,btn_up,btn_down,btn_start,
            btn_stop;
    TextView Read_Hmd;
    TextView Read_Temp;
    ImageView Image_Down;
    Uri UriVideo;
    boolean flag_ma= true;
    public int  MaskImage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final DatabaseReference myRef = database.getReference("Remote");
        final DatabaseReference ON_OFF = myRef.child("ON_OFF");
        final DatabaseReference MAN_AUT = myRef.child("MAN_AUT");
        final DatabaseReference DIRECC = myRef.child("IZQ_DER");
        final DatabaseReference UP_D = myRef.child("UP_DOW");
        final DatabaseReference STAR_STOP = myRef.child("STAR_STOP");
        final DatabaseReference myRefTemp = database.getReference("Remote/temp");
        final DatabaseReference myRefHmd = database.getReference("Remote/Hum");
        super.onCreate(savedInstanceState);
        storageReference.getStorage();
        setContentView(R.layout.activity_main);
        btn_on   = findViewById(R.id.Btn_On);
        //btn_on.setText("ON");
        btn_off   = findViewById(R.id.Btn_Off);
        //btn_off.setText("OFF");
        btn_right = findViewById(R.id.Btn_Right);
        //btn_right.setText("RIGHT");
        btn_left  = findViewById(R.id.Btn_Left);
        //btn_left.setText("LEFT");
        btn_up = findViewById(R.id.Btn_Up);
        //btn_up.setText("UP");
        btn_down  = findViewById(R.id.Btn_Down);
        //btn_down.setText("DOWN");
        btn_start = findViewById(R.id.Btn_Star);
        btn_stop  = findViewById(R.id.Btn_Stop);
        //btn_start.setText("START");
        //btn_stop.setText("STOP");
        //btn_graph = findViewById(R.id.Btn_Graph);

        btn_ma    = findViewById(R.id.BtnMan_Auto);
        //btn_ma.setText("M");
        Read_Temp =findViewById(R.id.State_Tmp);
        Image_Down = findViewById(R.id.Video_Download);
        Read_Hmd =findViewById(R.id.State_Hmd);

        Timer timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask,1, 9000);




        btn_ma.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(flag_ma==true){
                flag_ma=false;
                //btn_ma.setText("A");
                btn_right.setEnabled(false);
                btn_right.setBackgroundResource(R.drawable.rightdisable);
                btn_left.setEnabled(false);
                btn_left.setBackgroundResource(R.drawable.leftdisable);
                btn_up.setEnabled(false);
                btn_up.setBackgroundResource(R.drawable.updisable);
                btn_down.setEnabled(false);
                btn_down.setBackgroundResource(R.drawable.downdisable);
                MAN_AUT.setValue("1");
                btn_ma.setBackgroundResource(R.drawable.auto);
            }
            else{
                flag_ma=true;
                //btn_ma.setText("M");
                btn_right.setEnabled(true);
                btn_right.setBackgroundResource(R.drawable.right);
                btn_left.setEnabled(true);
                btn_left.setBackgroundResource(R.drawable.left);
                btn_up.setEnabled(true);
                btn_up.setBackgroundResource(R.drawable.up);
                btn_down.setEnabled(true);
                btn_down.setBackgroundResource(R.drawable.down);
                MAN_AUT.setValue("0");
                btn_ma.setBackgroundResource(R.drawable.man);

            }
        }
    });

        btn_left.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View view) {
                DIRECC.setValue("0");
            }
        });
        btn_right.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View view) {
                DIRECC.setValue("1");
            }
        });
        btn_up.setOnClickListener(new View.OnClickListener(

        ) {
            @Override
            public void onClick(View view) {
                UP_D.setValue("0");
            }
        });
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UP_D.setValue("1");
            }
        });
        myRefTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String value = dataSnapshot.getValue(String.class);
                Read_Temp.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        myRefHmd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Read_Hmd.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               //String newPost = dataSnapshot.getValue(String.class);
               //Read_Sensor.setText(newPost);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               /*String changedPost = dataSnapshot.getValue(String.class);
                Read_Sensor.setText(changedPost);*/
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ON_OFF.setValue("0");
            btn_off.setEnabled(false);
            btn_on.setEnabled(true);
            btn_on.setBackgroundResource(R.drawable.on);
            btn_off.setBackgroundResource(R.drawable.ondisable);

            }
        });
        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ON_OFF.setValue("1");
                btn_off.setEnabled(true);
                btn_off.setBackgroundResource(R.drawable.off);
                btn_on.setEnabled(false);
                btn_on.setBackgroundResource(R.drawable.ondisable);

            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STAR_STOP.setValue("1");
                btn_start.setEnabled(false);
                btn_start.setBackgroundResource(R.drawable.startdisable);
                btn_stop.setEnabled(true);
                btn_stop.setBackgroundResource(R.drawable.stop);

            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                STAR_STOP.setValue("0");
                btn_start.setEnabled(true);
                btn_start.setBackgroundResource(R.drawable.start);
                btn_stop.setEnabled(false);
                btn_stop.setBackgroundResource(R.drawable.stopdisable);

            }
        });
}

    class MyTimerTask extends TimerTask{


        @Override
        public void run() {

            runOnUiThread(new Runnable() {

                public void run() {
                   MaskImage++;
                    class StringSignature implements Key {
                        private String currentVersion;
                        public StringSignature(String currentVersion) {
                            this.currentVersion = (currentVersion);
                        }

                        @Override
                        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                        }
                    }
                    class GlideOptions {
                        public  RequestOptions LOGO_OPTION = new RequestOptions().placeholder(R.mipmap.ic_launcher).centerCrop()
                                .dontAnimate().dontTransform().diskCacheStrategy(DiskCacheStrategy.RESOURCE);

                        public  void updateSignatureOptions(String version){
                            LOGO_OPTION = LOGO_OPTION.signature(new StringSignature(version));
                        }
                    }
                    if(MaskImage==1){
                        UriVideo=  Uri.parse("https://firebasestorage.googleapis.com/v0/b/iot-prueba-8d2f8.appspot.com/o/CurrentImage_1?alt=media&token=2b4d4feb-b127-4a45-801c-49fdc20acb3a");
                        Glide.with(MainActivity.this)
                                .load(UriVideo)
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                .apply(RequestOptions.signatureOf(new StringSignature("s://iot-prueba-8d2f8.appspot.com/CurrentImage_1")))
                                .into(Image_Down);
                    }else {
                        if (MaskImage == 2) {
                            UriVideo = Uri.parse("https://firebasestorage.googleapis.com/v0/b/iot-prueba-8d2f8.appspot.com/o/CurrentImage_2?alt=media&token=98b3c59b-36f2-4bae-a8ff-867019aa0d15");
                            Glide.with(MainActivity.this)
                                    .load(UriVideo)
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .apply(RequestOptions.signatureOf(new StringSignature("s://iot-prueba-8d2f8.appspot.com/CurrentImage_1")))
                                    .into(Image_Down);
                        } else {
                            if (MaskImage == 3) {
                                UriVideo = Uri.parse("https://firebasestorage.googleapis.com/v0/b/iot-prueba-8d2f8.appspot.com/o/CurrentImage_3?alt=media&token=e865a10e-d5c4-4b72-b55b-a9d1ddeb1e3c");
                                MaskImage = 0;
                                Glide.with(MainActivity.this)
                                        .load(UriVideo)
                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                        .apply(RequestOptions.signatureOf(new StringSignature("s://iot-prueba-8d2f8.appspot.com/CurrentImage_1")))
                                        .into(Image_Down);
                            }
                        }
                    }
                }
            });
        }
    }
    /*public class Hilo_Images extends Thread {

        public void  run (){

            /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UriVideo = Uri.parse("https://firebasestorage.googleapis.com/v0/b/iot-prueba-8d2f8.appspot.com/o/CurrentImage?alt=media&token=e9b8512b-56aa-442b-8322-0d2694655d79");
                        runOnUiThread(new Runnable() {
                            public void  run () {
                                for (int i=0;i<=1000 ;i++ ) {

                                    try {
                                        Thread.sleep(500);

                                        class StringSignature implements Key {
                                            public StringSignature(String s) {
                                            }

                                            @Override
                                            public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

                                            }
                                        }

                                     /*   Glide.with(MainActivity.this)
                                                .load(UriVideo)
                                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                                .apply(RequestOptions.signatureOf(new StringSignature(String.valueOf(UriVideo))))
                                                .into(Image_Down);*/
                                  /*  } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });

            }
        }*/

}
