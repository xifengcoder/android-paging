/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.ui

import android.util.Log
import androidx.lifecycle.*
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.model.RepoSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

private const val VISIBLE_THRESHOLD = 5
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "Android"

class SearchRepositoriesViewModel(
    private val repository: GithubRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val uiStateLiveData: LiveData<UiStateData> //androidx.lifecycle.MediatorLiveData@aaa7110
    val onAction: (UiActionType) -> Unit

    init {
        val queryLiveData =
            MutableLiveData<String>(savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY)

        uiStateLiveData = queryLiveData
            .distinctUntilChanged()
            .switchMap { queryString ->
                liveData(EmptyCoroutineContext, 5000) {
                    val uiState = repository.getSearchResultStream(queryString)
                        .map {
                            UiStateData(queryString, it)
                        }
                        .asLiveData(Dispatchers.Main)
                    emitSource(uiState)
                }
            }

        onAction = { action ->
            Log.d(MainActivity.TAG, "onAction action: $action")
            when (action) {
                is UiActionType.Search -> {
                    queryLiveData.postValue(action.queryString)
                }
                is UiActionType.Scroll -> {
                    Log.d(
                        MainActivity.TAG,
                        "onAction Scroll shouldFetchMore: ${action.shouldFetchMore}"
                    )
                    if (action.shouldFetchMore) {
                        val immutableQuery = queryLiveData.value
                        if (immutableQuery != null) {
                            viewModelScope.launch {
                                repository.requestMore(immutableQuery)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = uiStateLiveData.value?.query
        super.onCleared()
    }
}

private val UiActionType.Scroll.shouldFetchMore
    get() = visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount

sealed class UiActionType {
    data class Search(val queryString: String) : UiActionType() {
        override fun toString(): String {
            return "Search{queryString: $queryString}"
        }
    }

    data class Scroll(
        val visibleItemCount: Int, val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiActionType() {
        override fun toString(): String {
            return "Scroll{visibleItemCount: " + visibleItemCount +
                    ", lastVisibleItemPosition: " + lastVisibleItemPosition +
                    " totalItemCount: " + totalItemCount + "}"
        }
    }
}

data class UiStateData(
    val query: String,
    val searchResult: RepoSearchResult
)