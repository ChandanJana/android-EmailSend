package com.e.emailapplication

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.e.emailapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val RESULT_LOAD_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, RESULT_LOAD_IMAGE)
            /*Thread(object : Runnable {
                override fun run() {
                    val mailer = MailService(
                        "janachandan1@gmail.com",
                        "hi.rahulroy1@gmail.com",
                        "Subject",
                        "TextBody",
                        "<b>HtmlBody</b>",
                        null
                    )
                    try {
                        mailer.sendAuthenticated()
                    } catch (e: Exception) {
                        Log.e("SendMail", e.message.toString())
                    }
                }
            }).start()*/
            /*Thread(object : Runnable {
                override fun run() {
                    try {
                        val sender = GMailSender()
                        //sender.addAttachment(picturePath, "Image file")
                        sender.sendMail(
                            "Test mail",
                            "This is new mail from testing okay",
                            "chandan.visiabletech@gmail.com",
                            "hi.rahulroy1@gmail.com"
                        )
                    } catch (e: Exception) {
                        //Toast.makeText(this@MainActivity,"Error "+ e.message,Toast.LENGTH_LONG).show();
                        Log.e("SendMail", e.message.toString())
                    }
                }
            }).start()*/
            /*var sm = SendMail(this, "hi.rahulroy1@gmail.com", "Test mail", "Test mail message")
            sm.execute()*/

            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage: Uri? = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(
                selectedImage!!,
                filePathColumn, null, null, null
            )
            cursor?.moveToFirst()
            val columnIndex: Int = cursor?.getColumnIndex(filePathColumn[0])!!
            val picturePath: String = cursor?.getString(columnIndex)
            Thread(object : Runnable {
                override fun run() {
                    try {
                        val sender = GMailSender()
                        sender.addAttachment(picturePath, "Image file")
                        sender.sendMail(
                            "Test mail",
                            "This is new mail from testing okay",
                            "chandan.visiabletech@gmail.com",
                            "hi.rahulroy1@gmail.com"
                        )
                    } catch (e: Exception) {
                        //Toast.makeText(this@MainActivity,"Error "+ e.message,Toast.LENGTH_LONG).show();
                        Log.e("SendMail", e.message.toString())
                    }
                }
            }).start()
            cursor?.close()
            //val imageView: ImageView = findViewById<View>(R.id.imgView) as ImageView
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}