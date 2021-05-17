package com.misentek.digitalroomcall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.misentek.digitalroomcall.ui.home.HomeFragment;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import static com.misentek.digitalroomcall.NadaAdpter.prevP;
import static com.misentek.digitalroomcall.NadaAdpter.prevS;
import static com.misentek.digitalroomcall.ui.home.HomeFragment.jadwalAdapter;
import static com.misentek.digitalroomcall.ui.home.HomeFragment.jadwalArrayList;
import static com.misentek.digitalroomcall.ui.ruangan.RuanganFragment.btnHapusRuangan;
import static com.misentek.digitalroomcall.ui.ruangan.RuanganFragment.ruanganAdapter;
import static com.misentek.digitalroomcall.ui.tambahjadwal.TambahJadwalFragment.checkButton;
import static com.misentek.digitalroomcall.ui.tambahjadwal.TambahJadwalFragment.getRealPathFromURI;
import static com.misentek.digitalroomcall.ui.tambahjadwal.TambahJadwalFragment.txtNamaJadwal;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<SetRuangan> setRuanganArrayList;
    public static ArrayList<Ruangan> ruanganArrayList;
    public static ArrayList<Ruangan> ruanganArrayListSemua;
    public static RecyclerView recyclerViewRuangan;
    public static RecyclerView recyclerViewNada;
    public static WebSocketClient mWebSocketClient;

    public static List<String> nadaList;
    private AppBarConfiguration mAppBarConfiguration;

    public static MaterialButtonToggleGroup materialButtonToggleGroup;
    public static MaterialButtonToggleGroup materialButtonToggleGroupHari;
    public static RecyclerView recyclerViewJadwal;
    public static TextView txtStatus;
    public static Activity activity;
    public static Button btnSimpanRuangan;
    public static Button btnBatalRuangan;
    public static String currentActiveButton="Semua Ruangan";
    public static String currentActiveFragment="home";
    public static String currentActiveJadwal="";
    public static String currentHari;
    public static String currentHariF;
    public static NadaAdpter nadaAdpter;
    public static String songs="";
    public static MaterialButton btnTambahRuangan;
    public static MaterialButton btnSemuaRuangan;
    private static Menu menu;
    public static Toolbar toolbar;
    static Handler handler;
    static Timer timer;
    static TimerTask timerTask;
    public static Calendar c;
    public static ProgressDialog progress;
    public static String prevState;

    public static String cardType="";
    public static String cardSize="";
    public static String cardUsed="";
    public static Integer vol=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        txtStatus = findViewById(R.id.txtStatus);
        this.activity=this;
        progress = new ProgressDialog(this);
        progress.setTitle("Tunggu");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        tryToConnect();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("Mengambil ulang data, mohon tunggu");
                tryToConnect();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_manual, R.id.nav_jadwal, R.id.nav_ruangan, R.id.nav_nada)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }

    public static void showProgress(String s) {
        if (!progress.isShowing()){
            progress.setMessage(s);
            progress.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        this.menu=menu;

        updateMenu();
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Log.d("fak",s);
                if (currentActiveFragment.equals("Nada") || currentActiveFragment.equals("Bel Manual")){
                    List<String> nada = new ArrayList<>();
                    if (s.equals("")){
                        nada.addAll(nadaList);
                    }else{
                        for (int x=0;x<nadaList.size();x++){
                            if (nadaList.get(x).toLowerCase().contains(s.toLowerCase())){
                                nada.add(nadaList.get(x));
                            }
                        }
                    }
                    nadaAdpter=new NadaAdpter(nada);
                    recyclerViewNada.setAdapter(nadaAdpter);
                }else{
                    ArrayList<Jadwal> tempJadwal = new ArrayList<>();
                    if (s.equals("")){
                        tempJadwal.addAll(jadwalArrayList);
                    }else{
                        for (int x=0;x<jadwalArrayList.size();x++){
                            if (
                                jadwalArrayList.get(x).getNama().toLowerCase().contains(s.toLowerCase()) ||
                                jadwalArrayList.get(x).getRuangan().toLowerCase().contains(s.toLowerCase()) ||
                                jadwalArrayList.get(x).getNada().toLowerCase().contains(s.toLowerCase())
                            ){
                                if (currentActiveFragment.equals("home")){
                                    if (jadwalArrayList.get(x).getHari().trim().toLowerCase().contains(currentHariF.trim().toLowerCase())){
                                        tempJadwal.add(jadwalArrayList.get(x));
                                    }
                                }else{
                                    tempJadwal.add(jadwalArrayList.get(x));
                                }
                            }
                        }
                    }
                    jadwalAdapter=new JadwalAdapter(tempJadwal);
                    recyclerViewJadwal.setAdapter(jadwalAdapter);
                }

                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.pengaturan){
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.nav_settings);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void tryToConnect(){
        try {

            connectWebSocket();
            txtStatus.setText("Menghubungkan...");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            txtStatus.setText("Gagal terhubung ke Digital Room Call ..." + e.getMessage());
        } catch (Exception e){
            txtStatus.setText("Gagal terhubung ke Digital Room Call ..." + e.getMessage());
        }
    }

    public static void connectWebSocket() throws URISyntaxException {
        URI uri;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String ipaddr = preferences.getString("ipaddr","192.168.43.142");
        uri = new URI("ws://"+ipaddr+":81/");
        if (mWebSocketClient!=null){
            mWebSocketClient.close();
            mWebSocketClient=null;
        }

        mWebSocketClient = new WebSocketClient(uri,new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                txtStatus.setText("terhubung ke " + ipaddr);
                sendMessage("getWifi_");
                if (currentActiveFragment.equals("home") || currentActiveFragment.equals("jadwal")){
                    sendMessage("get_jadwal");
                }else if(currentActiveFragment.equals("Bel Manual") || currentActiveFragment.equals("Nada")){
                    sendMessage("get_data_bel_manual");
                }else if(currentActiveFragment.equals("ruangan")){
                    sendMessage("get_data_ruangan");
                }
                //mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                //Log.d("fak",s);
                 activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.startsWith("waktu__")){
                            String msg = message.replace("waktu__","");
                            String[] w = msg.split(";")[0].split(",");
                            String temp = msg.split(";")[1];
                            Date d = new Date(Integer.parseInt(w[0]) ,Integer.parseInt(w[1]) ,Integer.parseInt(w[2]) ,Integer.parseInt(w[3]) ,Integer.parseInt(w[4]) ,Integer.parseInt(w[5]));
                            c=Calendar.getInstance();
                            TimeZone timeZone=TimeZone.getTimeZone("Asia/Jakarta");
                            c.setTimeZone(timeZone);
                            c.setTime(d);
                            c.set(Integer.parseInt(w[0]) ,Integer.parseInt(w[1]) ,Integer.parseInt(w[2]) ,Integer.parseInt(w[3]) ,Integer.parseInt(w[4]) ,Integer.parseInt(w[5]));
                            String[] months = {"Januari", "Februari", "Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
                            String[] days = {"Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};

                            currentHari = days[c.get(Calendar.DAY_OF_WEEK)-1];
                            currentHariF=currentHari;
                            if (menu!=null){
                                MenuItem time = menu.findItem(R.id.waktu);
                                if (timer!=null){
                                    timer.cancel();
                                }

                                handler = new Handler();
                                timer = new Timer(false);
                                timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                c.add(Calendar.SECOND,1);
                                                int dd = c.get(Calendar.DATE);
                                                int mm = c.get(Calendar.MONTH);
                                                int yy = c.get(Calendar.YEAR);

                                                String namabulan = months[mm];
                                                String hh ="0"+c.get(Calendar.HOUR_OF_DAY);
                                                hh=hh.substring(hh.length()-2);
                                                String m = "0"+c.get(Calendar.MINUTE);
                                                m=m.substring(m.length()-2);
                                                String s = "0"+c.get(Calendar.SECOND);
                                                s = s.substring(s.length()-2);
                                                String waktu=hh +":"+ m +":"+ s+"\n"+ currentHari + ", " + dd + " " + namabulan + " " + yy;

                                                time.setTitle(waktu);
                                            }
                                        });
                                    }
                                };
                                timer.scheduleAtFixedRate(timerTask, 1000, 1000); // every 5 seconds.
                            }


                            materialButtonToggleGroupHari.removeAllViews();
                            for (int x=0;x<days.length;x++){
                                MaterialButton btnRuangan = (MaterialButton)  activity.getLayoutInflater().inflate(R.layout.single_button_layout, materialButtonToggleGroup, false);
                                btnRuangan.setText(days[x]);
                                btnRuangan.setTag(days[x]);
                                materialButtonToggleGroupHari.addView(btnRuangan);
                                //Log.d("fak",days[x]+" = " + currentHari);
                                if (days[x].equals(currentHari) && currentActiveFragment.equals("home")){
                                    materialButtonToggleGroupHari.check(btnRuangan.getId());
                                }
                            }

                            String dd = "0"+c.get(Calendar.DATE);dd=dd.substring(dd.length()-2);
                            String mm = "0"+ (c.get(Calendar.MONTH) + 1);mm=mm.substring(mm.length()-2);
                            int yy = c.get(Calendar.YEAR);
                            String hh ="0"+c.get(Calendar.HOUR_OF_DAY);
                            hh=hh.substring(hh.length()-2);
                            String m = "0"+c.get(Calendar.MINUTE);
                            m=m.substring(m.length()-2);

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString("tanggal", yy+"/"+mm+"/"+dd);
                            editor.putString("jam", hh+":"+mm);
                            editor.apply();


                        }
                        if (message.startsWith("jadwal__")){
                            jadwalArrayList = new ArrayList<>();
                            String[] myarray = message.split("\n");
                            String tr = "";
                            int nextJadwal=0;
                            for (int i=0;i<myarray.length;i++){
                                String str = myarray[i];
                                if (str.indexOf(",")>0){
                                    String[] arr=myarray[i].split(";");
                                    String nama = arr[0].replace("jadwal__","");
                                    String hari = arr[1];
                                    String jam = arr[2];
                                    String nada= arr[3];
                                    String ruangan = arr[4];
                                    String aktif = arr[5];
                                    int h= Integer.parseInt(jam.split(":")[0]);
                                    int m= Integer.parseInt(jam.split(":")[1]);
                                    if (c!=null){
                                        Date d = new Date(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),h,m);
                                        if (c.before(d)){
                                            nextJadwal=i;
                                        }
                                    }

                                    hari=hari.substring(0,hari.length()-1);
                                    String[] arrHari = hari.split(",");
                                    String strHari="";
                                    for (int x=0;x<arrHari.length;x++){
                                        if(arrHari[x].endsWith("_on")){
                                            strHari+=arrHari[x].replace("_on","").replace("chk","").toUpperCase()+", ";
                                        }
                                    }
                                    if (strHari.length()>1){
                                        strHari=strHari.substring(0,strHari.length()-1);
                                    }
                                    jadwalArrayList.add(new Jadwal(nama, strHari, jam,nada  , ruangan,aktif));
                                }
                            }
                            Collections.sort(jadwalArrayList, new Comparator<Jadwal>() {
                                @Override
                                public int compare(Jadwal lhs, Jadwal rhs) {
                                    return lhs.getJam().compareTo(rhs.getJam());
                                }
                            });

                            jadwalAdapter = new JadwalAdapter(jadwalArrayList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity.getApplicationContext());
                            recyclerViewJadwal.setLayoutManager(layoutManager);
                            recyclerViewJadwal.setAdapter(jadwalAdapter);
                            recyclerViewJadwal.scrollToPosition(nextJadwal);
                            if (currentActiveFragment.equals("tambahjadwal")){
                                activity.onBackPressed();
                            }else{
                                HomeFragment.callBack();
                            }
                        }
                        if (message.startsWith("songs__")){

                            String msg=message.replace("songs__","");
                            nadaList = new ArrayList<String>();
                            if (msg.indexOf(",")>0){
                                String[] myarray = msg.split(",");
                                for (int i=0;i<myarray.length;i++){
                                    String str = myarray[i];
                                    nadaList.add(str);
                                }
                            }
                            Collections.sort(nadaList);

                            nadaAdpter = new NadaAdpter(nadaList);
                            if (recyclerViewNada!=null){
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
                                recyclerViewNada.setLayoutManager(layoutManager);
                                recyclerViewNada.setAdapter(nadaAdpter);
                            }


                        }
                        if (message.startsWith("speakerstate__")){
                            String msg = message.replace("speakerstate__","");
                            if (!msg.equals("")){
                                String roomsname = msg.split(";")[1];
                                if (roomsname!=null && !roomsname.equals("")){
                                    ArrayList<View> viewArrayList = new ArrayList<>();
                                    if (materialButtonToggleGroup!=null){
                                        materialButtonToggleGroup.findViewsWithText(viewArrayList,roomsname.trim().toLowerCase(),View.FIND_VIEWS_WITH_TEXT);
                                        if (viewArrayList.size()>0 && msg.startsWith("speaker_on")){
                                            for (int x=0;x<viewArrayList.size();x++){
                                                int id=viewArrayList.get(x).getId();
                                                Button button=materialButtonToggleGroup.findViewById(id);
                                                if (button.getText().toString().trim().toLowerCase().equals(roomsname.trim().toLowerCase())){
                                                    materialButtonToggleGroup.check(id);
                                                }
                                            }

                                        }
                                    }

                                }
                            }



                        }
                        if (message.startsWith("rooms__")){
                            String msg=message.replace("rooms__","");

                            setRuanganArrayList = new ArrayList<>();
                            String[] myarray = msg.split("\n");
                            if (materialButtonToggleGroup!=null){
                                materialButtonToggleGroup.removeAllViews();

                                btnSemuaRuangan = (MaterialButton)  activity.getLayoutInflater().inflate(R.layout.single_button_layout, materialButtonToggleGroup, false);
                                btnSemuaRuangan.setText("Semua Ruangan");
                                btnSemuaRuangan.setChecked(true);
                                btnSemuaRuangan.setTag(1);
                                materialButtonToggleGroup.addView(btnSemuaRuangan);
                                if (!currentActiveFragment.equals("Bel Manual")){
                                    materialButtonToggleGroup.setSelectionRequired(true);
                                }
                            }



                            addRuangan(false);
                            addRuangan(true);
                            addRuangan(false);
                            Integer index=3;
                            for (int i=0;i<myarray.length;i++){
                                String str = myarray[i];
                                if (str.indexOf(",")>0){
                                    String nama = str.split("~")[0].split(":")[1];
                                    String ruangans = str.split("~")[1];
                                    ruanganArrayList = new ArrayList<>();
                                    String[] ruangan = ruangans.split(",");
                                    for (int x=0;x<ruangan.length;x++){
                                        String strRuangan = ruangan[x];
                                        String id=String.valueOf(Integer.parseInt(strRuangan.split("_")[1].split("_")[0])+1) ;
                                        Boolean selected=false;
                                        if (strRuangan.endsWith("_on")){
                                            selected=true;
                                        }
                                        ruanganArrayList.add(new Ruangan("Ruangan " + id,selected));
                                    }
                                    setRuanganArrayList.add(new SetRuangan(nama,ruanganArrayList,false));

                                    if (materialButtonToggleGroup!=null){
                                        MaterialButton btnRuangan = (MaterialButton)  activity.getLayoutInflater().inflate(R.layout.single_button_layout, materialButtonToggleGroup, false);
                                        btnRuangan.setText(nama);
                                        btnRuangan.setTag(index);
                                        materialButtonToggleGroup.addView(btnRuangan);
                                    }

                                    index++;
                                }
                            }

                            if (materialButtonToggleGroup!=null){
                                btnTambahRuangan = (MaterialButton)  activity.getLayoutInflater().inflate(R.layout.single_button_layout, materialButtonToggleGroup, false);
                                btnTambahRuangan.setText("Tambah Set Ruangan");
                                btnTambahRuangan.setTag(0);
                                materialButtonToggleGroup.addView(btnTambahRuangan);



                                MaterialButton btnUbahNamaRuangan = (MaterialButton)  activity.getLayoutInflater().inflate(R.layout.single_button_layout, materialButtonToggleGroup, false);
                                btnUbahNamaRuangan.setText("Ubah Nama Ruangan");
                                btnUbahNamaRuangan.setTag(2);
                                materialButtonToggleGroup.addView(btnUbahNamaRuangan);


                                MaterialButton btnManual = (MaterialButton)  activity.getLayoutInflater().inflate(R.layout.single_button_layout, materialButtonToggleGroup, false);
                                btnManual.setText("Manual");
                                btnManual.setTag(0);
                                materialButtonToggleGroup.addView(btnManual);

                                materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                                    int buttonId = checkedId;
                                    MaterialButton button = materialButtonToggleGroup.findViewById(buttonId);
                                    int idx = (int) button.getTag();
                                    currentActiveButton=button.getText().toString();
                                    if (currentActiveButton.equals("Manual")){
                                        if (isChecked){
                                            ModalRuangan cdd=new ModalRuangan(MainActivity.activity, "Semua Ruangan");
                                            cdd.show();
                                        }
                                    }
                                    if (btnHapusRuangan!=null){
                                        btnHapusRuangan.setVisibility(View.VISIBLE);
                                        if (currentActiveButton.equals("Tambah Set Ruangan") || currentActiveButton.equals("Ubah Nama Ruangan")){
                                            btnHapusRuangan.setVisibility(View.GONE);
                                        }
                                    }

                                    if ( idx>=1){
                                        if (isChecked){
                                            getRuangan(idx);
                                        }else{
                                            getRuangan(0);
                                        }
                                        //Toast.makeText(activity, String.valueOf(group.getCheckedButtonId()),Toast.LENGTH_SHORT).show();
                                    }else{
                                        getRuangan(0);
                                    }

                                    if (currentActiveFragment.equals("Bel Manual")){
                                        //Log.d("fak",currentActiveButton);

                                        if (!isChecked){
                                            sendMessage("speaker_all_off");
                                            sendMessage("changeState_speaker_off;"+currentActiveButton);
                                        }else{
                                            if (currentActiveButton.equals("Semua Ruangan")){
                                                sendMessage("speaker_all_on");
                                            }else {
                                                if (!currentActiveButton.equals("Manual")){
                                                    for (int x=0;x<ruanganArrayList.size();x++){
                                                        String state = "_off";
                                                        if (ruanganArrayList.get(x).getSelected()){
                                                            state="_on";
                                                        }
                                                        sendMessage("speaker_"+x+state);
                                                    }
                                                }
                                            }
                                            sendMessage("changeState_speaker_on;"+currentActiveButton);
                                        }
                                    }
                                });
                                if (currentActiveFragment.equals("tambahjadwal") ){
                                    checkButton();
                                    btnTambahRuangan.setVisibility(View.GONE);
                                    btnUbahNamaRuangan.setVisibility(View.GONE);
                                    btnManual.setVisibility(View.GONE);
                                }else if (currentActiveFragment.equals("ruangan")){
                                    materialButtonToggleGroup.check(btnTambahRuangan.getId());
                                    btnSemuaRuangan.setVisibility(View.GONE);
                                    btnManual.setVisibility(View.GONE);
                                }else {
                                    btnManual.setVisibility(View.VISIBLE);
                                    btnTambahRuangan.setVisibility(View.GONE);
                                    btnUbahNamaRuangan.setVisibility(View.GONE);
                                }

                            }


                        }
                        if (message.startsWith("roomsname__")){
                            String str = message.replace("roomsname__","").split("\n")[0];
                            if (str.indexOf(",")>0){
                                String[] roomsName = str.split(",");
                                for (int i=0;i<roomsName.length;i++){
                                    String strRoomsName = roomsName[i].split(":")[1];
                                    //Log.d("fakfak",strRoomsName);
                                    for (int x=0;x<setRuanganArrayList.size();x++){
                                        setRuanganArrayList.get(x).getRuanganArrayList().get(i).setRuangan(strRoomsName);
                                        if (ruanganAdapter!=null){
                                            ruanganAdapter.notifyItemChanged(x);
                                        }

                                    }
                                    //ruanganArrayList.get(i).setRuangan(strRoomsName);
                                   // ruanganAdapter.notifyItemChanged(i);
                                }

                                if (recyclerViewRuangan!=null){
                                    ruanganAdapter = new RuanganAdapter(ruanganArrayList);
                                    recyclerViewRuangan.setAdapter(ruanganAdapter);
                                }

                            }


                        }
                        if (message.startsWith("wifi__")){
                            String msg = message.replace("wifi__","");
                            if (message.contains(";")){
                                String ssid = msg.split(";")[0];
                                String password = msg.split(";")[1];
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("ssid", ssid);
                                editor.putString("password", password);
                            }
                        }
                        if (message.startsWith("udah_")){
                            if (prevP!=null){
                                prevS.setVisibility(View.GONE);
                                prevP.setVisibility(View.VISIBLE);
                            }
                            if (prevState!=null){
                                txtStatus.setText(prevState);
                            }
                        }
                        if (message.startsWith("jadwalExists__")){
                            String msg=message.replace("jadwalExists__","");
                            if (currentActiveFragment.equals("tambahjadwal")){
                                Toast.makeText(activity,"Jadwal dengan nama " + msg + " sudah ada, silahkan beri nama lain",Toast.LENGTH_SHORT).show();
                                txtNamaJadwal.requestFocus();
                            }
                        }
                        if (message.startsWith("sdcard__")){
                            String msg=message.replace("sdcard__","");
                            String[] arrMsg=msg.split(";");
                            if (arrMsg.length>1){
                                cardType = arrMsg[0];
                                cardSize = arrMsg[1];
                                cardUsed = arrMsg[2];
                            }

                        }
                        progress.dismiss();


                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                txtStatus.setText("Sambungan terputus, periksa wifi anda");
                progress.dismiss();
            }

            @Override
            public void onError(Exception e) {
                txtStatus.setText("Sambungan terputus, periksa wifi anda" + e.getMessage());
                progress.dismiss();
            }
        };
        mWebSocketClient.connect();
    }

    public static void sendMessage(String message) {
        try {
            if(mWebSocketClient!=null){
                if (mWebSocketClient.getConnection().isOpen()){
                    mWebSocketClient.send(message);
                }else{
                    txtStatus.setText("Sambungan terputus");
                    progress.dismiss();
                }
                Log.d("fak",message);
            }


        }catch (Exception e){
            throw e;
        }

    }

    public static void changeState(String msg) {
        prevState = txtStatus.getText().toString();
        txtStatus.setText(msg);
    }

    public static void getRuangan(int id){
        ruanganArrayList = new ArrayList<>();
        ruanganArrayList.addAll(setRuanganArrayList.get(id).getRuanganArrayList());
        ruanganAdapter = new RuanganAdapter(ruanganArrayList);
        recyclerViewRuangan.setAdapter(ruanganAdapter);
    }

    public static void addRuangan(Boolean selected){
        ruanganArrayList = new ArrayList<>();
        for (int x=0;x<32;x++){
            ruanganArrayList.add(new Ruangan("Ruangan " + (x + 1),selected));
        }
        setRuanganArrayList.add(new SetRuangan("Semua Ruangan",ruanganArrayList,false));
    }

    public static void updateMenu(){
        if (menu!=null){
            MenuItem menuItem = menu.findItem(R.id.search);

            if (!currentActiveFragment.equals("ruangan")){
                menuItem.setVisible(true);
            }else{
                menuItem.setVisible(false);
            }
        }

    }

    public static  void initRuangan(Boolean selected){
        ruanganArrayList=new ArrayList<>();
        for (int i=0;i<32;i++){
            String ruangan="Ruangan " + Integer.valueOf(i+1);
            ruanganArrayList.add(new Ruangan(ruangan,selected) );
        }ruanganAdapter = new RuanganAdapter(ruanganArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity, 8);
        recyclerViewRuangan.setLayoutManager(layoutManager);
        recyclerViewRuangan.setAdapter(ruanganAdapter);

    }

    public static void upload(Uri uriSound){
        String selectedFilePath = getRealPathFromURI(activity,uriSound);
        File file = new File(selectedFilePath);
        if (file.isFile()){
            progress.show();
            progress.setMessage("Sedang mengunggah nada...");
            File myDir = new File("/storage/emulated/0/Music/Output");
            if (!myDir.exists()){
                myDir.mkdir();
            }
            String newFilePath="/storage/emulated/0/Music/Output/" + file.getName().split("\\.")[0] +".mp3";

            String query = "-y -i '"+selectedFilePath+"' -map 0:a:0 -b:a 320k -ac 1 '"+newFilePath+"'";
            //String query = "ffmpeg -i '"+selectedFilePath+"' -map 0:a:0 -b:a 96k '/storage/emulated/0/Music/Sound Bel Lainnya/test.mp3'";
            Log.d("mobile-ffmpeg",query);
            long executionId = FFmpeg.executeAsync(query, new ExecuteCallback() {

                @Override
                public void apply(final long executionId, final int returnCode) {
                    if (returnCode == RETURN_CODE_SUCCESS) {
                        Log.d("fak",newFilePath);
                        File newFile = new File(newFilePath);
                        if (newFile.isFile()){
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                            String ipaddr = preferences.getString("ipaddr","192.168.43.142");
                            String url = "http://"+ipaddr+"/upload";
                            //new UploadFileAsync().execute(selectedFilePath);
                            //play(this, uriSound);
                            OkHttpClient client = new OkHttpClient();
                            try {
                                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                        .addFormDataPart("file", newFile.getName(),
                                                RequestBody.create(MediaType.parse("audio/*"), newFile))
                                        .build();

                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(requestBody)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        Log.d("fak","response " +  response.body());
                                        progress.dismiss();
                                    }

                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        Log.d("fak", e.getMessage());
                                        Log.d("fak",e.getCause().toString());
                                        progress.dismiss();
                                    }

                                });
                            }catch (Exception ex) {
                                ex.printStackTrace();
                                progress.dismiss();
                                // Handle the error
                            }
                        }
                        Log.i(Config.TAG, "Async command execution completed successfully.");
                    } else if (returnCode == RETURN_CODE_CANCEL) {
                        progress.dismiss();
                        Log.i(Config.TAG, "Async command execution cancelled by user.");
                    } else {
                        progress.dismiss();
                        Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                    }
                }
            });
        }
    }

    public static void restore(String path){
        File file = new File(path);
        String paths=file.getAbsolutePath();
        Log.d("fak", "is file :" +file.isFile());
        if (file.isFile()){

            progress.show();
            progress.setMessage("Sedang mengembalikan data...");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            String ipaddr = preferences.getString("ipaddr","192.168.43.142");
            String url = "http://"+ipaddr+"/restore";
            //new UploadFileAsync().execute(selectedFilePath);
            //play(this, uriSound);
            OkHttpClient client = new OkHttpClient();
            try {
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(),
                                RequestBody.create(MediaType.parse("text/plain"), file))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.d("fak","response " +  response.body());
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("fak", "fail" + e.getMessage());
                        Log.d("fak",e.getCause().toString());
                        progress.dismiss();
                    }

                });
            }catch (Exception ex) {
                ex.printStackTrace();
                progress.dismiss();
                Log.e("fak", ex.getMessage());
                // Handle the error
            }
        }
    }

}