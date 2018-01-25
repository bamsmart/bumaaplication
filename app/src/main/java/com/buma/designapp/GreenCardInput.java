package com.buma.designapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.buma.db.DbActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class GreenCardInput extends Activity {
    private static final int DATE_DIALOG_ID = 999;
    private static final int ACTION_TAKE_PHOTO_S = 1;
    private static final int ACTION_TAKE_VIDEO = 3;
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private static final String VIDEO_STORAGE_KEY = "viewvideo";
    private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    Button.OnClickListener mTakeVidOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakeVideoIntent();
        }
    };
    private DbActivity oprDatabase = null;
    private SQLiteDatabase db = null;
    private String _id = "";
    private EditText txdeskripsi;
    private RadioButton rdBtnPlan;
    private RadioButton rdBtnDone;
    private RadioButton rdBtnCancel;
    private String Status = "";
    private EditText txjudul;
    private EditText txtanggal;
    private EditText txlokasi;
    private Button btnSimpan;
    private Button btnCancel;
    private DatePicker picker;
    private Button btnDate;
    private int year;
    private int month;
    private int day;
    private Boolean data_baru;
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private VideoView mVideoView;
    private Uri mVideoUri;
    @SuppressWarnings("unused")
    private String mCurrentPhotoPath;
    Button.OnClickListener mTakePicSOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
        }
    };
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            txtanggal.setText(new StringBuilder().append(month + 1).append("-")
                    .append(day).append("-").append(year).append(" "));

            // set selected date into datepicker also
            picker.init(year, month, day, null);

        }
    };

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String judul = "";
        String tanggal = "";
        String lokasi = "";
        String deskripsi = "";
        String _status = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        setCurrentDateOnView();
        addListenerOnButton();

        mImageView = (ImageView) findViewById(R.id.imageView1);
        mVideoView = (VideoView) findViewById(R.id.videoView1);
        mImageBitmap = null;
        mVideoUri = null;

        Button picBtn = (Button) findViewById(R.id.BtnTakePicture);
        setBtnListenerOrDisable(picBtn, mTakePicSOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE);

        Button vidBtn = (Button) findViewById(R.id.BtnTakeVideo);
        setBtnListenerOrDisable(vidBtn, mTakeVidOnClickListener,
                MediaStore.ACTION_VIDEO_CAPTURE);

        try {
            oprDatabase = new DbActivity(this);
            db = oprDatabase.getWritableDatabase();
            oprDatabase.createTable(db);

            Intent sender = getIntent();
            String status = sender.getExtras().getString("status");
            _id = sender.getExtras().getString("_id");

            if (status.equalsIgnoreCase("baru")) {
                data_baru = true;
            } else {
                data_baru = false;
                judul = sender.getExtras().getString("judul");
                tanggal = sender.getExtras().getString("tanggal");
                lokasi = sender.getExtras().getString("lokasi");
                deskripsi = sender.getExtras().getString("deskripsi");
                _status = sender.getExtras().getString("status");

            }
            // untuk radiobutton
            rdBtnPlan = (RadioButton) findViewById(R.id.rbPlan);
            rdBtnDone = (RadioButton) findViewById(R.id.rbDone);
            rdBtnCancel = (RadioButton) findViewById(R.id.rbCancel);

            txjudul = (EditText) findViewById(R.id.txTitle);
            txjudul.setText(judul);
            txtanggal = (EditText) findViewById(R.id.txDate);
            txtanggal.setText(tanggal);
            txlokasi = (EditText) findViewById(R.id.txLocations);
            txlokasi.setText(lokasi);
            txdeskripsi = (EditText) findViewById(R.id.txDesc);
            txdeskripsi.setText(deskripsi);
            if (_status.equalsIgnoreCase("Plan")) {
                rdBtnPlan.setChecked(true);
                rdBtnCancel.setChecked(false);
                rdBtnDone.setChecked(false);
            } else if (_status.equalsIgnoreCase("Cancel")) {
                rdBtnPlan.setChecked(false);
                rdBtnCancel.setChecked(true);
                rdBtnDone.setChecked(false);
            } else if (_status.equalsIgnoreCase("Done")) {
                rdBtnPlan.setChecked(false);
                rdBtnCancel.setChecked(false);
                rdBtnDone.setChecked(true);
            }

            btnSimpan = (Button) findViewById(R.id.BtnSave);

            if (data_baru == true)
                btnSimpan.setText("Save");
            else
                btnSimpan.setText("Edit");

            btnSimpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    reset();
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // display current date
    public void setCurrentDateOnView() {
        txtanggal = (EditText) findViewById(R.id.txDate);
        picker = (DatePicker) findViewById(R.id.PickerDate);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        txtanggal.setText(new StringBuilder().append(month + 1).append("-")
                .append(day).append("-").append(year).append(" "));

        picker.init(year, month, day, null);
    }

    public void addListenerOnButton() {
        btnDate = (Button) findViewById(R.id.BtnDate);

        btnDate.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
        }
        return null;
    }

    private void save() {
        String[] data = new String[]{_id, txjudul.getText().toString(),
                txtanggal.getText().toString(), txlokasi.getText().toString(),
                txdeskripsi.getText().toString(), Status};

        if (data_baru == true) {
            oprDatabase.insertActivity(db, data);
            txjudul.setText("");
            txtanggal.setText("");
            txlokasi.setText("");
            txdeskripsi.setText("");

            Toast.makeText(getApplicationContext(),
                    "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent();
            myIntent.setClass(getApplicationContext(), GreenCardDataActivity.class);
            startActivity(myIntent);

        } else if (data_baru == false) {
            oprDatabase.updateActivity(db, data);
            finish();
        }

    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            storageDir = Environment.getExternalStorageDirectory()
                    .getAbsoluteFile();
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name),
                    "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
                albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO_S:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    private void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);
    }

    private void handleCameraVideo(Intent intent) {
        mVideoUri = intent.getData();
        mVideoView.setVideoURI(mVideoUri);
        mImageBitmap = null;
        mVideoView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
    }

    public void reset() {
        txjudul.setText("");
        txtanggal.setText("");
        txlokasi.setText("");
        txdeskripsi.setText("");
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rbPlan:
                if (checked) {
                    Status = "Plan";
                }

                break;
            case R.id.rbDone:
                if (checked) {
                    Status = "Done";
                }
            case R.id.rbCancel:
                if (checked) {
                    Status = "Cancel";
                }
            default:
                break;
        }

    }

    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("request code " + requestCode + "result code"
                + resultCode + "Intent" + data);
        switch (requestCode) {

            case ACTION_TAKE_PHOTO_S: {
                if (resultCode == RESULT_OK) {
                    handleSmallCameraPhoto(data);
                }
                break;
            } // ACTION_TAKE_PHOTO_S

            case ACTION_TAKE_VIDEO: {
                if (resultCode == RESULT_OK) {
                    handleCameraVideo(data);
                }
                break;
            } // ACTION_TAKE_VIDEO
        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY,
                (mImageBitmap != null));
        outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY,
                (mVideoUri != null));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView
                .setVisibility(savedInstanceState
                        .getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
                        : ImageView.INVISIBLE);
        mVideoView.setVideoURI(mVideoUri);
        mVideoView
                .setVisibility(savedInstanceState
                        .getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? ImageView.VISIBLE
                        : ImageView.INVISIBLE);
    }

    private void setBtnListenerOrDisable(Button btn,
                                         Button.OnClickListener onClickListener, String intentName) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(getText(R.string.cannot).toString() + " "
                    + btn.getText());
            btn.setClickable(false);
        }
    }

    public String getCurrentDate() {
        StringBuilder builder = new StringBuilder();
        builder.append("Current Date: ");
        builder.append(picker.getDayOfMonth() + "/");
        builder.append((picker.getMonth() + 1) + "/");// month is 0 based
        builder.append(picker.getYear());
        return builder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.input, menu);
        return true;
    }

}
