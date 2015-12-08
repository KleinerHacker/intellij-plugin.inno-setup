package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.ui.components.panels.VerticalBox;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class JWebView extends VerticalBox {
    private WebView webView;
    private JFXPanel pnlWebView;

    public JWebView() {
        super();

//        final FixedSizeButton btnBack = new FixedSizeButton(25);
//        btnBack.setIcon(AllIcons.Actions.Back);
//        btnBack.setEnabled(false);
//        final FixedSizeButton btnNext = new FixedSizeButton(25);
//        btnNext.setIcon(AllIcons.Actions.Forward);
//        btnNext.setEnabled(false);
//
//        btnBack.addActionListener(e -> Platform.runLater(() -> {
//            if (webView.getEngine().getHistory().getCurrentIndex() > 0) {
//                webView.getEngine().getHistory().go(-1);
//            }
//        }));
//        btnNext.addActionListener(e -> Platform.runLater(() -> {
//            if (webView.getEngine().getHistory().getCurrentIndex() < webView.getEngine().getHistory().getMaxSize() - 1) {
//                webView.getEngine().getHistory().go(+1);
//            }
//        }));

        pnlWebView = new JFXPanel();
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            webView = new WebView();
            pnlWebView.setScene(new Scene(webView));

//            final BooleanBinding backBinding = Bindings.createBooleanBinding(
//                    () -> webView.getEngine().getHistory().getCurrentIndex() > 0,
//                    webView.getEngine().getHistory().currentIndexProperty(), webView.getEngine().getHistory().maxSizeProperty()
//            );
//            backBinding.addListener((v, o, n) -> btnBack.setEnabled(n));
//            final BooleanBinding forwardBinding = Bindings.createBooleanBinding(
//                    () -> webView.getEngine().getHistory().getCurrentIndex() < webView.getEngine().getHistory().getMaxSize() - 1,
//                    webView.getEngine().getHistory().currentIndexProperty(), webView.getEngine().getHistory().maxSizeProperty()
//            );
//            forwardBinding.addListener((v, o, n) -> btnNext.setEnabled(n));
        });

//        final HorizontalBox pnlToolbar = new HorizontalBox();
//        pnlToolbar.add(btnBack);
//        pnlToolbar.add(btnNext);

//        add(pnlToolbar);
        add(pnlWebView);
    }

    public WebEngine getEngine() {
        return webView.getEngine();
    }

    public void setZoom(double value) {
        webView.setZoom(value);
    }

    public double getZoom() {
        return webView.getZoom();
    }
}
