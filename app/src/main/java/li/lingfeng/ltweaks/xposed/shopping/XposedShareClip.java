package li.lingfeng.ltweaks.xposed.shopping;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import li.lingfeng.ltweaks.utils.Logger;
import li.lingfeng.ltweaks.utils.ShareUtils;
import li.lingfeng.ltweaks.xposed.XposedBase;
import li.lingfeng.ltweaks.ywhook.YWUtilsForMainFrameActivity;
import li.lingfeng.ltweaks.ywhook.YWUtilsLogger;

/**
 * Created by smallville on 2017/5/31.
 */

public abstract class XposedShareClip extends XposedBase {

    private Activity mActivity;
    private boolean mIsSharing = false;



    @Override
    protected void handleLoadPackage() throws Throwable {






        findAndHookMethod(ClipboardManager.class, "setPrimaryClip", ClipData.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (isSharing() && mActivity != null) {
                    Logger.i("ClipboardManager setPrimaryClip" + param.args[0]);
                    ClipData clipData = (ClipData) param.args[0];

                    ShareUtils.shareClipWithSnackbar(mActivity, clipData);
                }
            }
        });

        findAndHookActivity(getItemActivity(), "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mActivity = (Activity) param.thisObject;

                Intent intent = mActivity.getIntent();


                String foundProductId = "";
                if(null != intent){
                    if(null != intent.getExtras()){
                        foundProductId = intent.getExtras().get("id").toString();
                        Logger.toast_i(mActivity, "id:" + foundProductId);
                    }else{

                    }
                }
                ViewGroup rootView = (ViewGroup) mActivity.findViewById(android.R.id.content);
                recursiveLoopChildren(rootView);
                // YWUtilsLogger.printClassMethods2ExportedActivity(mActivity, null);

                /*
                Intent _intent = new Intent();
                _intent.setClassName(
                        // Your app's package name
                        "li.lingfeng.ltweaks",
                        // The full class name of the activity you want to start
                        "li.lingfeng.ltweaks.activities.WYViewHierarchyActivity");
                _intent.setType("data/view");
                _intent.putExtra(Intent.EXTRA_TEXT, intent.getExtras().toString());
                mActivity.startActivity(_intent);
                */

                Intent _fuck_intent = new Intent(Intent.ACTION_SEND);
                _fuck_intent.setType("text/plain");
                _fuck_intent.putExtra(Intent.EXTRA_TEXT, foundProductId);
                // mActivity.startActivity(_fuck_intent);

                /*
                Field[] fields = mActivity.getClass().getFields();
                String str = "";
                for(Field f:fields){
                    str = str + f.getName() + ";";
                }
                Logger.toast_i(mActivity, str);
                */

            }

            private void recursiveLoopChildren(ViewGroup parent) {
                for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                    final View child = parent.getChildAt(i);
                    if (child instanceof ViewGroup) {
                        recursiveLoopChildren((ViewGroup) child);
                        // DO SOMETHING WITH VIEWGROUP, AFTER CHILDREN HAS BEEN LOOPED

                    } else {
                        if (child != null) {
                            // DO SOMETHING WITH VIEW
                            Object title = YWUtilsForMainFrameActivity.invokeNoParamMeth(child, "getText");
                            if(null == title) continue;

                            if(title instanceof String && title.equals("详情")){
                                int redColorValue = Color.RED;
                                child.setBackgroundColor(redColorValue);
                            }
                            if(title instanceof String && title.equals("商品")){
                                int redColorValue = Color.RED;
                                child.setBackgroundColor(redColorValue);
                            }
                            if(title instanceof String && title.equals("评价")){
                                int redColorValue = Color.RED;
                                child.setBackgroundColor(redColorValue);
                            }
                        }
                    }
                }
            }


        });

        findAndHookActivity(getItemActivity(), "onStop", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                // Logger.toast_i(mActivity, "ProductDetailActivity: stop");
                mActivity = null;
            }
        });


        findAndHookActivity(getItemActivity(), "onCreateOptionsMenu", Menu.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Logger.toast_i(mActivity, "onCreateOptionsMenu");

            }
        });


        if (getShareActivity() != null) {
            findAndHookActivity(getShareActivity(), "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Logger.i("Share activity onResume.");
                    mIsSharing = true;
                    // Logger.toast_i(mActivity, "ShareActivity: share act resume");
                }
            });

            findAndHookActivity(getShareActivity(), "onPause", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Logger.i("Share activity onPause.");
                    mIsSharing = false;
                    // Logger.toast_i(mActivity, "ShareActivity: share act pause");
                }
            });
        }


    }

    protected abstract String getItemActivity();

    protected String getShareActivity() {
        return null;
    }

    private boolean isSharing() {
        return getShareActivity() == null ? true : mIsSharing;
    }
}
