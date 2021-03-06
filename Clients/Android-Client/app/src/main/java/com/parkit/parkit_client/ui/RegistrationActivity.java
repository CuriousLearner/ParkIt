package com.parkit.parkit_client.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.qrcode.encoder.QRCode;
import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.imgur.ImgurImageResponse;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.QRCodeResponse;
import com.parkit.parkit_client.rest.models.parkit.Vehicle;
import com.parkit.parkit_client.rest.services.ImgurService;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class RegistrationActivity extends ActionBarActivity {

    @Bind(R.id.edit_first_name)
    EditText firstNameEdit;

    @Bind(R.id.edit_last_name)
    EditText lastNameEdit;

    @Bind(R.id.edit_contact_number)
    EditText contactNumberEdit;

    @Bind(R.id.edit_address)
    EditText addressEdit;

    @Bind(R.id.edit_email_id)
    EditText emailEdit;

    @Bind(R.id.edit_password)
    EditText passwordEdit;

    @Bind(R.id.license_image)
    ImageView licenseImage;

    @Bind(R.id.rc_image)
    ImageView rcImage;

    @Bind(R.id.btn_license_click)
    Button licenseClickBtn;

    @Bind(R.id.btn_rc_click)
    Button rcClickBtn;

    @Bind(R.id.btn_register)
    Button submitBtn;

    @Bind(R.id.edit_vehicle_number)
    EditText vehicleNumberEdit;

    @Bind(R.id.spin_vehicle_type)
    Spinner vehicleTypeSpinner;


    private String[] vehicleTypesView, vehicleTypesModel;
    private String vehicleType;
    private Uri licenseImageUri, rcImageUri;
    private String licenseImageLink, rcImageLink;
    private ProgressDialog uploadProgress;
    final private String LOG_TAG = "Message : ";

    // codes
    public static final int REQUEST_RC_IMAGE_CAPTURE = 100;
    public static final int REQUEST_LICENSE_IMAGE_CAPTURE = 200;
    public static final int RESULT_CODE_OK = -1;


    // keys
    public static final String RC_IMAGE_URI_KEY = "rcImageUri";
    public static final String LICENSE_IMAGE_URI_KEY = "licenseImageUri";


    // utility method
    public void showShortToast(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        setupVehicleTypeSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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

    public void setupVehicleTypeSpinner() {
//        vehicleTypes = getResources().getStringArray(R.array.vehicle_types);
        vehicleTypesView = getResources().getStringArray(R.array.vehicle_types_view);
        vehicleTypesModel = getResources().getStringArray(R.array.vehicle_types_model);



//        ArrayAdapter<String> vehicleTypeAdapter = new ArrayAdapter<String>(
//                this.getApplicationContext(),
//                android.R.layout.simple_spinner_item,
//                vehicleTypes
//        );
        ArrayAdapter<String> vehicleTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_simple_spinner_item, R.id.text_vehicle_type, vehicleTypesView);
//        vehicleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(vehicleTypeAdapter);
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


    @OnClick(R.id.btn_rc_click)
    public void clickRC() {


        File imageDestination = getImageFile();

        if(imageDestination != null) {
            // create intent
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            rcImageUri = Uri.fromFile(imageDestination);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, rcImageUri);

            if(imageCaptureIntent.resolveActivity(this.getPackageManager()) != null) {
                this.startActivityForResult(imageCaptureIntent, REQUEST_RC_IMAGE_CAPTURE);
            } else {
                showShortToast("Resolution error !!!");
                Log.d(LOG_TAG, "Resolution error while capturing RC image");
            }

        } else {
            showShortToast("File creation error !!!");
            Log.d(LOG_TAG, "getImageFile returned null");
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // save URIs
        if(licenseImageUri != null) {
            outState.putString(LICENSE_IMAGE_URI_KEY, licenseImageUri.toString());
        }

        if(rcImageUri != null) {
            outState.putString(RC_IMAGE_URI_KEY, rcImageUri.toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // restore URIs
        if(!savedInstanceState.getString(LICENSE_IMAGE_URI_KEY, "").equals(""))
            licenseImageUri = Uri.parse(savedInstanceState.getString(LICENSE_IMAGE_URI_KEY, ""));

        if(!savedInstanceState.getString(RC_IMAGE_URI_KEY, "").equals(""))
            rcImageUri = Uri.parse(savedInstanceState.getString(RC_IMAGE_URI_KEY, ""));

        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnClick(R.id.btn_license_click)
    public void licenseClick() {
        File imageDestination = getImageFile();

        if(imageDestination != null) {
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            licenseImageUri = Uri.fromFile(imageDestination);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, licenseImageUri);

            if(imageCaptureIntent.resolveActivity(this.getPackageManager()) != null) {
                this.startActivityForResult(imageCaptureIntent, REQUEST_LICENSE_IMAGE_CAPTURE);
            } else {
                showShortToast("Resolution error !!!");
                Log.d(LOG_TAG, "Resolution error while capturing license image");
            }




        } else {
            showShortToast("File creation error !!!");
            Log.d(LOG_TAG, "getImageFile returned null");
        }




    }













    @OnClick(R.id.btn_register)
    public void register() {

        if(isInputValid()) {
            uploadLicenseAndRCImages();
        }

    }


    public void uploadLicenseAndRCImages() {

        if(licenseImageUri == null || rcImageUri == null) {
            showShortToast("Please fill all fields");
            return;
        }


        File licenseDestination = new File(licenseImageUri.getPath());
        TypedFile licenceTypedFile = new TypedFile("image/jpg", licenseDestination);


        uploadProgress = ProgressDialog.show(
                this, "Uploading", "Uploading images to ParkIt servers", false);
        RestClient.imgurService.postImage(
                ImgurService.CLIENT_ID,
                "title-test-" + System.currentTimeMillis(),
                "Anonymous test upload through imgur API",
                null,
                null,
                licenceTypedFile,
                new Callback<ImgurImageResponse>() {
                    @Override
                    public void success(ImgurImageResponse imgurImageResponse, Response response) {
                        if (imgurImageResponse.success) {
                            showShortToast("License image uploaded , link : " + imgurImageResponse.data.link);
                            Log.d(LOG_TAG, "License Image uploaded successfully \nLink : " + imgurImageResponse.data.link);
                            licenseImageLink = imgurImageResponse.data.link;
                            final TypedFile rcTypedFile = new TypedFile(
                                    "image/jpg",
                                    new File(rcImageUri.getPath())
                            );
                            // upload rc image
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
                                                showShortToast("RC image uploaded successfully");
                                                Log.d(LOG_TAG, "RC Image uploaded successfully \nLink : " + imgurImageResponse.data.link);
                                                rcImageLink = imgurImageResponse.data.link;
                                                registerOnParkIt();
                                            } else {

                                                showShortToast("Upload error !!!");
                                                Log.d(LOG_TAG, "in onSuccess for RC, but upload unsuccessfull");

                                            }

                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            uploadProgress.dismiss();
                                            showShortToast("Retrofit error !!!");
                                            Log.d(LOG_TAG, "in onFailure for RC");
                                        }
                                    }

                            );


                        } else {
                            uploadProgress.dismiss();
                            showShortToast("Retrofit error !!!");
                            Log.d(LOG_TAG, "in onSuccess for license, but upload unsuccessfull");

                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {
                        uploadProgress.dismiss();
                        showShortToast("Retrofit error !!!");
                        Log.d(LOG_TAG, "in onFailure for license");
                    }
                }


        );



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
            Log.d(LOG_TAG, "File is a directory");
        else
            Log.d(LOG_TAG, "File is not a directory");

        /*
        if(!imageFile.mkdirs()) {
            Log.d(LOG_TAG, "File directory not created");
        }
        */

        boolean dirsMade = imageFile.getParentFile().mkdirs();

        if(dirsMade)
            showShortToast("Image storage directory created");

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CODE_OK) {
            switch (requestCode) {
                case REQUEST_RC_IMAGE_CAPTURE :
                    // rcImageUri has been set

                    Bitmap rcPreview = decodeUri(rcImageUri);

                    // display preview
                    if(rcPreview != null) {
                        showShortToast("RC Image Captured");
                        rcImage.setImageBitmap(rcPreview);
                    } else {
                        showShortToast("Image decoding error");
                        Log.d(LOG_TAG, "decodeUri returned null");
                    }


                    break;
                case REQUEST_LICENSE_IMAGE_CAPTURE:
                    // licenseImageUri has been set

                    Bitmap licensePreview = decodeUri(licenseImageUri);

                    // display preview
                    if(licensePreview != null) {
                        licenseImage.setImageBitmap(licensePreview);
                        showShortToast("License Image Captured !!!");
                    } else {
                        showShortToast("Image decoding error");
                        Log.d(LOG_TAG, "decodeUri returned null");
                    }


            }
        }
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
                Log.d(LOG_TAG, "FileNotFoundException thrown at decodeUri\nDescription : "+fnfe);
                return null;
            }

        } else {
            Log.d(LOG_TAG, "imageDestinationUri is not set yet");
            return null;
        }
    }


    private void registerOnParkIt() {

//        rcImageLink = "https://www.google.com";


        // calculate vehicle type code

        String vehicleTypeCode = "";

        String selectedVehicleTypeView = vehicleTypeSpinner.getSelectedItem().toString();

        // initialize view and model values
        String  twoWheelerModel = vehicleTypesModel[0],
                fourWheelerModel = vehicleTypesModel[1],
                heavyVehicleModel = vehicleTypesModel[2],
                twoWheelerView = vehicleTypesView[0],
                fourWheelerView = vehicleTypesView[1],
                heavyVehicleView = vehicleTypesView[2];

        if(selectedVehicleTypeView.equals(twoWheelerView)) {
            vehicleTypeCode = twoWheelerModel;
        } else if(selectedVehicleTypeView.equals(fourWheelerView)) {
            vehicleTypeCode = fourWheelerModel;
        } else if(selectedVehicleTypeView.equals(heavyVehicleView)) {
            vehicleTypeCode = heavyVehicleModel;
        }

        Log.d(Constants.LOG_TAG, "Vehicle type code : "+vehicleTypeCode);


        Vehicle vehicle = new Vehicle(
//                vehicleTypeSpinner.getSelectedItem().toString(),
                vehicleTypeCode,
                vehicleNumberEdit.getText().toString(),
                rcImageLink
        );

        ArrayList<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);



