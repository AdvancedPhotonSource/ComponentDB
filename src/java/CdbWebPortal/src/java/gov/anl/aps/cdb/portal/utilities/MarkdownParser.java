/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import static com.vladsch.flexmark.html.renderer.CoreNodeRenderer.isSuppressedLinkPrefix;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.misc.Extension;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.Escaping;
import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.beans.PropertyValueMarkdownDocumentUploadBean;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;

/**
 *
 * @author djarosz
 */
public class MarkdownParser {
    
    private final static String markdownExample = "### Bullet List\n"
            + "\n\n"
            + "- Bullet list item\n"
            + "  - Sub item\n"
            + "- Another item\n"
            + "\n"
            + "### Numbered List\n"
            + "\n\n"
            + "1. Numbered List.\n"
            + "1. Another numbered item.\n"
            + "1. Third item. \n"
            + "\n"
            + "\n"
            + "### Text Decorate\n"
            + "\n"
            + "- **bold** text\n"
            + "- _italicized_ text\n"
            + "\n\n"
            + "## Link\n"
            + "\nMarkdown\n"
            + "[help](https://www.markdownguide.org/basic-syntax/)\n"
            + "\n\n"
            + "### Code Block\n\n"
            + "```\n"
            + "monospaced code content goes here\n"
            + "```\n"
            + "\n\n"
            + "# Heading Levels:\n"
            + "# Level 1 Heading\n"
            + "## Level 2 Heading\n"
            + "### Level 3 Heading\n"
            + "#### Level 4 Heading\n"
            + "##### Level 5 Heading\n"
            + "###### Level 6 Heading\n";                

    private static String markdownExampleAsHtml = null;
        
    private static String contextRoot = null;                
    
    private static final String MD_PROPERTY_APPLICATION_PATH = String.format("%s/", StorageUtility.getPropertyValueMarkdownDocumentsDirectory());   
    
    private static MutableDataHolder options = new MutableDataSet()
            .set(Parser.EXTENSIONS, Arrays.asList(
                    new Extension[]{
                        CDBFlexmarkExtension.create()
                    }
            ));
    private static Parser parser = Parser.builder(options).build();
    private static HtmlRenderer renderer = HtmlRenderer.builder(options).build();
    
    public static String parseMarkdownAsHTML(String text) {
        Node document = parser.parse(text); 
        String html = renderer.render(document); 
        
        return html;
    }  

    public static String getMarkdownExampleText() {
        return markdownExample;
    }

    public static String getMarkdownExampleHtml() {
        if (markdownExampleAsHtml == null) {
            markdownExampleAsHtml = parseMarkdownAsHTML(markdownExample);
        }

        return markdownExampleAsHtml;
    }

    static class CDBFlexmarkExtension implements HtmlRenderer.HtmlRendererExtension {

        @Override
        public void rendererOptions(@NotNull MutableDataHolder options) {
            // add any configuration settings to options you want to apply to everything, here
        }

        @Override
        public void extend(@NotNull HtmlRenderer.Builder htmlRendererBuilder, @NotNull String rendererType) {
            htmlRendererBuilder.nodeRendererFactory(new CDBNodeRenderer.Factory());
        }

        static CDBFlexmarkExtension create() {
            return new CDBFlexmarkExtension();
        }
    }

    // Node renderer to customize markdown html output. 
    static class CDBNodeRenderer implements NodeRenderer {

        public CDBNodeRenderer(DataHolder options) {

        }

        @Override
        public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
            return new HashSet<>(Arrays.asList(
                    new NodeRenderingHandler<>(Image.class, this::render),
                    new NodeRenderingHandler<>(Link.class, this::render)
            ));
        }

        private String getContextRoot() {
            if (contextRoot == null) {
                contextRoot = SessionUtility.getContextRoot();
            }
            return contextRoot;
        }
        
