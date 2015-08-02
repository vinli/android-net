package li.vin.net;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import rx.Subscriber;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by christophercasey on 8/1/15.
 */
public final class DeviceChooser {

  /*package*/ static Observable<Device> letUserChooseDevice(VinliApp vinliApp,
      @NonNull Activity activity) {
    return letUserChooseDevice(activity, AppObservable.bindActivity(activity, vinliApp.devices()));
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  /*package*/ static Observable<Device> letUserChooseDevice(VinliApp vinliApp,
      @NonNull Fragment fragment) {
    if (Build.VERSION.SDK_INT < 11) {
      throw new RuntimeException("cannot get Activity context from Fragment below API level 11.");
    }
    Activity activity = fragment.getActivity();
    if (activity == null) throw new IllegalStateException("bad Fragment lifecycle: null Activity.");
    return letUserChooseDevice(activity, AppObservable.bindFragment(fragment, vinliApp.devices()));
  }

  /*package*/ static Observable<Device> letUserChooseDevice(VinliApp vinliApp,
      @NonNull android.support.v4.app.Fragment fragment) {
    Activity activity = fragment.getActivity();
    if (activity == null) throw new IllegalStateException("bad Fragment lifecycle: null Activity.");
    return letUserChooseDevice(activity, AppObservable.bindFragment(fragment, vinliApp.devices()));
  }

  private static Observable<Device> letUserChooseDevice(
      @NonNull Context context,
      Observable<Page<Device>> devicesObservable) {
    return devicesObservable
        .collect(new Func0<List<Device>>() {
          @Override public List<Device> call() {
            return new ArrayList<>();
          }
        }, new Action2<List<Device>, Page<Device>>() {
          @Override public void call(List<Device> devices, Page<Device> devicePage) {
            devices.addAll(devicePage.getItems());
          }
        })
        .flatMap(new DeviceChooserDialogMapFunc(context));
  }

  private static class DeviceChooserDialogMapFunc implements Func1<List<Device>, Observable<Device>> {

    final WeakReference<Context> contextRef;

    DeviceChooserDialogMapFunc(Context context) {
      contextRef = new WeakReference<>(context);
    }

    @Override public Observable<Device> call(final List<Device> devices) {

      return Observable.create(new Observable.OnSubscribe<Device>() {
        @Override public void call(final Subscriber<? super Device> subscriber) {

            Context context = contextRef.get();
            if (context == null) {
              subscriber.onError(new RuntimeException("null context; weakreference died?"));
              return;
            }
            if (devices.isEmpty()) {
              subscriber.onError(new RuntimeException("no devices to choose from!"));
              return;
            }
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
                  text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                      parent.getResources().getDimensionPixelSize(R.dimen.device_chooser_item_text_size));
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
            final AtomicBoolean resultDelivered = new AtomicBoolean();
            spinner.setSelection(0);
            AlertDialog.Builder adb = new AlertDialog.Builder(context);
            adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                Spinner spinner = (Spinner) ((AlertDialog) dialog).getWindow()
                    .getDecorView()
                    .findViewById(R.id.device_chooser_spinner);
                if (resultDelivered.compareAndSet(false, true)) {
                  subscriber.onNext(devices.get(spinner.getSelectedItemPosition()));
                  subscriber.onCompleted();
                }
              }
            });
            adb.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int which) {
                if (resultDelivered.compareAndSet(false, true)) {
                  subscriber.onError(new RuntimeException("device chooser canceled!"));
                }
              }
            });
            adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override public void onCancel(DialogInterface dialog) {
                if (resultDelivered.compareAndSet(false, true)) {
                  subscriber.onError(new RuntimeException("device chooser canceled!"));
                }
              }
            });
            if (Build.VERSION.SDK_INT >= 17) {
              adb.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override public void onDismiss(DialogInterface dialog) {
                  if (resultDelivered.compareAndSet(false, true)) {
                    subscriber.onError(new RuntimeException("device chooser canceled!"));
                  }
                }
              });
            }
            adb.setTitle(R.string.title_device_chooser);
            adb.setView(spinner);
            adb.show();

            // Set margins (after layout params are provided by Dialog)
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) spinner.getLayoutParams();
            lp.topMargin = lp.bottomMargin = lp.leftMargin = lp.rightMargin =
                context.getResources().getDimensionPixelSize(R.dimen.standard_edge_margin);

        }
      }).subscribeOn(AndroidSchedulers.mainThread());
    }
  }

  private DeviceChooser() { }
}
