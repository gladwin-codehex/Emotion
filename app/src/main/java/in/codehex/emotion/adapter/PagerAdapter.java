package in.codehex.emotion.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.codehex.emotion.AngerPlaylistFragment;
import in.codehex.emotion.DisgustPlaylistFragment;
import in.codehex.emotion.FearPlaylistFragment;
import in.codehex.emotion.RomancePlaylistFragment;
import in.codehex.emotion.SadnessPlaylistFragment;
import in.codehex.emotion.SurprisePlaylistFragment;

/**
 * Created by Bobby on 04-09-2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public PagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RomancePlaylistFragment romancePlaylistFragment = new RomancePlaylistFragment();
                return romancePlaylistFragment;
            case 1:
                SadnessPlaylistFragment sadnessPlaylistFragment = new SadnessPlaylistFragment();
                return sadnessPlaylistFragment;
            case 2:
                AngerPlaylistFragment angerPlaylistFragment = new AngerPlaylistFragment();
                return angerPlaylistFragment;
            case 3:
                FearPlaylistFragment fearPlaylistFragment = new FearPlaylistFragment();
                return fearPlaylistFragment;
            case 4:
                SurprisePlaylistFragment surprisePlaylistFragment = new SurprisePlaylistFragment();
                return surprisePlaylistFragment;
            case 5:
                DisgustPlaylistFragment disgustPlaylistFragment = new DisgustPlaylistFragment();
                return disgustPlaylistFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
