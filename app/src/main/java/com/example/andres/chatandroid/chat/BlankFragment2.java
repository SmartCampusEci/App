package com.example.andres.chatandroid.chat;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.webkit.*;

import com.example.andres.chatandroid.R;


public class BlankFragment2 extends Fragment {

    private String url;
    private WebView myWebView;

    public BlankFragment2() {
        // Required empty public constructor
    }

    public void SetUrl(String url){
        this.url = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank_fragment2, container, false);

        SharedPreferences pref = this.getActivity().getSharedPreferences(Constantes.sharePreference, Context.MODE_PRIVATE);
        final String user = pref.getString(Constantes.PROPERTY_USER, "");
        final String pass = pref.getString(Constantes.PROPERTY_PASS, "");
        myWebView = (WebView) view.findViewById(R.id.web);
        myWebView.getSettings().setBuiltInZoomControls(true);

        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.setInitialScale(1);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setSupportZoom(false);
        myWebView.getSettings().setDomStorageEnabled(true);
        //myWebView.addJavascriptInterface(this,"fillContent");
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                myWebView.loadUrl("javascript:(function($scope) { " +
                        "var x = document.getElementsByTagName('input')[0];" +
                        "sessionStorage.user=" + user + ";" +
                        "})();");
            }
        });

        myWebView.loadUrl(url);
        recargar();
        return view;

    }

    public void recargar(){

        myWebView.reload();
    }



}
