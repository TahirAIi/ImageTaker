package tahir.imagetaker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tahir.imagetaker.logic.ImageDao

@Database(entities = [Image::class],version = 1)
abstract class ImageDatabase:RoomDatabase() {

    abstract fun imageDao():ImageDao

    companion object{
        @Volatile
        private var INSTANCE:ImageDatabase?=null

        fun getDatabase(context: Context):ImageDatabase{
            val tempInstance= INSTANCE
            if(tempInstance !=null)
            {
                return tempInstance
            }
            synchronized(this)
            {
                val instance= Room.databaseBuilder(context.applicationContext,ImageDatabase::class.java,"ImageDB").build()
                INSTANCE=instance
                return instance
            }
        }
    }
}