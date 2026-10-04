package com.media.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.media.player.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsPlaybackSpeed.setOnClickListener {
            Toast.makeText(requireContext(), "Playback Speed Options", Toast.LENGTH_SHORT).show()
        }

        binding.settingsEqualizer.setOnClickListener {
            Toast.makeText(requireContext(), "Equalizer Settings", Toast.LENGTH_SHORT).show()
        }

        binding.settingsStoragePermission.setOnClickListener {
            Toast.makeText(requireContext(), "Manage Storage Permissions", Toast.LENGTH_SHORT).show()
        }

        binding.settingsAbout.setOnClickListener {
            Toast.makeText(requireContext(), "Playit Style Media Explorer v1.0.0", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}