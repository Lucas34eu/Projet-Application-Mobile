package com.epitech.epicture.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.epitech.epicture.R
import com.epitech.epicture.databinding.FragmentSearchBinding
import com.epitech.epicture.ui.ImageGridAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

/**
 * Search fragment
 */
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchBaseObservable: SearchBaseObservable
    private val adapter = ImageGridAdapter(ImageGridAdapter.ClickListener {
        searchViewModel.selectImage(it)
    })
    private var searchJob: Job? = null

    /**
     * Creates fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
        searchBaseObservable = SearchBaseObservable()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.lifecycleOwner = this
        binding.searchList.adapter = adapter
        binding.baseObservable = searchBaseObservable
        binding.viewModel = searchViewModel

        searchViewModel.selectedImage.observe(viewLifecycleOwner, { selectedImage ->
            selectedImage?.let {
                this.findNavController().navigate(
                    SearchFragmentDirections.actionNavigationSearchToImageDetailsFragment(it.id)
                )
                searchViewModel.selectImageDone()
            }
        })

        initSearch()
        return binding.root
    }

    private val onEditorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_GO) {
            updateImageListFromQuery()
            true
        } else {
            false
        }
    }

    private val onKeyListener = View.OnKeyListener { _, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                updateImageListFromQuery()
            }
            true
        } else {
            false
        }
    }

    /**
     * Initializes search
     */
    private fun initSearch() {
        binding.baseQueryInput.setOnEditorActionListener(onEditorActionListener)
        binding.baseQueryInput.setOnKeyListener(onKeyListener)
        lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.searchList.scrollToPosition(0) }
        }
    }

    /**
     * Makes an HTTP request to imgur api and updates displayed image list
     */
    private fun updateImageListFromQuery() {
        val query = searchBaseObservable.getQuery().trim()

        view?.hideKeyboard()
        if (query.isNotEmpty()) {
            binding.baseQueryLayout.error = null
            simpleSearch(query)
        } else {
            binding.baseQueryLayout.error = "You must fill this field"
        }
    }

    /**
     * Hides keyboard
     */
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * Makes a simple search
     */
    private fun simpleSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            searchViewModel.simpleSearch(query).collectLatest {
                adapter.submitData(it)
            }
        }
    }

}