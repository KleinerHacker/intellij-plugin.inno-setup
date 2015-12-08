package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.ui.treeStructure.Tree;
import javafx.application.Platform;
import org.apache.commons.lang.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssChmUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssChmHelpBrowser extends Splitter {
    private static final String FILE_HELP_CONTENT = "hh_generated_contents.hhc";

    private static final class TreeItem {
        private final String title;
        private final String link;

        private TreeItem(String title, String link) {
            this.title = title;
            this.link = link;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final Tree tvHelp;
    private final JWebView webView;

    public IssChmHelpBrowser() {
        super(true);

        tvHelp = new Tree();
        tvHelp.setRootVisible(false);
        tvHelp.setCellRenderer(new TreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                return new JLabel(value.toString(), leaf ? AllIcons.FileTypes.Text : AllIcons.FileTypes.Archive, JLabel.LEFT);
            }
        });
        tvHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && tvHelp.getSelectionCount() > 0) {
                    Platform.runLater(() -> {
                        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tvHelp.getSelectionModel().getSelectionPath().getLastPathComponent();
                        final TreeItem treeItem = (TreeItem) treeNode.getUserObject();
                        if (treeItem.link != null) {
                            webView.getEngine().load(new File(treeItem.link).toURI().toString());
                        }
                    });
                }
            }
        });
        webView = new JWebView();

        final ScrollablePanel pnlScroll = new ScrollablePanel(new BorderLayout());
        pnlScroll.add(tvHelp);

        setFirstComponent(pnlScroll);
        setSecondComponent(webView);
    }

    public IssChmHelpBrowser(File chmFile) {
        this();
        showHelp(chmFile);
    }

    public void showHelp(File chmFile) {
        try {
            final File contentFile = IssChmUtils.decompileChm(chmFile);
            tvHelp.setModel(new DefaultTreeModel(parseContentTree(contentFile)));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); //TODO
        }
    }

    private static TreeNode parseContentTree(File path) {
        final File file = new File(path, FILE_HELP_CONTENT);
        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        try {
            final Document document = Jsoup.parse(file, "UTF-8");
            final Elements ulElements = document.body().getElementsByTag("ul");

            final ArrayList<String> keyList = new ArrayList<>();
            for (final Element element : ulElements) {
                parseList(element, rootNode, keyList, path.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }

        return rootNode;
    }

    private static void parseList(Element rootElement, DefaultMutableTreeNode rootNode, List<String> keyList, String path) {
        final List<Element> children = rootElement.getAllElements();

        DefaultMutableTreeNode lastTreeNode = null;
        for (final Element child : children) {
            if (child.tagName().equalsIgnoreCase("li")) {
                final Elements paramElements = child.getElementsByTag("object").get(0).getElementsByTag("param");
                final String value = paramElements.get(0).attr("value");
                if (value == null || value.trim().isEmpty())
                    continue;
                if (keyList.contains(value))
                    continue;
                final String link = paramElements.size() > 1 ? path + SystemUtils.FILE_SEPARATOR + paramElements.get(1).attr("value") : null;

                final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new TreeItem(value, link));
                rootNode.add(childNode);
                lastTreeNode = childNode;

                keyList.add(value);
            } else if (child.tagName().equalsIgnoreCase("ul")) {
                if (lastTreeNode == null)
                    continue;

                parseList(child, lastTreeNode, keyList, path);
            }
        }
    }
}