//        licenseImageLink = "https://www.google.com";
        final Customer customer = new Customer(
                firstNameEdit.getText().toString(),
                lastNameEdit.getText().toString(),
                contactNumberEdit.getText().toString(),
                addressEdit.getText().toString(),
                emailEdit.getText().toString(),
                passwordEdit.getText().toString(),
                licenseImageLink,
                vehicles
        );


        logCustomer(customer);

        //call ParkIt API
        RestClient.parkItService.registerCustomer(
                Constants.PARKIT_AUTH_TOKEN,
                customer,
                new Callback<QRCodeResponse>() {
                    @Override
                    public void success(QRCodeResponse qrCodeResponse, Response response) {
                        Log.d(LOG_TAG, "In onSuccess");
                        if (qrCodeResponse.QR_CODE_DATA != null) {
                            // success
                            SharedPreferences.Editor prefEditor =
                                    getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0)
                                            .edit();

                            Log.d(LOG_TAG, "Hash received : \n"+qrCodeResponse.QR_CODE_DATA);

                            prefEditor.putString(
                                    Constants.CONFIG_KEY_HASH,
                                    qrCodeResponse.QR_CODE_DATA);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_FIRST_NAME,
                                    customer.first_name);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_LAST_NAME,
                                    customer.last_name);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_EMAIL,
                                    customer.email
                            );
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_LICENSE_LINK,
                                    customer.driving_licence_link);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_RC_LINK,
                                    customer.vehicles.get(0).vehicle_rc_link);
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_CONTACT_NO,
                                    customer.contact_no
                            );
                            prefEditor.putString(
                                    Constants.CONFIG_KEY_ADDRESS,
                                    customer.address
                            );
                            prefEditor.apply();

                            showShortToast("Successfully registered !!!");


                            Intent openMainActivity = new Intent(
                                    RegistrationActivity.this,
                                    MainActivity.class
                            );
                            startActivity(openMainActivity);


                        } else {
                            showShortToast("ParkIt API error !!!");
                            Log.d(LOG_TAG, "QR_CODE_DATA is null");
                        }


                    }

                    @Override
                    public void failure(RetrofitError error) {

                        if(error.getResponse() != null) {

                            switch (error.getResponse().getStatus()) {
                                case 400:
                                    showShortToast("Invalid Form Data");
                                    Log.d(LOG_TAG, "Form data was invalid");
                                    break;
                                case 401:
                                    showShortToast("Invalid auth token");
                                    Log.d(LOG_TAG, "Auth token was invalid");
                                    break;
                            }
                        } else {
                            showShortToast("Cannot reach the servers");
                        }
                    }
                }
        );





    }

    public void logCustomer(Customer customer) {
        String customerRep = "Customer Details : " + "\n" +
                "First Name : " + customer.first_name + "\n" +
                "Last Name : "  + customer.last_name + "\n" +
                "Contact Number : " + customer.contact_no + "\n" +
                "Address : " + customer.address + "\n" +
                "License Image Link : " + customer.driving_licence_link + "\n" +
                "Vehicle Data : -->" +
                        "\tVehicle Number : " + customer.vehicles.get(0).vehicle_number + "\n" +
                        "\tVehicle Type : " + customer.vehicles.get(0).vehicle_type + "\n" +
                        "\tRC Image Link : " + customer.vehicles.get(0).vehicle_rc_link;

        Log.d(LOG_TAG, "Customer Data to POSTed : " + customerRep);



    }

    private boolean isInputValid() {

        Context ctx = this.getApplicationContext();


        // check for empty fields
        if(!((!firstNameEdit.getText().toString().equals("")) &&
            (!lastNameEdit.getText().toString().equals("")) &&
            (!contactNumberEdit.getText().toString().equals("")) &&
            (!addressEdit.getText().toString().equals("")) &&
            (!emailEdit.getText().toString().equals("")) &&
            (!passwordEdit.getText().toString().equals("")) &&
            licenseImageUri != null &&
            rcImageUri != null &&
            (!vehicleTypeSpinner.getSelectedItem().toString().equals("")) &&
            (!vehicleNumberEdit.getText().toString().equals(""))
        )){
            // something is not filled
            Utils.showShortToast("Please fill all fields !!!", ctx);
            return false;
        } else {

            // validate contact number
            if (contactNumberEdit.getText().toString().length() != 10) {
                Utils.showShortToast("Invalid contact number !!!", ctx);
                return false;
            } else {

                // validate email id
                Pattern emailRegexPattern = Pattern.compile(Constants.EMAIL_REGEX_RFC_5322);
                Matcher emailMatcher = emailRegexPattern.matcher(emailEdit.getText().toString());
                if(!emailMatcher.matches()) {
                    Utils.showShortToast("Invalid email address !!!", ctx);
                    return false;
                } else {

                    // validate password
                    if(passwordEdit.getText().toString().length() < 8) {
                        Utils.showShortToast(
                                "Your choice for a password is too short, " +
                                "\nMinimum password length is 8.", ctx);
                        return false;
                    } else {

                        // validate license plate number
                        // @TODO:Give a suitable license plate number hint.
                        Pattern licensePlateNumberPattern =
                                Pattern.compile(Constants.LICENSE_PLATE_NUMBER_REGEX);
                        Matcher licensePlateNumberMatcher= licensePlateNumberPattern
                                .matcher(vehicleNumberEdit.getText().toString());

                        if(!licensePlateNumberMatcher.matches()) {
                            Utils.showShortToast("Invalid license plate number", ctx);
                            return false;
                        } else {
                            // all validations succeeded
                            Log.d(Constants.LOG_TAG, "All validations passed");
                            return true;
                        }
                    }
                }
            }
        }
    }
}
