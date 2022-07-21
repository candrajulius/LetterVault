package com.google.developers.lettervault.data

import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.developers.lettervault.util.LetterLock
import com.google.developers.lettervault.util.executeThread

/**
 * Handles data sources and execute on the correct threads.
 */
class DataRepository(private val letterDao: LetterDao) {

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = LetterDatabase.getInstance(context)
                    instance = DataRepository(database.letterDao())
                }
                return instance
            }
        }
    }

    fun getRecentLetter(filterQuery: LetterState): LiveData<Letter>{
        return letterDao.getRecentLetter(getFilteredQuery(filterQuery))
    }

    fun openedLetter() : Letter{
       return letterDao.openLetter()
    }


    /**
     * Get letters with a filtered state for paging.
     */
    fun getLetters(filter: LetterState): LiveData<PagedList<Letter>> {

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()

        return LivePagedListBuilder(letterDao.getLetters(getFilteredQuery(filter)), config).build()

    }

    fun getLetter(id: Long): LiveData<Letter> {
        return letterDao.getLetter(id)
    }

    fun delete(letter: Letter) {
        letterDao.delete(letter)
    }

    /**
     * Add a letter to database and schedule a notification on
     * when the letter vault can be opened.
     */
    fun save(letter: Letter) = executeThread {
        letterDao.insert(letter)
    }

    /**
     * Update database with a decode letter content and update the opened timestamp.
     */
    fun openLetter(letter: Letter) = executeThread {
        val letterCopy = letter.copy(
            subject = LetterLock.retrieveMessage(letter.subject),
            content = LetterLock.retrieveMessage(letter.content),
            opened = System.currentTimeMillis()
        )
        letterDao.update(letterCopy)
    }

    /**
     * Create a raw query at runtime for filtering the letters.
     */
    private fun getFilteredQuery(filter: LetterState): SimpleSQLiteQuery {
        val now = System.currentTimeMillis()
        val simpleQuery = StringBuilder()
            .append("SELECT * FROM letter ")

        if (filter == LetterState.FUTURE) {
            simpleQuery.append("WHERE expires >= $now OR expires <= $now AND opened IS 0")
        }
        if (filter == LetterState.OPENED) {
            simpleQuery.append("WHERE opened IS NOT 0")
        }
        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}
