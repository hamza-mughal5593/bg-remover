package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.isseiaoki.simplecropview.CropImageView
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerBgBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.CropModel

class CropAdapter(val context: Context,val list:ArrayList<CropModel>,val itemClick: clickHandler) : RecyclerView.Adapter<CropAdapter.ViewHolder>() {


     var row_index=-1

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
            row_index=position
            notifyDataSetChanged()
        }

        if(row_index==position)
        {
            holder.binding.icon.setColorFilter(ContextCompat.getColor(context, R.color.theme_color), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.binding.option.setTextColor(context.resources.getColor(R.color.theme_color))

        }else
        {
            holder.binding.icon.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

            holder.binding.option.setTextColor(context.resources.getColor(R.color.white))
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