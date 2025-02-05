package kr.ac.kumoh.s20171278.map_map_challenge.main


import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.*

import kotlinx.android.synthetic.main.album_fragment_album_memo.*
import kotlinx.android.synthetic.main.main_fragment_main_memo_item.*
import kr.ac.kumoh.s20171278.map_map_challenge.R
import kr.ac.kumoh.s20171278.map_map_challenge.main.DetailMemoActivity.Companion.ALBUM_DATA
import kr.ac.kumoh.s20171278.map_map_challenge.MainActivity.Companion.KEY_ALBUM_NAME
import kr.ac.kumoh.s20171278.map_map_challenge.SelectImageActivity
import kr.ac.kumoh.s20171278.map_map_challenge.hashtag.Hashtag
import java.util.regex.Pattern


class MainMemoFragment : Fragment() {
    private val imageList = ArrayList<String>()
    private var albumData: ArrayList<SelectImageActivity.dbSite> = arrayListOf()
    private lateinit var albumName : String
    private var userUid: String? = null
    private var shareUserUid: String? = null
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        albumName = activity?.intent!!.getStringExtra(KEY_ALBUM_NAME)!!


        val view: View = inflater.inflate(R.layout.main_fragment_main_memo, container, false)

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        albumData = activity!!.intent.getParcelableArrayListExtra(ALBUM_DATA)!!
        val mAdapter = MemoAdapter()

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
            itemAnimator = DefaultItemAnimator()
        }
        mAdapter.notifyDataSetChanged()
    }

    fun setContent(mTagLists: String?, tags_view: TextView) {
//        var tag = ""
        var i:Int = 0
//        while (i < mTagLists.size)
//        {
//            tag += "#" + mTagLists.get(i) + ""
//            i++
//        }
        val tag = mTagLists!!
        val hashtagSpans = getSpans(tag, '#')
        val tagsContent : SpannableString = SpannableString(tag)
        i = 0
        while (i < hashtagSpans.size)
        {
            val span = hashtagSpans.get(i)
            val hashTagStart = span[0]
            val hashTagEnd = span[1]
            val hashTag = Hashtag(context!!)
            hashTag.setOnClickEventListener(object: Hashtag.ClickEventListener {
                override fun onClickEvent(data:String) {
                }
            })
            tagsContent.setSpan(hashTag, hashTagStart, hashTagEnd, 0)
            i++
        }
     //   val tags_view = findViewById(R.id.textview_tag) as TextView
        if (tags_view != null)
        {
            tags_view.setMovementMethod(LinkMovementMethod.getInstance())
            tags_view.setText(tagsContent)
        }
    }
    fun getSpans(body:String, prefix:Char):ArrayList<IntArray> {
        val spans = ArrayList<IntArray>()
        val pattern = Pattern.compile(prefix + "\\w+")
        //complie(prefix + "\\w+")
        val matcher = pattern.matcher(body)
        while (matcher.find())
        {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }
        return spans
    }


    inner class MemoAdapter(): RecyclerView.Adapter<MemoAdapter.ViewHolder>() {

        val picasso = Builder(activity).build()

        inner class ViewHolder(root: View) : RecyclerView.ViewHolder(root){
            val memoSite = itemView.findViewById<TextView>(R.id.memoSite)
            val memoTitle = itemView.findViewById<TextView>(R.id.memoTitle)
            val memoContent = itemView.findViewById<TextView>(R.id.memoContent)
            val memoViewPager = itemView.findViewById<ViewPager>(R.id.memoView)
            val memoDate = itemView.findViewById<TextView>(R.id.memoDate)
            val memoTag = itemView.findViewById<TextView>(R.id.memoTag)
//            val autoMemoTag = itemView.findViewById<AutoLinkTextView>(R.id.autoMemoTag)

        }

        override fun getItemCount(): Int{
            return albumData.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val root = LayoutInflater.from(context).inflate(R.layout.main_fragment_main_memo_item, parent, false)
            return ViewHolder(root)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var imageList: ArrayList<String> = arrayListOf()
            val items: ArrayList<String> = arrayListOf()

         //   autoMemoTag.hashTagModeColor = ContextCompat.getColor(applicationContext, R.color.colorRed)
            albumData[position].imageArray!!.let { items.add(it.toString()) }
//            holder.autoMemoTag.addAutoLinkMode(MODE_HASHTAG)
            holder.memoSite.text = albumData?.get(position)?.site
            holder.memoTitle.text = albumData?.get(position)?.title
//            holder.memoViewPager.adapter = ViewPagerAdapter(picasso, albumData?.get(position)?.imageArray!!)
            holder.memoViewPager.adapter = ViewPagerAdapter(albumData?.get(position)?.imageArray!!)
            holder.memoDate.text = albumData?.get(position)?.date
            holder.memoContent.text = albumData?.get(position)?.content
         //   holder.memoTag.text = albumData?.get(position)?.tag
            setContent(albumData?.get(position)?.tag, holder.memoTag)
//            holder.autoMemoTag.text = albumData?.get(position)?.tag
//            holder.autoMemoTag.onAutoLinkClick { item: AutoLinkItem->
//                Toast.makeText(holder.autoMemoTag.context, item.toString(), Toast.LENGTH_LONG).show()
//            }
        }
    }

    inner class ViewPagerAdapter(val item: ArrayList<String>) : PagerAdapter() {

        override fun getCount(): Int {
            return item.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater
                .from(container.context)
                .inflate(R.layout.main_fragment_main_memo_viewpager, container, false)

            val image: ImageView = view.findViewById(R.id.memoImage)

            val items = Uri.parse(item[position])

            Glide.with(container.context)
                .load(items)
                .placeholder(R.color.colorGray)
//                .fit()
                .centerCrop()
                .into(image)

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            // memoIndicator.attachTo(memoView)
            //container.removeView(view as View)
        }
    }

//    inner class ViewPagerAdapter(private val picasso: Picasso, val item: ArrayList<String>) : PagerAdapter() {
//
//        override fun getCount(): Int {
//            return item.size
//        }
//
//        override fun isViewFromObject(view: View, `object`: Any): Boolean {
//            return view == `object`
//        }
//
//        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            val view = LayoutInflater
//                .from(container.context)
//                .inflate(R.layout.main_fragment_main_memo_viewpager, container, false)
//
//            val image: ImageView = view.findViewById(R.id.memoImage)
//
//            val items = Uri.parse(item[position])
//
//            picasso
//                .load(items)
//                .placeholder(R.color.colorGray)
//                .fit()
//                .centerCrop()
//                .into(image)
//
//            container.addView(view)
//            return view
//        }
//
//        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
//            // memoIndicator.attachTo(memoView)
//            //container.removeView(view as View)
//        }
//    }

}