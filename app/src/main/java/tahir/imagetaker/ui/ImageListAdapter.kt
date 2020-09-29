package tahir.imagetaker.ui


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.imageitem.view.*
import tahir.imagetaker.R
import tahir.imagetaker.data.Image


class ImageListAdapter(owner:ViewModelStoreOwner,context: Context):RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {
    private lateinit var imageViewModel: ImageViewModel
    private val mOwner=owner
    private val mContext=context
    private val listItems = arrayOf("Delete picture")
    var imageList= emptyList<tahir.imagetaker.data.Image>()
    inner class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {

        init {

            itemView.setOnClickListener {
                val position=adapterPosition
              showOptions(position)

            }
        }

        private fun showOptions(itemPosition: Int)
        {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Select action:")
            builder.setItems(listItems) { dialog, position ->
                if (position == 0) {
                  showDeleteDialog(itemPosition)
                }
            }
            val dialog = builder.create()
            dialog.show()
        }

        private fun showDeleteDialog(itemPosition: Int)
        {
            val builder= AlertDialog.Builder(mContext)
            builder.setTitle("Confirm!")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("YES"){dialogInterface, i ->
                    delete(itemPosition)
                }
                .setNegativeButton("No"){dialogInterface, i ->
                }
                .show()
        }
        private fun delete(itemPosition: Int)
        {
            imageViewModel= ViewModelProvider(mOwner).get(ImageViewModel::class.java)
            imageViewModel.deleteImage(imageList[itemPosition])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
       return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.imageitem,parent,false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentImage = imageList[position]
        val temp:String=currentImage.image
        Glide.with(mContext).load(Uri.parse(temp)).into(holder.itemView.ivImage)
    }
    fun setData(image: List<Image>) {
        this.imageList = image
        notifyDataSetChanged()
    }

}