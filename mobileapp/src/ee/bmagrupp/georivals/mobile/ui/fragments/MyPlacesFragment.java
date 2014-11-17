package ee.bmagrupp.georivals.mobile.ui.fragments;

import ee.bmagrupp.georivals.mobile.ui.widgets.TabItem;
import android.app.Fragment;

public class MyPlacesFragment extends Fragment implements TabItem {
	private final int tabNameId;
	private final int tabIconId;

	public MyPlacesFragment(int tabNameId, int tabIconId) {
		this.tabNameId = tabNameId;
		this.tabIconId = tabIconId;
	}

	@Override
	public int getTabIconId() {
		return tabIconId;
	}

	@Override
	public int getTabNameId() {
		return tabNameId;
	}

}
