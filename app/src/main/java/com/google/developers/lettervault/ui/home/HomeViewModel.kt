package com.google.developers.lettervault.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.developers.lettervault.data.DataRepository
import com.google.developers.lettervault.data.Letter
import com.google.developers.lettervault.data.LetterState

/**
 * ViewMode for the HomeActivity only holds recent letter.
 */
class HomeViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _queryTypeRecent = MutableLiveData<LetterState>()

    init {
        _queryTypeRecent.value  = LetterState.FUTURE
    }

    fun getReccentLetter() = Transformations.switchMap(_queryTypeRecent){
        dataRepository.getRecentLetter(it)
    }

}
