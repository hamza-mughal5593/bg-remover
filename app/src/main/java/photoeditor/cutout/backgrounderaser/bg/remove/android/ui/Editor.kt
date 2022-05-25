package photoeditor.cutout.backgrounderaser.bg.remove.android.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import photoeditor.cutout.backgrounderaser.bg.remove.android.R
import photoeditor.cutout.backgrounderaser.bg.remove.android.adapters.*
import photoeditor.cutout.backgrounderaser.bg.remove.android.databinding.ActivityEditorBinding
import photoeditor.cutout.backgrounderaser.bg.remove.android.models.GenerealEditorModel
import photoeditor.cutout.backgrounderaser.bg.remove.android.util.ClipArt


class Editor : AppCompatActivity(), EditorAdapter.clickHandler, FiltersAdapter.filterHandler,BgAdapter.clikHandleBg,StickersAdapter.clickHandler,StickersSubAdapter.clickHandler {

    private lateinit var binding: ActivityEditorBinding
    private val editorOptions: ArrayList<GenerealEditorModel> = ArrayList()
    var count=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        System.loadLibrary("NativeImageProcessor");
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.model)

        changeStatusBarColor()
        initializeEditorOptionsList()
        initializeEditorOptionsRecyclerView()

    }

    override fun onResume() {
        super.onResume()
        binding.image.setImageBitmap(bitmap)

    }

    private fun changeStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    private fun initializeEditorOptionsList() {
        editorOptions.add(GenerealEditorModel("Filter", R.drawable.ic_effect))
        editorOptions.add(GenerealEditorModel("Stickers", R.drawable.ic_sticker))
        editorOptions.add(GenerealEditorModel("Text", R.drawable.ic_text))
        editorOptions.add(GenerealEditorModel("Adjustments", R.drawable.ic_adjustment))
        editorOptions.add(GenerealEditorModel("Crop", R.drawable.ic_crop))
        editorOptions.add(GenerealEditorModel("Backgrounds", R.drawable.ic_background))
    }

    private fun initializeEditorOptionsRecyclerView() {
        binding.recyclerEditingOptions.layoutManager =
            LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerEditingOptions.adapter = EditorAdapter(this@Editor, editorOptions, this)
    }

    override fun onFilterClick() {

        val filters = initializeFiltersList()
        initializeFiltersRecyclerView(filters)
        binding.recyclerEditingOptions.visibility = View.GONE
        binding.filtersMainLayout.visibility = View.VISIBLE
    }

    override fun onBgClick()
    {
        binding.recyclerEditingOptions.visibility = View.GONE
       // binding.filtersMainLayout.visibility = View.GONE
        binding.bgsMainLayout.visibility=View.VISIBLE

        bgClickHandlers()
    }

    private fun initializeFiltersRecyclerView(filters:ArrayList<GenerealEditorModel>)
    {
        binding.recyclerFilters.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerFilters.adapter = FiltersAdapter(this@Editor, filters, this)

        filterBackAndNextClick()

    }

    override fun onSelectFilter(position:Int)
    {
        if(bitmap !=null)
        {
            filteredImage = applyFilter(position,bitmap!!)
            binding.image.setImageBitmap(filteredImage)

        }else
        {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bgClickHandlers ()
    {
        binding.colorBg.setOnClickListener {

            val colors =initializeColorsList()
            initializeBgRecyclerView(colors)

            binding.bgsMainLayout.visibility=View.GONE
            binding.colorsMainsLayout.visibility=View.VISIBLE

            binding.bgType.text="Colors"


        }

        binding.gradientBg.setOnClickListener {

            val gradients =initializeGradientList()
            initializeBgRecyclerView(gradients)

            binding.bgsMainLayout.visibility=View.GONE
            binding.colorsMainsLayout.visibility=View.VISIBLE

            binding.bgType.text="Gradient"

        }

        binding.imagesBg.setOnClickListener {

            val images = initializeImagesList()
            initializeBgRecyclerView(images)

            binding.bgsMainLayout.visibility=View.GONE
            binding.colorsMainsLayout.visibility=View.VISIBLE

            binding.bgType.text="Images"

        }
    }

    private fun initializeBgRecyclerView (filters:ArrayList<Int>)
    {
        binding.recyclerColors.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerColors.adapter = BgAdapter(this@Editor, filters,this)
    }

    override fun onClickBg(bg: Int,position: Int)
    {
        if(position == 10 )
        {
            Toast.makeText(this, "Open Gallery", Toast.LENGTH_SHORT).show()
        }else
        {
            binding.bgMain.setBackgroundResource(bg)

        }
    }

    override fun onCropClick()
    {
        bitmap?.let {

            val intent = Intent (this,CropActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStickerClick() {

        binding.recyclerEditingOptions.visibility = View.GONE
        binding.stickersMainsLayout.visibility=View.VISIBLE

        val stickerTypes= initializeStickersTypes()
        initializeStickersTypesRecyclerView(stickerTypes)
        val stickers= initializeStickersListOne()
        initializeStickersRecyclerView(stickers)


    }

    private fun initializeStickersTypesRecyclerView (stickerTypes:ArrayList<Int>)
    {
        binding.recyclerStickersMain.layoutManager = LinearLayoutManager(this@Editor, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerStickersMain.adapter = StickersAdapter(stickerTypes,this)
    }
    private fun initializeStickersRecyclerView (sticker:ArrayList<Int>)
    {
        binding.recyclerStickersSub.layoutManager = GridLayoutManager(this@Editor, 4)
        binding.recyclerStickersSub.adapter = StickersSubAdapter(sticker,this)
    }


    override fun onClickStickerType(position: Int)
    {
        if(position == 0)
        {
            val stickers= initializeStickersListOne()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 1)
        {
            val stickers= initializeStickersListTwo()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 2)
        {
            val stickers= initializeStickersListThree()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 3)
        {
            val stickers= initializeStickersListFour()
            initializeStickersRecyclerView(stickers)
        }
        if(position == 4)
        {
            val stickers= initializeStickersListFive()
            initializeStickersRecyclerView(stickers)
        }
    }

    override fun onClickSticker(sticker: Int)
    {
        val ca = ClipArt(this, sticker)
        binding.bgMain.addView(ca)
        ca.id=++count
    }


    fun filterBackAndNextClick ()
    {
        binding.filterBack.setOnClickListener {

            binding.filtersMainLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE

            binding.image.setImageBitmap(bitmap)

        }

        binding.filterNext.setOnClickListener {

            binding.filtersMainLayout.visibility=View.GONE
            binding.recyclerEditingOptions.visibility=View.VISIBLE

            binding.image.setImageBitmap(filteredImage)

        }
    }

    companion object
    {
        var bitmap: Bitmap?=null
        var filteredImage: Bitmap?=null

    }

}