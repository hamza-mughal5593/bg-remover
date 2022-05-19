package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isseiaoki.simplecropview.CropImageView
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerBgBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.CropModel

class CropAdapter(val list:ArrayList<CropModel>,val itemClick: clickHandler) : RecyclerView.Adapter<CropAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = RecyclerEditorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            itemClick.onCropClick(list[position].mode)
        }
    }

    override fun getItemCount(): Int {

     return list.size
    }

    public class ViewHolder(val binding: RecyclerEditorBinding) : RecyclerView.ViewHolder(binding.root)
    {
      fun bind (crop: CropModel)
      {
          binding.icon.setImageResource(crop.image)
          binding.option.text=crop.mode.name
      }
    }

    interface clickHandler
    {
        fun onCropClick (mode:CropImageView.CropMode)
    }
}