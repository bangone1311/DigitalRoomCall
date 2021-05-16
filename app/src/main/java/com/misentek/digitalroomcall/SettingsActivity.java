package com.misentek.digitalroomcall;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;

import static com.misentek.digitalroomcall.MainActivity.activity;
import static com.misentek.digitalroomcall.MainActivity.c;
import static com.misentek.digitalroomcall.MainActivity.cardSize;
import static com.misentek.digitalroomcall.MainActivity.cardType;
import static com.misentek.digitalroomcall.MainActivity.cardUsed;
import static com.misentek.digitalroomcall.MainActivity.restore;
import static com.misentek.digitalroomcall.MainActivity.sendMessage;
import static com.misentek.digitalroomcall.MainActivity.updateMenu;
import static com.misentek.digitalroomcall.MainActivity.upload;
import static com.misentek.digitalroomcall.MainActivity.vol;

public class SettingsActivity extends AppCompatActivity {
    public static Preference preferenceTanggal;
    public static Preference preferenceJam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            Log.d("fak", resultCode + " ; " + requestCode);
            if(resultCode == RESULT_OK && requestCode == 15) {
                BufferedReader br;
                StringBuilder msg= new StringBuilder();
                String listFiles[] = {"/speakerstate.txt","/f.txt","/rooms.txt", "/namarooms.txt", "/wifi.txt"};
                //FileOutputStream os;
                try {
                    br = new BufferedReader(new InputStreamReader(activity.getContentResolver().openInputStream(data.getData())));
                    //WHAT TODO ? Is this creates new file with
                    //the name NewFileName on internal app storage?
                    //os = activity.openFileOutput("newFileName", Context.MODE_PRIVATE);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        //os.write(line.getBytes());
                        msg.append(line).append("\n");
                    }
                    br.close();

                    if (msg.toString().contains("thisisseparatorbetweenfiles")){
                        String[] list = msg.toString().split("thisisseparatorbetweenfiles");
                        for (int x=0;x<list.length;x++){
                            String namaFile = listFiles[x];
                            String isi = list[x];
                            String qry="restore_"+namaFile+"~"+isi;
                            sendMessage(qry);
                            //Log.d("fak",qry );
                        }

                    }else{
                        Toast.makeText(activity,"Maaf, file yang anda pilih tidak valid",Toast.LENGTH_SHORT).show();
                    }

                    //os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(activity,"Restore data berhasil, silahkan mulai ulang aplikasi",Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            if (c!=null){
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int date = c.get(Calendar.DAY_OF_MONTH);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                updateMenu();
                preferenceTanggal = getPreferenceManager().findPreference("tanggal");
                preferenceTanggal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        DatePickerDialog mDatePicker;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    String year= String.valueOf(i);
                                    int m =i1+1;
                                    String month=("0"+m);month=month.substring(month.length()-2);
                                    String day=("0"+i2);day=day.substring(day.length()-2);
                                    String date = year+"/"+month+"/"+day;
                                    //Log.d("fak",date);
                                    sendMessage("updateDate_"+ date);
                                    Toast.makeText(activity,"Berhasil mengubah Tanggal",Toast.LENGTH_SHORT).show();
                                }
                            },year,month,date);
                            mDatePicker.setTitle("Tetapkan tanggal");
                            mDatePicker.show();
                        }
                        return true;
                    }
                });

                preferenceJam = getPreferenceManager().findPreference("jam");
                if (preferenceJam != null) {
                    preferenceJam.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    MainActivity.sendMessage("updateTime_"+selectedHour + ":" + selectedMinute);
                                    Toast.makeText(activity,"Berhasil mengubah Waktu",Toast.LENGTH_SHORT).show();
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Tetapkan waktu");
                            mTimePicker.show();
                            return true;
                        }
                    });
                }

                EditTextPreference preferenceSSID = getPreferenceManager().findPreference("ssid");
                preferenceSSID.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String password = preferences.getString("password","12345678");
                        String wifi = "updateWifi_"+newValue+";"+password;
                        sendMessage(wifi);
                        Toast.makeText(activity,"Berhasil mengubah SSID",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                EditTextPreference preferencePass = getPreferenceManager().findPreference("password");
                preferencePass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String ssid = preferences.getString("ssid","Digital Room Call");
                        String wifi = "updateWifi_"+ssid+";"+newValue;
                        sendMessage(wifi);
                        Toast.makeText(activity,"Berhasil mengubah Password",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                Preference preferenceIP = getPreferenceManager().findPreference("ipaddr");
                preferenceIP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Toast.makeText(activity,"Berhasil mengubah Alamat IP",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                Preference preferenceBackup = getPreferenceManager().findPreference("backup");
                preferenceBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                        String ipaddr = preferences.getString("ipaddr","192.168.43.142");
                        Uri uri = Uri.parse("http://"+ipaddr+"/backup");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(browserIntent);
                        return true;
                    }
                });

                Preference preferenceRestore = findPreference("restore");
                preferenceRestore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle("Peringatan")
                                .setMessage("Jika anda melakukan restore, data saat ini akan terhapus")
                                .setPositiveButton("Restore", (dialog1, which) -> {
                                    Intent in = new Intent(Intent.ACTION_GET_CONTENT).setType("*/*");
                                    startActivityForResult(Intent.createChooser(in,"Pilih file backup"), 15);
                                })
                                .setNegativeButton("Cancel", null)
                                .create();
                        dialog.show();

                        return true;
                    }
                });

                Preference preferenceCheckSWVersion = findPreference("apk_version");
                preferenceCheckSWVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        BufferedReader in = null;
                        String data = null;

                        try{
                            HttpClient httpclient = new DefaultHttpClient();

                            HttpGet request = new HttpGet();
                            URI website = new URI("http://alanhardin.comyr.com/matt24/matt28.php");
                            request.setURI(website);
                            HttpResponse response = httpclient.execute(request);
                            in = new BufferedReader(new InputStreamReader(
                                    response.getEntity().getContent()));

                            // NEW CODE
                            String line = in.readLine();
                            Log.d(line);
                        }catch(Exception e){
                            Log.e("log_tag", "Error in http connection "+e.toString());
                        }
                        return true;
                    }
                });

                findPreference("memory_type").setSummary(cardType);
                findPreference("memory_capacity").setSummary(cardSize+" MB") ;
                findPreference("memory_used").setSummary(cardUsed+" MB");

                Preference volume = findPreference("volume");
                volume.setSummary(vol+"%");
                volume.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final AlertDialog.Builder popDialog = new AlertDialog.Builder(activity);
                        final SeekBar seek = new SeekBar(activity);
                        seek.setMax(100);
                        seek.setProgress(vol);
                        seek.setKeyProgressIncrement(1);

                        popDialog.setIcon(R.drawable.ic_baseline_volume_up_24);
                        popDialog.setTitle("Volume");
                        popDialog.setView(seek);
                        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                vol=i;
                                volume.setSummary(vol+"%");
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                sendMessage("setVolume_"+seekBar.getProgress());
                            }
                        });
                        popDialog.setPositiveButton("Ok",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog,int id)
                            {
                                dialog.dismiss();
                            }
                        });

                        popDialog.show();

                        return true;
                    }
                });

            }

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        public static void callback(){

        }
    }
}