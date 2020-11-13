package org.eclipse.glsp.example.binalyzer;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
 
public class BamlNamespacePrefixMapper extends NamespacePrefixMapper {

  private static final String DEFAULT_PREFIX = "";
  private static final String DEFAULT_URI = "https://schemas.denisvasilik.com/baml";

  private static final String GLSP_PREFIX = "g";
  private static final String GLSP_URI = "https://schemas.denisvasilik.com/glsp";

  @Override
  public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
      if("https://schemas.denisvasilik.com/baml".equals(namespaceUri))
          return "";

      if(GLSP_URI.equals(namespaceUri))
          return GLSP_PREFIX;

      return suggestion;
  }

  @Override
  public String[] getPreDeclaredNamespaceUris() {
      return new String[] { DEFAULT_URI, GLSP_URI };
  }

}