/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Raphaël Bussa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.andres.chatandroid.headerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andres.chatandroid.R;

public class HeaderView extends RelativeLayout {

    private ImageView headerBackground;
    private AvatarView avatar;
    private TextView userName;
    private TextView userEmail;
    private HeaderInterface.OnHeaderClickListener onHeaderClickListener;
    private HeaderInterface.OnHeaderLongClickListener onHeaderLongClickListener;

    public void setOnHeaderClickListener(HeaderInterface.OnHeaderClickListener onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }

    public void setOnHeaderLongClickListener(HeaderInterface.OnHeaderLongClickListener onHeaderLongClickListener) {
        this.onHeaderLongClickListener = onHeaderLongClickListener;
    }

    public HeaderView(Context context) {
        super(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height(context));
        setLayoutParams(params);
        addView(headerBackground());
        addView(headerUserContainer());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onHeaderClickListener != null) {
                    onHeaderClickListener.onClick();
                }
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onHeaderLongClickListener != null) {
                    onHeaderLongClickListener.onLongClick();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private ImageView headerBackground() {
        LayoutParams backgroundParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        headerBackground = new ImageView(getContext());
        headerBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        headerBackground.setLayoutParams(backgroundParams);
        return headerBackground;
    }

    private LinearLayout headerUserContainer() {
        int marginSize = getResources().getDimensionPixelSize(R.dimen.margin);
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar);
        int textSize = getResources().getDimensionPixelSize(R.dimen.text);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.BOTTOM);
        linearLayout.setPadding(marginSize, marginSize, marginSize, marginSize);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, textSize);
        userName = new TextView(getContext());
        userName.setTextColor(Color.WHITE);
        userName.setTypeface(Typeface.DEFAULT_BOLD);
        userName.setGravity(Gravity.CENTER_VERTICAL);
        userName.setSingleLine();
        userName.setEllipsize(TextUtils.TruncateAt.END);
        userName.setLayoutParams(textParams);
        userEmail = new TextView(getContext());
        userEmail.setTextColor(Color.WHITE);
        userEmail.setGravity(Gravity.CENTER_VERTICAL);
        userEmail.setSingleLine();
        userEmail.setEllipsize(TextUtils.TruncateAt.END);
        userEmail.setLayoutParams(textParams);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarParams.setMargins(0, 0, 0, marginSize);
        avatar = new AvatarView(getContext());
        avatar.setLayoutParams(avatarParams);
        avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        linearLayout.addView(avatar);
        linearLayout.addView(userName);
        linearLayout.addView(userEmail);
        return linearLayout;
    }

    public ImageView background() {
        return headerBackground;
    }

    public AvatarView avatar() {
        return avatar;
    }

    public void username(String username) {
        this.userName.setText(username);
    }

    public void email(String email) {
        this.userEmail.setText(email);
    }

    private int height(Context context) {
        int result = getResources().getDimensionPixelSize(R.dimen.normal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = result + getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

}
