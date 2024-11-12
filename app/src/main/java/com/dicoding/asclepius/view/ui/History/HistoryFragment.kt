package com.dicoding.asclepius.view.ui.History

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.adapter.HistoryAdapter
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: HistoryViewModelFactory =
            HistoryViewModelFactory.getInstance(requireActivity())
        val historyViewModel: HistoryViewModel by viewModels {
            factory
        }

        val adapterHistory = HistoryAdapter{ analizeResult ->
            historyViewModel.deleteHistory(analizeResult)
        }

        historyViewModel.getHistory().observe(viewLifecycleOwner) { history ->
            binding.progressBar3.visibility = View.GONE
            val detail = arrayListOf<HistoryEntity>()
            history.map {
                val detailItem = HistoryEntity(
                    id = it.id,
                    imageUri = it.imageUri,
                    prediction = it.prediction
                )
                detail.add(detailItem)
            }
            adapterHistory.submitList(detail)
            if (detail.isEmpty()) {
                binding.textNotifications.visibility = View.VISIBLE
                binding.textNotifications.text = getString(R.string.empty_history)
            } else {
                binding.textNotifications.visibility = View.GONE
            }
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterHistory
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}