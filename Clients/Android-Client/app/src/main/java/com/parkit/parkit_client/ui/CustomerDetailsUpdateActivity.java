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
import android.widget.EditText;
import android.widget.ImageView;

import com.parkit.parkit_client.Constants;
import com.parkit.parkit_client.MainActivity;
import com.parkit.parkit_client.R;
import com.parkit.parkit_client.Utils;
import com.parkit.parkit_client.rest.RestClient;
import com.parkit.parkit_client.rest.models.imgur.ImgurImageResponse;
import com.parkit.parkit_client.rest.models.parkit.Customer;
import com.parkit.parkit_client.rest.models.parkit.CustomerModificationResponse;
import com.parkit.parkit_client.rest.services.ImgurService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class CustomerDetailsUpdateActivity extends ActionBarActivity {



    private static final int REQUEST_LICENSE_IMAGE_CAPTURE = 1;
    private static final int RESULT_CODE_OK = -1;

    private Customer currentCustomer, updatedCustomer;
    private Uri licenseImageUri = null;
    private String updatedlicenseImageLink;
    private ProgressDialog uploadProgress;

    // view bindings

    @Bind(R.id.edit_first_name)
    EditText firstNameEdit;

    @Bind(R.id.edit_last_name)
    EditText lastNameEdit;

    @Bind(R.id.edit_contact_number)
    EditText contactNumberEdit;

    @Bind(R.id.edit_address)
    EditText addressEdit;

    @Bind(R.id.image_view_license)
    ImageView licenseImageView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details_update);
        ButterKnife.bind(this);

        Intent sourceIntent = this.getIntent();
        if(!sourceIntent.hasExtra(Constants.EXTRA_KEY_CUSTOMER)) {
            Log.d(Constants.LOG_TAG, "Customer extra missing");
        } else {
            Customer currentCustomer = sourceIntent
                    .getParcelableExtra(Constants.EXTRA_KEY_CUSTOMER);
            Log.d(Constants.LOG_TAG, "Customer data received : " + currentCustomer.toString());
            this.currentCustomer = currentCustomer;
            initializeForm(currentCustomer);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_details_update, menu);
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

    private void initializeForm(Customer customer) {

        firstNameEdit.setText(customer.first_name);
        lastNameEdit.setText(customer.last_name);
        contactNumberEdit.setText(customer.contact_no);
        addressEdit.setText(customer.address);
        Picasso.with(this)
                .load(customer.driving_licence_link)
                .into(licenseImageView);
    }


    // OnClicks

    @OnClick(R.id.btn_update)
    public void updatePersonalDetails() {


        if( firstNameEdit.getText().toString().equals("") ||
            lastNameEdit.getText().toString().equals("") ||
            contactNumberEdit.getText().toString().equals("")||
            addressEdit.getText().toString().equals("")
            ) {
            Log.d(Constants.LOG_TAG, "Some fields are empty");
            Utils.showShortToast("Some fields are empty", this.getApplicationContext());
        } else {

            updatedCustomer = new Customer(
                    firstNameEdit.getText().toString(),
                    lastNameEdit.getText().toString(),
                    contactNumberEdit.getText().toString(),
                    addressEdit.getText().toString(),
                    currentCustomer.driving_licence_link,
                    currentCustomer.vehicles
            );

            uploadLicenseImage();




        }



    }


    private void uploadLicenseImage() {

        if(licenseImageUri == null) {
            // don't update license image link
            saveOnParkIt();
        } else {
            File licenseDestination = new File(licenseImageUri.getPath());
            TypedFile licenceTypedFile = new TypedFile("image/jpg", licenseDestination);

            uploadProgress = ProgressDialog.show(
                    this, "Uploading", "Uploading image to ParkIt servers", false);

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
                            uploadProgress.dismiss();
                            if (imgurImageResponse.success) {
                                Utils.showShortToast(
                                        "License image uploaded , link : " +
                                                imgurImageResponse.data.link,
                                        CustomerDetailsUpdateActivity.this.getApplicationContext());
                                Log.d(Constants.LOG_TAG,
                                        "License Image uploaded successfully \nLink : "
                                                + imgurImageResponse.data.link);
                                updatedlicenseImageLink = imgurImageResponse.data.link;

                                Log.d(Constants.LOG_TAG, "New license link has been set, "+
                                                "new link : " + updatedCustomer.driving_licence_link
                                );
                                updatedCustomer.driving_licence_link = updatedlicenseImageLink;

                            } else {
                                uploadProgress.dismiss();
                                Utils.showShortToast(
                                        "Retrofit error !!!",
                                        CustomerDetailsUpdateActivity.this.getApplicationContext()
                                );
                                Log.d(Constants.LOG_TAG,
                                        "in onSuccess for license, but upload unsuccessfull");
                            }
                            // irrespective of upload success save details
                            saveOnParkIt();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            uploadProgress.dismiss();
                            Utils.showShortToast(
                                    "Retrofit error !!!",
                                    CustomerDetailsUpdateActivity.this.getApplicationContext()
                            );
                            Log.d(Constants.LOG_TAG, "in onFailure for license");
                            // irrespective of upload success save details
                            saveOnParkIt();
                        }
                    }
            );
        }



    }


    // OnClick

    @OnClick(R.id.btn_license_click)
    public void clickLicenseImage() {

        File imageDestination = getImageFile();
        if(imageDestination != null) {
            // file created
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            licenseImageUri = Uri.fromFile(imageDestination);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, licenseImageUri);

            if(imageCaptureIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(imageCaptureIntent, REQUEST_LICENSE_IMAGE_CAPTURE);
            } else {
                // resolution error
                Utils.showShortToast("Resolution Error !!!", this.getApplicationContext());
                Log.d(Constants.LOG_TAG, "Resolution error while capturing license image");
            }

        } else {
            Utils.showShortToast("File creation error !!!", this.getApplicationContext());
            Log.d(Constants.LOG_TAG, "getImageFile returned null");
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CODE_OK) {
            switch(requestCode) {
                case REQUEST_LICENSE_IMAGE_CAPTURE :
                    // licenseImageUri has been set

                    Bitmap licenseImagePreview = decodeUri(licenseImageUri);

                    // show preview
                    if(licenseImagePreview != null) {
                        licenseImageView.setImageBitmap(licenseImagePreview);
                        Utils.showShortToast(
                                "License Image Captured !!!",
                                this.getApplicationContext()
                        );
                    } else {
                        Utils.showShortToast("Image decoding error", this.getApplicationContext());
                        Log.d(Constants.LOG_TAG, "decodeUri returned null");
                    }
                    break;
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
                Log.d(Constants.LOG_TAG,
                        "FileNotFoundException thrown at decodeUri\nDescription : "+fnfe);
                return null;
            }

        } else {
            Log.d(Constants.LOG_TAG, "imageDestinationUri is not set yet");
            return null;
        }
    }

    private void saveOnParkIt() {



        Log.d(Constants.LOG_TAG, "Customer to be PUT : "+updatedCustomer);

        // get user hash
        SharedPreferences accountDetails = this
                .getSharedPreferences(Constants.KEY_SHARED_PREFERENCES, 0);

        String userHash = accountDetails.getString(Constants.CONFIG_KEY_HASH, "");

        if(userHash.equals("")) {
            Utils.showLongToast("Hash Error !!!", this.getApplicationContext());
            Log.d(Constants.LOG_TAG, "User hash has not been set");
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
                        // 200
                        if(response.getStatus() == 200) {
                            // update successful
                            Log.d(Constants.LOG_TAG, "200 OK");
                            Utils.showShortToast(
                                    "Account details updated !!!",
                                    CustomerDetailsUpdateActivity.this.getApplicationContext()
                            );
                            // restart
                            Intent restartIntent = new Intent(
                                    CustomerDetailsUpdateActivity.this, MainActivity.class);
                            startActivity(restartIntent);
                        } else {
                            Log.d(  Constants.LOG_TAG,
                                    "Unexpected success response code received : "
                                            +response.getStatus()
                            );
                            Utils.showLongToast(
                                    "Internal Application Error !!!"+
                                     "\nPlease contact ParkIt officials",
                                    CustomerDetailsUpdateActivity.this.getApplicationContext()
                            );
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        // 404, 401
                        Context ctx = CustomerDetailsUpdateActivity.this.getApplicationContext();
                        if(error.getResponse() == null) {
                            Log.d(Constants.LOG_TAG, "Response is null, error kind : "
                                    + error.getKind());
                            Utils.showShortToast("Update was unsuccessfull", ctx);
                            return;
                        }

                        switch(error.getResponse().getStatus()) {
                            case 400:
                                Utils.showShortToast("Customer account not found !!!", ctx);
                                break;
                            case 401:
                                Log.d(Constants.LOG_TAG, "Invalid auth token");
                                Utils.showLongToast(
                                        "Internal Application Error !!!" +
                                                "\nPlease contact ParkIt officials",
                                        CustomerDetailsUpdateActivity.this.getApplicationContext()
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
                                        CustomerDetailsUpdateActivity.this.getApplicationContext()
                                );
                                break;
                        }
                    }
                }
        );

    }
}
