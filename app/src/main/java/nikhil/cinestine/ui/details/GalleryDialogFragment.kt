package nikhil.cinestine.ui.details

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import nikhil.cinestine.R

class GalleryDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val urls = requireArguments().getStringArrayList(ARG_URLS).orEmpty()
        val start = requireArguments().getInt(ARG_START, 0)
        val pager = ViewPager2(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            adapter = ImagePagerAdapter(urls)
            setCurrentItem(start.coerceIn(0, (urls.size - 1).coerceAtLeast(0)), false)
        }
        return Dialog(requireContext(), theme).apply {
            setContentView(pager)
            setOnShowListener {
                pager.setOnClickListener { dismiss() }
            }
        }
    }

    private class ImagePagerAdapter(
        private val urls: List<String>
    ) : RecyclerView.Adapter<ImagePagerAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_gallery_page, parent, false) as ImageView
            return Holder(image)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.image.load(urls[position]) {
                crossfade(true)
                placeholder(R.drawable.ic_poster_placeholder)
            }
        }

        override fun getItemCount(): Int = urls.size

        class Holder(val image: ImageView) : RecyclerView.ViewHolder(image)
    }

    companion object {
        private const val ARG_URLS = "urls"
        private const val ARG_START = "start"

        fun newInstance(urls: List<String>, startIndex: Int) = GalleryDialogFragment().apply {
            arguments = Bundle().apply {
                putStringArrayList(ARG_URLS, ArrayList(urls))
                putInt(ARG_START, startIndex)
            }
        }
    }
}
