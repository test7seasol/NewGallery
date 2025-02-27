package com.gallery.photos.editpic.callendservice.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gallery.photos.editphotovideo.callendservice.fragments.RecentsPictureFragmentCall;
import com.gallery.photos.editpic.Fragment.AllVideosFragmentcall;
import com.gallery.photos.editpic.R;
import com.gallery.photos.editpic.callendservice.model.ContactCDO;
import com.gallery.photos.editpic.databinding.FragmentAfterCallBinding;

public final class AfterCallFragment extends Fragment {
    public static final Companion Companion = new Companion();
    public FragmentAfterCallBinding binding;
    private ContactCDO contact;
    private String contactNumber = "";
    private String contactName = "";
    private String contactID = "";

    public final void setContactNumber(String str) {
        this.contactNumber = str;
    }

    public final void setContactName(String str) {
        this.contactName = str;
    }

    public final void setContactID(String str) {
        this.contactID = str;
    }

    public final void setContact(ContactCDO contactCDO) {
        this.contact = contactCDO;
    }

    public static final class Companion {
        public final AfterCallFragment getInstance(String str, String str2, String str3, ContactCDO contactCDO) {
            AfterCallFragment afterCallFragment = new AfterCallFragment();
            afterCallFragment.setContactNumber(str);
            afterCallFragment.setContactName(str2);
            afterCallFragment.setContactID(str3);
            afterCallFragment.setContact(contactCDO);
            return afterCallFragment;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentAfterCallBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initView();
    }

    public final void initView() {
        bindListener();
    }



    public  void bindListener() {


        binding.tvPhoto.setTextColor(getResources().getColor(R.color.appcolor));
        binding.tvVideo.setTextColor(getResources().getColor(R.color.appgrey));
        binding.ivPhoto.setColorFilter(getResources().getColor(R.color.appcolor));
        binding.ivVideo.setColorFilter(getResources().getColor(R.color.appgrey));
        loadFragment(new RecentsPictureFragmentCall());


        binding.cvPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {

                binding.tvPhoto.setTextColor(getResources().getColor(R.color.appcolor));
                binding.tvVideo.setTextColor(getResources().getColor(R.color.appgrey));

                binding.ivPhoto.setColorFilter(getResources().getColor(R.color.appcolor));
                binding.ivVideo.setColorFilter(getResources().getColor(R.color.appgrey));

                loadFragment(new RecentsPictureFragmentCall());
               /* startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();*/

            }
        });
        binding.cvVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {

                binding.tvVideo.setTextColor(getResources().getColor(R.color.appcolor));
                binding.tvPhoto.setTextColor(getResources().getColor(R.color.appgrey));


                binding.ivVideo.setColorFilter(getResources().getColor(R.color.appcolor));
                binding.ivPhoto.setColorFilter(getResources().getColor(R.color.appgrey));


                loadFragment(new AllVideosFragmentcall());

            }
        });



    }

    private Fragment activeFragment = null;

    private void loadFragment(Fragment newFragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String fragmentTag = newFragment.getClass().getSimpleName();

        // Check if the fragment already exists
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (activeFragment != null) {
            transaction.hide(activeFragment); // Hide the currently active fragment
        }

        if (fragment == null) {
            // Add the new fragment only if it's not already added
            fragment = newFragment;
            transaction.add(R.id.framecontainercall, fragment, fragmentTag);
        } else {
            // If the fragment exists, just show it
            transaction.show(fragment);
        }

        transaction.commitAllowingStateLoss();
        activeFragment = fragment;
    }



}
