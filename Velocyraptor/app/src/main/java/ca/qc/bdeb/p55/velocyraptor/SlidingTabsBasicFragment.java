/*
 * Copyright (C) 2013 The Android Open Source Project
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

package ca.qc.bdeb.p55.velocyraptor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ca.qc.bdeb.p55.velocyraptor.common.Formatting;
import ca.qc.bdeb.p55.velocyraptor.common.view.ArrayAdapterAchievement;
import ca.qc.bdeb.p55.velocyraptor.common.view.ArrayAdapterDeCourse;
import ca.qc.bdeb.p55.velocyraptor.common.view.SlidingTabLayout;
import ca.qc.bdeb.p55.velocyraptor.db.AppDatabase;
import ca.qc.bdeb.p55.velocyraptor.model.Achievement;
import ca.qc.bdeb.p55.velocyraptor.model.Course;
import ca.qc.bdeb.p55.velocyraptor.model.HistoriqueDeCourse;

/**
 * A basic sample which shows how to use {@link ca.qc.bdeb.p55.velocyraptor.common.view.SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";

    private static final Map<Integer, Course.TypeCourse> raceTypeSpinnerPositionToType = new HashMap<>();

    static {
        raceTypeSpinnerPositionToType.put(0, Course.TypeCourse.VELO);
        raceTypeSpinnerPositionToType.put(1, Course.TypeCourse.APIED);
    }

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)

    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     * <p>
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    // Inflate a new layout from our resources
                    view = getActivity().getLayoutInflater().inflate(R.layout.fragment_stats_cumulative,
                            container, false);

                    populateCumulativeStatsView(view);

                    // Add the newly created View to the ViewPager
                    container.addView(view);
                    break;
                case 1:

                    view = getActivity().getLayoutInflater().inflate(R.layout.fragment_historique,
                            container, false);
                    // Add the newly created View to the ViewPager
                    container.addView(view);

                    ListView listViewHistorique = (ListView) view.findViewById(R.id.historiquecourse_lstview);
                    ArrayList<HistoriqueDeCourse> lstCourse = new ArrayList<>();
                    lstCourse = AppDatabase.getInstance().getAllLastRaces();
                    if (lstCourse != null) {
                        ArrayAdapterDeCourse adapterDeCourse = new ArrayAdapterDeCourse(getActivity(), R.layout.historique_une_course, lstCourse);
                        listViewHistorique.setAdapter(adapterDeCourse);
                    }
                    break;

                case 2:
                    view = getActivity().getLayoutInflater().inflate(R.layout.fragment_accomplissement,
                            container, false);
                    ListView listViewSucces = (ListView) view.findViewById(R.id.accomplissement_lstview);
                    ArrayList<Achievement> lstAchievement = new ArrayList<>();
                    lstAchievement = AppDatabase.getInstance().getAllAchievement();
                    ArrayAdapterAchievement adapteurAchievement=  new ArrayAdapterAchievement(getActivity(), R.layout.accomplissement_un_accomplissement, lstAchievement);
                    listViewSucces.setAdapter(adapteurAchievement);
                    // Add the newly created View to the ViewPager
                    container.addView(view);
                    break;
            }


            // Return the View
            return view;
        }

        /**
         * Dans une vue, remplit les données cumulatives en définissant le contenu des
         * vues du fragment en conséquence.
         *
         * @param statsFragmentView Vue de statistiques cumulatives.
         */
        private void populateCumulativeStatsView(final View statsFragmentView) {
            Spinner raceTypeChooser = (Spinner) statsFragmentView.findViewById(R.id.stats_spin_racetype);

            raceTypeChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateCumulativeStatsView(statsFragmentView, raceTypeSpinnerPositionToType.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            updateCumulativeStatsView(statsFragmentView, raceTypeSpinnerPositionToType.get(0));
        }

        private void updateCumulativeStatsView(View view, Course.TypeCourse typeCourse) {
            final AppDatabase db = AppDatabase.getInstance();

            TextView nbRacesLabel = (TextView) view.findViewById(R.id.stats_lbl_nbraces);
            TextView durationLabel = (TextView) view.findViewById(R.id.stats_lbl_totalduration);
            TextView distanceLabel = (TextView) view.findViewById(R.id.stats_lbl_totaldistance);
            TextView caloriesLabel = (TextView) view.findViewById(R.id.stats_lbl_totalcalories);
            TextView stepsLabel = (TextView) view.findViewById(R.id.stats_lbl_nbsteps);

            nbRacesLabel.setText(getString(R.string.nbraces) + " " + db.getNumberRaces(typeCourse));
            durationLabel.setText(getString(R.string.totalduration) + " "
                    + Formatting.formatNiceDuration(db.getTotalDurationInSeconds(typeCourse)));
            distanceLabel.setText(getString(R.string.totaldistance) + " "
                    + Formatting.formatDistance(db.getTotalDistance(typeCourse)));
            caloriesLabel.setText(getString(R.string.totalcalories) + " " + db.getTotalCalories(typeCourse));

            if (typeCourse == Course.TypeCourse.APIED) {
                stepsLabel.setVisibility(View.VISIBLE);
                stepsLabel.setText(getString(R.string.totalsteps) + " " + db.getTotalSteps());
            } else {
                stepsLabel.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
