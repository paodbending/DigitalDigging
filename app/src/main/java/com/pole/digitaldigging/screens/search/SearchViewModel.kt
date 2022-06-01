package com.pole.digitaldigging.screens.search

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pole.domain.usecases.GetSearchResults
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    getSearchResults: GetSearchResults,
) : ViewModel() {

    private val model: Model = ModelImpl(
        getSearchResults = getSearchResults,
        coroutineScope = viewModelScope,
    )

    fun buildPresenter(view: View, lifecycleOwner: LifecycleOwner): Presenter {
        return PresenterImpl(
            model = model,
            view = view,
            lifecycleOwner = lifecycleOwner
        )
    }
}