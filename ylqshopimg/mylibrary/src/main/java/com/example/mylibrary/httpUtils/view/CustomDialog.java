package com.example.mylibrary.httpUtils.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mylibrary.R;

public class CustomDialog extends Dialog {
    private String content;

    public CustomDialog(Context context, String content) {
        super(context, R.style.CustomDialog);
        this.content = content;
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (CustomDialog.this.isShowing())
                    CustomDialog.this.dismiss();
                break;
        }
        return true;
    }

    public void setContent(String content) {
        findViewById(R.id.tvcontent).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvcontent)).setText(content);
    }

    private void initView() {
        setContentView(R.layout.dialog_view);
        if (this.content == null) {
            findViewById(R.id.tvcontent).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.tvcontent)).setText(content);
        }
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.8f;
        getWindow().setAttributes(attributes);
        setCancelable(false);
    }
}
