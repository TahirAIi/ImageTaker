package tahir.imagetaker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imageTbl")
class Image(

   @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "imageId")
    var  imageId:Int,

    @ColumnInfo(name = "image")
 var image:String
)