        private String getAttachmentEndpoint(String url, boolean scaled) {            
            if (url.startsWith(PropertyValueMarkdownDocumentUploadBean.MARKDOWN_ATTACHMENT_PREFIX)) { 
                url = url.replace(PropertyValueMarkdownDocumentUploadBean.MARKDOWN_ATTACHMENT_PREFIX, MD_PROPERTY_APPLICATION_PATH); 

                url = getContextRoot() + url; 
                
                if (scaled) {
                    // TODO use arg for API 
                    // url = url + '/' + CdbPropertyValue.SCALED_IMAGE_EXTENSION; 
                    url = url + CdbPropertyValue.SCALED_IMAGE_EXTENSION; 
                } 
            }
            
            return url; 
        }

        private void render(Image node, NodeRendererContext context, HtmlWriter html) {
            BasedSequence nodeUrl = node.getUrl();
            ResolvedLink aLink = context.resolveLink(LinkType.LINK, node.getUrl().unescape(), null, null);
            ResolvedLink imgLink = context.resolveLink(LinkType.IMAGE, node.getUrl().unescape(), null, null);
            
            String fullResUrl = aLink.getUrl();
            String scaledUrl = imgLink.getUrl();

            if (nodeUrl.startsWith("/")) {
                fullResUrl = getAttachmentEndpoint(fullResUrl, false); 
                scaledUrl = getAttachmentEndpoint(scaledUrl, true); 
            }

            // Create a link to full size image. 
            html.attr("href", fullResUrl);
            html.attr("target", "_log_img");
            html.srcPos(node.getChars()).withAttr(aLink).tag("a");

            // Update image node to use scaled image
            if (nodeUrl.startsWith("/")) {
                nodeUrl = nodeUrl.append(CdbPropertyValue.SCALED_IMAGE_EXTENSION);
                node.setUrl(nodeUrl);
            }

            // Standard image render function. Original is "private" 
            // See https://github.com/vsch/flexmark-java/blob/cc3a2f59ba6e532833f4805f8134b4dc966ff837/flexmark/src/main/java/com/vladsch/flexmark/html/renderer/CoreNodeRenderer.java#L617
            if (!(context.isDoNotRenderLinks() || isSuppressedLinkPrefix(node.getUrl(), context))) {
                String altText = new TextCollectingVisitor().collectAndGetText(node);

                if (!node.getUrlContent().isEmpty()) {
                    // reverse URL encoding of =, &
                    String content = Escaping.percentEncodeUrl(node.getUrlContent()).replace("+", "%2B").replace("%3D", "=").replace("%26", "&amp;");
                    scaledUrl += content;
                }

                html.attr("src", scaledUrl);
                html.attr("alt", altText);

                // we have a title part, use that
                if (node.getTitle().isNotNull()) {
                    imgLink = imgLink.withTitle(node.getTitle().unescape());
                }

                html.attr(imgLink.getNonNullAttributes());
                html.srcPos(node.getChars()).withAttr(imgLink).tagVoid("img");
            }
            // End of image block 

            // Close a tag after adding image 
            html.tag("/a");
        }                

        //See https://github.com/vsch/flexmark-java/blob/cc3a2f59ba6e532833f4805f8134b4dc966ff837/flexmark/src/main/java/com/vladsch/flexmark/html/renderer/CoreNodeRenderer.java#L642
        void render(Link node, NodeRendererContext context, HtmlWriter html) {
            if (context.isDoNotRenderLinks() || isSuppressedLinkPrefix(node.getUrl(), context)) {
                context.renderChildren(node);
            } else {
                ResolvedLink resolvedLink = context.resolveLink(LinkType.LINK, node.getUrl().unescape(), null, null);
                String url = resolvedLink.getUrl();

                url = getAttachmentEndpoint(url, false);

                html.attr("href", url);
                html.attr("target", "_log_link");

                // we have a title part, use that
                if (node.getTitle().isNotNull()) {
                    resolvedLink = resolvedLink.withTitle(node.getTitle().unescape());
                }

                html.attr(resolvedLink.getNonNullAttributes());
                html.srcPos(node.getChars()).withAttr(resolvedLink).tag("a");
                context.renderChildren(node);
                html.tag("/a");
            }
        }

        public static class Factory implements NodeRendererFactory {

            @NotNull
            @Override
            public NodeRenderer apply(@NotNull DataHolder options) {
                return new CDBNodeRenderer(options);
            }
        }
    }
    
        
}
