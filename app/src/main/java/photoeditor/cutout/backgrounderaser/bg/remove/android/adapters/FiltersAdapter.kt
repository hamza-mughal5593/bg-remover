package photoeditor.cutout.backgrounderaser.bg.remove.android.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.RecyclerFiltersBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel

class FiltersAdapter(val context: Context, private val filters:ArrayList<GenerealEditorModel>, val itemClick: FiltersAdapter.filterHandler) : RecyclerView.Adapter<FiltersAdapter.ViewHolder>()  {


    private var  row_index:Int =-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerFiltersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(filters[position])
        holder.itemView.setOnClickListener {
            row_index=holder.adapterPosition
            handleClickListener(position)
            notifyDataSetChanged()
        }

        handleItemBgColors(holder)

    }

    override fun getItemCount(): Int {
        return filters.size
    }


    fun handleItemBgColors (holder: ViewHolder)
    {
        if(row_index == holder.adapterPosition)
        {
            holder.binding.nameLayout.setBackgroundColor(context.resources.getColor(R.color.theme_color))
        }else
        {
            holder.binding.nameLayout.setBackgroundColor(context.resources.getColor(R.color.black))

        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun handleClickListener (position: Int)
    {
            itemClick.onSelectFilter(position)
    }


    class ViewHolder(val binding: RecyclerFiltersBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(option: GenerealEditorModel)
        {
            binding.icon.setImageResource(option.icon)
            binding.name.text=option.option
        }
    }

    interface filterHandler
    {
        fun onSelectFilter(position: Int)
    }

}