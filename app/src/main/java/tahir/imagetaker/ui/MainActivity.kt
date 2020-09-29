package tahir.imagetaker.ui


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import tahir.imagetaker.R
import tahir.imagetaker.data.Image
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {
    private val listItems = arrayOf("Take image", "Choose from gallery ")
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private val FILE_NAME="photo.jpg"

    private lateinit var cameraIntent: Intent
    private lateinit var galleryIntent: Intent
    private lateinit var imageList: List<Image>
    private lateinit var photoFile:File
    private lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewModel=ViewModelProvider(this).get(ImageViewModel::class.java)

        var adapter=ImageListAdapter(this,this)
        rvImages.layoutManager=LinearLayoutManager(this)
        rvImages.adapter=adapter

      imageViewModel.getAllImages.observe(this, Observer {

          adapter.setData(it)
          imageList=it
          // If there is no item to display, show the "Add Images" text view

          if (it.isEmpty()) {
              rvImages.visibility = View.GONE
              tvAddImages.visibility = View.VISIBLE
          } else {
              rvImages.visibility = View.VISIBLE
              tvAddImages.visibility = View.GONE
          }
      })
        fabAdd.setOnClickListener {


            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select action:")
            builder.setItems(listItems) { dialog, position ->
                if (position == 0) {
                    checkForCameraPermission(Manifest.permission.CAMERA,"Camera",CAMERA_REQUEST_CODE)
                }
                else {
                    checkForStoragePermissions(Manifest.permission.READ_EXTERNAL_STORAGE,"Storage",GALLERY_REQUEST_CODE)
                }
            }

            val dialog = builder.create()
            dialog.show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.menu_delete->showDeleteAllDialog()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDeleteAllDialog()
    {
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Confirm!")
            .setMessage("Are you sure you want to delete all images?")
            .setPositiveButton("YES"){dialogInterface, i ->
                deleteAll()
            }
            .setNegativeButton("No"){dialogInterface, i ->
            }
            .show()
    }
    private fun deleteAll()
    {
        imageViewModel.deleteAllImages()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //If user has captured or selected the image successfully then proceed
        if(resultCode== Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> saveCameraImageToDB()
                GALLERY_REQUEST_CODE -> saveGalleryImageToDB(data)
            }
        }
    }
private fun saveGalleryImageToDB(data: Intent?)
{
    //get uri from intent
    val uri=data?.data
    val image=Image(0,uri.toString())
    imageViewModel.addImage(image)
}
    private fun saveCameraImageToDB()
    {
        //get uri from file
        val uri=Uri.fromFile(photoFile)
        val image=Image(0,uri.toString())
        imageViewModel.addImage(image)

    }

    private fun startCameraIntent()
    {
        cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile=getPhotoFile(FILE_NAME)
        val fileProvider=FileProvider.getUriForFile(this,"tahir.imagetaker.fileprovider",photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileProvider)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun startGalleryIntent()
    {
        galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
   galleryIntent.setType("image/*")
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }


    private fun checkForCameraPermission(permission:String, name:String, requestCode:Int )
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            when{
                //check if permission has been granted
                ContextCompat.checkSelfPermission(applicationContext,permission)==PackageManager.PERMISSION_GRANTED->
                {
                    //if permission has been granted then start camera intent
                    startCameraIntent()
                }
                //if permission in not granted, show dialog why permission in necessary
                shouldShowRequestPermissionRationale(permission)->showDialog(permission,name,requestCode)

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)
            }
        }
    }

    private fun checkForStoragePermissions(permission:String, name:String, requestCode:Int )
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            when{
                //check if permission has been granted
                ContextCompat.checkSelfPermission(applicationContext,permission)==PackageManager.PERMISSION_GRANTED->{
                    //if permission has been granted then start gallery intent
                    startGalleryIntent()
                }
                //if permission in not granted, show dialog why permission in necessary
                shouldShowRequestPermissionRationale(permission)->showDialog(permission,name,requestCode)

                else -> ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {

        val storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName,".jpg",storageDir)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        fun innerCheck(name:String)
        {
            //if requested permission is for camera
            if(requestCode==CAMERA_REQUEST_CODE) {
                //if permission is not granted then display toast
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Allow permission to use this feature", Toast.LENGTH_LONG).show()
                } else {
                    //permission is granted, start the camera intent
                    startCameraIntent()
                }
            }
            else if(requestCode==GALLERY_REQUEST_CODE){
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Allow permission to use this feature", Toast.LENGTH_LONG).show()
                } else {
                    startGalleryIntent()
                }
            }
        }
        when(requestCode)
        {
            CAMERA_REQUEST_CODE->innerCheck("Camera")
            GALLERY_REQUEST_CODE->innerCheck("Images")
        }
    }
    private fun showDialog(permission: String,name: String,requestCode: Int)
    {

        val builder=AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setMessage("Permission of $name is required to use this feature")
            .setPositiveButton("Allow Permission"){dialog,which->
                ActivityCompat.requestPermissions(this, arrayOf(permission),requestCode)

            }
        builder.show()
    }



}