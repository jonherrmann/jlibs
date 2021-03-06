/**
 * Copyright 2015 Santhosh Kumar Tekuri
 *
 * The JLibs authors license this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jlibs.xml.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author Santhosh Kumar T
 */
public class DOMNamespaceContext implements NamespaceContext{
    private Node node;

    public DOMNamespaceContext(Node node){
        this.node = node;
    }

    @Override
    public String getNamespaceURI(String prefix){
        if(prefix==null)
            throw new IllegalArgumentException("prefix is null");
        else if(XMLConstants.XML_NS_PREFIX.equals(prefix))
            return XMLConstants.XML_NS_URI;
        else if(XMLConstants.XMLNS_ATTRIBUTE.equals(prefix))
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        else{
            Attr attr = DOMUtil.findNamespaceDeclarationForPrefix(node, prefix);
            if(attr!=null)
                return attr.getNodeValue();
            else if(XMLConstants.DEFAULT_NS_PREFIX.equals(prefix))
                return XMLConstants.NULL_NS_URI;
            else
                return null;
        }
    }

    @Override
    public String getPrefix(String namespaceURI){
        if(namespaceURI==null)
            throw new IllegalArgumentException("namespaceURI is null");
        else if(XMLConstants.XML_NS_URI.equals(namespaceURI))
            return XMLConstants.XML_NS_PREFIX;
        else if(XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI))
            return XMLConstants.XMLNS_ATTRIBUTE;
        else{
            Attr attr = DOMUtil.findNamespaceDeclarationForURI(node, namespaceURI);
            if(attr!=null)
                return attr.getLocalName();
            else if(XMLConstants.NULL_NS_URI.equals(namespaceURI))
                return XMLConstants.DEFAULT_NS_PREFIX;
            else
                return null;
        }
    }

    @Override
    public Iterator getPrefixes(String namespaceURI){
        return new Iterator(node);
    }

    public static class Iterator implements java.util.Iterator<String>{
        private Node node;
        private Set<String> prefixes = new HashSet<String>();
        private int attrIndex = -1;

        public Iterator(Node node){
            this.node = node;
            findNext();
        }

        private void findNext(){
            do{
                NamedNodeMap attrs = node.getAttributes();
                if(attrs!=null){
                    int length = attrs.getLength();
                    for(++attrIndex; attrIndex<length; attrIndex++){
                        Attr attr = (Attr)attrs.item(attrIndex);
                        if(DOMUtil.isNamespaceDeclaration(attr) && !prefixes.contains(attr.getLocalName()))
                            return;
                    }
                }
                node = node.getParentNode();
                attrIndex = -1;
            }while(node!=null);
        }

        @Override
        public boolean hasNext(){
            return node!=null;
        }

        private Attr attr;

        @Override
        public String next(){
            if(hasNext()){
                attr = (Attr)node.getAttributes().item(attrIndex);
                prefixes.add(attr.getLocalName());
                findNext();
                return attr.getLocalName();
            }else
                throw new NoSuchElementException();
        }

        public String getNamespaceURI(){
            return attr.getNamespaceURI();
        }

        @Override
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
}
