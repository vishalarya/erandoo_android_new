package erandoo.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import erandoo.app.R;
import erandoo.app.database.Project;
import erandoo.app.utilities.Util;

public class ViewPagerAdapter extends PagerAdapter {
	private Context context;
	private ArrayList<Project> arrayList;
	private LayoutInflater inflater;

	public ViewPagerAdapter(Context context, ArrayList<Project> arrayList) {
		this.context = context;
		this.arrayList = arrayList;
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		ImageView imgflag;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.ed_portfolio_slider_row, container, false);
		imgflag = (ImageView) itemView.findViewById(R.id.imv);		
		imgflag.setScaleType(ScaleType.FIT_XY);
		
		Util.loadImage(imgflag,arrayList.get(position).task_image, R.drawable.ic_launcher);
		imgflag.setScaleType(ScaleType.FIT_XY);
		((ViewPager) container).addView(itemView);
		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((LinearLayout) object);
	}
}
