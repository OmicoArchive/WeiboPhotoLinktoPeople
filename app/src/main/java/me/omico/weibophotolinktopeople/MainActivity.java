package me.omico.weibophotolinktopeople;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private InputMethodManager inputMethodManager;

    private FloatingActionButton fab;
    private TextInputLayout textInputLayout;
    private TextInputEditText editText;

    private String editTextString;
    private boolean needClearInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));

        fab = findViewById(R.id.find);
        textInputLayout = findViewById(R.id.search_text_input_layout);
        editText = findViewById(R.id.search_edit_text);

        initListener();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.constraint_layout:
                if (inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.find:
                if (!needClearInput) {
                    Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])+$");
                    if (editTextString == null) {
                        textInputLayout.setError("输入框为空");
                    } else if (!pattern.matcher(editTextString).matches()) {
                        textInputLayout.setError("错误：不是有效链接");
                    } else if (editTextString.contains("sinaimg.cn") && editTextString.endsWith(".jpg")) {
                        String photoName = editTextString.split("/")[4].split(".jpg")[0];
                        String userId;

                        if (photoName.substring(0, 2).equals("00")) {
                            userId = String.valueOf(Base62.decode(photoName.substring(2, 8)));
                        } else {
                            userId = String.valueOf(Long.parseLong(photoName.substring(0, 8), 16));
                        }

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://weibo.com/" + userId));
                        startActivity(intent);
                        needClearInput = true;
                        setFabImage(true);
                    }
                } else {
                    editText.setText("");
                    needClearInput = false;
                    setFabImage(false);
                }
                break;
        }
    }

    private void initListener() {
        findViewById(R.id.constraint_layout).setOnClickListener(this);
        fab.setOnClickListener(this);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                needClearInput = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editTextString = editText.getText().toString();
                if (editTextString.equals(""))
                    setFabImage(false);
            }
        });
    }

    private void setFabImage(boolean needClearInput) {
        fab.setImageResource(needClearInput ? R.drawable.ic_clear : R.drawable.ic_search);
    }
}
