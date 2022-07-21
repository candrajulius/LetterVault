package com.google.developers.lettervault.ui.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import com.google.developers.lettervault.R
import com.google.developers.lettervault.data.Letter
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * View holds a letter for RecyclerView.
 */
class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var letter: Letter
    private val context = itemView.context
    private val simpleDate = SimpleDateFormat("MMM d Y, h:mm a", Locale.getDefault())

    fun bindData(letter: Letter, clickListener: (Letter) -> Unit) {
        this.letter = letter
        itemView.setOnClickListener { clickListener(letter) }
        val textOpenend = itemView.findViewById<TextView>(R.id.textOpened)
        val subjectText = itemView.findViewById<TextView>(R.id.subject)
        val content = itemView.findViewById<TextView>(R.id.content)
        val smallLockImage = itemView.findViewById<ImageView>(R.id.lock)
        val largeLockImage = itemView.findViewById<ImageView>(R.id.imageLock)

        subjectText.text = letter.subject
        content.text = letter.content

        if (letter.expires < System.currentTimeMillis() && letter.opened != 0L) {
            val opened =
                context.getString(R.string.title_opened, simpleDate.format(letter.opened))
            textOpenend.text = opened
            largeLockImage.visibility = View.GONE
            smallLockImage.visibility = View.VISIBLE
        } else {
            if (letter.expires < System.currentTimeMillis()) {
                val ready = context.getString(R.string.letter_ready)
                textOpenend.text = ready
                largeLockImage.visibility = View.VISIBLE
                smallLockImage.visibility = View.GONE
            } else {
                val opening =
                    context.getString(R.string.letter_opening, simpleDate.format(letter.expires))
                textOpenend.text = opening
                largeLockImage.visibility = View.VISIBLE
                smallLockImage.visibility = View.GONE
            }
        }
    }

    /**
     * This method is used during automated tests.
     *
     * DON'T REMOVE THIS METHOD
     */
    @VisibleForTesting
    fun getLetter(): Letter = letter
}
