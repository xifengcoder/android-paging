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

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.android.codelabs.paging.Injection
import com.example.android.codelabs.paging.databinding.ActivitySearchRepositoriesBinding
import com.example.android.codelabs.paging.model.RepoSearchResult

class SearchRepositoriesActivity : AppCompatActivity() {
    companion object {
        const val TAG = "paging"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchRepositoriesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this))
            .get(SearchRepositoriesViewModel::class.java)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)
        binding.bindState(viewModel.state, viewModel.onAction)
    }

    /**
     * Binds the [UiStateData] provided  by the [SearchRepositoriesViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun ActivitySearchRepositoriesBinding.bindState(
        uiStateData: LiveData<UiStateData>,
        onAction: (UiActionType) -> Unit
    ) {
        val repoAdapter = ReposAdapter()
        recyclerView.adapter = repoAdapter

        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onAction)
                true
            } else {
                false
            }
        }
        searchEdit.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onAction)
                true
            } else {
                false
            }
        }

        uiStateData
            .map(UiStateData::query)
            .distinctUntilChanged()
            .observe(this@SearchRepositoriesActivity, { queryString ->
                searchEdit.setText(queryString)
            })

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                onAction(
                    UiActionType.Scroll(
                        visibleItemCount, lastVisibleItem, totalItemCount
                    )
                )
            }
        })

        uiStateData
            .map(UiStateData::searchResult)
            .distinctUntilChanged()
            .observe(this@SearchRepositoriesActivity) { result ->
                Log.d(TAG, "observe result: $result")
                when (result) {
                    is RepoSearchResult.Success -> {
                        showEmptyList(result.data.isEmpty())
                        repoAdapter.submitList(result.data)
                    }
                    is RepoSearchResult.Error -> {
                        Toast.makeText(
                            this@SearchRepositoriesActivity,
                            "\uD83D\uDE28 Wooops $result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun ActivitySearchRepositoriesBinding.updateRepoListFromInput(uiAction: (UiActionType) -> Unit) {
        Log.d(TAG, "updateRepoListFromInput");
        searchEdit.text.trim().let {
            if (it.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                uiAction(UiActionType.Search(it.toString()))
            }
        }
    }

    private fun ActivitySearchRepositoriesBinding.showEmptyList(show: Boolean) {
        emptyList.isVisible = show
        recyclerView.isVisible = !show
    }
}
