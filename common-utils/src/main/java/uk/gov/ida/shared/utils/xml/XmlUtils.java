package uk.gov.ida.shared.utils.xml;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Due to security requirements, {@link javax.xml.parsers.DocumentBuilder} and
 * {@link javax.xml.parsers.DocumentBuilderFactory} should *only* be used via
 * the utility methods in this class.  For more information on the vulnerabilities
 * identified, see the tests.
 * @see uk.gov.ida.shared.utils.xml.XmlUtilsTest
 */
public abstract class XmlUtils {
    private static final String FEATURE_DISALLOW_DOCTYPE_DECLARATIONS =
            "http://apache.org/xml/features/disallow-doctype-decl";

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtils.class);

    public static String writeToString(Node node) {
        try {
            StringWriter docWriter = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(node), new StreamResult(docWriter));
            return docWriter.toString();
        } catch (TransformerException ex) {
            LOG.error("Unable to convert Element to String", ex);
            throw Throwables.propagate(ex);
        }
    }

    public static Element convertToElement(String xmlString) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(FEATURE_DISALLOW_DOCTYPE_DECLARATIONS, true);
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
    }
}

