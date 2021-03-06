/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package net.reichholf.dreamdroid.adapter;

import java.util.ArrayList;

import net.reichholf.dreamdroid.DreamDroid;
import net.reichholf.dreamdroid.R;
import net.reichholf.dreamdroid.helpers.DateTime;
import net.reichholf.dreamdroid.helpers.ExtendedHashMap;
import net.reichholf.dreamdroid.helpers.ImageLoader;
import net.reichholf.dreamdroid.helpers.Python;
import net.reichholf.dreamdroid.helpers.enigma2.Event;
import net.reichholf.dreamdroid.helpers.enigma2.Picon;
import net.reichholf.dreamdroid.helpers.enigma2.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author sre
 * 
 */
public class ServiceListAdapter extends ArrayAdapter<ExtendedHashMap> {
	@SuppressWarnings("unused")
	private static String LOG_TAG = "ServiceListAdapter";
	private ImageLoader mImageLoader;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param services
	 */
	public ServiceListAdapter(Context context, int textViewResourceId, ArrayList<ExtendedHashMap> services) {
		super(context, textViewResourceId, services);
		mImageLoader = new ImageLoader();
		mImageLoader.setMode(ImageLoader.Mode.CORRECT);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		ExtendedHashMap service = getItem(position);
		return !Service.isMarker(service.getString(Service.KEY_REFERENCE));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ExtendedHashMap service = getItem(position);
		String next = service.getString(Event.PREFIX_NEXT.concat(Event.KEY_EVENT_TITLE));
		String now = service.getString(Event.KEY_EVENT_TITLE);

		boolean hasNext = next != null && !"".equals(next);
		boolean hasNow = now != null && !"".equals(now);

		int viewId = android.R.id.text1;
		int layoutId = R.layout.simple_list_item_1;

		if (Service.isMarker(service.getString(Service.KEY_REFERENCE))) {
			layoutId = R.layout.service_list_marker;
			hasNow = false;
		} else {
			if (hasNext) {
				viewId = R.id.service_list_item_nn;
				layoutId = R.layout.service_list_item_nn;
			} else if (hasNow) {
				viewId = R.id.service_list_item;
				layoutId = R.layout.service_list_item;
			}
		}
		if (view == null || view.getId() != viewId) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = li.inflate(layoutId, null);
		}

		ImageView piconView = (ImageView) view.findViewById(R.id.picon);
		Picon.setPiconForView(getContext(), piconView, mImageLoader, service);
		
		if(layoutId != R.layout.service_list_marker)
			view.setBackgroundResource(R.drawable.card_list_item_selector);

		if (service != null) {
			if (!hasNow) {
				setTextView(view, android.R.id.text1, service.getString(Event.KEY_SERVICE_NAME));
				return view;
			}
			setTextView(view, R.id.service_name, service.getString(Event.KEY_SERVICE_NAME));
			setTextView(view, R.id.event_now_title, service.getString(Event.KEY_EVENT_TITLE));
			setTextView(view, R.id.event_now_start, service.getString(Event.KEY_EVENT_START_TIME_READABLE));
			setTextView(view, R.id.event_now_duration, service.getString(Event.KEY_EVENT_DURATION_READABLE));
			ProgressBar progress = (ProgressBar) view.findViewById(R.id.service_progress);

			long max = -1;
			long cur = -1;
			String duration = service.getString(Event.KEY_EVENT_DURATION);
			String start = service.getString(Event.KEY_EVENT_START);

			if (duration != null && start != null && !Python.NONE.equals(duration) && !Python.NONE.equals(start)) {
				try {
					max = Double.valueOf(duration).longValue() / 60;
					cur = max - DateTime.getRemaining(duration, start);
				} catch (Exception e) {
					Log.e(DreamDroid.LOG_TAG, e.toString());
				}
			}

			if (max > 0 && cur >= 0) {
				progress.setVisibility(View.VISIBLE);
				progress.setMax((int) max);
				progress.setProgress((int) cur);
			} else {
				progress.setVisibility(View.GONE);
			}

			if (hasNext) {
				setTextView(view, R.id.event_next_title,
						service.getString(Event.PREFIX_NEXT.concat(Event.KEY_EVENT_TITLE)));
				setTextView(view, R.id.event_next_start,
						service.getString(Event.PREFIX_NEXT.concat(Event.KEY_EVENT_START_TIME_READABLE)));
				setTextView(view, R.id.event_next_duration,
						service.getString(Event.PREFIX_NEXT.concat(Event.KEY_EVENT_DURATION_READABLE)));
			}
		}

		return view;
	}

	private void setTextView(View view, int id, String value) {
		TextView tv = (TextView) view.findViewById(id);
		if (tv != null)
			tv.setText(value);
	}

}
