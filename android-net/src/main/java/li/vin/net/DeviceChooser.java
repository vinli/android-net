package li.vin.net;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;

/**
 * Created by christophercasey on 8/1/15.
 */
public final class DeviceChooser {

  /*package*/ static void letUserChooseDevice(VinliApp vinliApp,
      @NonNull Activity activity,
      @NonNull PendingIntent pendingIntent,
      @NonNull String deviceChosenExtraKey) {
    letUserChooseDevice(activity, AppObservable.bindActivity(activity, vinliApp.devices()),
        pendingIntent, deviceChosenExtraKey);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  /*package*/ static void letUserChooseDevice(VinliApp vinliApp,
      @NonNull Fragment fragment,
      @NonNull PendingIntent pendingIntent,
      @NonNull String deviceChosenExtraKey) {
    if (Build.VERSION.SDK_INT < 11) {
      throw new RuntimeException("cannot get Activity context from Fragment below API level 11.");
    }
    Activity activity = fragment.getActivity();
    if (activity == null) throw new IllegalStateException("bad Fragment lifecycle: null Activity.");
    letUserChooseDevice(activity, AppObservable.bindFragment(fragment, vinliApp.devices()),
        pendingIntent, deviceChosenExtraKey);
  }

  /*package*/ static void letUserChooseDevice(VinliApp vinliApp,
      @NonNull android.support.v4.app.Fragment fragment,
      @NonNull PendingIntent pendingIntent,
      @NonNull String deviceChosenExtraKey) {
    Activity activity = fragment.getActivity();
    if (activity == null) throw new IllegalStateException("bad Fragment lifecycle: null Activity.");
    letUserChooseDevice(activity, AppObservable.bindFragment(fragment, vinliApp.devices()),
        pendingIntent, deviceChosenExtraKey);
  }

  private static void letUserChooseDevice(
      @NonNull Context context,
      Observable<Page<Device>> devicesObservable,
      @NonNull final PendingIntent pendingIntent,
      @NonNull final String deviceChosenExtraKey) {
    final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
    final ArrayList<Device> devices = new ArrayList<>();
    devicesObservable.subscribe(new Subscriber<Page<Device>>() {
      @Override public void onCompleted() {
        Context context = contextWeakReference.get();
        if (context == null) return;
        if (devices.isEmpty()) return;
        Spinner spinner = new Spinner(context);
        spinner.setId(R.id.device_chooser_spinner);
        spinner.setAdapter(new BaseAdapter() {
          @Override public int getCount() {
            return devices.size();
          }

          @Override public Object getItem(int position) {
            return devices.get(position);
          }

          @Override public long getItemId(int position) {
            return position;
          }

          @Override public View getView(int position, View convertView, ViewGroup parent) {
            TextView text;
            if (convertView == null) {
              text = new TextView(parent.getContext());
              text.setTextSize(TypedValue.COMPLEX_UNIT_PX, parent.getResources()
                  .getDimensionPixelSize(R.dimen.device_chooser_item_text_size));
              int pad = parent.getResources().getDimensionPixelSize(R.dimen.standard_edge_margin);
              text.setPadding(pad, pad / 2, pad, pad / 2);
              text.setMaxLines(1);
              text.setEllipsize(TextUtils.TruncateAt.END);
            } else {
              text = (TextView) convertView;
            }
            text.setText(devices.get(position).name());
            return text;
          }
        });
        spinner.setSelection(0);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            Intent i = new Intent();
            Spinner spinner = (Spinner) ((AlertDialog) dialog)
                .getWindow()
                .getDecorView()
                .findViewById(R.id.device_chooser_spinner);
            i.putExtra(deviceChosenExtraKey, devices.get(spinner.getSelectedItemPosition()));
            try {
              pendingIntent.send(((AlertDialog) dialog).getContext(), 0, i);
            } catch (PendingIntent.CanceledException e) {
              // ignore exception, just means caller canceled and we don't care
            }
          }
        });
        adb.setNegativeButton(android.R.string.cancel, null);
        adb.setTitle(R.string.title_device_chooser);
        adb.setView(spinner);
        adb.show();

        // Set margins (after layout params are provided by Dialog)
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) spinner.getLayoutParams();
        lp.topMargin = lp.bottomMargin = lp.leftMargin = lp.rightMargin =
            context.getResources().getDimensionPixelSize(R.dimen.standard_edge_margin);
      }

      @Override public void onError(Throwable e) {
        Log.d(DeviceChooser.class.getSimpleName(), "letUserChooseDevice error: " + e);
      }

      @Override public void onNext(Page<Device> devicePage) {
        devices.addAll(devicePage.getItems());
      }
    });
  }

  private DeviceChooser() { }
}
