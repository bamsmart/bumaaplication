package com.buma.designapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.db.DbGreenCard;
import com.buma.image.Base64;
import com.buma.session.SessionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GreenCardActivity extends Activity {
    // Image Upload
    private static final int PICK_IMAGE = 1;
    private static final int PICK_Camera_IMAGE = 2;
    // Session Manager Class
    SessionManager session;
    // Format tanggal
    @SuppressLint("SimpleDateFormat")
    Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Uri imageUri;
    MediaPlayer mp = new MediaPlayer();
    private DbGreenCard oprDatabase = null;
    private SQLiteDatabase db = null;
    private TextView waktuBahaya;
    private EditText tempatBahaya;
    private EditText rincianBahaya;
    private EditText rincianTindakan;
    private RadioButton rbAA, rbA, rbB, rbC, rbManual, rbChooser;
    private ImageButton btnSimpan;
    @SuppressWarnings("unused")
    private String kodeBahaya, placeChecked = "";
    private Spinner spinner_jenis, spinner_tempat;
    private Boolean data_baru;
    private String _id = "";
    private String _flag = "";
    private ImageView imgView;
    private Bitmap bitmap;
    private String selectedImagePath;

    public static byte[] convertDrawableToByteArray(Drawable drawableresource) {
        byte[] byteImage = null;
        try {
            Bitmap imgBitmap = ((BitmapDrawable) drawableresource).getBitmap();
            ByteArrayOutputStream imgByteStream = new ByteArrayOutputStream();
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, imgByteStream);
            byteImage = imgByteStream.toByteArray();

        } catch (NullPointerException e) {
            return null;
        }
        return byteImage;
    }

    @SuppressWarnings("unused")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String danger_type = "";
        String place = "";
        Date tanggal = Calendar.getInstance().getTime();
        String time = format.format(tanggal);
        String detail = "";
        String plan = "";
        String danger = "";
        byte[] byteImage = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_card);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "<b>Green Card<b>" + "</font>"));

        Resources res = getResources();
        Bitmap b = BitmapFactory.decodeResource(res, R.drawable.item_bg_green);
        BitmapDrawable bgColor = new BitmapDrawable(res, b);
        actionBar.setBackgroundDrawable(bgColor);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        View view = View.inflate(getApplicationContext(), R.layout.actionbar,
                null);
        actionBar.setCustomView(view);


        imgView = (ImageView) findViewById(R.id.ImageView);
        spinner_tempat = (Spinner) findViewById(R.id.ddltempatbahaya);
        spinner_jenis = (Spinner) findViewById(R.id.ddljeniskondisi);
        rbManual = (RadioButton) findViewById(R.id.rbtempat);
        rbChooser = (RadioButton) findViewById(R.id.rbtempat1);
        rbChooser.setChecked(true);

        // =====================================================
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.tempat_bahaya_array,
                android.R.layout.simple_spinner_dropdown_item);
        spinner_tempat.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.jenis_bahaya_array,
                android.R.layout.simple_spinner_dropdown_item);
        spinner_jenis.setAdapter(adapter);

        // ======================================================
        try {
            oprDatabase = new DbGreenCard(this);
            db = oprDatabase.getWritableDatabase();
            oprDatabase.createTable(db);

            Intent sender = getIntent();
            String status = sender.getExtras().getString("status");
            if (status.equalsIgnoreCase("baru")) {
                data_baru = true;
            } else {
                data_baru = false;
                _id = sender.getExtras().getString("_id");
                danger_type = sender.getExtras().getString("jenis");
                place = sender.getExtras().getString("place");
                time = sender.getExtras().getString("time");
                detail = sender.getExtras().getString("detail");
                plan = sender.getExtras().getString("plan");
                danger = sender.getExtras().getString("danger");
                _flag = sender.getExtras().getString("flag");
                byteImage = sender.getExtras().getByteArray(("image"));
                setImage(byteImage);
            }

            waktuBahaya = (TextView) findViewById(R.id.tvTanggal);
            waktuBahaya.setText(Html.fromHtml(time));
            spinner_jenis = (Spinner) findViewById(R.id.ddljeniskondisi);
            tempatBahaya = (EditText) findViewById(R.id.txtempat_bahaya);
            if (!place.trim().equals("")) {
                rbManual.setChecked(true);
                rbChooser.setChecked(false);
                tempatBahaya.setText(place);
            } else {
                rbManual.setChecked(false);
                rbChooser.setChecked(true);
            }
            rincianBahaya = (EditText) findViewById(R.id.txrincian_bahaya);
            rincianBahaya.setText(detail);
            rincianTindakan = (EditText) findViewById(R.id.txrincian_tindakan);
            rincianTindakan.setText(plan);

            rbAA = (RadioButton) findViewById(R.id.rbAA);
            rbA = (RadioButton) findViewById(R.id.rbA);
            rbB = (RadioButton) findViewById(R.id.rbB);
            rbC = (RadioButton) findViewById(R.id.rbC);

            if (danger.equalsIgnoreCase("AA")) {
                rbAA.setChecked(true);
                rbA.setChecked(false);
                rbB.setChecked(false);
                rbC.setChecked(false);
                kodeBahaya = "AA";
            } else if (danger.equalsIgnoreCase("A")) {
                rbAA.setChecked(false);
                rbA.setChecked(true);
                rbB.setChecked(false);
                rbC.setChecked(false);
                kodeBahaya = "A";
            } else if (danger.equalsIgnoreCase("B")) {
                rbAA.setChecked(false);
                rbA.setChecked(false);
                rbB.setChecked(true);
                rbC.setChecked(false);
                kodeBahaya = "B";
            } else if (danger.equalsIgnoreCase("C")) {
                rbAA.setChecked(false);
                rbA.setChecked(false);
                rbB.setChecked(false);
                rbC.setChecked(true);
            } else {
                rbAA.setChecked(false);
                rbA.setChecked(false);
                rbB.setChecked(false);
                rbC.setChecked(true);
                kodeBahaya = "C";
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        btnSimpan = (ImageButton) findViewById(R.id.btn_save);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    public void setImage(byte[] byteImage) {
        imgView.setImageBitmap(BitmapFactory.decodeByteArray(byteImage, 0,
                byteImage.length));
    }

    private void save() {
        String danger_place = "";

        if (rbManual.isChecked()) {
            danger_place = tempatBahaya.getText().toString();
        } else {
            danger_place = spinner_tempat.getSelectedItem().toString();
        }

        if (data_baru == true) {
            String[] data = new String[]{spinner_jenis.getSelectedItem().toString(),
                    danger_place,
                    waktuBahaya.getText().toString(),
                    rincianBahaya.getText().toString(),
                    rincianTindakan.getText().toString(), kodeBahaya, "SAVED"};

            if ((!data[0].toString().equalsIgnoreCase(""))
                    && (!data[1].toString().equalsIgnoreCase(""))
                    && (!data[2].toString().equalsIgnoreCase(""))) {
                if (convertDrawableToByteArray(imgView.getDrawable()) != null) {
                    oprDatabase.insertGreenCardWithImage(db, data,
                            convertDrawableToByteArray(imgView.getDrawable()));
                    Toast.makeText(getApplicationContext(),
                            "Data berhasil ditambahkan", Toast.LENGTH_SHORT)
                            .show();
                    resetField();
                    Intent i = new Intent();
                    i.setClass(getApplicationContext(), ListGreenCard.class);
                    startActivity(i);
                } else {
                    showAlertDialog(GreenCardActivity.this,
                            "Photo Required",
                            "Silahkan ambil photo sebagai bukti bahaya!!!.", false);

                }
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        " Isikan tempat ditemukan bahaya!, \n Isikan deskripsi potensi bahaya!, \n Isikan Rencana tindakan perbaikan segera! ",
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            if (rbManual.isChecked()) {
                danger_place = tempatBahaya.getText().toString();
            } else {
                danger_place = spinner_tempat.getSelectedItem().toString();
            }
            String[] data = new String[]{_id,
                    spinner_jenis.getSelectedItem().toString(),
                    danger_place,
                    waktuBahaya.getText().toString(),
                    rincianBahaya.getText().toString(),
                    rincianTindakan.getText().toString(), kodeBahaya, _flag};
            if (_flag.equalsIgnoreCase("SAVED")) {
                oprDatabase.updateGreenCard(db, data,
                        convertDrawableToByteArray(imgView.getDrawable()));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Data tidak diupdate!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rbAA:
                if (checked) {
                    kodeBahaya = "AA";
                }
                break;
            case R.id.rbA:
                if (checked) {
                    kodeBahaya = "A";
                }
                break;
            case R.id.rbB:
                if (checked) {
                    kodeBahaya = "B";
                }
                break;
            case R.id.rbC:
                if (checked) {
                    kodeBahaya = "C";
                }
                break;
            default:
                break;
        }
    }

    public void resetField() {
        tempatBahaya.setText("");
        rincianBahaya.setText("");
        rincianTindakan.setText("");
        rbC.setChecked(true);
        imgView.setImageBitmap(null);
    }

    public void onDestroy() {
        super.onDestroy();
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.green_card, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent intn = new Intent();
                intn.setClass(getApplicationContext(), MainMenu.class);
                startActivity(intn);
                break;
            case R.id.action_camera:
                // define the file-name to save photo taken by Camera activity
                String fileName = "new-photo-name.jpg";
                // create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION,
                        "Image captured by camera");
                // imageUri is the current activity attribute, define and save it
                // for later usage (also in onSaveInstanceState)
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                // create new Intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, PICK_Camera_IMAGE);
                return true;

            case R.id.action_gallery:
                try {
                    Intent gintent = new Intent();
                    gintent.setType("image/*");
                    gintent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(gintent, "Select Picture"),
                            PICK_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
                return true;

            case R.id.action_refresh:
                resetField();
                return true;
            case R.id.action_send:
                save();
                return true;
            case R.id.action_list_data:
                Intent i = new Intent();
                i.setClass(getApplicationContext(), ListGreenCard.class);
                startActivity(i);
                return true;
        }
        return false;
    }

    public void CameraOn() {
        // define the file-name to save photo taken by Camera activity
        String fileName = "new-photo-name.jpg";
        // create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,
                "Image captured by camera");
        // imageUri is the current activity attribute, define and save it
        // for later usage (also in onSaveInstanceState)
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, PICK_Camera_IMAGE);
    }

    public void GalleryOpen() {
        try {
            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(gintent, "Select Picture"), PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    public void suitCamera() {
        // define the file-name to save photo taken by Camera activity
        String fileName = "new-photo-name.jpg";
        // create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,
                "Image captured by camera");
        // imageUri is the current activity attribute, define and save it
        // for later usage (also in onSaveInstanceState)
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, PICK_Camera_IMAGE);
    }

    public void openGallery() {
        try {
            Intent gintent = new Intent();
            gintent.setType("image/*");
            gintent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(gintent, "Select Picture"), PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 640;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        imgView.setImageBitmap(bitmap);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
            case PICK_Camera_IMAGE:
                if (resultCode == RESULT_OK) {
                    // use imageUri here to access the image
                    selectedImageUri = imageUri;

                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (selectedImageUri != null) {
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                if (filePath != null) {
                    decodeFile(filePath);
                } else {
                    bitmap = null;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void onChoosePlaceClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {

            case R.id.rbtempat:
                if (checked) {
                    placeChecked = "Manual";
                    rbChooser.setChecked(false);
                }
                break;
            case R.id.rbtempat1:
                if (checked) {
                    placeChecked = "Chooser";
                    rbManual.setChecked(false);
                }
                break;
            default:
                break;
        }
    }

    public void setRbManualChecked(View view) {
        rbManual.setChecked(true);
        rbChooser.setChecked(false);

    }

    class ImageGalleryTask extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {
            InputStream is;
            BitmapFactory.Options bfo;
            Bitmap bitmapOrg;
            ByteArrayOutputStream bao;

            bfo = new BitmapFactory.Options();
            bfo.inSampleSize = 2;

            bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            String ba1 = Base64.encodeBytes(ba);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("image", ba1));
            nameValuePairs.add(new BasicNameValuePair("cmd", "image_android"));
            Log.v("log_tag", System.currentTimeMillis() + ".jpg");
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new
                        // Here you need to put your server file address
                        HttpPost("http://www.picsily.com/upload_photo.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.v("log_tag", "In the try Loop");
            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";
            // (null);
        }
    }

}
