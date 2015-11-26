package com.parkit.parkit_client.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.imgur.ImgurImageResponse;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.CustomerModificationResponse;
import com.parkit.parkit_client.rest.models.parkit.Vehicle;
import com.parkit.parkit_client.rest.services.ImgurService;
import com.parkit.parkit_client.rest.services.ParkItService;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class AddVehicleActivity extends ActionBarActivity {


    private Uri rcImageUri;
    private String[] vehicleTypes;
    private String vehicleType, rcImageLink;
    private ProgressDialog uploadProgress;
    private Customer currentCustomer, updatedCustomer;


    private static final int REQUEST_RC_IMAGE_CAPTURE = 1;
    private static final int RESULT_CODE_OK = -1;

    // view bindings

    @Bind(R.id.spinner_vehicle_type)
    Spinner vehicleTypeSpinner;

    @Bind(R.id.edit_vehicle_number)
    EditText vehicleNumberEdit;

    @Bind(R.id.image_view_vehicle_rc)
    ImageView rcImageView;

    @Bind(R.id.root_view_add_vehicle)
    LinearLayout rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        Intent sourceIntent = this.getIntent();
        ButterKnife.bind(this);
        setupVehicleTypeSpinner();
        if(sourceIntent.getExtras() == null ||
                sourceIntent.getParcelableExtra(Constants.EXTRA_KEY_CUSTOMER) == null) {
            Log.d(Constants.LOG_TAG, "Customer not received from previous activity");
            Utils.showShortToast("Internal Application Error !!!", this.getApplicationContext());
            rootView.setVisibility(View.GONE);
        } else {
            currentCustomer = sourceIntent.getParcelableExtra(Constants.EXTRA_KEY_CUSTOMER);
            Log.d(Constants.LOG_TAG, "Customer data : "+currentCustomer);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_vehicle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    // OnClicks

    @OnClick(R.id.btn_rc_click)
    public void clickRCImage() {
        File imageDestination = getImageFile();

        if(imageDestination != null) {
            Intent rcImageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            rcImageUri = Uri.fromFile(imageDestination);
            rcImageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, rcImageUri);

            if(rcImageCaptureIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(rcImageCaptureIntent,REQUEST_RC_IMAGE_CAPTURE);
            } else {
                Utils.showShortToast("Resolution Error !!!", this.getApplicationContext());
                Log.d(Constants.LOG_TAG, "Resolution error while capturing rc image");
            }

        } else {
            Log.d(Constants.LOG_TAG, "getImageFile() return null");
            Utils.showShortToast("File creation error !!!", this.getApplicationContext());
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CODE_OK && requestCode == REQUEST_RC_IMAGE_CAPTURE) {
            // rcImageUri has been set

            Bitmap rcImagePreview = decodeUri(rcImageUri);

            if(rcImagePreview != null) {
                rcImageView.setImageBitmap(rcImagePreview);
                Utils.showShortToast(
                        "RC Image Captured !!!",
                        this.getApplicationContext()
                );
            } else {
                Utils.showShortToast(
                        "Image decoding error !!!",
                        this.getApplicationContext()
                );
                Log.d(Constants.LOG_TAG, "decodeUri returned null");
            }
        }
    }

    @OnClick(R.id.btn_add_vehicle)
    public void addVehicle() {
        if(vehicleType == null ||
           vehicleNumberEdit.getText().toString().equals("") ||
           rcImageUri == null) {
            Utils.showShortToast("Please fill all fields", this.getApplicationContext());
            return;
        }

        uploadRCImage();


    }


    public File getImageFile() {

        String fileName = "ParkIt"+File.separator+"ParkIt" + "-"+ "Upload" + "-" +
                System.currentTimeMillis() + ".png";

        File imageFile;

        // check if external storage is mounted
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // external storage available
            imageFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    fileName);

        } else {
            // external storage not available
            // store image in cache
            imageFile = new File(
                    getCacheDir(),fileName
            );
        }


        if(imageFile.isDirectory())
            Log.d(Constants.LOG_TAG, "File is a directory");
        else
            Log.d(Constants.LOG_TAG, "File is not a directory");

        /*
        if(!imageFile.mkdirs()) {
            Log.d(LOG_TAG, "File directory not created");
        }
        */

        boolean dirsMade = imageFile.getParentFile().mkdirs();

        if(dirsMade)
            Utils.showShortToast("Image storage directory created", this.getApplicationContext());

        /*boolean isCreated = false;
        try {
            isCreated = imageFile.createNewFile();
        } catch(IOException ioe) {
            Log.d(LOG_TAG,"IOException occurred while creating image file"+
                    "\nDescription : "+ioe.toString());
        }
        return (isCreated)? imageFile : null;
        */
        return imageFile;
    }

    public Bitmap decodeUri(Uri imageDestinationUri) {
        // decode file from image destination uri
        if(imageDestinationUri != null) {
            // get original image dimensions
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inJustDecodeBounds = true;

            try {

                BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(imageDestinationUri),
                        null,
                        bfo);

                // bfo now has original dimensions

                // required image size
                final int REQUIRED_IMAGE_WIDTH = 350;
                final int REQUIRED_IMAGE_HEIGHT = 150;


                // calculate scale

                int scale = 1, imageWidth = bfo.outWidth, imageHeight = bfo.outHeight;
                while(true) {
                    if(imageHeight / 2 < REQUIRED_IMAGE_HEIGHT
                            || imageWidth / 2 < REQUIRED_IMAGE_WIDTH) {
                        break;
                    } else {
                        imageWidth /= 2;
                        imageHeight /= 2;
                        scale *= 2;
                    }
                }



                // decode image with calculated scale
                BitmapFactory.Options scaledOptions = new BitmapFactory.Options();
                scaledOptions.inSampleSize = scale;

                return BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(imageDestinationUri),
                        null,
                        scaledOptions
                );

            } catch(FileNotFoundException fnfe) {
                Log.d(Constants.LOG_TAG, "FileNotFoundException thrown at decodeUri\nDescription : "
                        +fnfe);
                return null;
            }

        } else {
            Log.d(Constants.LOG_TAG, "imageDestinationUri is not set yet");
            return null;
        }
    }


    public void setupVehicleTypeSpinner() {
        vehicleTypes = this.getResources().getStringArray(R.array.vehicle_types);
        ArrayAdapter<String> vehicleTypesAdapter = new ArrayAdapter<String>(
                this.getApplicationContext(),
                android.R.layout.simple_spinner_item,
                vehicleTypes
        );
        vehicleTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypesAdapter);
        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicleType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void uploadRCImage() {

        if(rcImageUri == null) {
            Utils.showShortToast(
                    "Please click an image of the vehicle's RC",
                    this.getApplicationContext()
            );
            return;
        }

        File rcImageFile = new File(rcImageUri.getPath());
        TypedFile rcTypedFile = new TypedFile("image/jpg", rcImageFile);

        uploadProgress = ProgressDialog.show(
                this, "Uploading", "Uploading RC image to ParkIt servers", false);

        RestClient.imgurService.postImage(
                ImgurService.CLIENT_ID,
                "title-test-" + System.currentTimeMillis(),
                "Anonymous test upload through imgur API",
                null,
                null,
                rcTypedFile,
                new Callback<ImgurImageResponse>() {
                    @Override
                    public void success(ImgurImageResponse imgurImageResponse, Response response) {
                        uploadProgress.dismiss();
                        if (imgurImageResponse.success) {
                            Utils.showShortToast(
                                    "License image uploaded , link : " +
                                            imgurImageResponse.data.link,
                                    AddVehicleActivity.this.getApplicationContext()
                            );
                            Log.d(Constants.LOG_TAG,
                                    "License Image uploaded successfully \nLink : " +
                                            imgurImageResponse.data.link);
                            rcImageLink = imgurImageResponse.data.link;
                            modifyCustomerAccountOnParkIt();

                        } else {
                            Log.d(Constants.LOG_TAG, "in imgur onSuccess, but image upload failed");
                            Utils.showShortToast(
                                    "Upload error !!!",
                                    AddVehicleActivity.this.getApplicationContext()
                            );


                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        uploadProgress.dismiss();
                        Utils.showShortToast(
                                "Retrofit error !!!",
                                AddVehicleActivity.this.getApplicationContext()
                        );
                        Log.d(Constants.LOG_TAG, "in onFailure for rc");
                    }
                }
        );



    }

    private void modifyCustomerAccountOnParkIt() {

        updatedCustomer = currentCustomer;
        Vehicle newVehicle = new Vehicle(
                vehicleType,
                vehicleNumberEdit.getText().toString(),
                rcImageLink
        );
        updatedCustomer.vehicles.add(newVehicle);


        SharedPreferences accountDetails = getSharedPreferences(
                Constants.KEY_SHARED_PREFERENCES, 0);

        String userHash = accountDetails.getString(Constants.CONFIG_KEY_HASH, "");

        if(userHash.equals("")) {
            Log.d(Constants.LOG_TAG, "User hash is not set");
            Utils.showShortToast("Hash Error !!!", this.getApplicationContext());
            return;
        }


        RestClient.parkItService.putCustomer(
                Constants.PARKIT_AUTH_TOKEN,
                userHash,
                updatedCustomer,
                new Callback<CustomerModificationResponse>() {
                    @Override
                    public void success(
                            CustomerModificationResponse customerModificationResponse,
                            Response response) {

                        if(response.getStatus() == 200) {
                            // addition successful
                            Log.d(Constants.LOG_TAG, "200 OK");
                            Utils.showShortToast(
                                    "Vehicle added successfully",
                                    AddVehicleActivity.this.getApplicationContext()
                            );

                            // restart
                            Intent restartIntent = new Intent(
                                    AddVehicleActivity.this, MainActivity.class);
                            startActivity(restartIntent);

                        } else {
                            Log.d(Constants.LOG_TAG,
                                    "Unexpected success status code received : "
                                            +response.getStatus());
                            Utils.showShortToast(
                                    "Internal Application Error !!!" +
                                    "\nPlease contact ParkIt officials",
                                    AddVehicleActivity.this.getApplicationContext());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Context ctx = AddVehicleActivity.this.getApplicationContext();
                        if(error.getResponse() == null) {
                            Log.d(Constants.LOG_TAG, "Response is null, error kind : "
                                    + error.getKind());
                            Utils.showShortToast("Vehicle addition was unsuccessfull", ctx);
                        } else {

                            switch (error.getResponse().getStatus()) {
                                case 400:
                                    Utils.showShortToast("Customer account not found !!!", ctx);
                                    break;
                                case 401:
                                    Log.d(Constants.LOG_TAG, "Invalid auth token");
                                    Utils.showLongToast(
                                            "Internal Application Error !!!" +
                                                    "\nPlease contact ParkIt officials",
                                            AddVehicleActivity.this.getApplicationContext()
                                    );
                                    break;
                                default:
                                    Log.d(Constants.LOG_TAG,
                                            "Unexpected success response code received : "
                                                    + error.getResponse().getStatus()
                                    );
                                    Utils.showLongToast(
                                            "Internal Application Error !!!"+
                                                    "\nPlease contact ParkIt officials",
                                            AddVehicleActivity.this.getApplicationContext()
                                    );
                                    break;
                            }
                        }

                    }
                }

        );


    }
}
